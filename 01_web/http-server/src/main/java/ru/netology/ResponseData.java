package ru.netology;

public class ResponseData {
    private final int code;
    private final String description;
    private final String contentType;
    private final long contentLength;

    public ResponseData(int code, String description, String contentType, long contentLength) {
        this.code = code;
        this.description = description;
        this.contentType = contentType;
        this.contentLength = contentLength;
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public String getContentType() {
        return contentType;
    }

    public long getContentLength() {
        return contentLength;
    }
}
