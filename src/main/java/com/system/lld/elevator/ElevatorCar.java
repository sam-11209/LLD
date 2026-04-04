package com.system.lld.elevator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * ElevatorCar.java  (V2 — calls retryPending when a stop is served)
 *
 * What changed from V1?
 * ─────────────────────
 * arriveAt() now accepts the full elevators list + dispatch reference
 * so it can call dispatch.retryPending() after clearing a stop.
 *
 * This is the "trigger" that drains the pending queue:
 *   car arrives at floor → stop removed → car may now be free
 *   → retryPending() fires → pending requests get assigned
 */
public class ElevatorCar {

    private final int id;
    private ElevatorStatus      status;
    private TreeSet<Integer>    upStops;
    private TreeSet<Integer>    downStops;
    private Set<Integer>        accessibleFloors;

    public ElevatorCar(int id, int startFloor, Set<Integer> accessibleFloors) {
        this.id               = id;
        this.status           = new ElevatorStatus(startFloor, Direction.IDLE);
        this.upStops          = new TreeSet<>();
        this.downStops        = new TreeSet<>();
        this.accessibleFloors = accessibleFloors;
    }

    // ------------------------------------------------------------------ //

    public void addFloorRequest(int floor) {
        if (!accessibleFloors.isEmpty() && !accessibleFloors.contains(floor)) {
            System.out.println("Car " + id + " cannot access floor " + floor);
            return;
        }

        int current = status.getCurrentFloor();

        if (floor > current)       upStops.add(floor);
        else if (floor < current)  downStops.add(floor);

        if (status.getCurrentDirection() == Direction.IDLE) {
            if      (!upStops.isEmpty())   status.setCurrentDirection(Direction.UP);
            else if (!downStops.isEmpty()) status.setCurrentDirection(Direction.DOWN);
        }
    }

    // ------------------------------------------------------------------ //

    public int getNextStop() {
        int current = status.getCurrentFloor();

        switch (status.getCurrentDirection()) {
            case UP:
                Integer nextUp = upStops.ceiling(current);
                if (nextUp != null) return nextUp;
                if (!downStops.isEmpty()) {
                    status.setCurrentDirection(Direction.DOWN);
                    return downStops.last();
                }
                status.setCurrentDirection(Direction.IDLE);
                return -1;

            case DOWN:
                Integer nextDown = downStops.floor(current);
                if (nextDown != null) return nextDown;
                if (!upStops.isEmpty()) {
                    status.setCurrentDirection(Direction.UP);
                    return upStops.first();
                }
                status.setCurrentDirection(Direction.IDLE);
                return -1;

            default:
                return -1;
        }
    }

    // ------------------------------------------------------------------ //

    /**
     * V2 — arriveAt now accepts dispatch + elevators so it can
     * trigger retryPending() after each stop.
     *
     * Why trigger here?
     *  This is the exact moment a car's load potentially decreases
     *  (it just served a stop, may now be IDLE). Any pending request
     *  that was blocked because all cars were busy should be retried now.
     *
     * @param floor     the floor just reached
     * @param dispatch  the ElevatorDispatch holding the pending queue
     * @param elevators all cars (needed for re-evaluation in retryPending)
     */
    public void arriveAt(int floor,
                         ElevatorDispatch dispatch,
                         List<ElevatorCar> elevators) {

        upStops.remove(floor);
        downStops.remove(floor);
        status.setCurrentFloor(floor);

        System.out.println("[ARRIVE]  Car " + id + " arrived at floor " + floor);

        // Recalculate direction
        if (upStops.isEmpty() && downStops.isEmpty()) {
            status.setCurrentDirection(Direction.IDLE);
            System.out.println("[IDLE]    Car " + id + " is now idle.");
        }

        // KEY: now that this car may be free, retry any pending requests
        dispatch.retryPending(elevators);
    }

    // ------------------------------------------------------------------ //

    public int              getId()               { return id; }
    public ElevatorStatus   getStatus()           { return status; }
    public TreeSet<Integer> getUpStops()          { return upStops; }
    public TreeSet<Integer> getDownStops()        { return downStops; }
    public Set<Integer>     getAccessibleFloors() { return accessibleFloors; }

    @Override
    public String toString() {
        return "ElevatorCar{id=" + id + ", " + status
                + ", up=" + upStops + ", down=" + downStops + "}";
    }
}
