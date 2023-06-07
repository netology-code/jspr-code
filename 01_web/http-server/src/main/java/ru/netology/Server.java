package ru.netology;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    Map<String, Map<String, Handler>> handlers = new ConcurrentHashMap<>();
    private final ExecutorService executorService;
    private final int poolSize;
    private final int port;

    public Server(int poolSize, int port) {
        this.poolSize = poolSize;
        this.port = port;
        this.executorService = Executors.newFixedThreadPool(poolSize);
    }

    public void start(int port) {
        try (final var serverSocket = new ServerSocket(port)) {
            while (true) {
                final var socket = serverSocket.accept();
                executorService.submit(() -> connect(socket));
            }
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }

    private void connect(Socket socket) {
        try (socket;
             var in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             var out = new BufferedOutputStream(socket.getOutputStream());
        ) {
            // read only request line for simplicity
            // must be in form GET /path HTTP/1.1
            final var requestLine = in.readLine();
            final var parts = requestLine.split(" ");
            if (parts.length != 3) {
                // just close socket
                return;
            }
            final var request = new Request(parts[0], parts[1]);
            if (!handlers.containsKey(request.getMethod())) {
                notFoundMessage(out);
            }
            var methodHandlers = handlers.get(request.getMethod());
            if (!methodHandlers.containsKey(request.getPath())) {
                notFoundMessage(out);
            }
            var handler = methodHandlers.get(request.getPath());
            if (handler == null) {
                notFoundMessage(out);
            }
            var getQueryParams = request.getQueryParams();
            var path = request.getPath();
            var method = request.getMethod();
            System.out.println();
            System.out.println("Метод запроса: " + method);
            System.out.println("Ресурс: " + path);
            System.out.println("Параметры запроса: " + getQueryParams);
            System.out.println("Версия протокола: " + parts[2]);
            handler.handle(request, out);
        } catch (IOException ioException) {
            ioException.printStackTrace();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public void addHandler(String method, String path, Handler handler) {
        if (handlers.get(method) == null) {
            handlers.put(method, new ConcurrentHashMap<>());
        }
        handlers.get(method).put(path, handler);
    }

    public void notFoundMessage(BufferedOutputStream out) throws IOException {
        out.write((
                "HTTP/1.1 404 Not Found\r\n" +
                        "Content-Length: 0\r\n" +
                        "Connection: close\r\n" +
                        "\r\n"
        ).getBytes());
        out.flush();
    }
}