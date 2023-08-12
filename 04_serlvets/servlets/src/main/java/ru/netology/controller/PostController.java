package ru.netology.controller;

import com.google.gson.Gson;
import org.springframework.stereotype.Controller;
import ru.netology.model.Post;
import ru.netology.service.PostService;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Reader;

@Controller
public class PostController {
  public static final String APPLICATION_JSON = "application/json";
  private final PostService service;
  private final Gson gson = new Gson();

  public PostController(PostService service) {
    this.service = service;
  }

  public void all(HttpServletResponse response) throws IOException {
    response.setContentType(APPLICATION_JSON);
    final var data = service.all();
    serializeResponse(data, response);
  }

  public void getById(long id, HttpServletResponse response) throws IOException {
    final var post = service.getById(id);
    serializeResponse(post, response);
  }

  public void save(Reader body, HttpServletResponse response) throws IOException {
    response.setContentType(APPLICATION_JSON);
    final var post = gson.fromJson(body, Post.class);
    final var data = service.save(post);
    serializeResponse(data, response);
  }

  public void removeById(long id, HttpServletResponse response) throws IOException {
    service.removeById(id);
    serializeResponse("Post " + id + " removed", response);
  }
  private void serializeResponse(Object data, HttpServletResponse response) throws IOException {
    response.getWriter().print(gson.toJson(data));
  }
}
