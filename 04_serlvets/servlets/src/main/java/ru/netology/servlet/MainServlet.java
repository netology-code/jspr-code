package ru.netology.servlet;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import ru.netology.controller.PostController;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class MainServlet extends HttpServlet {
  private final String API_POSTS = "/api/posts";
  private final String API_POSTS_D = "/api/posts/\\d+";
  private final String STR = "/";
  private final String GET_METHOD = "GET";
  private final String POST_METHOD = "POST";
  private final String DELETE_METHOD = "DELETE";
  private PostController controller;

  @Override
  public void init() {
    // final var repository = new PostRepository();
    // final var service = new PostService(repository);
    // controller = new PostController(service);
    final var context = new AnnotationConfigApplicationContext("ru.netology");
    controller = context.getBean(PostController.class);
  }

  @Override
  protected void service(HttpServletRequest req, HttpServletResponse resp) {
    // если деплоились в root context, то достаточно этого
    try {
      final var path = req.getRequestURI();
      final var method = req.getMethod();
      // primitive routing
      if (method.equals(GET_METHOD) && path.equals(API_POSTS)) {
        controller.all(resp);
        return;
      }
      if (method.equals(GET_METHOD) && path.matches(API_POSTS_D)) {
        // easy way
        final var id = parseId(path);
        controller.getById(id, resp);
        return;
      }
      if (method.equals(POST_METHOD) && path.equals(API_POSTS)) {
        controller.save(req.getReader(), resp);
        return;
      }
      if (method.equals(DELETE_METHOD) && path.matches(API_POSTS_D)) {
        // easy way
        final var id = parseId(path);
        controller.removeById(id, resp);
        return;
      }
      resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
    } catch (Exception e) {
      e.printStackTrace();
      resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }
  }

  private long parseId(String path) {
    return Long.parseLong(path.substring(path.lastIndexOf(STR)));
  }
}

