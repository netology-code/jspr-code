package ru.netology;

public class Request {
    private String requestMethod;
    private String requestHeader;
    private String requestBody;

    protected Request() {
    }

    public String getRequestMethod() {
        return requestMethod;
    }

    protected Request setRequestMethod(String requestMethod) {
        this.requestMethod = requestMethod;
        return this;
    }

    public String getRequestHeader() {
        return requestHeader;
    }

    protected Request setRequestHeader(String requestHeader) {
        this.requestHeader = requestHeader;
        return this;
    }

    public String getRequestBody() {
        return requestBody;
    }

    protected Request setRequestBody(String requestBody) {
        this.requestBody = requestBody;
        return this;
    }
}
