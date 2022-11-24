package ru.netology;

import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.net.URLEncodedUtils;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class Request {
    private boolean isCorrect;
    private String requestMethod;
    private String requestPath;
    private String queryLine;
    private boolean isQuery;
    private List<NameValuePair> queryParams;
    private List<NameValuePair> requestHeaders;
    private String requestBody;
    private List<NameValuePair> postParams;

    protected Request() {}

    public String getRequestMethod() {
        return requestMethod;
    }
    public String getPath() { return requestPath; }
    public boolean getIsCorrect() { return isCorrect; }

    public void setIsCorrect(boolean bool) {this.isCorrect = bool;}
    public void setRequestMethod(String method){this.requestMethod = method;}
    public void setRequestPath(String requestPath){this.requestPath = requestPath;}
    public void setQueryLine(String queryLine){this.queryLine = queryLine;}
    public void setIsQuery(boolean isQuery){this.isQuery = isQuery;}
    public void setRequestHeaders(List<NameValuePair> requestHeaders){this.requestHeaders = requestHeaders;}

    protected List<NameValuePair> getHeaders() { return requestHeaders; }
    protected List<NameValuePair> getPostParams() { return postParams;}
    protected boolean getIsQuery() { return isQuery; }

    protected List<NameValuePair> getQueryParams() {
        try {
            queryParams = URLEncodedUtils.parse(new URI(queryLine), StandardCharsets.UTF_8);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return queryParams;
    }

    protected List<NameValuePair> getQueryParam(String paramName) {
        if (!queryParams.isEmpty()) {
            List<NameValuePair> result = new ArrayList<>();
            queryParams.stream()
                    .filter(x->x.getName().equals(paramName))
                    .forEach(x->result.add(x));
            return result;
        }
        return null;
    }

    protected List<NameValuePair> getHeader(String headerName) {
        if (!requestHeaders.isEmpty()) {
            List<NameValuePair> header = new ArrayList<>();
            requestHeaders.stream()
                    .filter(x->x.getName().trim().equals(headerName))
                    .forEach(x->header.add(x));
        }
        return null;
    }

    protected List<NameValuePair> getPostParam(String postParamName) {
        if (!postParams.isEmpty()) {
            List<NameValuePair> postParam = new ArrayList<>();
            postParams.stream()
                    .filter(x->x.getName().trim().equals(postParamName))
                    .forEach(x->postParam.add(x));
        }
        return null;
    }

}
