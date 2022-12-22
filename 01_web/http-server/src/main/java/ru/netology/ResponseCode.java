package ru.netology;

public enum ResponseCode {
    NOT_FOUND(404, "Not found"),
    OK(200, "OK");

    private final int code;
    private final String status;

    ResponseCode(int code, String status) {
        this.code = code;
        this.status = status;
    }

    public int getCode() {
        return code;
    }

    public String getStatus() {
        return status;
    }
}
