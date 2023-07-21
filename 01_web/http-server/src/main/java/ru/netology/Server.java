package ru.netology;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.HttpClientBuilder;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;


import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

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

      final String path = parts[1];
      final String queryString = extractQueryString(path);
      final String handlerPath = extractHandlerPath(path);
      final Handler handler = handlers.get(handlerPath);

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

      final Request request = new Request(queryString);
      final Response response = new Response(out);
      handler.handle(request, response);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private String extractQueryString(String path) {
    final int queryIndex = path.indexOf('?');
    if (queryIndex == -1) {
      return "";
    }
    return path.substring(queryIndex + 1);
  }

  private String extractHandlerPath(String path) {
    final int queryIndex = path.indexOf('?');
    if (queryIndex == -1) {
      return path;
    }
    return path.substring(0, queryIndex);
  }

  private void handleMessages(Request request, Response response) throws IOException {
    final String lastParam = request.getQueryParam("last");
    final int last = (lastParam != null) ? Integer.parseInt(lastParam) : 0;

    try {
      final HttpClient httpClient = HttpClientBuilder.create().build();
      final URIBuilder uriBuilder = new URIBuilder("https://example.com/messages");
      uriBuilder.addParameter("last", Integer.toString(last));
      final HttpGet httpGet = new HttpGet(uriBuilder.build());
      final HttpResponse httpResponse = httpClient.execute(httpGet);

      response.setStatus(httpResponse.getStatusLine().getStatusCode());
      httpResponse.getEntity().writeTo(response.getOutputStream());
    } catch (Exception e) {
      response.setStatus(500);
      response.println("Error: " + e.getMessage());
    }
  }
}
