package ru.netology;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.Callable;

public class Connection implements Callable<Boolean> {

    private Server server;
    private Request request;

    protected Connection(Server server) {
        this.server = server;
    }


    @Override
    public Boolean call() throws Exception {
        try (
                final var socket = server.getServerSocket().accept();
                final var in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                final var out = new BufferedOutputStream(socket.getOutputStream())
        ) {

            final var requestLine = in.readLine();
            final var parts = requestLine.split(" ");

            request = new Request();

            if (parts.length == 3) {
                request.setRequestBody(parts[2]).setRequestHeader(parts[1]).setRequestMethod(parts[0]);
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

            if (!server.getHandlers().isEmpty()) {
                Iterator iterator = server.getHandlers().entrySet().iterator();
                while (iterator.hasNext()) {
                    Map.Entry<String, Map<String, Handler>> pair = (Map.Entry) iterator.next();
                    if (request.getRequestMethod().equals(pair.getKey())) {
                        Iterator iterator1 = pair.getValue().entrySet().iterator();
                        while (iterator1.hasNext()) {
                            Map.Entry<String, Handler> map1 = (Map.Entry) iterator1.next();
                            if (map1.getKey().equals(request.getRequestHeader())) {
                                map1.getValue().handle(request, out);
                                return false;
                            }
                        }
                    }
                }
            }

            final var path = request.getRequestHeader();
            if (!server.getValidPaths().contains(path)) {
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
