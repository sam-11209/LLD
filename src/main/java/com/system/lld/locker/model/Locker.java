package com.system.lld.locker.model;

import com.system.lld.locker.exception.MaximumStoragePeriodExceededException;

import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Represents a physical locker unit within a shipping locker site.
 * Thread-safe access is ensured using a {@link ReentrantLock}.
 */
public class Locker {
    private final String lockerId;
    private final LockerSize size;
    private final ReentrantLock lock = new ReentrantLock();

    private ShippingPackage currentPackage;
    private Date assignmentDate;
    private String accessCode;

    /**
     * Constructs a Locker with an auto-generated unique ID for a given LockerSize.
     *
     * @param size the size classification of the locker
     */
    public Locker(LockerSize size) {
        this.lockerId = "LKR-" + size.name() + "-" + UUID.randomUUID().toString().substring(0, 8);
        this.size = size;
    }

    /**
     * Constructs a Locker with a specific ID and LockerSize.
     *
     * @param lockerId the unique identifier for the locker
     * @param size     the size classification of the locker
     */
    public Locker(String lockerId, LockerSize size) {
        this.lockerId = lockerId;
        this.size = size;
    }

    /**
     * Gets the unique identifier of this locker.
     *
     * @return the locker ID string
     */
    public String getLockerId() {
        return lockerId;
    }

    /**
     * Gets the size classification of this locker.
     *
     * @return the LockerSize enum value
     */
    public LockerSize getSize() {
        return size;
    }

    /**
     * Thread-safely retrieves the package currently stored in this locker.
     *
     * @return the ShippingPackage currently in the locker, or null if empty
     */
    public ShippingPackage getCurrentPackage() {
        lock.lock();
        try {
            return currentPackage;
        } finally {
            lock.unlock();
        }
    }

    /**
     * Thread-safely retrieves the timestamp when the current package was assigned.
     *
     * @return a defensive copy of the assignment Date, or null if empty
     */
    public Date getAssignmentDate() {
        lock.lock();
        try {
            return assignmentDate != null ? new Date(assignmentDate.getTime()) : null;
        } finally {
            lock.unlock();
        }
    }

    /**
     * Thread-safely retrieves the current 6-digit pickup access code.
     *
     * @return the access code string, or null if empty
     */
    public String getAccessCode() {
        lock.lock();
        try {
            return accessCode;
        } finally {
            lock.unlock();
        }
    }

    /**
     * Thread-safely checks if this locker is available (unoccupied).
     *
     * @return true if no package is stored, false otherwise
     */
    public boolean isAvailable() {
        lock.lock();
        try {
            return currentPackage == null;
        } finally {
            lock.unlock();
        }
    }

    /**
     * Checks if the locker is available to be occupied.
     *
     * @return true if empty, false otherwise
     */
    public boolean tryOccupy() {
        lock.lock();
        try {
            return currentPackage == null;
        } finally {
            lock.unlock();
        }
    }

    /**
     * Thread-safely assigns a package to this locker and generates a new random 6-digit access code.
     *
     * @param pkg  the ShippingPackage to place in the locker
     * @param date the deposit timestamp
     * @throws IllegalStateException if the locker is already occupied
     */
    public void assignPackage(ShippingPackage pkg, Date date) {
        lock.lock();
        try {
            if (this.currentPackage != null) {
                throw new IllegalStateException("Locker " + lockerId + " is already occupied.");
            }
            this.currentPackage = pkg;
            this.assignmentDate = date != null ? new Date(date.getTime()) : new Date();
            this.accessCode = generateAccessCode();
        } finally {
            lock.unlock();
        }
    }

    /**
     * Thread-safely releases (empties) the locker, clearing the stored package, assignment date, and access code.
     */
    public void releaseLocker() {
        lock.lock();
        try {
            this.currentPackage = null;
            this.assignmentDate = null;
            this.accessCode = null;
        } finally {
            lock.unlock();
        }
    }

    /**
     * Calculates storage fee based on the duration the package has stayed in the locker,
     * the user's account locker policy (free period days), and the daily charge rate for this locker size.
     *
     * @param currentDate the timestamp of pickup attempt
     * @return the calculated storage charge as BigDecimal
     * @throws MaximumStoragePeriodExceededException if package duration exceeds max allowed days in user's policy
     */
    public BigDecimal calculateStorageCharges(Date currentDate) {
        lock.lock();
        try {
            if (currentPackage == null || assignmentDate == null) {
                return BigDecimal.ZERO;
            }

            Account user = currentPackage.getUser();
            AccountLockerPolicy policy = user != null ? user.getLockerPolicy() : new AccountLockerPolicy(3, 7);

            Date now = currentDate != null ? currentDate : new Date();
            long diffInMillis = now.getTime() - assignmentDate.getTime();
            long totalDaysUsed = Math.max(0, diffInMillis / (1000 * 60 * 60 * 24));

            if (totalDaysUsed > policy.getMaximumPeriodDays()) {
                currentPackage.updateShippingStatus(ShippingStatus.EXPIRED);
                throw new MaximumStoragePeriodExceededException(
                    "Package has exceeded maximum allowed storage period of " + policy.getMaximumPeriodDays() + " days");
            }

            long chargeableDays = Math.max(0, totalDaysUsed - policy.getFreePeriodDays());
            return size.getDailyCharge().multiply(new BigDecimal(chargeableDays));
        } finally {
            lock.unlock();
        }
    }

    /**
     * Convenience method to calculate storage charges using the current system date.
     *
     * @return the calculated storage charge as BigDecimal
     */
    public BigDecimal calculateStorageCharges() {
        return calculateStorageCharges(new Date());
    }

    /**
     * Thread-safely validates whether the given access code matches the generated pickup code.
     *
     * @param code the candidate access code string
     * @return true if valid and matching, false otherwise
     */
    public boolean checkAccessCode(String code) {
        lock.lock();
        try {
            return this.accessCode != null && this.accessCode.equals(code);
        } finally {
            lock.unlock();
        }
    }

    /**
     * Generates a random 6-digit numeric pickup code.
     *
     * @return a 6-digit code string
     */
    private String generateAccessCode() {
        // Generate secure 6-digit random code
        int code = ThreadLocalRandom.current().nextInt(100000, 1000000);
        return String.valueOf(code);
    }

    /**
     * Gets the ReentrantLock associated with this locker for external synchronization block usage.
     *
     * @return the ReentrantLock instance
     */
    public ReentrantLock getLock() {
        return lock;
    }
}

