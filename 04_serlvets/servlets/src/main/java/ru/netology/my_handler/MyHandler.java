package ru.netology.my_handler;

import ru.netology.controller.PostController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@FunctionalInterface
public interface MyHandler {

    void handle(HttpServletRequest req, HttpServletResponse res);
}
