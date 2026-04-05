package com.system.lld.elevator;

import java.util.List;

/**
 * ElevatorSimulator.java  (Approach 2 — floor by floor movement)
 *
 * How it works:
 *  Instead of jumping directly to the destination, the car moves
 *  ONE floor at a time in its current direction.
 *
 *  At EVERY floor it passes, it checks:
 *    "Is this floor in my upStops or downStops TreeSet?"
 *      → YES → call arriveAt()  (serve the stop, remove from TreeSet)
 *      → NO  → just update currentFloor and keep moving
 *
 * This correctly handles mid-route requests:
 *   Car at floor 0 heading to floor 10.
 *   Someone inside presses floor 5.
 *   Floor 5 gets added to upStops.
 *   Car passes floor 5 → TreeSet check hits → stops at 5 → continues to 10.
 *
 * Visual:
 *   upStops = {5, 10}
 *   0 → 1 → 2 → 3 → 4 → 5 (stop!) → 6 → 7 → 8 → 9 → 10 (stop!)
 *                            ↑                              ↑
 *                       arriveAt(5)                   arriveAt(10)
 */
public class ElevatorSimulator {

    private final ElevatorDispatch  dispatch;
    private final List<ElevatorCar> elevators;

    public ElevatorSimulator(ElevatorDispatch dispatch,
                             List<ElevatorCar> elevators) {
        this.dispatch  = dispatch;
        this.elevators = elevators;
    }

    // ------------------------------------------------------------------ //
    //  Core — move one floor at a time until next scheduled stop
    // ------------------------------------------------------------------ //

    /**
     * Moves the car ONE floor at a time in its current direction.
     * At each floor, checks if it is a scheduled stop.
     *
     * Stops (and returns) as soon as it serves one stop from the TreeSet.
     * Call this in a loop to drain all stops.
     *
     * @param car the elevator car to advance
     * @return    the floor that was served, or -1 if car is already idle
     */
    public int step(ElevatorCar car) {

        // No stops pending — nothing to do
        if (car.getNextStop() == -1) {
            System.out.println("[SIM] Car " + car.getId() + " is IDLE.");
            return -1;
        }

        Direction dir    = car.getStatus().getCurrentDirection();
        int currentFloor = car.getStatus().getCurrentFloor();

        System.out.println("[SIM] Car " + car.getId()
                + " starting from floor " + currentFloor
                + " direction " + dir);

        // Move one floor at a time
        while (true) {

            // Advance by one floor in current direction
            if (dir == Direction.UP) {
                currentFloor++;
            } else if (dir == Direction.DOWN) {
                currentFloor--;
            }

            System.out.println("[SIM] Car " + car.getId()
                    + " passing floor " + currentFloor + "...");

            // ── KEY CHECK ──────────────────────────────────────────────
            // Is this floor a scheduled stop in the TreeSet?
            // This is the "sensor" — fires at every floor the car passes
            if (shouldStopAt(car, currentFloor)) {

                System.out.println("[SIM] Car " + car.getId()
                        + " STOPPING at floor " + currentFloor);

                // Floor is served — removes from TreeSet,
                // updates currentFloor, triggers retryPending()
                car.arriveAt(currentFloor, dispatch, elevators);
                return currentFloor;
            }

            // Not a stop — just update position and keep moving
            car.getStatus().setCurrentFloor(currentFloor);

            // Safety guard against runaway loop
            if (currentFloor < 0 || currentFloor > 200) {
                System.out.println("[SIM] ERROR: Car " + car.getId() + " out of bounds!");
                break;
            }
        }

        return -1;
    }

    // ------------------------------------------------------------------ //
    //  Convenience — drain all stops for one car
    // ------------------------------------------------------------------ //

    /**
     * Keeps calling step() until the car becomes IDLE.
     * Serves ALL pending stops in the correct SCAN order.
     */
    public void runUntilIdle(ElevatorCar car) {
        System.out.println("\n[SIM] Running Car " + car.getId() + " until idle...");
        while (car.getNextStop() != -1) {
            step(car);
        }
        System.out.println("[SIM] Car " + car.getId() + " has served all stops.\n");
    }

    // ------------------------------------------------------------------ //
    //  Run all cars
    // ------------------------------------------------------------------ //

    public void runAll() {
        for (ElevatorCar car : elevators) {
            runUntilIdle(car);
        }
        System.out.println("[SIM] All cars idle.");
    }

    // ------------------------------------------------------------------ //
    //  Private helper — the "floor sensor"
    // ------------------------------------------------------------------ //

    /**
     * Checks both TreeSets — this is the equivalent of the physical
     * floor-level sensor in a real elevator.
     *
     * Checks BOTH upStops and downStops because a cabin button press
     * can add a stop in either set regardless of current direction.
     * e.g. car going UP, passenger inside presses floor 2 (below) →
     *      floor 2 is in downStops → still needs to be caught here.
     */
    private boolean shouldStopAt(ElevatorCar car, int floor) {
        return car.getUpStops().contains(floor)
                || car.getDownStops().contains(floor);
    }
}
