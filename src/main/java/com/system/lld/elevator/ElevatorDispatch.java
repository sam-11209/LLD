package com.system.lld.elevator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
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
    private List<ElevatorCar> elevators;

    /**
     * Holds requests that could not be assigned to any elevator yet.
     * Retried every time an elevator becomes free (arriveAt callback).
     */
    private final Queue<FloorRequest> pendingRequests = new LinkedList<>();
    
    
    // NEW: remembers the last assignment made for a given hall call
    private Map<Integer, ElevatorCar> assignedCarByFloor = new HashMap<>();

    // ------------------------------------------------------------------ //
    //  Constructors & Setters
    // ------------------------------------------------------------------ //

    public ElevatorDispatch(DispatchingStrategy strategy) {
        this.strategy = strategy;
    }

    public ElevatorDispatch(DispatchingStrategy strategy, List<ElevatorCar> elevators) {
        this.strategy = strategy;
        this.elevators = elevators;
    }

    public void setElevators(List<ElevatorCar> elevators) {
        this.elevators = elevators;
    }

    public List<ElevatorCar> getElevators() {
        return elevators;
    }

    // ------------------------------------------------------------------ //
    //  Core dispatch — with queue fallback
    // ------------------------------------------------------------------ //

	/**
	 * Convenience method using internal elevators list.
	 */
	public ElevatorCar dispatchElevatorCar(int floor, Direction dir) {
		return dispatchElevatorCar(this.elevators, floor, dir);
	}

	/**
	 * Attempts to dispatch an elevator for (floor, dir).
	 *
	 * If the strategy cannot find a suitable car right now, the request is saved in
	 * pendingRequests and will be retried later.
	 *
	 * @param elevators all elevator cars
	 * @param floor     requested floor
	 * @param dir       requested direction
	 */
	public ElevatorCar dispatchElevatorCar(List<ElevatorCar> elevators, int floor, Direction dir) {
		if (elevators != null) {
			this.elevators = elevators;
		}

		ElevatorCar elevatorCar = tryAssign(elevators, floor, dir);

		if (elevatorCar == null) {
			FloorRequest pending = new FloorRequest(floor, dir);
			pendingRequests.add(pending);
			System.out.println("[QUEUED]  No car available now. " + pending + " added to pending queue. "
					+ "Queue size: " + pendingRequests.size());
		}
		assignedCarByFloor.put(floor, elevatorCar);  
		return elevatorCar;
	}

	
	// NEW: anyone who needs to know "which car got assigned to floor X" asks here
	public ElevatorCar getAssignedElevator(int floor) {
		return assignedCarByFloor.get(floor);
	}
	
    // ------------------------------------------------------------------ //
    //  Retry — called when any elevator finishes a stop
    // ------------------------------------------------------------------ //

    /**
     * Retries pending requests using the internally registered elevators list.
     */
    public void retryPending() {
        retryPending(this.elevators);
    }

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
        if (elevators != null) {
            this.elevators = elevators;
        }
        if (pendingRequests.isEmpty()) return;

        System.out.println("[RETRY]   Attempting to assign "
                + pendingRequests.size() + " pending request(s)...");

        // Iterate a snapshot so we can safely remove while iterating
        int size = pendingRequests.size();

        for (int i = 0; i < size; i++) {
            FloorRequest req = pendingRequests.poll(); // remove from head

            ElevatorCar elevatorCar = tryAssign(elevators, req.getFloor(), req.getDir());

            if (elevatorCar == null) {
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
     * @return ElevatorCar if a car was successfully assigned, null otherwise
     */
    private ElevatorCar tryAssign(List<ElevatorCar> elevators,
                               int floor,
                               Direction dir) {

        ElevatorCar chosenElevatorCar = strategy.selectElevator(elevators, floor, dir);

        if (chosenElevatorCar != null) {
            System.out.println("[ASSIGNED] Car " + chosenElevatorCar.getId()
                    + " → floor " + floor + " [" + dir + "]");
            chosenElevatorCar.addFloorRequest(floor); // I called this method inside selectElevator method because we have multiple
											// elevators their and if we can not add floor in one elevator we can add
											// floor in other elevator instead of adding them in pending request
			return chosenElevatorCar;
        }
        return null;
    }

    // ------------------------------------------------------------------ //
    //  Getters
    // ------------------------------------------------------------------ //

    public DispatchingStrategy       getStrategy()        { return strategy; }
    public void                      setStrategy(DispatchingStrategy s) { this.strategy = s; }
    public Queue<FloorRequest>       getPendingRequests() { return pendingRequests; }
    public int                       pendingCount()       { return pendingRequests.size(); }
}
