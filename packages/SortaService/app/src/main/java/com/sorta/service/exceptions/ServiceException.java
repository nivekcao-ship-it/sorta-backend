package com.sorta.service.exceptions;

public abstract class ServiceException extends RuntimeException {
    private final int statusCode;
    
    protected ServiceException(final int statusCode, final String message) {
        super(message);
        this.statusCode = statusCode;
    }

    protected ServiceException(final int statusCode, final String message, final Exception cause) {
        super(message, cause);
        this.statusCode = statusCode;
    }
    
    public int getStatusCode() { 
        return statusCode; 
    }
}