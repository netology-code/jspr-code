package ru.netology;

import java.io.IOException;
import java.io.OutputStream;

public class Response {
  private final OutputStream outputStream;
  private int status = 200;

  public Response(OutputStream outputStream) {
    this.outputStream = outputStream;
  }

  public void setStatus(int status) {
    this.status = status;
  }

  public OutputStream getOutputStream() {
    return outputStream;
  }

  public void println(String message) throws IOException {
    final String response = message + "\r\n";
    outputStream.write(response.getBytes());
  }

  public void write(byte[] data) throws IOException {
    outputStream.write(data);
  }
}

