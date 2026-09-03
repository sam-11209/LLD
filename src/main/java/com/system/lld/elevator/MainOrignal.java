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
		ElevatorCar assignedElevator2 = system.getAssignedElevator(2);
		system.selectFloor(6, assignedElevator2);

		panel7.pressButton(Direction.DOWN);
		ElevatorCar assignedElevator7 = system.getAssignedElevator(7);
		system.selectFloor(1, assignedElevator7);
		system.printStatus();

		System.out.println("\n--- Cabin: passenger presses floor 5 inside Car 1 ---");
		// upStops = {5, 10}

		// ✅ Fixed: pass dispatch and elevators so retryPending() can fire
		ElevatorCar car1 = system.getElevators().get(0);
		System.out.println("\n--- Simulating Car 1 travel ---");
		int next = car1.getNextStop();
		System.out.println("Car 1 next stop: " + next);
		car1.arriveAt(next, system.getDispatch());
		System.out.println("Car 1 after arrival: " + car1);

		System.out.println("\n=== Factory Pattern demo ===");
		// Factory creating standard elevator car
		ElevatorCar standardCar = ElevatorFactory.createElevator(3);
		System.out.println("Factory created standard car: " + standardCar);

		// Factory creating specialized express elevator car with restricted stops
		java.util.Set<Integer> expressFloors = java.util.Set.of(0, 10, 20);
		ElevatorCar expressCar = ElevatorFactory.createElevator(ElevatorType.EXPRESS, 4, 0, expressFloors);
		System.out.println("Factory created express car: " + expressCar);
	}
}
