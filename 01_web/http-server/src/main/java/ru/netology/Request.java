package ru.netology;

import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.net.URLEncodedUtils;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class Request {
    private String requestMethod;
    private String requestHeader;
    private String requestBody;
    private List<String> requestPath;
    private List<NameValuePair> requestParams;

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

    protected List<NameValuePair> getQueryParams() {
        try {
            requestParams = URLEncodedUtils.parse( new URI(requestHeader) , StandardCharsets.UTF_8);
            return requestParams;
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return null;
    }

    protected List<String> getPathSegments() {
        requestPath = URLEncodedUtils.parsePathSegments(requestHeader, StandardCharsets.UTF_8);
        return requestPath;
    }


}
