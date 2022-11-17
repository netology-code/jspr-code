package ru.netology;

import java.util.List;

public class Main {
  public static void main(String[] args) {
    final var validPaths = List.of("/index.html", "/spring.svg", "/spring.png", "/resources.html", "/styles.css", "/app.js", "/links.html", "/forms.html", "/classic.html", "/events.html", "/events.js");
    int port = 9999;

    Server server = new Server(port, validPaths);
    server.run();

  }
}


