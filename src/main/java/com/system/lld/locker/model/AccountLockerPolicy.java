package com.system.lld.locker.model;

/**
 * Defines storage duration policies for user accounts, specifying the free storage grace period
 * and maximum allowed storage duration before a package is considered expired.
 */
public class AccountLockerPolicy {
    private final int freePeriodDays;
    private final int maximumPeriodDays;

    /**
     * Constructs an AccountLockerPolicy with free period and maximum period limits in days.
     *
     * @param freePeriodDays    number of initial days for which locker usage is free of charge
     * @param maximumPeriodDays maximum total days a package can remain stored before expiring
     * @throws IllegalArgumentException if freePeriodDays is negative or maximumPeriodDays < freePeriodDays
     */
    public AccountLockerPolicy(int freePeriodDays, int maximumPeriodDays) {
        if (freePeriodDays < 0 || maximumPeriodDays < freePeriodDays) {
            throw new IllegalArgumentException("Invalid policy parameters: freePeriodDays must be non-negative and maximumPeriodDays must be >= freePeriodDays");
        }
        this.freePeriodDays = freePeriodDays;
        this.maximumPeriodDays = maximumPeriodDays;
    }

    /**
     * Gets the number of free storage days granted by this policy.
     *
     * @return free period in days
     */
    public int getFreePeriodDays() {
        return freePeriodDays;
    }

    /**
     * Gets the maximum storage days allowed before package expiration.
     *
     * @return maximum storage period in days
     */
    public int getMaximumPeriodDays() {
        return maximumPeriodDays;
    }
}

