package ru.netology;

import java.io.*;

import java.nio.file.Files;
import java.nio.file.Path;


public class Main {
    public static void main(String[] args) throws IOException {
        final var server = new Server(64);

        server.addHandler("GET", "/index.html", (request, out) -> {
            try {
                var filePath = Path.of(".", "01_web", "http-server", "public", request.path());
                var mimeType = Files.probeContentType(filePath);
                var sizeFile = Files.size(filePath);
                outResponse(mimeType, sizeFile, out, filePath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        server.addHandler("POST", "/messages", (request, out) -> {
            try {
                Path filePath = Path.of(".", "01_web", "http-server", "public", request.path());
                String mimeType = Files.probeContentType(filePath);
                long sizeFile = Files.size(filePath);
              outResponse(mimeType, sizeFile, out, filePath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        server.startedServer(9998);
    }
    private static void outResponse(String mimeType, long size, BufferedOutputStream bufferedOutputStream, Path path) throws IOException {
        bufferedOutputStream.write((
                "HTTP/1.1 200 OK\r\n" +
                        "Content-Type: " + mimeType + "\r\n" +
                        "Content-Length: " + size + "\r\n" +
                        "Connection: close\r\n" +
                        "\r\n"
        ).getBytes());
        Files.copy(path, bufferedOutputStream);
        bufferedOutputStream.flush();
    }
}


