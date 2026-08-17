package com.system.lld.locker;

import com.system.lld.locker.exception.InvalidAccessCodeException;
import com.system.lld.locker.exception.MaximumStoragePeriodExceededException;
import com.system.lld.locker.exception.NoLockerAvailableException;
import com.system.lld.locker.exception.PackageIncompatibleException;
import com.system.lld.locker.model.*;
import com.system.lld.locker.observer.EmailNotificationObserver;
import com.system.lld.locker.observer.SmsNotificationObserver;
import com.system.lld.locker.service.LockerManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

class ShippingLockerSystemTest {

    private Site site;
    private LockerManager lockerManager;
    private Account testAccount;
    private EmailNotificationObserver emailObserver;
    private SmsNotificationObserver smsObserver;

    @BeforeEach
    void setUp() {
        Map<LockerSize, Integer> lockerCounts = new HashMap<>();
        lockerCounts.put(LockerSize.SMALL, 2);
        lockerCounts.put(LockerSize.MEDIUM, 2);
        lockerCounts.put(LockerSize.LARGE, 1);

        site = new Site("SITE-101", "Downtown Locker Hub", lockerCounts, null);
        lockerManager = new LockerManager(site);

        AccountLockerPolicy policy = new AccountLockerPolicy(2, 5); // 2 free days, max 5 days
        testAccount = new Account("ACC-1", "Alice Smith", policy);
        lockerManager.addAccount(testAccount);

        emailObserver = new EmailNotificationObserver();
        smsObserver = new SmsNotificationObserver();
        lockerManager.addObserver(emailObserver);
        lockerManager.addObserver(smsObserver);
    }

    @Test
    @DisplayName("Should assign package to smallest fitting locker and retrieve successfully")
    void testBasicPackageAssignmentAndPickup() {
        BasicShippingPackage pkg = new BasicShippingPackage("ORD-101", testAccount,
                new BigDecimal("8.00"), new BigDecimal("8.00"), new BigDecimal("8.00"));

        Date dropDate = new Date();
        Locker locker = lockerManager.assignPackage(pkg, dropDate);

        assertNotNull(locker);
        assertEquals(LockerSize.SMALL, locker.getSize());
        assertEquals(ShippingStatus.IN_LOCKER, pkg.getStatus());
        assertFalse(locker.isAvailable());
        assertNotNull(locker.getAccessCode());

        // Verify observers received notification
        assertFalse(emailObserver.getSentEmailLogs().isEmpty());
        assertFalse(smsObserver.getSentSmsLogs().isEmpty());

        String accessCode = locker.getAccessCode();

        // Pickup on same day -> 0 charges
        Locker pickedLocker = lockerManager.pickUpPackage(accessCode, dropDate);
        assertEquals(locker, pickedLocker);
        assertEquals(ShippingStatus.RETRIEVED, pkg.getStatus());
        assertTrue(locker.isAvailable());
        assertEquals(new BigDecimal("0.00"), testAccount.getUsageCharges());
    }

    @Test
    @DisplayName("Should assign package to next available larger locker if exact size is full (Smallest Fit Strategy)")
    void testSmallestFitLockerAssignment() {
        // Fill both SMALL lockers
        BasicShippingPackage pkg1 = new BasicShippingPackage("ORD-1", testAccount, new BigDecimal("5.00"), new BigDecimal("5.00"), new BigDecimal("5.00"));
        BasicShippingPackage pkg2 = new BasicShippingPackage("ORD-2", testAccount, new BigDecimal("5.00"), new BigDecimal("5.00"), new BigDecimal("5.00"));

        Locker locker1 = lockerManager.assignPackage(pkg1);
        Locker locker2 = lockerManager.assignPackage(pkg2);

        assertEquals(LockerSize.SMALL, locker1.getSize());
        assertEquals(LockerSize.SMALL, locker2.getSize());

        // Place a 3rd package requiring SMALL locker -> should get upgraded to MEDIUM locker
        BasicShippingPackage pkg3 = new BasicShippingPackage("ORD-3", testAccount, new BigDecimal("5.00"), new BigDecimal("5.00"), new BigDecimal("5.00"));
        Locker locker3 = lockerManager.assignPackage(pkg3);

        assertEquals(LockerSize.MEDIUM, locker3.getSize());
    }

    @Test
    @DisplayName("Should throw PackageIncompatibleException if package exceeds all locker sizes")
    void testPackageIncompatible() {
        BasicShippingPackage hugePkg = new BasicShippingPackage("ORD-HUGE", testAccount,
                new BigDecimal("100.00"), new BigDecimal("100.00"), new BigDecimal("100.00"));

        assertThrows(PackageIncompatibleException.class, () -> lockerManager.assignPackage(hugePkg));
    }

