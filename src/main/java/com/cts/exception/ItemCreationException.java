package com.cts.exception;

public class ItemCreationException extends ApplicationException {
    public ItemCreationException(String message) {
        super(message);
    }

    public ItemCreationException(String message, Throwable cause) {
        super(message);
        initCause(cause);
    }
}
