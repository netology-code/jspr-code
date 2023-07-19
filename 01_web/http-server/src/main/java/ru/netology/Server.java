package ru.netology;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class Server {

    private final ExecutorService exService;
    private final Map<String, Map<String, Handler>> mapRequest = new ConcurrentHashMap<>();
    private final Handler outNegativeResponse = ((request, out) -> {
        try {
            out.write((
                    "HTTP/1.1 404 Not Found\r\n" +
                            "Content-Length: 0\r\n" +
                            "Connection: close\r\n" +
                            "\r\n"
            ).getBytes());
            out.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    });


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

    public void addHandler(String method, String path, Handler handler) {
        if (mapRequest.get(method) == null) {
            mapRequest.put(method, new ConcurrentHashMap<>());
        }
        mapRequest.get(method).put(path, handler);
    }

    private void connection(Socket socket) {

        while (true) {
            try (InputStream inputStream = socket.getInputStream();
                 BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(socket.getOutputStream())) {
                Request requestLine = Request.parsingHttpRequest(inputStream);
                Map<String, Handler> path = mapRequest.get(requestLine != null ? requestLine.method() : null);
                if (path == null) {
                    outNegativeResponse.handle(requestLine, bufferedOutputStream);
                    return;
                }
                Handler handler = path.get(requestLine != null ? requestLine.path() : null);
                if (handler == null) {
                    outNegativeResponse.handle(requestLine, bufferedOutputStream);
                    return;
                }

                handler.handle(requestLine, bufferedOutputStream);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
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
