package ru.netology;

public class Response {
    private final ResponseCode code;
    private final String contentType;
    private final long contentLength;

    public Response(ResponseCode code, String contentType, long contentLength) {
        this.code = code;
        this.contentType = contentType;
        this.contentLength = contentLength;
    }

    public ResponseCode getResponseCode() {
        return code;
    }

    public String getContentType() {
        return contentType;
    }

    public long getContentLength() {
        return contentLength;
    }
}
