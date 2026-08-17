package com.system.lld.locker.exception;

/**
 * Exception thrown when an invalid, expired, or non-existent access code is provided during package pickup.
 */
public class InvalidAccessCodeException extends RuntimeException {
    /**
     * Constructs a new InvalidAccessCodeException with the specified detail message.
     *
     * @param message error detail message
     */
    public InvalidAccessCodeException(String message) {
        super(message);
    }
}

