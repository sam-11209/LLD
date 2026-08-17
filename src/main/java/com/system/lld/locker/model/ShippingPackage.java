package com.system.lld.locker.model;

import java.math.BigDecimal;

/**
 * Interface representing a shipping package to be stored in a locker system.
 */
public interface ShippingPackage {
    /**
     * Gets the unique order identifier for this package.
     *
     * @return order ID string
     */
    String getOrderId();

    /**
     * Gets the recipient/owner account of this package.
     *
     * @return the associated Account
     */
    Account getUser();

    /**
     * Gets the width dimension of the package.
     *
     * @return width as BigDecimal
     */
    BigDecimal getWidth();

    /**
     * Gets the height dimension of the package.
     *
     * @return height as BigDecimal
     */
    BigDecimal getHeight();

    /**
     * Gets the depth dimension of the package.
     *
     * @return depth as BigDecimal
     */
    BigDecimal getDepth();

    /**
     * Gets the current shipping/lifecycle status of the package.
     *
     * @return the ShippingStatus enum value
     */
    ShippingStatus getStatus();

    /**
     * Updates the shipping/lifecycle status of the package.
     *
     * @param status the new ShippingStatus
     */
    void updateShippingStatus(ShippingStatus status);

    /**
     * Determines the minimum {@link LockerSize} required to fit this package.
     *
     * @return the calculated LockerSize
     * @throws com.system.lld.locker.exception.PackageIncompatibleException if package exceeds all locker sizes
     */
    LockerSize getLockerSize();
}

