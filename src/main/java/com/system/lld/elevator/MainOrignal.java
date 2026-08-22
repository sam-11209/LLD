package com.system.lld.elevator;

public class MainOrignal {

//    	// ------------------------------------------------------------------ //
//    	// main — quick demo / smoke test
//    	// ------------------------------------------------------------------ //
//    	/**
//    	 * Quick demo showing the full system working end-to-end.
//    	 *
//    	 * Scenario: • 2 elevator cars, starting at floor 0. • Strategy:
//    	 * ShortestSeekTimeFirst. • Several hall calls arrive; we check which car gets
//    	 * assigned.
//    	 */
	public static void main(String[] args) {

		ElevatorSystem system = new ElevatorSystem(2, new ShortestSeekTimeFirst());
		system.printStatus();

		system.requestElevator(5, Direction.UP);
		system.requestElevator(3, Direction.DOWN);
		system.requestElevator(8, Direction.UP);
		system.printStatus();

		System.out.println("\n=== Observer pattern demo ===");
		HallwayButtonPanel panel2 = system.createButtonPanel(2);
		HallwayButtonPanel panel7 = system.createButtonPanel(7);

		panel2.pressButton(Direction.UP);
		panel7.pressButton(Direction.DOWN);
		system.printStatus();

		// ✅ Fixed: pass dispatch and elevators so retryPending() can fire
		ElevatorCar car1 = system.getElevators().get(0);
		System.out.println("\n--- Simulating Car 1 travel ---");
		int next = car1.getNextStop();
		System.out.println("Car 1 next stop: " + next);
		car1.arriveAt(next, system.getDispatch(), system.getElevators()); // ← fixed
		System.out.println("Car 1 after arrival: " + car1);
	}
}
