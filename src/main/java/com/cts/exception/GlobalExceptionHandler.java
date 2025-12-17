package com.cts.exception;

import io.jsonwebtoken.JwtException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {


    // ================= ITEM =================

    @ExceptionHandler(ItemNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleItemNotFound(ItemNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ErrorResponse.builder()
                        .success(false)
                        .errorCode("ITEM_NOT_FOUND")
                        .message(ex.getMessage())
                        .build());
    }

    @ExceptionHandler(ItemCreationException.class)
    public ResponseEntity<ErrorResponse> handleItemCreation(ItemCreationException ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ErrorResponse.builder()
                        .success(false)
                        .errorCode("ITEM_OPERATION_FAILED")
                        .message(ex.getMessage())
                        .build());
    }

    // ================= AUTH =================

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentials() {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ErrorResponse.builder()
                        .success(false)
                        .errorCode("INVALID_CREDENTIALS")
                        .message("Username or password is incorrect")
                        .build());
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleForbidden() {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ErrorResponse.builder()
                        .success(false)
                        .errorCode("ACCESS_DENIED")
                        .message("You do not have permission to access this resource")
                        .build());
    }

    // ================= VALIDATION =================

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        String errorMessage = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(e -> e.getField() + ": " + e.getDefaultMessage())
                .findFirst()
                .orElse("Validation failed");

        return ResponseEntity.badRequest()
                .body(ErrorResponse.builder()
                        .success(false)
                        .errorCode("VALIDATION_FAILED")
                        .message(errorMessage)
                        .build());
    }

    // ================= FALLBACK =================

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ErrorResponse.builder()
                        .success(false)
                        .errorCode("INTERNAL_ERROR")
                        .message("Something went wrong. Please try again later.")
                        .build());
    }


    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFound(
            UsernameNotFoundException ex) {

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                ErrorResponse.builder()
                        .success(false)
                        .errorCode("USER_NOT_FOUND")
                        .message(ex.getMessage())
                        .build()
        );
    }

    @ExceptionHandler(JwtException.class)
    public ResponseEntity<ErrorResponse> handleJwt(JwtException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ErrorResponse.builder()
                        .success(false)
                        .errorCode("INVALID_JWT")
                        .message("Invalid or expired token")
                        .build());
    }


    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrity(DataIntegrityViolationException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ErrorResponse.builder()
                        .success(false)
                        .errorCode("DATA_INTEGRITY_VIOLATION")
                        .message(ex.getMostSpecificCause().getMessage())
                        .build());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException ex) {
        return ResponseEntity.badRequest()
                .body(ErrorResponse.builder()
                        .success(false)
                        .errorCode("BAD_REQUEST")
                        .message(ex.getMessage())
                        .build());
    }


}
