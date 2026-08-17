package com.system.lld.locker.exception;

/**
 * Exception thrown when a package's dimensions exceed all available locker size categories in the system.
 */
public class PackageIncompatibleException extends RuntimeException {
    /**
     * Constructs a new PackageIncompatibleException with the specified detail message.
     *
     * @param message error detail message
     */
    public PackageIncompatibleException(String message) {
        super(message);
    }
}

