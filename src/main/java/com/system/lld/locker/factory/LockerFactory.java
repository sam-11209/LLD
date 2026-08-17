package com.system.lld.locker.factory;

import com.system.lld.locker.model.Locker;
import com.system.lld.locker.model.LockerSize;

/**
 * Factory class for creating {@link Locker} instances.
 */
public class LockerFactory {

    private LockerFactory() {
        // Private constructor to prevent instantiation
    }

    /**
     * Creates a new Locker instance of the specified size with an auto-generated locker ID.
     *
     * @param size the LockerSize for the locker
     * @return a new Locker instance
     * @throws IllegalArgumentException if size is null
     */
    public static Locker createLocker(LockerSize size) {
        if (size == null) {
            throw new IllegalArgumentException("LockerSize cannot be null");
        }
        return switch (size) {
            case SMALL -> new Locker(LockerSize.SMALL);
            case MEDIUM -> new Locker(LockerSize.MEDIUM);
            case LARGE -> new Locker(LockerSize.LARGE);
            case XLARGE -> new Locker(LockerSize.XLARGE);
        };
    }

    /**
     * Creates a new Locker instance with a custom locker ID and specified size.
     *
     * @param lockerId custom unique identifier for the locker
     * @param size     the LockerSize for the locker
     * @return a new Locker instance
     * @throws IllegalArgumentException if size is null
     */
    public static Locker createLocker(String lockerId, LockerSize size) {
        if (size == null) {
            throw new IllegalArgumentException("LockerSize cannot be null");
        }
        return new Locker(lockerId, size);
    }
}

