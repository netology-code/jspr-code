package ru.netology;

import ru.netology.server.Server;

import java.io.*;
import java.net.ServerSocket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;

public class Main {
  public static void main(String[] args) {
    Server server = new Server(64);
    server.listen(9999);
  }
}


