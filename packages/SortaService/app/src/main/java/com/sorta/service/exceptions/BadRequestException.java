package com.sorta.service.exceptions;

public class BadRequestException extends ServiceException {
    public BadRequestException(final String message) {
        super(400, message);
    }
    public BadRequestException(final String message, final Exception cause) {
        super(400, message, cause);
    }
}