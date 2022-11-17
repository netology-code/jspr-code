package ru.netology;

public class Request {
    String requestMethod;
    String requestHeader;
    String requestBody;

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
}
