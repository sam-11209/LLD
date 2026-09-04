package restaurant.table;

import restaurant.menu.MenuItem;
import restaurant.order.OrderItem;
import restaurant.reservation.Reservation;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Represents one physical table in the restaurant: fixed tableId and capacity,
 * along with its active reservations and ordered items.
 *
 * Concurrency Strategies:
 * 1. Section 9.1: reserveIfAvailable utilizes ConcurrentHashMap.putIfAbsent to eliminate
 *    the check-then-act race between checking table availability and adding the booking.
 * 2. Section 9.3: Both reservations and orderedItems are backed by ConcurrentHashMap.
 *    Order additions and removals are serialized per MenuItem key using atomic compute
 *    and computeIfPresent blocks, backed by CopyOnWriteArrayList. This prevents race
 *    conditions where two threads might create duplicate lists or leave orphaned empty lists.
 */
public class Table {
    private final int tableId;
    private final int capacity;

    // Keyed by truncated hour LocalDateTime -> Reservation (Section 9.1, 9.3)
    private final ConcurrentMap<LocalDateTime, Reservation> reservations = new ConcurrentHashMap<>();

    // Keyed by MenuItem -> Thread-safe List of OrderItem instances (Section 9.3)
    private final ConcurrentMap<MenuItem, List<OrderItem>> orderedItems = new ConcurrentHashMap<>();

    public Table(int tableId, int capacity) {
        if (tableId <= 0) {
            throw new IllegalArgumentException("tableId must be positive: " + tableId);
        }
        if (capacity <= 0) {
            throw new IllegalArgumentException("capacity must be positive: " + capacity);
        }
        this.tableId = tableId;
        this.capacity = capacity;
    }

    public int getTableId() {
        return tableId;
    }

    public int getCapacity() {
        return capacity;
    }

    /**
     * Checks if the table is free at the given reservation time.
     * Note: This is a point-in-time hint. Use reserveIfAvailable() for atomic reservation.
     */
    public boolean isAvailableAt(LocalDateTime reservationTime) {
        Objects.requireNonNull(reservationTime, "reservationTime must not be null");
        return !reservations.containsKey(reservationTime);
    }

    /**
     * Concurrency Fix (Section 9.1):
     * Atomically reserves the table at the given time if no conflicting reservation exists.
     * ConcurrentHashMap.putIfAbsent returns null if and only if the key was absent and the entry
     * was inserted.
     *
     * @param time reservation start time
     * @param reservation the reservation to assign
     * @return true if successfully reserved, false if already occupied
     */
    public boolean reserveIfAvailable(LocalDateTime time, Reservation reservation) {
        Objects.requireNonNull(time, "time must not be null");
        Objects.requireNonNull(reservation, "reservation must not be null");
        return reservations.putIfAbsent(time, reservation) == null;
    }

    /**
     * Unconditional reservation insertion.
     */
    public void addReservation(Reservation reservation) {
        Objects.requireNonNull(reservation, "reservation must not be null");
        reservations.put(reservation.getTime(), reservation);
    }

    /**
     * Removes the reservation for the given time slot.
     *
     * @return true if an existing reservation was removed, false otherwise
     */
    public boolean removeReservation(LocalDateTime reservationTime) {
        Objects.requireNonNull(reservationTime, "reservationTime must not be null");
        return reservations.remove(reservationTime) != null;
    }

    /**
     * Concurrency Fix (Section 9.3):
     * Adds an order of MenuItem to the table using atomic compute with CopyOnWriteArrayList.
     *
     * @param item the MenuItem ordered
     * @return the created OrderItem
     */
    public OrderItem addOrder(MenuItem item) {
        Objects.requireNonNull(item, "MenuItem must not be null");
        OrderItem orderItem = new OrderItem(item);
        orderedItems.compute(item, (k, list) -> {
            if (list == null) {
                list = new CopyOnWriteArrayList<>();
            }
            list.add(orderItem);
            return list;
        });
        return orderItem;
    }

    /**
     * Adds multiple quantities of a MenuItem to the table.
     */
    public void addOrder(MenuItem item, int quantity) {
        Objects.requireNonNull(item, "MenuItem must not be null");
        if (quantity <= 0) return;
        orderedItems.compute(item, (k, list) -> {
            if (list == null) {
                list = new CopyOnWriteArrayList<>();
            }
            for (int i = 0; i < quantity; i++) {
                list.add(new OrderItem(item));
            }
            return list;
        });
    }

    /**
     * Concurrency Fix (Section 9.3):
     * Removes one OrderItem of the given MenuItem.
     * Uses computeIfPresent so that removing the first item and pruning the map entry
     * if the list becomes empty is atomic with respect to concurrent addOrder operations.
     *
     * @param item the MenuItem to remove
     * @return the removed OrderItem, or null if none existed
     */
    public OrderItem removeOrder(MenuItem item) {
        Objects.requireNonNull(item, "MenuItem must not be null");
        final OrderItem[] removed = new OrderItem[1];
        orderedItems.computeIfPresent(item, (k, list) -> {
            if (!list.isEmpty()) {
                removed[0] = list.remove(0);
            }
            return list.isEmpty() ? null : list;
        });
        return removed[0];
    }

    /**
     * Calculates the total bill for all ordered items currently on the table.
     * Uses BigDecimal arithmetic to avoid floating-point inaccuracies.
     */
    public BigDecimal calculateBillAmount() {
        return orderedItems.values().stream()
                .flatMap(Collection::stream)
                .map(oi -> oi.getItem().getPrice())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public Map<LocalDateTime, Reservation> getReservations() {
        return Collections.unmodifiableMap(reservations);
    }

    public Map<MenuItem, List<OrderItem>> getOrderedItems() {
        return Collections.unmodifiableMap(orderedItems);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Table table)) return false;
        return tableId == table.tableId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(tableId);
    }

    @Override
    public String toString() {
        return "Table{" +
                "tableId=" + tableId +
                ", capacity=" + capacity +
                '}';
    }
}
