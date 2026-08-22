package com.system.lld.elevator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * ElevatorDispatch.java  (V2 — with pending queue + retry)
 *
 * What changed from V1?
 * ─────────────────────
 * V1 problem:  if no elevator is available, the request is silently dropped.
 *
 * V2 solution: a pendingRequests Queue.
 *
 *   1. Try to dispatch immediately.
 *   2. If no car is available → park the request in pendingRequests.
 *   3. Whenever a car finishes a stop (arriveAt is called), it calls
 *      retryPending() — which re-attempts every waiting request.
 *
 * Queue choice — LinkedList as Queue:
 *   - FIFO order: oldest requests are retried first (fair).
 *   - O(1) add and remove from head/tail.
 *   - Could be swapped for a PriorityQueue if you want to retry by
 *     proximity or wait-time instead of arrival order.
 */
public class ElevatorDispatch {

    private DispatchingStrategy strategy;

    /**
     * Holds requests that could not be assigned to any elevator yet.
     * Retried every time an elevator becomes free (arriveAt callback).
     */
    private final Queue<FloorRequest> pendingRequests = new LinkedList<>();

    // ------------------------------------------------------------------ //
    //  Constructor
    // ------------------------------------------------------------------ //

    public ElevatorDispatch(DispatchingStrategy strategy) {
        this.strategy = strategy;
    }

    // ------------------------------------------------------------------ //
    //  Core dispatch — with queue fallback
    // ------------------------------------------------------------------ //

    /**
     * Attempts to dispatch an elevator for (floor, dir).
     *
     * If the strategy cannot find a suitable car right now, the request
     * is saved in pendingRequests and will be retried later.
     *
     * @param elevators all elevator cars
     * @param floor     requested floor
     * @param dir       requested direction
     */
    public void dispatchElevatorCar(List<ElevatorCar> elevators,
                                    int floor,
                                    Direction dir) {

        boolean assigned = tryAssign(elevators, floor, dir);

        if (!assigned) {
            FloorRequest pending = new FloorRequest(floor, dir);
            pendingRequests.add(pending);
            System.out.println("[QUEUED]  No car available now. "
                    + pending + " added to pending queue. "
                    + "Queue size: " + pendingRequests.size());
        }
    }

    // ------------------------------------------------------------------ //
    //  Retry — called when any elevator finishes a stop
    // ------------------------------------------------------------------ //

    /**
     * Re-attempts every pending request in FIFO order.
     *
     * Called by ElevatorCar.arriveAt() via a callback so that as soon
     * as a car becomes free it immediately picks up waiting requests.
     *
     * Design note:
     *   We iterate over a copy of the queue. For each pending request,
     *   if it can now be assigned, it is removed from the queue.
     *   Requests that still can't be assigned stay queued.
     *
     * @param elevators all elevator cars (one of them just became free)
     */
    public void retryPending(List<ElevatorCar> elevators) {
        if (pendingRequests.isEmpty()) return;

        System.out.println("[RETRY]   Attempting to assign "
                + pendingRequests.size() + " pending request(s)...");

        // Iterate a snapshot so we can safely remove while iterating
        int size = pendingRequests.size();

        for (int i = 0; i < size; i++) {
            FloorRequest req = pendingRequests.poll(); // remove from head

            boolean assigned = tryAssign(elevators, req.getFloor(), req.getDir());

            if (!assigned) {
                // Still no car — put it back at the tail (stays pending)
                pendingRequests.add(req);
                System.out.println("[RETRY]   Still no car for " + req
                        + ". Kept in queue.");
            }
        }
    }

    // ------------------------------------------------------------------ //
    //  Private helper
    // ------------------------------------------------------------------ //

    /**
     * Asks the strategy to pick a car and, if found, assigns the request.
     *
     * @return true if a car was successfully assigned, false otherwise
     */
    private boolean tryAssign(List<ElevatorCar> elevators,
                               int floor,
                               Direction dir) {

        ElevatorCar chosen = strategy.selectElevator(elevators, floor, dir);

        if (chosen != null) {
            System.out.println("[ASSIGNED] Car " + chosen.getId()
                    + " → floor " + floor + " [" + dir + "]");
			chosen.addFloorRequest(floor); // I called this method inside selectElevator method because we have multiple
											// elevators their and if we can not add floor in one elevator we can add
											// floor in other elevator instead of adding them in pending request
			return true;
        }
        return false;
    }

    // ------------------------------------------------------------------ //
    //  Getters
    // ------------------------------------------------------------------ //

    public DispatchingStrategy       getStrategy()        { return strategy; }
    public void                      setStrategy(DispatchingStrategy s) { this.strategy = s; }
    public Queue<FloorRequest>       getPendingRequests() { return pendingRequests; }
    public int                       pendingCount()       { return pendingRequests.size(); }
}
