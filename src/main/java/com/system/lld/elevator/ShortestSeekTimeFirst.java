package com.system.lld.elevator;
import java.util.List;

/**
 * ShortestSeekTimeFirst.java
 *
 * Concrete implementation of DispatchingStrategy.
 *
 * Algorithm:
 *  Picks the elevator with the MINIMUM estimated "seek time" to reach
 *  the requested floor. Seek time = |currentFloor − requestedFloor|.
 *
 *  This mirrors the SSTF disk scheduling algorithm applied to elevators.
 *
 * New addition from class diagram (+accessibleFloors check):
 *  Before considering any elevator, it checks whether the requested
 *  floor is in that car's accessibleFloors set. Cars that cannot reach
 *  the floor are skipped entirely.
 *
 * Tradeoffs:
 *  ✓ Minimises average wait time.
 *  ✓ Respects per-car floor access restrictions.
 *  ✗ Slightly more complex than FCFS.
 *  ✗ Can cause starvation of far-away floors under heavy load
 *    (same issue as SSTF in disk scheduling).
 */
public class ShortestSeekTimeFirst implements DispatchingStrategy {

    /**
     * Selects the nearest eligible elevator.
     *
     * Eligibility rules:
     *  1. accessibleFloors is empty (no restriction) OR contains the
     *     requested floor.
     *  2. Among eligible cars, choose the one with smallest distance.
     *     Tie-break: prefer a car already moving in the same direction.
     */
    @Override
    public ElevatorCar selectElevator(List<ElevatorCar> elevators,
                                      int floor,
                                      Direction dir) {

        ElevatorCar best     = null;
        int         bestCost = Integer.MAX_VALUE;

        for (ElevatorCar car : elevators) {

            // --- accessibleFloors check (new field in modified ElevatorCar) ---
            if (!car.getAccessibleFloors().isEmpty()
                    && !car.getAccessibleFloors().contains(floor)) {
                continue; // this car cannot reach the requested floor
            }

            int distance = Math.abs(car.getStatus().getCurrentFloor() - floor);

            // Prefer a car already heading the same way (lower effective cost)
            if (car.getStatus().getCurrentDirection() == dir) {
                distance -= 1; // small bonus to same-direction cars
            }

            if (distance < bestCost) {
                bestCost = distance;
                best     = car;
            }
        }

        return best; // null if no eligible car found
    }
}
