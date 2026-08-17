package com.system.lld.locker;

import com.system.lld.locker.exception.InvalidAccessCodeException;
import com.system.lld.locker.exception.MaximumStoragePeriodExceededException;
import com.system.lld.locker.exception.PackageIncompatibleException;
import com.system.lld.locker.factory.LockerFactory;
import com.system.lld.locker.model.*;
import com.system.lld.locker.observer.EmailNotificationObserver;
import com.system.lld.locker.observer.SmsNotificationObserver;
import com.system.lld.locker.service.LockerManager;

import java.math.BigDecimal;
import java.util.*;

/**
 * Demo runner showcasing the end-to-end functionality of the Shipping Locker System:
 * locker site setup, package deposits, free vs paid pickup charges, expiration handling, and error scenarios.
 */
public class ShippingLockerDemo {

    /**
     * Main entry point executing the demo walkthrough.
     *
     * @param args command line arguments
     */
    public static void main(String[] args) {

        System.out.println("=== Shipping Locker System Demo ===");

        // 1. Initialize Site with lockers
        Map<LockerSize, Integer> lockerCounts = new EnumMap<>(LockerSize.class);
        lockerCounts.put(LockerSize.SMALL, 2);
        lockerCounts.put(LockerSize.MEDIUM, 2);
        lockerCounts.put(LockerSize.LARGE, 1);

        Site site = new Site("SITE-HUB-01", "Central Station Locker Hub", lockerCounts, null);
        LockerManager lockerManager = new LockerManager(site);

        // 2. Attach Observers
        EmailNotificationObserver emailObserver = new EmailNotificationObserver();
        SmsNotificationObserver smsObserver = new SmsNotificationObserver();
        lockerManager.addObserver(emailObserver);
        lockerManager.addObserver(smsObserver);

        // 3. Create User Account (2 free days, 5 max days)
        AccountLockerPolicy policy = new AccountLockerPolicy(2, 5);
        Account userAccount = new Account("ACC-99", "John Doe", policy);
        lockerManager.addAccount(userAccount);

        System.out.println("\n--- Step 1: Depositing Packages ---");

        // Deposit Package 1 (Small)
        BasicShippingPackage pkg1 = new BasicShippingPackage(
                "ORD-1001", userAccount, new BigDecimal("8.00"), new BigDecimal("8.00"), new BigDecimal("8.00"));
        Locker locker1 = lockerManager.assignPackage(pkg1);
        System.out.println("Package 1 Assigned: Locker ID = " + locker1.getLockerId()
                + ", Size = " + locker1.getSize() + ", Access Code = " + locker1.getAccessCode());

        // Deposit Package 2 (Medium)
        BasicShippingPackage pkg2 = new BasicShippingPackage(
                "ORD-1002", userAccount, new BigDecimal("15.00"), new BigDecimal("15.00"), new BigDecimal("15.00"));
        Locker locker2 = lockerManager.assignPackage(pkg2);
        System.out.println("Package 2 Assigned: Locker ID = " + locker2.getLockerId()
                + ", Size = " + locker2.getSize() + ", Access Code = " + locker2.getAccessCode());

        System.out.println("\n--- Step 2: Same-day Retrieval (Free Period) ---");
        String code1 = locker1.getAccessCode();
        Locker retrievedLocker1 = lockerManager.pickUpPackage(code1, new Date());
        System.out.println("Package 1 retrieved successfully. Account balance charge: $" + userAccount.getUsageCharges());

        System.out.println("\n--- Step 3: Delayed Retrieval (After Free Period) ---");
        String code2 = locker2.getAccessCode();
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, 4); // 4 days later (2 days chargeable @ $10/day = $20)
        Date pickupDate = cal.getTime();

        Locker retrievedLocker2 = lockerManager.pickUpPackage(code2, pickupDate);
        System.out.println("Package 2 retrieved after 4 days. Total Account Charges accumulated: $" + userAccount.getUsageCharges());

        System.out.println("\n--- Step 4: Expiration Demonstration ---");
        BasicShippingPackage pkg3 = new BasicShippingPackage(
                "ORD-1003", userAccount, new BigDecimal("5.00"), new BigDecimal("5.00"), new BigDecimal("5.00"));
        Locker locker3 = lockerManager.assignPackage(pkg3);

        Calendar expCal = Calendar.getInstance();
        expCal.add(Calendar.DAY_OF_MONTH, 6); // 6 days later (exceeds max 5 days)

        try {
            lockerManager.pickUpPackage(locker3.getAccessCode(), expCal.getTime());
        } catch (MaximumStoragePeriodExceededException e) {
            System.out.println("Expected Exception caught: " + e.getMessage());
            System.out.println("Package status after expiration: " + pkg3.getStatus());
            System.out.println("Is locker available now? " + locker3.isAvailable());
        }

        System.out.println("\n--- Step 5: Error Handling ---");
        try {
            lockerManager.pickUpPackage("000000");
        } catch (InvalidAccessCodeException e) {
            System.out.println("Invalid code exception caught: " + e.getMessage());
        }

        try {
            BasicShippingPackage hugePkg = new BasicShippingPackage(
                    "ORD-HUGE", userAccount, new BigDecimal("100.00"), new BigDecimal("100.00"), new BigDecimal("100.00"));
            lockerManager.assignPackage(hugePkg);
        } catch (PackageIncompatibleException e) {
            System.out.println("Incompatible package exception caught: " + e.getMessage());
        }

        System.out.println("\n=== Shipping Locker System Demo Completed Successfully ===");
    }
}
