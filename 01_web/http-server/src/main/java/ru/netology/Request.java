package ru.netology;

import org.apache.http.NameValuePair;

import java.util.List;
import java.util.stream.Collectors;

public class Request {
    private final String method;
    private final String path;
    private final String fullPath;
    private final String headers;
    private final String body;
    private final List<NameValuePair> nameValuePairs;

    public Request(
            String method,
            String path,
            String fullPath,
            String headers,
            String body,
            List<NameValuePair> nameValuePairs
    ) {
        this.method = method;
        this.path = path;
        this.fullPath = fullPath;
        this.headers = headers;
        this.body = body;
        this.nameValuePairs = nameValuePairs;
    }

    public String getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public String getFullPath() {
        return fullPath;
    }

    public String getHeaders() {
        return headers;
    }

    public String getBody() {
        return body;
    }

    public List<NameValuePair> getQueryParams() {
        return nameValuePairs;
    }

    public List<NameValuePair> getQueryParamByName(String name){
        return nameValuePairs.stream()
                .filter(nameValuePair -> nameValuePair.getName().equals(name))
                .collect(Collectors.toList());
    }

    @Override
    public String toString() {
        return "Request{" +
                "method='" + method + '\'' +
                ", path='" + path + '\'' +
                ", fullPath='" + fullPath + '\'' +
                ", headers='" + headers + '\'' +
                ", body='" + body + '\'' +
                ", nameValuePairs=" + nameValuePairs +
                '}';
    }
}
