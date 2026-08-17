package com.system.lld.locker.observer;

import com.system.lld.locker.model.Account;

/**
 * Observer interface following the Observer Pattern to receive system event notifications.
 */
public interface LockerEventObserver {

    /**
     * Receives a notification with a message and the target user account.
     *
     * @param message the notification message string
     * @param account the target Account
     */
    void update(String message, Account account);

    /**
     * Default handler method for processing a structured {@link LockerEvent}.
     *
     * @param event the LockerEvent received
     */
    default void onLockerEvent(LockerEvent event) {
        if (event != null) {
            update(event.getMessage(), event.getAccount());
        }
    }
}

