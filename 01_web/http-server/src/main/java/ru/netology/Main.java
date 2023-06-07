package ru.netology;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;

public class Main {
    private static final int poolSize = 64;
    private static final int port = 9999;

    public static void main(String[] args) {

        Server server = new Server(poolSize);

        server.addHandler("GET", "/classic.html", Main::processFile);
        server.addHandler("GET", "/events.html", Main::processFile);
        server.addHandler("GET", "/forms.html", Main::processFile);
        server.addHandler("GET", "/index.html", Main::processFile);
        server.addHandler("GET", "/links.html", Main::processFile);
        server.addHandler("GET", "/resources.html", Main::processFile);
        server.addHandler("GET", "/events.js", Main::processFile);
        server.addHandler("GET", "/spring.png", Main::processFile);
        server.addHandler("GET", "/spring.svg", Main::processFile);
        server.addHandler("GET", "/styles.css", Main::processFile);
        server.addHandler("GET", "/app.js", Main::processFile);

        server.start(port);
    }

    public static void processFile(Request request, BufferedOutputStream out) throws IOException {
        final var filePath = Path.of(".", "public", request.getPath());
        final var mimeType = Files.probeContentType(filePath);

        // special case for classic
        if (request.getPath().equals("/classic.html")) {
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
    }
}

