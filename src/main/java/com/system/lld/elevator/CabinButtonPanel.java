package com.system.lld.elevator;

import java.util.Set;

/**
 * CabinButtonPanel.java
 *
 * Represents the floor-selection panel INSIDE a specific elevator cab.
 *
 * This was missing from V1 and V2. The hallway panel handles "call an
 * elevator to my floor". This panel handles "take me to floor X" once
 * the passenger is already inside the elevator.
 *
 * Key difference from HallwayButtonPanel:
 *  - No dispatching needed  → we already know which car the user is in.
 *  - No strategy needed     → just forward directly to ElevatorCar.
 *  - No direction needed    → ElevatorCar.addFloorRequest() figures out
 *                             UP/DOWN from current floor vs requested floor.
 *
 * Validation handled here:
 *  1. Requested floor must be a valid floor number (>= 0).
 *  2. Requested floor must be in the car's accessibleFloors (if restricted).
 *  3. Cannot request the floor the car is already on.
 */
public class CabinButtonPanel {

    /** The elevator car this panel belongs to. */
    private final ElevatorCar car;

    // ------------------------------------------------------------------ //
    //  Constructor
    // ------------------------------------------------------------------ //

    /**
     * @param car the specific ElevatorCar this panel is physically inside
     */
    public CabinButtonPanel(ElevatorCar car) {
        this.car = car;
    }

    // ------------------------------------------------------------------ //
    //  Core method — called when passenger presses a floor button
    // ------------------------------------------------------------------ //

    /**
     * Called when a passenger inside the elevator presses a floor button.
     *
     * This is the missing method you identified. It:
     *  1. Validates the requested floor.
     *  2. Directly tells the car to add that floor as a stop.
     *
     * No dispatch, no strategy, no observer — the car is already known.
     *
     * @param requestedFloor the floor the passenger wants to go to
     */
    public void pressFloorButton(int requestedFloor) {

        int currentFloor = car.getStatus().getCurrentFloor();

        // Validation 1: cannot request current floor
        if (requestedFloor == currentFloor) {
            System.out.println("[CABIN] Car " + car.getId()
                    + " is already on floor " + requestedFloor + ". Ignoring.");
            return;
        }

        // Validation 2: floor must be non-negative
        if (requestedFloor < 0) {
            System.out.println("[CABIN] Invalid floor: " + requestedFloor);
            return;
        }

        // Validation 3: check accessibleFloors restriction
        Set<Integer> accessible = car.getAccessibleFloors();
        if (!accessible.isEmpty() && !accessible.contains(requestedFloor)) {
            System.out.println("[CABIN] Car " + car.getId()
                    + " cannot access floor " + requestedFloor
                    + ". Accessible floors: " + accessible);
            return;
        }

        // All checks passed — add the stop directly to the car
        System.out.println("[CABIN] Passenger in Car " + car.getId()
                + " pressed floor " + requestedFloor
                + " (currently on floor " + currentFloor + ")");

        car.addFloorRequest(requestedFloor);
    }

    // ------------------------------------------------------------------ //
    //  Getter
    // ------------------------------------------------------------------ //

    public ElevatorCar getCar() {
        return car;
    }
}
