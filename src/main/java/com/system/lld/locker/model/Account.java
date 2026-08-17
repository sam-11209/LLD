package com.system.lld.locker.model;

import java.math.BigDecimal;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Represents a user/customer account in the locker system.
 * Tracks account details, locker policies, and thread-safely accumulates usage charges.
 */
public class Account {
    private final String accountId;
    private final String ownerName;
    private final AccountLockerPolicy lockerPolicy;
    private final AtomicReference<BigDecimal> usageCharges;

    /**
     * Constructs a user Account with ID, owner name, and an associated locker usage policy.
     *
     * @param accountId    unique identifier for the account
     * @param ownerName    name of the account owner
     * @param lockerPolicy the AccountLockerPolicy governing free and max storage periods
     */
    public Account(String accountId, String ownerName, AccountLockerPolicy lockerPolicy) {
        this.accountId = accountId;
        this.ownerName = ownerName;
        this.lockerPolicy = lockerPolicy;
        this.usageCharges = new AtomicReference<>(new BigDecimal("0.00"));
    }

    /**
     * Gets the unique account identifier.
     *
     * @return account ID string
     */
    public String getAccountId() {
        return accountId;
    }

    /**
     * Gets the name of the account owner.
     *
     * @return owner name string
     */
    public String getOwnerName() {
        return ownerName;
    }

    /**
     * Gets the locker policy assigned to this account.
     *
     * @return the AccountLockerPolicy
     */
    public AccountLockerPolicy getLockerPolicy() {
        return lockerPolicy;
    }

    /**
     * Gets the total accumulated locker usage charges for this account.
     *
     * @return usage charges as BigDecimal
     */
    public BigDecimal getUsageCharges() {
        return usageCharges.get();
    }

    /**
     * Thread-safely adds storage charges to the account's total usage charges.
     * Ignored if amount is null or non-positive.
     *
     * @param amount the charge amount to add
     */
    public void addUsageCharge(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            return;
        }
        usageCharges.updateAndGet(current -> current.add(amount));
    }
}

