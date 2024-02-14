package ru.netology.servlet;

import ru.netology.controller.PostController;
import ru.netology.repository.PostRepositoryImpl;
import ru.netology.service.PostService;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class MainServlet extends HttpServlet {
  private static final String POSTS_ENDPOINT = "/api/posts";
  private static final String POSTS_WITH_ID_ENDPOINT = "/api/posts/";

  private PostController controller;

  @Override
  public void init() {
    final var repository = new PostRepositoryImpl();
    final var service = new PostService(repository);
    controller = new PostController(service);
  }

  @Override
  protected void service(HttpServletRequest req, HttpServletResponse resp) {
    try {
      final var path = req.getRequestURI();
      final var method = req.getMethod();

      if (method.equals("GET") && path.equals(POSTS_ENDPOINT)) {
        controller.all(resp);
        return;
      }

      if (method.equals("GET") && path.startsWith(POSTS_WITH_ID_ENDPOINT)) {
        final var id = Long.parseLong(path.substring(POSTS_WITH_ID_ENDPOINT.length()));
        controller.getById(id, resp);
        return;
      }

      if (method.equals("POST") && path.equals(POSTS_ENDPOINT)) {
        controller.save(req.getReader(), resp);
        return;
      }

      if (method.equals("DELETE") && path.startsWith(POSTS_WITH_ID_ENDPOINT)) {
        final var id = Long.parseLong(path.substring(POSTS_WITH_ID_ENDPOINT.length()));
        controller.removeById(id, resp);
        return;
      }

      resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
    } catch (Exception e) {
      e.printStackTrace();
      resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }
  }
}
