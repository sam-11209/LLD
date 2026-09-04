package restaurant.core;

import restaurant.menu.Menu;
import restaurant.menu.MenuItem;
import restaurant.order.CancelCommand;
import restaurant.order.DeliverCommand;
import restaurant.order.OrderItem;
import restaurant.order.OrderManager;
import restaurant.order.SendToKitchenCommand;
import restaurant.order.Status;
import restaurant.reservation.Reservation;
import restaurant.reservation.ReservationManager;
import restaurant.table.Layout;
import restaurant.table.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/**
 * =========================================================================================
 * DESIGN PATTERNS & CONCURRENCY STRATEGY SUMMARY
 * =========================================================================================
 *
 * 1. DESIGN PATTERNS USED:
 *    a. FACADE PATTERN:
 *       The Restaurant class serves as a unified, cohesive facade over the underlying
 *       subsystems (Menu, Layout, ReservationManager, and OrderManager). It holds NO internal
 *       data structures (maps/sets/lists) of its own, delegating all domain operations to their
 *       respective experts. This keeps the client-facing API simple, modular, and decoupled.
 *
 *    b. COMMAND PATTERN:
 *       Order-lifecycle state mutations (send to kitchen, deliver to customer, cancel item)
 *       are encapsulated as first-class command objects (SendToKitchenCommand, DeliverCommand,
 *       CancelCommand implementing OrderCommand). OrderManager serves as the invoker, queuing
 *       and executing commands, while OrderItem acts as the receiver. This decouples who
 *       requests the action from how and when it is executed.
 *
 * 2. CONCURRENCY & THREAD-SAFETY STRATEGY (Non-Functional Requirements / Section 9):
 *    a. Fine-Grained Table Reservation Atomicity (Section 9.1):
 *       Table.reserveIfAvailable uses ConcurrentHashMap.putIfAbsent(time, reservation) to
 *       eliminate the classic check-then-act race without global locks. If multiple threads
 *       race to book the same slot/capacity, ReservationManager scans candidate tables.
 *    b. Thread-Safe Reservations Set & Idempotent Cancellation (Section 9.2):
 *       ReservationManager uses Collections.newSetFromMap(new ConcurrentHashMap<>()) to allow
 *       concurrent bookings and cancellations. Cancellation races are gated on Set.remove(),
 *       ensuring only the winning thread cleans up the physical table.
 *    c. Atomic Table Orders (Section 9.3):
 *       Table.orderedItems uses ConcurrentHashMap keyed by MenuItem. Order additions and
 *       removals are serialized per MenuItem via atomic compute and computeIfPresent blocks
 *       backed by CopyOnWriteArrayList, preventing lost updates and orphaned map keys.
 *    d. Dynamic Menu Concurrency (Section 9.4):
 *       Menu is backed by ConcurrentHashMap, allowing concurrent additions and reads.
 *    e. Immediate State Visibility (Section 9.5):
 *       OrderItem.status is declared volatile with synchronized state transition guards
 *       to guarantee immediate cross-thread visibility across servers and kitchen threads.
 *    f. Atomic Order Drain & Invocation (Section 9.6):
 *       OrderManager uses a ConcurrentLinkedQueue and poll()-based loop. Commands are never
 *       lost or double-executed even when multiple threads submit commands concurrently.
 *    g. Safe Publication of Layout (Section 9.7):
 *       Layout structures are constructed once and safely published via final fields.
 * =========================================================================================
 */
public class Restaurant {
    private final String name;
    private final Menu menu;
    private final Layout layout;
    private final ReservationManager reservationManager;
    private final OrderManager orderManager;

    /**
     * Constructs a new Restaurant facade with its required collaborators.
     * ReservationManager and OrderManager are instantiated internally to preserve encapsulation.
     */
    public Restaurant(String name, Menu menu, Layout layout) {
        this.name = Objects.requireNonNull(name, "name must not be null");
        this.menu = Objects.requireNonNull(menu, "menu must not be null");
        this.layout = Objects.requireNonNull(layout, "layout must not be null");
        this.reservationManager = new ReservationManager(layout);
        this.orderManager = new OrderManager();
    }

