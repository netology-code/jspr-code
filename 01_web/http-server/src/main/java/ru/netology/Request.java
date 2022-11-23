package ru.netology;

import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.http.message.BasicNameValuePair;
import org.apache.hc.core5.net.URLEncodedUtils;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class Request {
    private String requestMethod;
    private String requestLine;
    private String requestBody;
    private String requestPath;
    private List<NameValuePair> queryParams;
    private char[] buffer;
    private List<NameValuePair> requestHeaders;
    private List<NameValuePair> postParams;

    protected Request() {
    }

    public void setBuffer(char[] buffer) {
        this.buffer = buffer;
    }

    public char[] getBuffer() {
        return buffer;
    }

    public String getRequestMethod() {
        return requestMethod;
    }

    protected Request setRequestMethod(String requestMethod) {
        this.requestMethod = requestMethod;
        return this;
    }

    public String getRequestLine() {
        return requestLine;
    }

    protected Request setRequestLine(String requestLine) {
        this.requestLine = requestLine;
        setPath();
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
            queryParams = URLEncodedUtils.parse( new URI(requestLine) , StandardCharsets.UTF_8);
            return queryParams;
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return null;
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

    private String setPath() {
        if (!requestLine.isEmpty()& requestLine.contains("?")) {
            requestPath = requestLine.substring(0, requestLine.indexOf("?"));
            return requestPath;
        } else {
            return null;
        }
    }

    protected String getPath() {
        return requestPath;
    }

    protected List<NameValuePair> getHeaders() {
            char[] targetF = new char[]{'\r', '\n', '\r', '\n'};
            char[] targetS = new char[]{'\r', '\n'};
            int startOfBody = indexOf(buffer,targetS,0,buffer.length)+2;
            int finishOfBody = indexOf(buffer,targetF,startOfBody,buffer.length);
            requestHeaders = new ArrayList<>();
            String[] bodyValues = String.valueOf(buffer).substring(startOfBody,finishOfBody).split("\r\n");
            Stream.of(bodyValues)
                    .forEach(x->{
                       String[] strgs = x.split(":");
                       requestHeaders.add(new BasicNameValuePair(strgs[0],strgs[1]));
                    });
            return requestHeaders;
    }

    protected List<NameValuePair> getHeader(String headerName) {
        if (!requestHeaders.isEmpty()) {
            List<NameValuePair> postParam = new ArrayList<>();
            postParam.stream()
                    .filter(x->x.getName().equals(headerName))
                    .forEach(x->postParam.add(x));
        }
        return null;
    }

    private static int indexOf(char[] array, char[] target, int start, int max) {
        outer:
        for (int i = start; i < max - target.length + 1; i++) {
            for (int j = 0; j < target.length; j++) {
                if (array[i + j] != target[j]) {
                    continue outer;
                }
            }
            return i;
        }
        return -1;
    }

}
