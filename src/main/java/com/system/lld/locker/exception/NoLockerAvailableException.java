package com.system.lld.locker.exception;

/**
 * Exception thrown when no suitable locker is currently available to accommodate a package deposit request.
 */
public class NoLockerAvailableException extends RuntimeException {
    /**
     * Constructs a new NoLockerAvailableException with the specified detail message.
     *
     * @param message error detail message
     */
    public NoLockerAvailableException(String message) {
        super(message);
    }
}

