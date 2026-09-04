package restaurant.reservation;

import restaurant.table.Table;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Immutable record of one booking: party name, party size, reservation time, and assigned Table.
 *
 * Thread Safety:
 * Fully immutable — all fields are final and there are no mutating methods,
 * making Reservation instances inherently thread-safe to publish across threads.
 */
public final class Reservation {
    private final String partyName;
    private final int partySize;
    private final LocalDateTime time;
    private final Table assignedTable;

    public Reservation(String partyName, int partySize, LocalDateTime time, Table assignedTable) {
        this.partyName = Objects.requireNonNull(partyName, "partyName must not be null");
        if (partySize <= 0) {
            throw new IllegalArgumentException("partySize must be greater than 0: " + partySize);
        }
        this.partySize = partySize;
        this.time = Objects.requireNonNull(time, "time must not be null");
        this.assignedTable = Objects.requireNonNull(assignedTable, "assignedTable must not be null");
    }

    public String getPartyName() {
        return partyName;
    }

    public int getPartySize() {
        return partySize;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public Table getAssignedTable() {
        return assignedTable;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Reservation that)) return false;
        return partySize == that.partySize &&
                Objects.equals(partyName, that.partyName) &&
                Objects.equals(time, that.time) &&
                Objects.equals(assignedTable.getTableId(), that.assignedTable.getTableId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(partyName, partySize, time, assignedTable.getTableId());
    }

    @Override
    public String toString() {
        return "Reservation{" +
                "partyName='" + partyName + '\'' +
                ", partySize=" + partySize +
                ", time=" + time +
                ", assignedTable=" + assignedTable.getTableId() +
                '}';
    }
}
