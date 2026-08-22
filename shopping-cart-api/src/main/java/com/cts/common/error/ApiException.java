package com.cts.common.error;

import org.springframework.http.HttpStatus;

public class ApiException extends RuntimeException {
    private final HttpStatus status;
    private final String code;

    public ApiException(HttpStatus status, String code, String message) {
        super(message);
        this.status = status;
        this.code = code;
    }
    public HttpStatus getStatus() { return status; }
    public String getCode() { return code; }
    public static ApiException notFound(String resource) { return new ApiException(HttpStatus.NOT_FOUND, "NOT_FOUND", resource + " was not found"); }
    public static ApiException forbidden(String message) { return new ApiException(HttpStatus.FORBIDDEN, "FORBIDDEN", message); }
    public static ApiException conflict(String code, String message) { return new ApiException(HttpStatus.CONFLICT, code, message); }
    public static ApiException badRequest(String code, String message) { return new ApiException(HttpStatus.BAD_REQUEST, code, message); }
}
