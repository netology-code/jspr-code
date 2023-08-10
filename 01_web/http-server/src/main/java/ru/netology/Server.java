package ru.netology;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
  private final Map<String, Handler> handlers = new ConcurrentHashMap<>();
  private final ExecutorService threadPool = Executors.newFixedThreadPool(64);

  public Server() {
    handlers.put("/messages", this::handleMessages);
  }

  public void start() {
    try (final ServerSocket serverSocket = new ServerSocket(9999)) {
      while (true) {
        final Socket socket = serverSocket.accept();
        threadPool.submit(() -> handleConnection(socket));
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void handleConnection(Socket socket) {
    try (socket;
            final BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            final BufferedOutputStream out = new BufferedOutputStream(socket.getOutputStream())
    ) {
      final String requestLine = in.readLine();
      final String[] parts = requestLine.split(" ");

      if (parts.length != 3) {
        // just close socket
        return;
      }

      String httpMethod = parts[0];
      String queryString = parts[1];
      String httpVersion = parts[2];
      Request request = new Request(httpMethod, queryString, httpVersion);

      final Handler handler = handlers.get(request.getPath());

      if (handler == null) {
        out.write((
                "HTTP/1.1 404 Not Found\r\n" +
                        "Content-Length: 0\r\n" +
                        "Connection: close\r\n" +
                        "\r\n"
        ).getBytes());
        out.flush();
        return;
      }

      handler.handle(request, new Response(out));
    } catch (IOException | URISyntaxException e) {
      e.printStackTrace();
      throw new RuntimeException(e);
    }
  }

  private void handleMessages(Request request, Response response) throws IOException {
    String recipient = request.getQueryParam("recipient");
    String responseBody = "The letter to recipient" + recipient + " has been delivered";
    OutputStream out = response.getOutputStream();
    out.write((
            "HTTP/1.1 200 OK\r\n"
            + "Content-Type: text/html\r\n"
            + "Content-Length: " + responseBody.length() + "\r\n"
            + "\r\n"
            + responseBody
    ).getBytes());
    out.flush();
  }
}
