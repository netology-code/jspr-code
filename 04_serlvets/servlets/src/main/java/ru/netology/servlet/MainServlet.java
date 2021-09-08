package ru.netology.servlet;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import ru.netology.controller.PostController;
import ru.netology.my_handler.MyHandler;
import ru.netology.repository.PostRepository;
import ru.netology.service.PostService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;


public class MainServlet extends HttpServlet {
    public static final String GET = "GET";
    public static final String POST = "POST";
    public static final String PUT = "PUT";
    public static final String DELETE = "DELETE";
    private static final String PATH_API_POST = "/api/posts";
    private static final String PATH_API_POST_DIGIT = "/api/posts/digit";
    private static final String SLASH = "/";
    private static final HashMap<String, HashMap<String, MyHandler>>
            HANDLERS = new HashMap<>();


    private PostController controller;


    @Override
    public void init() {
        /*final var repository = new PostRepository();
        final var service = new PostService(repository);
        controller = new PostController(service);*/
        // отдаём список пакетов, в которых нужно искать аннотированные классы
        final var context = new AnnotationConfigApplicationContext("ru.netology");

        // получаем по имени бина
        final var controller = context.getBean("postController");

        // получаем по классу бина
        final var service = context.getBean(PostService.class);

        // controller приводим к типу, так как бин получен по имени (String) и возвращает Object
        addHandlers(HANDLERS, (PostController) controller);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        final var path = (req.getRequestURI().matches("/api/posts/\\d+")) ?
                PATH_API_POST_DIGIT : req.getRequestURI();

        if (HANDLERS.getOrDefault(GET, null)
                .getOrDefault(path, null) != null) {

            HANDLERS.get(GET).get(path)
                    .handle(req, resp);
        } else resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {


        if (HANDLERS.getOrDefault(POST, null)
                .getOrDefault(req.getRequestURI(), null) != null) {

            HANDLERS.get(POST).get(req.getRequestURI())
                    .handle(req, resp);
        } else resp.setStatus(HttpServletResponse.SC_NOT_FOUND);

    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (HANDLERS.getOrDefault(PUT, null)
                .getOrDefault(req.getRequestURI(), null) != null) {

            HANDLERS.get(PUT).get(req.getRequestURI())
                    .handle(req, resp);
        } else resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        final var path = (req.getRequestURI().matches("/api/posts/\\d+")) ?
                PATH_API_POST_DIGIT : req.getRequestURI();
        if (path == PATH_API_POST_DIGIT) {
            if (HANDLERS.getOrDefault(DELETE, null)
                    .getOrDefault(path, null) != null) {

                HANDLERS.get(DELETE).get(path)
                        .handle(req, resp);
            } else resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
        } else resp.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
    }

    private static void addHandlers
            (HashMap<String, HashMap<String, MyHandler>> handlers, PostController controller) {
        handlers
                .computeIfAbsent(GET, k -> new HashMap<>())
                .computeIfAbsent(PATH_API_POST, k -> (MyHandler) (req, resp) -> {
                    try {
                        controller.all(resp);
                    } catch (Exception e) {
                        e.printStackTrace();
                        resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    }
                });

        handlers
                .computeIfAbsent(GET, k -> new HashMap<>())
                .computeIfAbsent(PATH_API_POST_DIGIT, k -> (MyHandler) (req, resp) -> {
                    try {
                        final var path = req.getRequestURI();
                        final var id = Long.parseLong(path.substring(path.lastIndexOf(SLASH) + 1));
                        if (id > 0) {
                            controller.getById(id, resp);
                        } else resp.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);

                    } catch (Exception e) {
                        e.printStackTrace();
                        resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    }
                });

        handlers
                .computeIfAbsent(PUT, k -> new HashMap<>())
                .computeIfAbsent(PATH_API_POST, k -> (MyHandler) (req, resp) -> {
                    try {
                        controller.save(req.getReader(), resp);
                        return;
                    } catch (Exception e) {
                        e.printStackTrace();
                        resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    }
                });

        handlers
                .computeIfAbsent(POST, k -> new HashMap<>())
                .computeIfAbsent(PATH_API_POST, k -> (MyHandler) (req, resp) -> {
                    try {
                        controller.save(req.getReader(), resp);
                        return;
                    } catch (Exception e) {
                        e.printStackTrace();
                        resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    }
                });

        handlers
                .computeIfAbsent(DELETE, k -> new HashMap<>())
                .computeIfAbsent(PATH_API_POST_DIGIT, k -> (MyHandler) (req, resp) -> {
                    try {
                        final var path = req.getRequestURI();
                        final var id = Long.parseLong(path.substring(path.lastIndexOf(SLASH) + 1));
                        controller.removeById(id, resp);
                    } catch (Exception e) {
                        e.printStackTrace();
                        resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    }
                });
    }
}

