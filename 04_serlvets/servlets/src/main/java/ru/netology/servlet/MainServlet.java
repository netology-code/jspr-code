package ru.netology.servlet;

import ru.netology.controller.PostController;
import ru.netology.repository.PostRepository;
import ru.netology.service.PostService;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class MainServlet extends HttpServlet {
  public static final String apiPosts = "/api/posts";
  public static final String apiPostsD = "/api/posts/\\d+";
  public static final String str = "/";
  public static final String getMethod = "GET";
  public static final String postMethod = "POST";
  public static final String deleteMethod = "DELETE";
  private PostController controller;

  @Override
  public void init() {
    final var repository = new PostRepository();
    final var service = new PostService(repository);
    controller = new PostController(service);
  }

  @Override
  protected void service(HttpServletRequest req, HttpServletResponse resp) {
    // если деплоились в root context, то достаточно этого
    try {
      final var path = req.getRequestURI();
      final var method = req.getMethod();
      // primitive routing
      if (method.equals(getMethod) && path.equals(apiPosts)) {
        controller.all(resp);
        return;
      }
      if (method.equals(getMethod) && path.matches(apiPostsD)) {
        // easy way
        final var id = parseId(path);
        controller.getById(id, resp);
        return;
      }
      if (method.equals(postMethod) && path.equals(apiPosts)) {
        controller.save(req.getReader(), resp);
        return;
      }
      if (method.equals(deleteMethod) && path.matches(apiPostsD)) {
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
    return Long.parseLong(path.substring(path.lastIndexOf(str)));
  }
}

