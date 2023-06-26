package ru.netology;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.ToDoubleBiFunction;

public class Server {

    ExecutorService exService;
    private int val = 0;

    public Server(int sizeThread) {
        this.exService = Executors.newFixedThreadPool(sizeThread);
    }

    public void startedServer(int port) {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            while (true) {
                Socket socket = serverSocket.accept();
                exService.submit(() -> {
                    connection(socket);
                });
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void connection(Socket socket) {

        final var validPaths = List.of("/index.html", "/spring.svg", "/spring.png", "/resources.html", "/styles.css", "/app.js", "/links.html", "/forms.html", "/classic.html", "/events.html", "/events.js");

        try {
            while (true) {
                try (
                        final var in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                        final var out = new BufferedOutputStream(socket.getOutputStream());
                ) {

                    final var requestLine = in.readLine();
                    final var parts = requestLine.split(" ");

                    if (parts.length != 3) {
                        // just close socket
                        continue;
                    }

                    final var path = parts[1];


                    if (!validPaths.contains(path)) {

                        // First metod
                        outNegativeResponse(out);
                        continue;
                    }

                    final var filePath = Path.of(".", "public", path);
                    final var mimeType = Files.probeContentType(filePath);

                    // Second metod

                    // special case for classic
                    if (path.equals("/classic.html")) {
                        final var template = Files.readString(filePath);
                        final var content = template.replace(
                                "{time}",
                                LocalDateTime.now().toString()
                        ).getBytes();
                        // Second metod
                        outResponse(mimeType, content.length, out, filePath);
                        continue;
                    }

                    final var length = Files.size(filePath);
                    // Second metod
                    outResponse(mimeType, length, out, filePath);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void outResponse(String mimeType, long size, BufferedOutputStream bufferedOutputStream, Path path) throws IOException {
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

    private void outNegativeResponse(BufferedOutputStream bufferedOutputStream) throws IOException {
        bufferedOutputStream.write((
                "HTTP/1.1 404 Not Found\r\n" +
                        "Content-Length: 0\r\n" +
                        "Connection: close\r\n" +
                        "\r\n"
        ).getBytes());
        bufferedOutputStream.flush();
    }
}
