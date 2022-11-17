package ru.netology;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class Main {
  public static void main(String[] args) {
    final var validPaths = List.of("/index.html", "/spring.svg", "/spring.png", "/resources.html", "/styles.css", "/app.js", "/links.html", "/forms.html", "/classic.html", "/events.html", "/events.js");
    int port = 9999;

    Server server = new Server(port, validPaths);

    server.addHandler("GET", "/messages", new Handler() {
      @Override
      public void handle(Request request, BufferedOutputStream out) {
        try {
          final var path = request.requestHeader;
          final var filePath = Path.of(".", "public/app.js");
          final var mimeType = Files.probeContentType(filePath);
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
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    });

    server.run();

  }
}


