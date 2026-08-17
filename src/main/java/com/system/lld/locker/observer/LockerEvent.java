package com.system.lld.locker.observer;

import com.system.lld.locker.model.Account;
import com.system.lld.locker.model.Locker;

import java.util.Date;

/**
 * Encapsulates event metadata generated when actions occur in the locker system
 * (e.g. package assignment, retrieval, expiration).
 */
public class LockerEvent {

    /**
     * Enum of possible locker event types.
     */
    public enum EventType {
        /** Triggered when a package is placed into a locker */
        PACKAGE_ASSIGNED,
        /** Triggered when a package is retrieved by a customer */
        PACKAGE_RETRIEVED,
        /** Triggered when a package exceeds its maximum storage duration */
        MAXIMUM_STORAGE_EXCEEDED
    }

    private final EventType eventType;
    private final String message;
    private final Account account;
    private final Locker locker;
    private final Date timestamp;

    /**
     * Constructs a LockerEvent with event type, human-readable message, user account, and locker instance.
     *
     * @param eventType the type of event
     * @param message   descriptive notification message
     * @param account   the user account associated with the package/event
     * @param locker    the locker involved in the event
     */
    public LockerEvent(EventType eventType, String message, Account account, Locker locker) {
        this.eventType = eventType;
        this.message = message;
        this.account = account;
        this.locker = locker;
        this.timestamp = new Date();
    }

    /**
     * Gets the type of event.
     *
     * @return EventType enum value
     */
    public EventType getEventType() {
        return eventType;
    }

    /**
     * Gets the descriptive event message.
     *
     * @return notification message string
     */
    public String getMessage() {
        return message;
    }

    /**
     * Gets the user Account associated with this event.
     *
     * @return Account instance
     */
    public Account getAccount() {
        return account;
    }

    /**
     * Gets the Locker instance associated with this event.
     *
     * @return Locker instance
     */
    public Locker getLocker() {
        return locker;
    }

    /**
     * Gets the timestamp when this event occurred.
     *
     * @return Date timestamp
     */
    public Date getTimestamp() {
        return timestamp;
    }
}

