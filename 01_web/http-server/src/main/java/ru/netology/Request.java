package ru.netology;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Request {
    private final String method;
    private final String path;
    private final List<NameValuePair> queryParams;

    public Request(String method, String path) throws URISyntaxException {
        this.method = method;
        URI uri = new URI(path);
        this.path = uri.getPath();
        this.queryParams = URLEncodedUtils.parse(uri, Charset.defaultCharset());
    }

    public String getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public List<NameValuePair> getQueryParam(String name) {
        return queryParams.stream()
                .filter(x -> Objects.equals(x.getName(), name))
                .collect(Collectors.toList());
    }

    public List<NameValuePair> getQueryParams() {
        return queryParams;
    }
}