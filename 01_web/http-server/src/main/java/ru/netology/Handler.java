package ru.netology;

import java.io.BufferedOutputStream;

public interface Handler {
    void handle (Request request, BufferedOutputStream out);
}
