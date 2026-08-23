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
    /**
     * Adds a floor stop request to the correct TreeSet.
     *
     * Logic:
     *  1. Ignore the request if the floor is not accessible to this car.
     *  2. If the requested floor is ABOVE the current floor → upStops.
     *  3. If the requested floor is BELOW the current floor → downStops.
     *  4. If the elevator is IDLE, decide direction immediately.
     *
     * Key insight: we NEVER reject a valid floor request just because
     * the elevator is currently moving the other way. We park it in the
     * appropriate TreeSet and it will be served on the return trip.
     *
     * @param floor the floor number being requested
     */
    public boolean addFloorRequest(int floor) {
        if (!accessibleFloors.isEmpty() && !accessibleFloors.contains(floor)) {
            System.out.println("Car " + id + " cannot access floor " + floor);
            return false;
        }

        int current = status.getCurrentFloor();

        if (floor > current)       upStops.add(floor);
        else if (floor < current)  downStops.add(floor);

        if (status.getCurrentDirection() == Direction.IDLE) {
            if      (!upStops.isEmpty())   status.setCurrentDirection(Direction.UP);
            else if (!downStops.isEmpty()) status.setCurrentDirection(Direction.DOWN);
        }
        return true;
    }

    // ------------------------------------------------------------------ //
    /**
     * ==================================================================
     * ======================||  SCAN Algorithm  ||======================
     * ==================================================================
     * The elevator should continue in its current direction until all 
     * requests in that direction are served, then reverse and go to the 
     * farthest request in the opposite direction. This minimizes seek time.
     * 
     * Returns the next floor this elevator should stop at, based on
     * the SCAN algorithm:
     *  • Moving UP  → next floor in upStops that is >= currentFloor.
     *                 If none left, reverse: take highest from downStops.
     *  • Moving DOWN→ next floor in downStops that is <= currentFloor.
     *                 If none left, reverse: take lowest from upStops.
     *  • IDLE       → -1 (no pending stops).
     *
     * This method does NOT advance the elevator; it only reads ahead.
     * A real simulation would call this in a loop, move to that floor,
     * then call it again.
     *
     * @return next stop floor, or -1 if no pending requests
     */
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
     * Arrive at floor and notify dispatch to retry pending requests.
     *
     * @param floor    the floor just reached
     * @param dispatch the ElevatorDispatch holding the pending queue
     */
    public void arriveAt(int floor, ElevatorDispatch dispatch) {
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
        if (dispatch != null) {
            dispatch.retryPending();
        }
    }

    /**
     * Backward-compatible arriveAt method.
     */
    public void arriveAt(int floor,
                         ElevatorDispatch dispatch,
                         List<ElevatorCar> elevators) {
        if (dispatch != null) {
            dispatch.setElevators(elevators);
        }
        arriveAt(floor, dispatch);
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
