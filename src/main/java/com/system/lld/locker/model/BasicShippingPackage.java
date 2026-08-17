package com.system.lld.locker.model;

import com.system.lld.locker.exception.PackageIncompatibleException;

import java.math.BigDecimal;

/**
 * Basic implementation of the {@link ShippingPackage} interface.
 * Represents a package with specific 3D dimensions (width, height, depth) and an associated user account.
 */
public class BasicShippingPackage implements ShippingPackage {
    private final String orderId;
    private final Account user;
    private final BigDecimal width;
    private final BigDecimal height;
    private final BigDecimal depth;
    private volatile ShippingStatus status;

    /**
     * Constructs a BasicShippingPackage with order ID, recipient account, and package dimensions.
     * Sets initial status to {@link ShippingStatus#CREATED}.
     *
     * @param orderId unique order identifier
     * @param user    account of the recipient
     * @param width   width of the package
     * @param height  height of the package
     * @param depth   depth of the package
     */
    public BasicShippingPackage(String orderId, Account user, BigDecimal width, BigDecimal height, BigDecimal depth) {
        this.orderId = orderId;
        this.user = user;
        this.width = width;
        this.height = height;
        this.depth = depth;
        this.status = ShippingStatus.CREATED;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getOrderId() {
        return orderId;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Account getUser() {
        return user;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BigDecimal getWidth() {
        return width;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BigDecimal getHeight() {
        return height;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BigDecimal getDepth() {
        return depth;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ShippingStatus getStatus() {
        return status;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateShippingStatus(ShippingStatus status) {
        this.status = status;
    }

    /**
     * Computes the smallest {@link LockerSize} that can accommodate the package's dimensions.
     * Iterates through available locker sizes in ascending order.
     *
     * @return the fitting LockerSize
     * @throws PackageIncompatibleException if no locker size is large enough to fit the package
     */
    @Override
    public LockerSize getLockerSize() {
        for (LockerSize size : LockerSize.values()) {
            if (size.fits(width, height, depth)) {
                return size;
            }
        }
        throw new PackageIncompatibleException("No locker size available for package orderId: " + orderId);
    }
}

