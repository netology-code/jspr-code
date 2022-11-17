package ru.netology;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.concurrent.Callable;

public class Connection implements Callable<Boolean> {

    Server server;
    Request request;

    protected Connection(Server server) {
        this.server = server;
    }


    @Override
    public Boolean call() throws Exception {
        try (
                final var socket = server.serverSocket.accept();
                final var in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                final var out = new BufferedOutputStream(socket.getOutputStream())
        ) {

            final var requestLine = in.readLine();
            final var parts = requestLine.split(" ");

            request = new Request();

            if (parts.length == 3) {
                request.setRequestBody(parts[2]).setRequestHeader(parts[1]).setRequestMethod(parts[0]);
            } else if (parts.length == 2) {
                request.setRequestHeader(parts[1]).setRequestMethod(parts[0]);
            } else if (!server.handlers.isEmpty() & server.handlers.containsKey(request.requestMethod) & server.handlers.get(request.requestMethod).containsKey(request.requestHeader)) {
                server.handlers.get(request.requestMethod).get(request.requestHeader).handle(request, out);
            } else {
                out.write((
                        "HTTP/1.1 404 Not Found\r\n" +
                                "Content-Length: 0\r\n" +
                                "Connection: close\r\n" +
                                "\r\n"
                ).getBytes());
                out.flush();
                return false;
            }

            final var path = request.requestHeader;
            if (!server.validPaths.contains(path)) {
                out.write((
                        "HTTP/1.1 404 Not Found\r\n" +
                                "Content-Length: 0\r\n" +
                                "Connection: close\r\n" +
                                "\r\n"
                ).getBytes());
                out.flush();
                return false;
            }

            final var filePath = Path.of(".", "public", path);
            final var mimeType = Files.probeContentType(filePath);

            // special case for classic
            if (path.equals("/classic.html")) {
                final var template = Files.readString(filePath);
                final var content = template.replace(
                        "{time}",
                        LocalDateTime.now().toString()
                ).getBytes();
                out.write((
                        "HTTP/1.1 200 OK\r\n" +
                                "Content-Type: " + mimeType + "\r\n" +
                                "Content-Length: " + content.length + "\r\n" +
                                "Connection: close\r\n" +
                                "\r\n"
                ).getBytes());
                out.write(content);
                out.flush();
                return false;
            }

            final var length = Files.size(filePath);
            out.write((
                    "HTTP/1.1 200 OK\r\n" +
                            "Content-Type: " + mimeType + "\r\n" +
                            "Content-Length: " + length + "\r\n" +
                            "Connection: close\r\n" +
                            "\r\n"
            ).getBytes());
            Files.copy(filePath, out);
            out.flush();
            return false;
        }
    }
}
