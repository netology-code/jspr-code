package ru.netology;

import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.net.URLEncodedUtils;

import java.io.*;
import java.nio.charset.Charset;
import java.util.*;

public class Request {
    private final static int LIMIT = 4096;
    private final String method;
    private final InputStream body;
    private final String path;
    private final List<String> headers;
    private final Map<String, List<String>> post;
    private final Map<String, List<String>> query;


    public Request(String method, InputStream body, String path, List<String> headers, Map<String, List<String>> post,
                   Map<String, List<String>> query) {
        this.method = method;
        this.body = body;
        this.path = path;
        this.headers = headers;
        this.post = post;
        this.query = query;
    }

    public static Request parsingHttpRequest(InputStream inputStream) throws IOException {
        BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
        bufferedInputStream.mark(LIMIT);
        byte[] buffer = new byte[LIMIT];
        int read = bufferedInputStream.read(buffer);
        byte[] requestLineDelimiter = new byte[]{'\r', '\n'};
        int requestStartedLineEnd = indexOf(buffer, requestLineDelimiter, 0, read);
        if (requestStartedLineEnd == -1) {
            return null;
        }
        String[] requestStartedLine = new String(Arrays.copyOf(buffer, requestStartedLineEnd)).split(" ");
        if (requestStartedLine.length != 3) {
            return null;
        }
        String method = requestStartedLine[0];
        if (!method.equals("GET") && !method.equals("POST")) {
            return null;
        }
        String pathAndQuery = requestStartedLine[1];
        if (!pathAndQuery.startsWith("/")) {
            return null;
        }
        String path;
        Map<String, List<String>> query;
        if (pathAndQuery.contains("?")) {
            String[] urlPathAndQuery = pathAndQuery.split("/?");
            path = urlPathAndQuery[0];
            String pathQuery = urlPathAndQuery[1];
            query = getQueryParams(pathQuery);
        } else {
            path = pathAndQuery;
            query = null;
        }
        byte[] headersDelimiter = new byte[]{'\r', '\n', '\r', '\n'};
        int headersStarted = requestStartedLineEnd + requestLineDelimiter.length;
        int headersEnd = indexOf(buffer, headersDelimiter, headersStarted, read);
        if (headersEnd == -1) {
            return null;
        }
        bufferedInputStream.reset();
        bufferedInputStream.skip(headersStarted);
        byte[] headersBytes = bufferedInputStream.readNBytes(headersEnd - headersStarted);
        List<String> headers = Arrays.asList(new String(headersBytes).split("\r\n"));
        Map<String, List<String>> post = null;
        if (!method.equals("GET")) {
            bufferedInputStream.skip(headersDelimiter.length);
            Optional<String> contentLength = extractHeader(headers, "Content-Length");
            if (contentLength.isPresent()) {
                int length = Integer.parseInt(contentLength.get());
                byte[] bodyBytes = bufferedInputStream.readNBytes(length);
                String body = new String(bodyBytes);
                if (body.contains("=")) {
                    post = getQueryParams(body);
                }
            }
        }
        return new Request(method, inputStream, path, headers, post, query);
    }

    public static Map<String, List<String>> getQueryParams(String url) {
        Map<String, List<String>> queryParams = new HashMap<>();
        List<NameValuePair> params = URLEncodedUtils.parse(url, Charset.defaultCharset(), '&');
        for (NameValuePair param : params) {
            if (queryParams.containsKey(param.getName())) {
                queryParams.get(param.getName()).add(param.getValue());
            } else {
                List<String> values = new ArrayList<>();
                values.add(param.getValue());
                queryParams.put(param.getName(), values);
            }
        }
        return queryParams;
    }

    private static Optional<String> extractHeader(List<String> headers, String header) {
        return headers.stream()
                .filter(o -> o.startsWith(header))
                .map(o -> o.substring(o.indexOf(" ")))
                .map(String::trim)
                .findFirst();
    }

    private static int indexOf(byte[] array, byte[] target, int start, int max) {
        outer:
        for (int i = start; i < max - target.length + 1; i++) {
            for (int j = 0; j < target.length; j++) {
                if (array[j + i] != target[j]) {
                    continue outer;
                }
            }
            return 1;
        }
        return -1;
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

    public List<String> getHeaders() {
        return headers;
    }

    public Map<String, List<String>> getPost() {
        return post;
    }

    public Map<String, List<String>> getQuery() {
        return query;
    }
}