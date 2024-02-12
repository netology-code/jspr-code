package ru.netology;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class Request {
    private final String path;
    private final Map<String, String> queryParams;

    public Request(String requestLine) {
        String[] parts = requestLine.split(" ");
        this.path = parts[1];
        this.queryParams = parseQueryParameters(parts[1]);
    }

    public String getPath() {
        return path;
    }

    public String getQueryParam(String name) {
        return queryParams.get(name);
    }

    public Map<String, String> getQueryParams() {
        return queryParams;
    }

    private Map<String, String> parseQueryParameters(String path) {
        Map<String, String> params = new HashMap<>();
        String[] parts = path.split("\\?");
        if (parts.length > 1) {
            String[] pairs = parts[1].split("&");
            for (String pair : pairs) {
                int idx = pair.indexOf("=");
                try {
                    String key = URLDecoder.decode(pair.substring(0, idx), StandardCharsets.UTF_8.name());
                    String value = URLDecoder.decode(pair.substring(idx + 1), StandardCharsets.UTF_8.name());
                    params.put(key, value);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        }
        return params;
    }
}

