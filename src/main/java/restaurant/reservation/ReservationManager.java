package restaurant.reservation;

import restaurant.table.Layout;
import restaurant.table.Table;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages all restaurant reservations, table assignment delegation, time slot queries,
 * and reservation cancellations.
 *
 * Concurrency Strategies:
 * 1. Section 9.1: createReservation utilizes a find-and-atomic-reserve loop with Table.reserveIfAvailable.
 *    If multiple threads race to reserve tables of the same capacity for the same time slot,
 *    ConcurrentHashMap.putIfAbsent on the Table guarantees exactly one winner per table/slot.
 *    Losing threads seamlessly scan and reserve subsequent candidate tables.
 * 2. Section 9.2: The reservations set is backed by Collections.newSetFromMap(new ConcurrentHashMap<>())
 *    to handle concurrent additions and cancellations. In removeReservation, reservations.remove()
 *    acts as the single atomic source of truth for cancellation races — only the winning thread clears
 *    the reservation from the Table.
 */
public class ReservationManager {
    private final Layout layout;
    private final Set<Reservation> reservations = Collections.newSetFromMap(new ConcurrentHashMap<>());

    public ReservationManager(Layout layout) {
        this.layout = Objects.requireNonNull(layout, "layout must not be null");
    }

    /**
     * Finds available 1-hour time slots within [rangeStart, rangeEnd] for a given party size.
     *
     * @param rangeStart search window start
     * @param rangeEnd search window end
     * @param partySize number of guests
     * @return array of available LocalDateTime hour slots
     */
    public LocalDateTime[] findAvailableTimeSlots(LocalDateTime rangeStart, LocalDateTime rangeEnd, int partySize) {
        Objects.requireNonNull(rangeStart, "rangeStart must not be null");
        Objects.requireNonNull(rangeEnd, "rangeEnd must not be null");
        if (rangeEnd.isBefore(rangeStart)) {
            throw new IllegalArgumentException("rangeEnd must not be before rangeStart");
        }

        List<LocalDateTime> availableSlots = new ArrayList<>();
        LocalDateTime current = rangeStart.truncatedTo(ChronoUnit.HOURS);
        LocalDateTime endHour = rangeEnd.truncatedTo(ChronoUnit.HOURS);

        while (!current.isAfter(endHour)) {
            Table candidate = layout.findAvailableTable(partySize, current);
            if (candidate != null) {
                availableSlots.add(current);
            }
            current = current.plusHours(1);
        }

        return availableSlots.toArray(new LocalDateTime[0]);
    }

    /**
     * Creates a reservation for a party at the desired time (truncated to hour).
     *
     * Concurrency Fix (Section 9.1):
     * Retrieves candidates from layout and attempts atomic reserveIfAvailable on Table.
     * If another thread won the slot during the race window, this method scans the next
     * eligible candidate table until a booking is secured or candidates are exhausted.
     *
     * @param partyName name of the reserving party
     * @param partySize number of guests
     * @param desiredTime requested reservation time
     * @return the confirmed Reservation
     * @throws IllegalStateException if no suitable table is available at that time
     */
    public Reservation createReservation(String partyName, int partySize, LocalDateTime desiredTime) {
        Objects.requireNonNull(partyName, "partyName must not be null");
        Objects.requireNonNull(desiredTime, "desiredTime must not be null");
        if (partySize <= 0) {
            throw new IllegalArgumentException("partySize must be greater than 0");
        }

        LocalDateTime slotTime = desiredTime.truncatedTo(ChronoUnit.HOURS);
        List<Table> candidates = layout.findCandidateTables(partySize, slotTime);

        for (Table candidateTable : candidates) {
            // Create reservation instance pointing to candidate table
            Reservation reservation = new Reservation(partyName, partySize, slotTime, candidateTable);
            // Atomic check-and-act on Table (Section 9.1)
            if (candidateTable.reserveIfAvailable(slotTime, reservation)) {
                reservations.add(reservation);
                return reservation;
            }
            // Another thread grabbed candidateTable for this slot; loop to next candidate
        }

        throw new IllegalStateException("No available table found for party size " + partySize + " at " + slotTime);
    }

    /**
     * Concurrency Fix (Section 9.2):
     * Removes an existing reservation. Uses reservations.remove() as the atomic gatekeeper:
     * only if this thread successfully removes the reservation from the set does it proceed
     * to clear the reservation from the Table.
     *
     * @param partyName name of reserving party
     * @param partySize size of party
     * @param reservationTime reservation time
     * @return true if successfully found and removed, false otherwise
     */
    public boolean removeReservation(String partyName, int partySize, LocalDateTime reservationTime) {
        Objects.requireNonNull(partyName, "partyName must not be null");
        Objects.requireNonNull(reservationTime, "reservationTime must not be null");

        LocalDateTime slotTime = reservationTime.truncatedTo(ChronoUnit.HOURS);

        Reservation target = reservations.stream()
                .filter(r -> r.getPartyName().equalsIgnoreCase(partyName) &&
                             r.getPartySize() == partySize &&
                             r.getTime().equals(slotTime))
                .findFirst()
                .orElse(null);

        if (target != null && reservations.remove(target)) {
            // Won the race to cancel — clear it from the physical table
            target.getAssignedTable().removeReservation(slotTime);
            return true;
        }

        return false;
    }

    /**
     * Look up a reservation by party name (useful for customer arrival lookup).
     */
    public Reservation getReservationByPartyName(String partyName) {
        Objects.requireNonNull(partyName, "partyName must not be null");
        return reservations.stream()
                .filter(r -> r.getPartyName().equalsIgnoreCase(partyName))
                .findFirst()
                .orElse(null);
    }

    public Set<Reservation> getReservations() {
        return Collections.unmodifiableSet(reservations);
    }

    public Layout getLayout() {
        return layout;
    }
}
