package ru.netology;

import java.io.IOException;

public interface Handler {
  void handle(Request request, Response response) throws IOException;
}
