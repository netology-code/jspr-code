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
            if (!parts[0].isEmpty()) {
                request.setRequestMethod(parts[0]);
            }
            if (!parts[1].isEmpty()) {
                request.setRequestHeader(parts[1]);
            }
            if (!parts[2].isEmpty()) {
                request.setRequestBody(parts[2]);
            }

            if (server.handlers.containsKey(request.requestMethod)) {
                if (server.handlers.get(request.requestMethod).containsKey(request.requestHeader)) {
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

            return false;
        }
    }
}
