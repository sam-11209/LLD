package com.system.lld.locker.observer;

import com.system.lld.locker.model.Account;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Concrete observer implementation that simulates sending email notifications to customers upon locker events.
 */
public class EmailNotificationObserver implements LockerEventObserver {

    private final List<String> sentEmailLogs = Collections.synchronizedList(new ArrayList<>());

    /**
     * Handles event notification by logging and printing an email message.
     *
     * @param message notification text
     * @param account target user Account
     */
    @Override
    public void update(String message, Account account) {
        String recipient = (account != null && account.getOwnerName() != null) ? account.getOwnerName() : "Customer";
        String logEntry = "EMAIL to [" + recipient + "]: " + message;
        sentEmailLogs.add(logEntry);
        System.out.println(logEntry);
    }

    /**
     * Returns a copy of all sent email log entries for audit or testing purposes.
     *
     * @return List of email log strings
     */
    public List<String> getSentEmailLogs() {
        return new ArrayList<>(sentEmailLogs);
    }
}