    /**
     * Alternate constructor allowing dependency-injected managers (useful for testing).
     */
    public Restaurant(String name, Menu menu, Layout layout, ReservationManager reservationManager, OrderManager orderManager) {
        this.name = Objects.requireNonNull(name, "name must not be null");
        this.menu = Objects.requireNonNull(menu, "menu must not be null");
        this.layout = Objects.requireNonNull(layout, "layout must not be null");
        this.reservationManager = Objects.requireNonNull(reservationManager, "reservationManager must not be null");
        this.orderManager = Objects.requireNonNull(orderManager, "orderManager must not be null");
    }

    // =========================================================================
    // RESERVATION FACADE METHODS
    // =========================================================================

    public LocalDateTime[] findAvailableTimeSlots(LocalDateTime rangeStart, LocalDateTime rangeEnd, int partySize) {
        return reservationManager.findAvailableTimeSlots(rangeStart, rangeEnd, partySize);
    }

    public Reservation createScheduledReservation(String partyName, int partySize, LocalDateTime time) {
        return reservationManager.createReservation(partyName, partySize, time);
    }

    public Reservation createWalkInReservation(String partyName, int partySize) {
        return reservationManager.createReservation(partyName, partySize, LocalDateTime.now());
    }

    public void removeReservation(String partyName, int partySize, LocalDateTime reservationTime) {
        reservationManager.removeReservation(partyName, partySize, reservationTime);
    }

    // =========================================================================
    // ORDERING & KITCHEN FACADE METHODS (Command Pattern Integration)
    // =========================================================================

    /**
     * Places an order for a MenuItem on a Table, then encapsulates and executes
     * a SendToKitchenCommand via the OrderManager invoker.
     */
    public void orderItem(Table table, MenuItem item) {
        Objects.requireNonNull(table, "table must not be null");
        Objects.requireNonNull(item, "item must not be null");

        OrderItem orderItem = table.addOrder(item);
        orderManager.addCommand(new SendToKitchenCommand(orderItem));
        orderManager.executeCommands();
    }

    /**
     * Cancels an order for a MenuItem on a Table.
     * Encapsulates and executes a CancelCommand for the item via OrderManager,
     * then removes it from the table's active items.
     */
    public void cancelItem(Table table, MenuItem item) {
        Objects.requireNonNull(table, "table must not be null");
        Objects.requireNonNull(item, "item must not be null");

        OrderItem removedItem = table.removeOrder(item);
        if (removedItem != null) {
            orderManager.addCommand(new CancelCommand(removedItem));
            orderManager.executeCommands();
        }
    }

    /**
     * Transitions an ordered item on the table to DELIVERED status via DeliverCommand.
     */
    public void deliverItem(Table table, MenuItem item) {
        Objects.requireNonNull(table, "table must not be null");
        Objects.requireNonNull(item, "item must not be null");

        List<OrderItem> items = table.getOrderedItems().get(item);
        if (items != null) {
            for (OrderItem orderItem : items) {
                if (orderItem.getStatus() == Status.SENT_TO_KITCHEN) {
                    orderManager.addCommand(new DeliverCommand(orderItem));
                    orderManager.executeCommands();
                    break;
                }
            }
        }
    }

    // =========================================================================
    // BILLING FACADE METHOD
    // =========================================================================

    /**
     * Calculates the single total bill amount for the given table.
     */
    public BigDecimal calculateTableBill(Table table) {
        Objects.requireNonNull(table, "table must not be null");
        return table.calculateBillAmount();
    }

    // =========================================================================
    // GETTERS
    // =========================================================================

    public String getName() {
        return name;
    }

    public Menu getMenu() {
        return menu;
    }

    public Layout getLayout() {
        return layout;
    }

    public ReservationManager getReservationManager() {
        return reservationManager;
    }

    public OrderManager getOrderManager() {
        return orderManager;
    }
}
