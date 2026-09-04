package restaurant.table;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Owns and organizes all physical tables in the restaurant.
 * Indexed by table ID (tablesById) for direct lookup and by capacity (tablesByCapacity, sorted ascending)
 * to quickly find the smallest table that can accommodate a party.
 *
 * Concurrency Strategy (Section 9.7):
 * tablesById and tablesByCapacity are populated once during Layout initialization and are never
 * structurally mutated afterward. In the Java Memory Model, all final fields set during construction
 * are safely published to other threads once constructor execution completes. Thus, standard
 * HashMap and TreeMap are sufficient without synchronization or locking overhead for reads.
 * Note that findAvailableTable is an advisory lookup (hint) — actual reservation atomicity is
 * enforced by Table.reserveIfAvailable (Section 9.1).
 */
public class Layout {
    private final Map<Integer, Table> tablesById;
    private final SortedMap<Integer, Set<Table>> tablesByCapacity;

    /**
     * Constructs a Layout from a list of table capacities.
     * Generates tableIds sequentially starting from 1.
     *
     * @param tableCapacities list of capacities for each table
     */
    public Layout(List<Integer> tableCapacities) {
        Objects.requireNonNull(tableCapacities, "tableCapacities must not be null");
        Map<Integer, Table> byId = new HashMap<>();
        SortedMap<Integer, Set<Table>> byCap = new TreeMap<>();

        int id = 1;
        for (Integer cap : tableCapacities) {
            Table table = new Table(id++, cap);
            byId.put(table.getTableId(), table);
            byCap.computeIfAbsent(cap, k -> new LinkedHashSet<>()).add(table);
        }

        this.tablesById = Collections.unmodifiableMap(byId);
        this.tablesByCapacity = Collections.unmodifiableSortedMap(byCap);
    }

    /**
     * Overloaded constructor accepting an existing collection of Table objects.
     */
    public Layout(Collection<Table> tables) {
        Objects.requireNonNull(tables, "tables must not be null");
        Map<Integer, Table> byId = new HashMap<>();
        SortedMap<Integer, Set<Table>> byCap = new TreeMap<>();

        for (Table table : tables) {
            byId.put(table.getTableId(), table);
            byCap.computeIfAbsent(table.getCapacity(), k -> new LinkedHashSet<>()).add(table);
        }

        this.tablesById = Collections.unmodifiableMap(byId);
        this.tablesByCapacity = Collections.unmodifiableSortedMap(byCap);
    }

    /**
     * Finds the smallest available table that fits the given party size at the reservation time.
     *
     * @param partySize minimum capacity required
     * @param reservationTime requested time
     * @return an available Table, or null if none is available
     */
    public Table findAvailableTable(int partySize, LocalDateTime reservationTime) {
        for (Table table : findCandidateTables(partySize, reservationTime)) {
            return table;
        }
        return null;
    }

    /**
     * Returns an ordered list of candidate tables fitting the party size and available at the given time,
     * ordered by capacity (smallest fit first).
     * Used by ReservationManager to retry on concurrent race collisions (Section 9.1).
     */
    public List<Table> findCandidateTables(int partySize, LocalDateTime reservationTime) {
        Objects.requireNonNull(reservationTime, "reservationTime must not be null");
        List<Table> candidates = new ArrayList<>();

        // Tail map returns all entries with key >= partySize in ascending order
        SortedMap<Integer, Set<Table>> eligibleMap = tablesByCapacity.tailMap(partySize);
        for (Set<Table> tables : eligibleMap.values()) {
            for (Table table : tables) {
                if (table.isAvailableAt(reservationTime)) {
                    candidates.add(table);
                }
            }
        }
        return candidates;
    }

    public Table getTableById(int tableId) {
        return tablesById.get(tableId);
    }

    public Map<Integer, Table> getTablesById() {
        return tablesById;
    }

    public SortedMap<Integer, Set<Table>> getTablesByCapacity() {
        return tablesByCapacity;
    }
}
