package com.cts.common.error;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.Instant;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ApiException.class)
    ResponseEntity<ErrorResponse> handleApi(ApiException exception, HttpServletRequest request) {
        return response(exception.getStatus(), exception.getCode(), exception.getMessage(), request);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException exception, HttpServletRequest request) {
        FieldError error = exception.getBindingResult().getFieldError();
        String message = error == null ? "Request validation failed" : error.getField() + ": " + error.getDefaultMessage();
        return response(HttpStatus.BAD_REQUEST, "VALIDATION_FAILED", message, request);
    }

    @ExceptionHandler({HttpMessageNotReadableException.class, MethodArgumentTypeMismatchException.class})
    ResponseEntity<ErrorResponse> handleMalformedRequest(Exception exception, HttpServletRequest request) {
        return response(HttpStatus.BAD_REQUEST, "MALFORMED_REQUEST", "Request body or parameter is invalid", request);
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    ResponseEntity<ErrorResponse> handleUploadSize(MaxUploadSizeExceededException exception, HttpServletRequest request) {
        return response(HttpStatus.PAYLOAD_TOO_LARGE, "IMAGE_TOO_LARGE", "Image exceeds the configured size limit", request);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    ResponseEntity<ErrorResponse> handleNoRoute(NoResourceFoundException exception, HttpServletRequest request) {
        return response(HttpStatus.NOT_FOUND, "NOT_FOUND", "Route was not found", request);
    }

    @ExceptionHandler(Exception.class)
    ResponseEntity<ErrorResponse> handleUnexpected(Exception exception, HttpServletRequest request) {
        log.error("Unhandled request failure; traceId={}", traceId(request), exception);
        return response(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_ERROR", "An unexpected error occurred", request);
    }

    private ResponseEntity<ErrorResponse> response(HttpStatus status, String code, String message, HttpServletRequest request) {
        return ResponseEntity.status(status).body(new ErrorResponse(false, code, message, traceId(request), Instant.now()));
    }

    private String traceId(HttpServletRequest request) {
        Object value = request.getAttribute(RequestCorrelationFilter.TRACE_ID_ATTRIBUTE);
        return value == null ? "unavailable" : value.toString();
    }
}
