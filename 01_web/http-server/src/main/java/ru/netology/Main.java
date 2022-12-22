package ru.netology;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class Main {
    public static void main(String[] args) {
        Server server = new Server();

        server.addHandler("GET", "/messages", (request, responseStream) -> {
            final String text = "GET /messages\n" +
                    "Headers: " + request.getHeaders();
            write(text, responseStream);
        });

        server.addHandler("POST", "/messages", (request, responseStream) -> {
            String body = request.getBody().isEmpty() ? "Body is empty" : request.getBody();
            String text = "It's POST messages\n" +
                    "Headers: " + request.getHeaders() + "\n" +
                    "Body: " + body;
            write(text, responseStream);
        });
        server.startServer();

    }
    private static void write(String content, BufferedOutputStream out) throws IOException {
        String respBuilder = "HTTP/1.1 200 OK\r\n" +
                "Content-Type: text/html\r\n" +
                "Content-Length: " + content.length() + "\r\n" +
                "Connection: close\r\n" +
                "\r\n";

        out.write(respBuilder.getBytes());
        out.write(content.getBytes(StandardCharsets.UTF_8));
    }
}


