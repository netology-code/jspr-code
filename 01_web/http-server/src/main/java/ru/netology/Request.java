package ru.netology;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class Request {
    private final String method;
    private final InputStream body;
    private final String path;
    private final Map<String, String> headers;

    public Request(String method, InputStream body, String path, Map<String, String> headers) {
        this.method = method;
        this.body = body;
        this.path = path;
        this.headers = headers;
    }

    public static Request parsingHttpRequest(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String[] requestStartedLine = bufferedReader.readLine().split(" ");
        String methodHttp = requestStartedLine[0];
        String pathHttp = requestStartedLine[1];
        Map<String, String> headers = setHeaders(bufferedReader);
        return new Request(methodHttp, inputStream, pathHttp, headers);

    }

    private static Map<String, String> setHeaders(BufferedReader bufferedReader) throws IOException {
        Map<String, String> headers = new HashMap<>();
        String headersHttpLine;
        while (!(headersHttpLine = bufferedReader.readLine()).equals("")) {
            int i = headersHttpLine.indexOf(":");
            String key = headersHttpLine.substring(0, i);
            String value = headersHttpLine.substring(i + 2);
            headers.put(key, value);
        }
        return headers;
    }

    public String getMethod() {
        return method;
    }

    public InputStream getBody() {
        return body;
    }

    public String getPath() {
        return path;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }
}