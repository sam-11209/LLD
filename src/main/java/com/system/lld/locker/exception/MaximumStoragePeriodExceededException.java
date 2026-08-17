package com.system.lld.locker.exception;

/**
 * Exception thrown when a package has been stored in a locker past its maximum allowed storage duration limit.
 */
public class MaximumStoragePeriodExceededException extends RuntimeException {
    /**
     * Constructs a new MaximumStoragePeriodExceededException with the specified detail message.
     *
     * @param message error detail message
     */
    public MaximumStoragePeriodExceededException(String message) {
        super(message);
    }
}

