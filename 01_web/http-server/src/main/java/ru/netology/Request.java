package ru.netology;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Request {
  private final String queryString;
  private final Map<String, String> queryParams;

  public Request(String queryString) {
    this.queryString = queryString;
    this.queryParams = parseQueryString(queryString);
  }

  public String getQueryParam(String name) {
    return queryParams.get(name);
  }

  public Map<String, String> getQueryParams() {
    return queryParams;
  }

  private Map<String, String> parseQueryString(String queryString) {
    final List<NameValuePair> params = URLEncodedUtils.parse(queryString, StandardCharsets.UTF_8);
    final Map<String, String> paramMap = new HashMap<>();
    for (NameValuePair param : params) {
      paramMap.put(param.getName(), param.getValue());
    }
    return paramMap;
  }
}
