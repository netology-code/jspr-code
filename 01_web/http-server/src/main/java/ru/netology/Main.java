package ru.netology;

public class Main {
  public static void main(String[] args) {
    int poolSize = 64;
    int port = 9999;
    Server server = new Server(poolSize);
    server.start(port);

  }
}


