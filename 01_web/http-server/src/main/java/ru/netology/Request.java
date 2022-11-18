package ru.netology;

public class Request {
    private String requestMethod;
    private String requestHeader;
    private String requestBody;

    protected Request() {
    }

    protected Request setRequestMethod(String requestMethod) {
        this.requestMethod = requestMethod;
        return this;
    }

    protected Request setRequestHeader(String requestHeader) {
        this.requestHeader = requestHeader;
        return this;
    }

    protected Request setRequestBody(String requestBody) {
        this.requestBody = requestBody;
        return this;
    }

    public String getRequestMethod() {
        return requestMethod;
    }

    public String getRequestHeader() {
        return requestHeader;
    }

    public String getRequestBody() {
        return requestBody;
    }
}
