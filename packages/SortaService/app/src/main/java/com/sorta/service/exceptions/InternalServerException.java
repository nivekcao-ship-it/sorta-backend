package com.sorta.service.exceptions;

public class InternalServerException extends ServiceException {
    public InternalServerException(final String message) {
        super(500, message);
    }
    public InternalServerException(final String message, final Exception cause) {
        super(500, message, cause);
    }
}