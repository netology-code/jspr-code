package ru.netology;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Main {
  public static void main(String[] args) {
    final var server = new Server(64);
    server.addHandler("GET", "/index.html", new Handler() {
      @Override
      public void handle(Request request, BufferedOutputStream out) {
        try {
          Path filePath = Path.of(".", "01_web", "http-server", "public", request.getPath());
          String mimeType = Files.probeContentType(filePath);
          long sizeFile = Files.size(filePath);
          outResponse(mimeType, sizeFile, out, filePath);
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    });
    server.addHandler("POST", "/messages", new Handler() {
      @Override
      public void handle(Request request, BufferedOutputStream out) {
        try {
          Path filePath = Path.of(".", "01_web", "http-server", "public", request.getPath());
          String mimeType = Files.probeContentType(filePath);
          long sizeFile = Files.size(filePath);
          outResponse(mimeType, sizeFile, out, filePath);
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    });
    server.startedServer(9999);
  }

  public static void outResponse(String mimeType, long size, BufferedOutputStream bufferedOutputStream, Path path) throws IOException {
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