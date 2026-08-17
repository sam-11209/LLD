package com.system.lld.locker.model;

import java.math.BigDecimal;

/**
 * Enumeration representing available physical locker sizes (SMALL, MEDIUM, LARGE, XLARGE).
 * Each size defines its dimensional capacity (width, height, depth) and daily storage charge.
 */
public enum LockerSize {
    SMALL("Small", new BigDecimal("5.00"), new BigDecimal("10.00"), new BigDecimal("10.00"), new BigDecimal("10.00")),
    MEDIUM("Medium", new BigDecimal("10.00"), new BigDecimal("20.00"), new BigDecimal("20.00"), new BigDecimal("20.00")),
    LARGE("Large", new BigDecimal("15.00"), new BigDecimal("30.00"), new BigDecimal("30.00"), new BigDecimal("30.00")),
    XLARGE("X-Large", new BigDecimal("25.00"), new BigDecimal("45.00"), new BigDecimal("45.00"), new BigDecimal("45.00"));

    private final String sizeName;
    private final BigDecimal dailyCharge;
    private final BigDecimal width;
    private final BigDecimal height;
    private final BigDecimal depth;

    LockerSize(String sizeName, BigDecimal dailyCharge, BigDecimal width, BigDecimal height, BigDecimal depth) {
        this.sizeName = sizeName;
        this.dailyCharge = dailyCharge;
        this.width = width;
        this.height = height;
        this.depth = depth;
    }

    /**
     * Gets the display name of the locker size.
     *
     * @return display size name string
     */
    public String getSizeName() {
        return sizeName;
    }

    /**
     * Gets the daily storage charge rate for this locker size.
     *
     * @return daily charge as BigDecimal
     */
    public BigDecimal getDailyCharge() {
        return dailyCharge;
    }

    /**
     * Gets the maximum package width accommodated by this locker size.
     *
     * @return width as BigDecimal
     */
    public BigDecimal getWidth() {
        return width;
    }

    /**
     * Gets the maximum package height accommodated by this locker size.
     *
     * @return height as BigDecimal
     */
    public BigDecimal getHeight() {
        return height;
    }

    /**
     * Gets the maximum package depth accommodated by this locker size.
     *
     * @return depth as BigDecimal
     */
    public BigDecimal getDepth() {
        return depth;
    }

    /**
     * Evaluates whether package dimensions (width, height, depth) fit within this locker size.
     *
     * @param pkgWidth  width of package
     * @param pkgHeight height of package
     * @param pkgDepth  depth of package
     * @return true if all package dimensions are less than or equal to locker dimensions, false otherwise
     */
    public boolean fits(BigDecimal pkgWidth, BigDecimal pkgHeight, BigDecimal pkgDepth) {
        return this.width.compareTo(pkgWidth) >= 0 &&
               this.height.compareTo(pkgHeight) >= 0 &&
               this.depth.compareTo(pkgDepth) >= 0;
    }
}

