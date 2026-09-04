package restaurant;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import restaurant.core.Restaurant;
import restaurant.menu.Category;
import restaurant.menu.Menu;
import restaurant.menu.MenuItem;
import restaurant.order.CancelCommand;
import restaurant.order.DeliverCommand;
import restaurant.order.OrderItem;
import restaurant.order.OrderManager;
import restaurant.order.SendToKitchenCommand;
import restaurant.order.Status;
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

import static org.junit.jupiter.api.Assertions.*;

public class RestaurantTest {

    private Menu menu;
    private Layout layout;
    private Restaurant restaurant;
    private MenuItem burger;
    private MenuItem fries;
    private MenuItem drink;

    @BeforeEach
    void setUp() {
        menu = new Menu();
        burger = new MenuItem("Burger", "Juicy Angus beef burger", new BigDecimal("12.50"), Category.MAIN);
        fries = new MenuItem("Fries", "Crispy French fries", new BigDecimal("4.00"), Category.APPETIZER);
        drink = new MenuItem("Lemonade", "Fresh squeezed lemonade", new BigDecimal("3.50"), Category.DESSERT);

        menu.addItem(burger);
        menu.addItem(fries);
        menu.addItem(drink);

        // Capacities: Table 1 (cap 2), Table 2 (cap 4), Table 3 (cap 6)
        layout = new Layout(Arrays.asList(2, 4, 6));
        restaurant = new Restaurant("Gourmet Express", menu, layout);
    }

    @Test
    @DisplayName("Should create scheduled reservation on smallest fitting table")
    void testCreateScheduledReservation() {
        LocalDateTime time = LocalDateTime.now().plusDays(1).truncatedTo(ChronoUnit.HOURS);
        Reservation res = restaurant.createScheduledReservation("John Doe", 3, time);

        assertNotNull(res);
        assertEquals("John Doe", res.getPartyName());
        assertEquals(3, res.getPartySize());
        assertEquals(time, res.getTime());
        // Smallest fitting table for size 3 is Table 2 (capacity 4)
        assertEquals(4, res.getAssignedTable().getCapacity());
        assertFalse(res.getAssignedTable().isAvailableAt(time));
    }

    @Test
    @DisplayName("Should reject reservation when no suitable table is available")
    void testRejectReservationWhenFull() {
        LocalDateTime time = LocalDateTime.now().plusDays(1).truncatedTo(ChronoUnit.HOURS);
        // Party of 10 exceeds max capacity of 6
        assertThrows(IllegalStateException.class, () ->
                restaurant.createScheduledReservation("Big Party", 10, time)
        );
    }

    @Test
    @DisplayName("Should find available time slots within a given range")
    void testFindAvailableTimeSlots() {
        LocalDateTime start = LocalDateTime.now().plusDays(1).truncatedTo(ChronoUnit.HOURS);
        LocalDateTime end = start.plusHours(3);

        LocalDateTime[] slots = restaurant.findAvailableTimeSlots(start, end, 2);
        assertEquals(4, slots.length); // start, +1, +2, +3
    }

    @Test
    @DisplayName("Should seat walk-in party at currently available table")
    void testWalkInReservation() {
        Reservation walkIn = restaurant.createWalkInReservation("Jane Walkin", 2);
        assertNotNull(walkIn);
        assertEquals("Jane Walkin", walkIn.getPartyName());
        assertEquals(2, walkIn.getPartySize());
        assertNotNull(walkIn.getAssignedTable());
    }

    @Test
    @DisplayName("Should remove reservation and make table available again")
    void testRemoveReservation() {
        LocalDateTime time = LocalDateTime.now().plusDays(2).truncatedTo(ChronoUnit.HOURS);
        Reservation res = restaurant.createScheduledReservation("Cancel Me", 2, time);
        Table table = res.getAssignedTable();

        assertFalse(table.isAvailableAt(time));
        restaurant.removeReservation("Cancel Me", 2, time);
        assertTrue(table.isAvailableAt(time));
    }

