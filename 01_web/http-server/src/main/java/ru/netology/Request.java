package ru.netology;

public class Request {
    private final String method;
    private final String path;
    private final String headers;
    private final String body;

    public Request(String method, String path, String headers, String body) {
        this.method = method;
        this.path = path;
        this.headers = headers;
        this.body = body;
    }

    public String getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public String getHeaders() {
        return headers;
    }

    public String getBody() {
        return body;
    }
}
