package com.system.lld.locker.model;

/**
 * Enumeration representing the lifecycle states of a package in the shipping locker system.
 */
public enum ShippingStatus {
    /** Package object is created */
    CREATED,
    /** Package is pending assignment */
    PENDING,
    /** Package is currently stored in a locker */
    IN_LOCKER,
    /** Package has been successfully retrieved by the customer */
    RETRIEVED,
    /** Package remained in locker beyond the maximum allowed storage duration */
    EXPIRED
}

