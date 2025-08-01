package com.sorta.service.exceptions;

import java.security.Provider;

public class NotFoundException extends ServiceException {
    public NotFoundException(final String message) {
        super(404, message);
    }

    public NotFoundException(final String message, final Exception cause) {
        super(404, message, cause);
    }
}
