package ru.netology;

import java.io.BufferedOutputStream;
import java.io.IOException;

public interface Handler {
    void handle(Request request, BufferedOutputStream out) throws IOException;
}