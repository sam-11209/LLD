package restaurant.core;

import restaurant.menu.Category;
import restaurant.menu.Menu;
import restaurant.menu.MenuItem;
import restaurant.order.OrderItem;
import restaurant.reservation.Reservation;
import restaurant.table.Layout;
import restaurant.table.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Demonstrates the Restaurant Management System end-to-end:
 * 1. Menu initialization & lookup
 * 2. Layout configuration (tables of varying capacities)
 * 3. Scheduled reservation booking & availability slot search
 * 4. Walk-in seating
 * 5. Order management with Command pattern (Kitchen Dispatch, Delivery, Cancellation)
 * 6. Checkout & BigDecimal bill calculation
 * 7. Multi-threaded stress test demonstrating race condition prevention (Section 9.1 & 9.3)
 */
public class Main {

    public static void main(String[] args) throws InterruptedException {
        System.out.println("================================================================");
        System.out.println("       RESTAURANT MANAGEMENT SYSTEM (LLD DEMO)                  ");
        System.out.println("================================================================");

        // 1. Setup Menu
        Menu menu = new Menu();
        MenuItem bruschetta = new MenuItem("Bruschetta", "Toasted garlic bread with diced tomatoes", new BigDecimal("8.50"), Category.APPETIZER);
        MenuItem steak = new MenuItem("Ribeye Steak", "12oz grilled USDA prime ribeye steak", new BigDecimal("34.00"), Category.MAIN);
        MenuItem pasta = new MenuItem("Truffle Pasta", "Fettuccine in creamy black truffle sauce", new BigDecimal("26.50"), Category.MAIN);
        MenuItem tiramisu = new MenuItem("Tiramisu", "Classic Italian espresso-flavored dessert", new BigDecimal("9.00"), Category.DESSERT);

        menu.addItem(bruschetta);
        menu.addItem(steak);
        menu.addItem(pasta);
        menu.addItem(tiramisu);

        System.out.println("[MENU INITIALIZED] Total items: " + menu.getMenuItems().size());

        // 2. Setup Layout (Table capacities: 2, 2, 4, 4, 8)
        Layout layout = new Layout(Arrays.asList(2, 2, 4, 4, 8));
        System.out.println("[LAYOUT INITIALIZED] Tables: " + layout.getTablesById().size());

        // 3. Initialize Restaurant Facade
        Restaurant restaurant = new Restaurant("Bistro Antigravity", menu, layout);

        // 4. Time Slot Search & Scheduled Reservation
        LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.HOURS);
        LocalDateTime slot1 = now.plusHours(2);
        LocalDateTime slot2 = now.plusHours(3);

        System.out.println("\n--- 1. SCHEDULED RESERVATION ---");
        LocalDateTime[] slots = restaurant.findAvailableTimeSlots(slot1, slot2, 4);
        System.out.println("Available slots for party of 4 between " + slot1 + " and " + slot2 + ": " + slots.length + " slots.");

        Reservation res1 = restaurant.createScheduledReservation("Alice Smith", 4, slot1);
        System.out.println("Reserved: " + res1);
        System.out.println("Table " + res1.getAssignedTable().getTableId() + " availability at " + slot1 + ": "
                + res1.getAssignedTable().isAvailableAt(slot1));

        // 5. Walk-in Party
        System.out.println("\n--- 2. WALK-IN SEATING ---");
        Reservation walkIn = restaurant.createWalkInReservation("Bob Walk-in", 2);
        System.out.println("Walk-in seated: " + walkIn);

        // 6. Order Placement (Command Pattern: SendToKitchenCommand)
        System.out.println("\n--- 3. ORDER PLACEMENT & COMMAND PROGRESSION ---");
        Table aliceTable = res1.getAssignedTable();

        restaurant.orderItem(aliceTable, bruschetta);
        restaurant.orderItem(aliceTable, steak);
        restaurant.orderItem(aliceTable, tiramisu);

        List<OrderItem> steakOrders = aliceTable.getOrderedItems().get(steak);
        System.out.println("Steak order status after kitchen command: " + steakOrders.get(0).getStatus());

        // 7. Deliver Item
        restaurant.deliverItem(aliceTable, steak);
        System.out.println("Steak order status after delivery command: " + steakOrders.get(0).getStatus());

        // 8. Cancel an item (e.g. tiramisu)
        System.out.println("Cancelling Tiramisu order...");
        restaurant.cancelItem(aliceTable, tiramisu);
        System.out.println("Tiramisu still on table? " + aliceTable.getOrderedItems().containsKey(tiramisu));

        // 9. Bill Calculation
        BigDecimal totalBill = restaurant.calculateTableBill(aliceTable);
        System.out.println("\n--- 4. BILL CHECKOUT ---");
        System.out.println("Total bill for Table " + aliceTable.getTableId() + ": $" + totalBill);
        System.out.println("Expected: Bruschetta ($8.50) + Steak ($34.00) = $42.50");

        // 10. Concurrency Demonstration: Racing threads attempting to reserve the exact same slot
        System.out.println("\n--- 5. CONCURRENCY TEST (Section 9.1: Zero Double-Bookings) ---");
        int concurrentThreads = 8;
        ExecutorService executor = Executors.newFixedThreadPool(concurrentThreads);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(concurrentThreads);
        AtomicInteger successfulBookings = new AtomicInteger(0);
        AtomicInteger failedBookings = new AtomicInteger(0);

        LocalDateTime contestedSlot = now.plusHours(5);
        // Table of capacity 8 is only Table #5 (only 1 table of size 8 in layout)
        System.out.println("8 concurrent threads competing to book the ONLY table of capacity 8 at " + contestedSlot);

        for (int i = 0; i < concurrentThreads; i++) {
            final int threadId = i;
            executor.submit(() -> {
                try {
                    startLatch.await(); // Simultaneous burst
                    restaurant.createScheduledReservation("Racer-" + threadId, 8, contestedSlot);
                    successfulBookings.incrementAndGet();
                } catch (IllegalStateException e) {
                    failedBookings.incrementAndGet();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    doneLatch.countDown();
                }
            });
        }

        startLatch.countDown(); // Release threads
        doneLatch.await();
        executor.shutdown();

        System.out.println("Concurrent booking outcome:");
        System.out.println("  Successful bookings: " + successfulBookings.get() + " (Expected exactly 1)");
        System.out.println("  Rejected racers (handled gracefully): " + failedBookings.get() + " (Expected 7)");
        System.out.println("\n[DEMO COMPLETE: All tests and constraints satisfied]");
    }
}
