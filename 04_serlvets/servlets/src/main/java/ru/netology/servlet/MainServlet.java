package ru.netology.servlet;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import ru.netology.controller.PostController;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainServlet extends HttpServlet {
    private PostController controller;
    public static final String API_POSTS = "/api/posts";
    public static final String API_POSTS_D = "/api/posts/\\d+";

    @Override
    public void init() {
        final var context = new AnnotationConfigApplicationContext("ru.netology");
        controller = context.getBean(PostController.class);
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) {

        try {
            final var path = req.getRequestURI();
            final var method = req.getMethod();
            if (method.equals("GET") && path.equals(API_POSTS)) {
                controller.all(resp);
                return;
            }
            if (method.equals("GET") && path.matches(API_POSTS_D)) {
                Pattern pattern = Pattern.compile("/api/posts/(\\d+)");
                Matcher matcher = pattern.matcher(path);
                if (matcher.find()) {
                    String idString = matcher.group(1);
                    long id = Long.parseLong(idString);
                    controller.getById(id, resp);
                    return;
                }
            }
            if (method.equals("POST") && path.equals(API_POSTS)) {
                controller.save(req.getReader(), resp);
                return;
            }
            if (method.equals("DELETE") && path.matches(API_POSTS_D)) {
                Pattern pattern = Pattern.compile("/api/posts/(\\d+)");
                Matcher matcher = pattern.matcher(path);
                if (matcher.find()) {
                    String idString = matcher.group(1);
                    long id = Long.parseLong(idString);
                    controller.removeById(id, resp);
                    return;
                }
            }
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
        } catch (Exception e) {
            e.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}
