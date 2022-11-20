package ru.netology;

import java.io.IOException;
import java.util.List;

public class Main {
  public static void main(String[] args) {

    final var validPaths = List.of("/index.html", "/spring.svg", "/spring.png", "/resources.html", "/styles.css", "/app.js", "/links.html", "/forms.html", "/classic.html", "/events.html", "/events.js");
    int port = 9999;

    Server server = new Server(port, validPaths);

    server.addHandler("GET", "/messages", (request, out) -> {
      try {
        out.write((
                "HTTP/1.1 418 I’m a teapot\r\n" +
                        "Content-Length: 0" + "\r\n" +
                        "Connection: close\r\n" +
                        "\r\n"
        ).getBytes());
        out.flush();
      } catch (IOException e) {
        e.printStackTrace();
      }
    });

    server.listen();

  }
}


