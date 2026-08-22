package com.system.lld.elevator;
import java.util.List;

/**
 * FirstComeFirstServe.java
 *
 * Concrete implementation of DispatchingStrategy.
 *
 * Algorithm:
 *  Simply picks the FIRST elevator that is either IDLE or already
 *  moving in the same direction as the request.
 *
 *  This is the simplest possible strategy — O(n) scan, no cost
 *  function. Good for low-traffic buildings or as a fallback.
 *
 * Tradeoffs:
 *  ✓ Simple and predictable.
 *  ✗ Does not minimise travel distance (ShortestSeekTimeFirst does better).
 *  ✗ May assign a far-away elevator when a closer one is available.
 */
public class FirstComeFirstServe implements DispatchingStrategy {

    /**
	 * Selects the first elevator that is: 1. IDLE (best case — it's free) 2.
	 * Already moving in the same direction AND on the correct side (so it can pick
	 * up the passenger on the way).
	 *
	 * Falls back to the first IDLE car if no directional match is found.
	 */
	@Override
	public ElevatorCar selectElevator(List<ElevatorCar> elevators, int floor, Direction dir) {

		ElevatorCar idleFallback = null;

		for (ElevatorCar car : elevators) {
			ElevatorStatus s = car.getStatus();
			Direction carDir = s.getCurrentDirection();
			int carFloor = s.getCurrentFloor();

			// Priority 1: car moving same direction and can still pick up
			if (carDir == dir) {
				if (dir == Direction.UP && carFloor <= floor) {
					if (car.addFloorRequest(floor))
						return car;
				}
				if (dir == Direction.DOWN && carFloor >= floor) {
					if (car.addFloorRequest(floor))
						return car;
				}
			}

			// Priority 2: idle car as fallback
			if (carDir == Direction.IDLE && idleFallback == null) {
				if (car.addFloorRequest(floor))
					idleFallback = car;

			}
		}

		// Return idle fallback, or null if all cars are busy in wrong direction
		return idleFallback;
	}
}
