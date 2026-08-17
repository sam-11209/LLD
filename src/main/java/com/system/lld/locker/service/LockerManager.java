package com.system.lld.locker.service;

import com.system.lld.locker.exception.InvalidAccessCodeException;
import com.system.lld.locker.exception.MaximumStoragePeriodExceededException;
import com.system.lld.locker.model.*;
import com.system.lld.locker.observer.LockerEvent;
import com.system.lld.locker.observer.LockerEventObserver;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Service manager for managing package deposits, pickups, user accounts, and event observers
 * within a shipping locker site.
 */
public class LockerManager {
    private final Site site;
    private final Map<String, Account> accounts = new ConcurrentHashMap<>();
    private final Map<String, Locker> accessCodeMap = new ConcurrentHashMap<>();
    private final CopyOnWriteArrayList<LockerEventObserver> observers = new CopyOnWriteArrayList<>();

    /**
     * Constructs a LockerManager for the given locker site.
     *
     * @param site the Site managed by this LockerManager
     */
    public LockerManager(Site site) {
        this.site = site;
    }

    /**
     * Constructs a LockerManager with an initial set of user accounts.
     *
     * @param site     the Site managed by this LockerManager
     * @param accounts map of initial account IDs to Account objects
     */
    public LockerManager(Site site, Map<String, Account> accounts) {
        this.site = site;
        if (accounts != null) {
            this.accounts.putAll(accounts);
        }
    }

    /**
     * Gets the locker site associated with this manager.
     *
     * @return the Site instance
     */
    public Site getSite() {
        return site;
    }

    /**
     * Registers a user account in the locker system.
     *
     * @param account the Account to register
     */
    public void addAccount(Account account) {
        if (account != null) {
            accounts.put(account.getAccountId(), account);
        }
    }

    /**
     * Retrieves a registered account by its unique account ID.
     *
     * @param accountId the unique account identifier
     * @return the matching Account, or null if not found
     */
    public Account getAccount(String accountId) {
        return accounts.get(accountId);
    }

    /**
     * Registers an observer to receive notifications on locker events (e.g. package assigned, retrieved, expired).
     *
     * @param observer the LockerEventObserver to add
     */
    public void addObserver(LockerEventObserver observer) {
        if (observer != null) {
            observers.addIfAbsent(observer);
        }
    }

    /**
     * Unregisters an observer from locker event notifications.
     *
     * @param observer the LockerEventObserver to remove
     */
    public void removeObserver(LockerEventObserver observer) {
        if (observer != null) {
            observers.remove(observer);
        }
    }

    /**
     * Notifies all registered observers about a locker event.
     *
     * @param event the LockerEvent details
     */
    private void notifyObservers(LockerEvent event) {
        for (LockerEventObserver observer : observers) {
            try {
                observer.onLockerEvent(event);
            } catch (Exception e) {
                System.err.println("Error notifying observer: " + e.getMessage());
            }
        }
    }

    /**
     * Assigns a package to an available locker at the site for a specific deposit date.
     * Generates a pickup access code and triggers a notification to observers.
     *
     * @param pkg  the ShippingPackage to deposit
     * @param date the deposit timestamp
     * @return the Locker assigned to the package
     * @throws com.system.lld.locker.exception.NoLockerAvailableException if no suitable locker is available
     * @throws com.system.lld.locker.exception.PackageIncompatibleException if the package exceeds all locker dimensions
     */
    public Locker assignPackage(ShippingPackage pkg, Date date) {
        Locker locker = site.placePackage(pkg, date);
        if (locker != null) {
            String accessCode = locker.getAccessCode();
            accessCodeMap.put(accessCode, locker);

            String message = "Package " + pkg.getOrderId() + " assigned to locker " + locker.getLockerId() + ". Pickup Access Code: " + accessCode;
            LockerEvent event = new LockerEvent(LockerEvent.EventType.PACKAGE_ASSIGNED, message, pkg.getUser(), locker);
            notifyObservers(event);
        }
        return locker;
    }

    /**
     * Convenience method to assign a package using the current system date as deposit date.
     *
     * @param pkg the ShippingPackage to deposit
     * @return the assigned Locker
     */
    public Locker assignPackage(ShippingPackage pkg) {
        return assignPackage(pkg, new Date());
    }

    /**
     * Handles package retrieval using a pickup access code at a given pickup date.
     * Calculates applicable storage charges or marks package as expired if maximum period is exceeded.
     *
     * @param accessCode the 6-digit pickup code
     * @param pickupDate the timestamp when the customer attempts retrieval
     * @return the Locker that contained the retrieved package
     * @throws InvalidAccessCodeException              if access code is invalid or empty
     * @throws MaximumStoragePeriodExceededException if package remained in locker beyond account's maximum allowed period
     */
    public Locker pickUpPackage(String accessCode, Date pickupDate) {
        if (accessCode == null || accessCode.trim().isEmpty()) {
            throw new InvalidAccessCodeException("Access code cannot be empty.");
        }

        Locker locker = accessCodeMap.get(accessCode);
        if (locker == null || !locker.checkAccessCode(accessCode)) {
            throw new InvalidAccessCodeException("Invalid access code: " + accessCode);
        }

        locker.getLock().lock();
        try {
            // Remove code mapping to prevent duplicate pickups
            accessCodeMap.remove(accessCode);
            ShippingPackage pkg = locker.getCurrentPackage();

            try {
                BigDecimal charge = locker.calculateStorageCharges(pickupDate);
                locker.releaseLocker();

                if (pkg != null) {
                    if (pkg.getUser() != null) {
                        pkg.getUser().addUsageCharge(charge);
                    }
                    pkg.updateShippingStatus(ShippingStatus.RETRIEVED);

                    String message = "Package " + pkg.getOrderId() + " picked up successfully. Usage Charge: $" + charge;
                    LockerEvent event = new LockerEvent(LockerEvent.EventType.PACKAGE_RETRIEVED, message, pkg.getUser(), locker);
                    notifyObservers(event);
                }
                return locker;

            } catch (MaximumStoragePeriodExceededException e) {
                locker.releaseLocker();
                if (pkg != null) {
                    pkg.updateShippingStatus(ShippingStatus.EXPIRED);
                    String message = "Package " + pkg.getOrderId() + " expired! Exceeded maximum storage period.";
                    LockerEvent event = new LockerEvent(LockerEvent.EventType.MAXIMUM_STORAGE_EXCEEDED, message, pkg.getUser(), locker);
                    notifyObservers(event);
                }
                throw e;
            }
        } finally {
            locker.getLock().unlock();
        }
    }

    /**
     * Convenience method to pick up a package using the current system date as pickup date.
     *
     * @param accessCode the 6-digit pickup access code
     * @return the Locker from which the package was retrieved
     */
    public Locker pickUpPackage(String accessCode) {
        return pickUpPackage(accessCode, new Date());
    }

    /**
     * Returns the Locker currently mapped to the provided access code.
     *
     * @param accessCode the pickup access code
     * @return the assigned Locker, or null if invalid or expired
     */
    public Locker getLockerByAccessCode(String accessCode) {
        return accessCodeMap.get(accessCode);
    }
}