    @Test
    @DisplayName("Should process order commands and correctly calculate bill")
    void testOrderLifecycleAndBilling() {
        Table table = layout.getTableById(1);

        restaurant.orderItem(table, burger);
        restaurant.orderItem(table, fries);

        // Check order status is SENT_TO_KITCHEN
        List<OrderItem> burgers = table.getOrderedItems().get(burger);
        assertNotNull(burgers);
        assertEquals(1, burgers.size());
        assertEquals(Status.SENT_TO_KITCHEN, burgers.get(0).getStatus());

        // Deliver burger
        restaurant.deliverItem(table, burger);
        assertEquals(Status.DELIVERED, burgers.get(0).getStatus());

        // Calculate bill: 12.50 + 4.00 = 16.50
        BigDecimal bill = restaurant.calculateTableBill(table);
        assertEquals(new BigDecimal("16.50"), bill);

        // Cancel fries
        restaurant.cancelItem(table, fries);
        assertNull(table.getOrderedItems().get(fries));

        // Bill after cancellation: 12.50
        BigDecimal billAfterCancel = restaurant.calculateTableBill(table);
        assertEquals(new BigDecimal("12.50"), billAfterCancel);
    }

    @Test
    @DisplayName("Command Pattern: OrderItem state transitions and guards")
    void testOrderCommandGuards() {
        OrderItem item = new OrderItem(burger);
        assertEquals(Status.PENDING, item.getStatus());

        // Send to kitchen
        new SendToKitchenCommand(item).execute();
        assertEquals(Status.SENT_TO_KITCHEN, item.getStatus());

        // Deliver
        new DeliverCommand(item).execute();
        assertEquals(Status.DELIVERED, item.getStatus());

        // Attempt to cancel after delivered - must remain DELIVERED
        new CancelCommand(item).execute();
        assertEquals(Status.DELIVERED, item.getStatus());
    }

    @Test
    @DisplayName("OrderManager atomic queue draining")
    void testOrderManagerAtomicDrain() {
        OrderManager manager = new OrderManager();
        OrderItem item1 = new OrderItem(burger);
        OrderItem item2 = new OrderItem(fries);

        manager.addCommand(new SendToKitchenCommand(item1));
        manager.addCommand(new SendToKitchenCommand(item2));

        manager.executeCommands();
        assertEquals(Status.SENT_TO_KITCHEN, item1.getStatus());
        assertEquals(Status.SENT_TO_KITCHEN, item2.getStatus());
        assertTrue(manager.isQueueEmpty());
    }

    @Test
    @DisplayName("Section 9.1: Concurrency - Zero double-bookings under high contention")
    void testConcurrentReservationsNoDoubleBooking() throws InterruptedException {
        // Layout with a single table of capacity 4
        Layout singleTableLayout = new Layout(List.of(4));
        Restaurant singleTableRestaurant = new Restaurant("Single Table Bistro", menu, singleTableLayout);

        int numThreads = 16;
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(numThreads);

        AtomicInteger successes = new AtomicInteger(0);
        AtomicInteger failures = new AtomicInteger(0);
        LocalDateTime time = LocalDateTime.now().plusDays(3).truncatedTo(ChronoUnit.HOURS);

        for (int i = 0; i < numThreads; i++) {
            final int id = i;
            executor.submit(() -> {
                try {
                    startLatch.await();
                    singleTableRestaurant.createScheduledReservation("Racer-" + id, 4, time);
                    successes.incrementAndGet();
                } catch (IllegalStateException e) {
                    failures.incrementAndGet();
                } catch (Exception e) {
                    fail("Unexpected exception: " + e.getMessage());
                } finally {
                    doneLatch.countDown();
                }
            });
        }

        startLatch.countDown();
        doneLatch.await();
        executor.shutdown();

        // Exactly one reservation must have won the slot
        assertEquals(1, successes.get(), "Only one thread must succeed in booking the single table");
        assertEquals(numThreads - 1, failures.get(), "All other concurrent racers must be safely rejected");
    }

    @Test
    @DisplayName("Section 9.3: Concurrency - Concurrent orders on same table calculate bill accurately")
    void testConcurrentOrdersBilling() throws InterruptedException {
        Table table = layout.getTableById(2); // Capacity 4
        int orderCount = 50;
        ExecutorService executor = Executors.newFixedThreadPool(8);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(orderCount);

        for (int i = 0; i < orderCount; i++) {
            executor.submit(() -> {
                try {
                    startLatch.await();
                    table.addOrder(fries); // $4.00 each
                } catch (Exception e) {
                    fail("Exception during concurrent add: " + e.getMessage());
                } finally {
                    doneLatch.countDown();
                }
            });
        }

        startLatch.countDown();
        doneLatch.await();
        executor.shutdown();

        BigDecimal expectedTotal = new BigDecimal("4.00").multiply(BigDecimal.valueOf(orderCount));
        assertEquals(expectedTotal, table.calculateBillAmount());
        assertEquals(orderCount, table.getOrderedItems().get(fries).size());
    }
}