    @Test
    @DisplayName("Should calculate daily charges after free period")
    void testDailyChargesCalculation() {
        BasicShippingPackage pkg = new BasicShippingPackage("ORD-201", testAccount,
                new BigDecimal("15.00"), new BigDecimal("15.00"), new BigDecimal("15.00")); // MEDIUM locker ($10.00/day)

        Calendar cal = Calendar.getInstance();
        Date dropDate = cal.getTime();

        Locker locker = lockerManager.assignPackage(pkg, dropDate);
        assertEquals(LockerSize.MEDIUM, locker.getSize());

        // Pickup 4 days later (Free period = 2 days, chargeable days = 4 - 2 = 2 days -> $20.00)
        cal.add(Calendar.DAY_OF_MONTH, 4);
        Date pickupDate = cal.getTime();

        lockerManager.pickUpPackage(locker.getAccessCode(), pickupDate);

        assertEquals(new BigDecimal("20.00"), testAccount.getUsageCharges());
        assertEquals(ShippingStatus.RETRIEVED, pkg.getStatus());
        assertTrue(locker.isAvailable());
    }

    @Test
    @DisplayName("Should throw MaximumStoragePeriodExceededException when exceeding max period")
    void testMaximumStoragePeriodExceeded() {
        BasicShippingPackage pkg = new BasicShippingPackage("ORD-EXP", testAccount,
                new BigDecimal("8.00"), new BigDecimal("8.00"), new BigDecimal("8.00"));

        Calendar cal = Calendar.getInstance();
        Date dropDate = cal.getTime();

        Locker locker = lockerManager.assignPackage(pkg, dropDate);
        String accessCode = locker.getAccessCode();

        // Pickup 6 days later (Max period is 5 days)
        cal.add(Calendar.DAY_OF_MONTH, 6);
        Date pickupDate = cal.getTime();

        assertThrows(MaximumStoragePeriodExceededException.class, () -> lockerManager.pickUpPackage(accessCode, pickupDate));
        assertEquals(ShippingStatus.EXPIRED, pkg.getStatus());
        assertTrue(locker.isAvailable());
    }

    @Test
    @DisplayName("Should throw InvalidAccessCodeException for invalid or reused access codes")
    void testInvalidAccessCode() {
        assertThrows(InvalidAccessCodeException.class, () -> lockerManager.pickUpPackage("INVALID-CODE"));
    }

    @Test
    @DisplayName("Should handle high volume concurrent package deposits safely")
    void testConcurrentPackagePlacement() throws InterruptedException, ExecutionException {
        int numThreads = 5;
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        ConcurrentLinkedQueue<String> accessCodes = new ConcurrentLinkedQueue<>();
        AtomicInteger successCount = new AtomicInteger(0);

        List<Callable<Void>> tasks = new ArrayList<>();
        for (int i = 0; i < numThreads; i++) {
            final int id = i;
            tasks.add(() -> {
                BasicShippingPackage pkg = new BasicShippingPackage("CONC-ORD-" + id, testAccount,
                        new BigDecimal("5.00"), new BigDecimal("5.00"), new BigDecimal("5.00"));
                try {
                    Locker locker = lockerManager.assignPackage(pkg);
                    if (locker != null) {
                        accessCodes.add(locker.getAccessCode());
                        successCount.incrementAndGet();
                    }
                } catch (NoLockerAvailableException e) {
                    // Expected if lockers run out
                }
                return null;
            });
        }

        List<Future<Void>> futures = executor.invokeAll(tasks);
        for (Future<Void> f : futures) {
            f.get();
        }

        executor.shutdown();

        // Total lockers available in site: 2 SMALL + 2 MEDIUM + 1 LARGE = 5 lockers
        assertEquals(5, successCount.get());
        assertEquals(5, accessCodes.size());

        // Perform concurrent pickups
        ExecutorService pickupExecutor = Executors.newFixedThreadPool(5);
        List<Callable<Void>> pickupTasks = new ArrayList<>();
        for (String code : accessCodes) {
            pickupTasks.add(() -> {
                lockerManager.pickUpPackage(code);
                return null;
            });
        }

        List<Future<Void>> pickupFutures = pickupExecutor.invokeAll(pickupTasks);
        for (Future<Void> pf : pickupFutures) {
            pf.get();
        }

        pickupExecutor.shutdown();

        // All lockers should be released and available again
        for (Set<Locker> lockerSet : site.getLockers().values()) {
            for (Locker l : lockerSet) {
                assertTrue(l.isAvailable());
            }
        }
    }
}
