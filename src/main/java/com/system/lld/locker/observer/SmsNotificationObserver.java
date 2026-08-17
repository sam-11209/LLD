package com.system.lld.locker.observer;

import com.system.lld.locker.model.Account;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Concrete observer implementation that simulates sending SMS notifications to customers upon locker events.
 */
public class SmsNotificationObserver implements LockerEventObserver {

    private final List<String> sentSmsLogs = Collections.synchronizedList(new ArrayList<>());

    /**
     * Handles event notification by logging and printing an SMS message.
     *
     * @param message notification text
     * @param account target user Account
     */
    @Override
    public void update(String message, Account account) {
        String recipient = (account != null && account.getOwnerName() != null) ? account.getOwnerName() : "Customer";
        String logEntry = "SMS to [" + recipient + "]: " + message;
        sentSmsLogs.add(logEntry);
        System.out.println(logEntry);
    }

    /**
     * Returns a copy of all sent SMS log entries for audit or testing purposes.
     *
     * @return List of SMS log strings
     */
    public List<String> getSentSmsLogs() {
        return new ArrayList<>(sentSmsLogs);
    }
}

