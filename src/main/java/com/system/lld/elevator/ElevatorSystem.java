package com.system.lld.elevator;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * ElevatorSystem.java
 *
 * Top-level façade / entry point for the entire elevator system.
 *
 * Responsibilities: 1. Own and initialise all ElevatorCar objects. 2. Own the
 * ElevatorDispatch (which holds the strategy). 3. Expose requestElevator(floor,
 * dir) — the single public API that external callers (e.g., a building
 * management system, a REST controller, or Main) use to request a lift.
 *
 * Design pattern: FAÇADE Callers don't need to know about ElevatorDispatch,
 * strategies, or TreeSets. They just call requestElevator() and the system
 * handles everything internally.
 *
 * Wiring overview: ElevatorSystem ├── List<ElevatorCar> (the physical cabs) └──
 * ElevatorDispatch (holds DispatchingStrategy)
 *
 * The Observer chain (HallwayButtonPanel → ElevatorDispatchCtrl) is set up
 * inside this class too, so all wiring is in one place.
 */
public class ElevatorSystem {

	/** All elevator cars managed by this system. */
	private List<ElevatorCar> elevators;

	/** The dispatcher (holds the pluggable strategy). */
	private ElevatorDispatch dispatch;
	
	//This is added to know that we have reached at a particular destination now we have to call arrive at method
	//For now we are calling it manually but in new MainElevatorSimulator class we are using this
	private  ElevatorSimulator  simulator;

	// ------------------------------------------------------------------ //
	// Constructor
	// ------------------------------------------------------------------ //

	/**
	 * Initialises the system with a given number of elevator cars and a chosen
	 * dispatching strategy.
	 *
	 * @param numCars  how many elevator cars to create
	 * @param strategy the algorithm to use for car selection
	 */
	public ElevatorSystem(int numCars, DispatchingStrategy strategy) {
		this.elevators = new ArrayList<>();
		// Create cars — all start at floor 0, no floor restriction
		for (int i = 1; i <= numCars; i++) {
			elevators.add(new ElevatorCar(i, 0, new HashSet<>()));
		}
		this.dispatch = new ElevatorDispatch(strategy, elevators);
	}

	public ElevatorSystem(ElevatorDispatch dispatch, List<ElevatorCar> elevators, ElevatorSimulator simulator) {
		this.dispatch = dispatch;
		this.elevators = elevators;
		this.simulator = simulator;
		if (this.dispatch != null) {
			this.dispatch.setElevators(elevators);
		}
	}


	// ------------------------------------------------------------------ //
	// Public API
	// ------------------------------------------------------------------ //

	/**
	 * Called when a passenger presses the UP or DOWN button on a floor.
	 *
	 * This is the primary entry point. Internally it delegates to ElevatorDispatch
	 * which uses the configured DispatchingStrategy to assign the best car.
	 *
	 * @param floor the floor where the button was pressed
	 * @param dir   UP or DOWN
	 */
	public void requestElevator(int floor, Direction dir) {
		System.out.println("\n=== New request: floor=" + floor + " dir=" + dir + " ===");
		dispatch.dispatchElevatorCar(elevators, floor, dir);
	}
	
	 /**
     * Called when a passenger already inside an elevator presses a
     * floor button on the cabin panel.
     *
     * Internally creates a CabinButtonPanel for the given car and
     * delegates to it — so all validation (accessible floors, current
     * floor check, negative floor check) lives in CabinButtonPanel,
     * not here.
     *
     * @param floor    the floor the passenger wants to go to
     * @param elevator the specific ElevatorCar the passenger is inside
     */
    public void selectFloor(int floor, ElevatorCar elevator) {
        System.out.println("\n=== Cabin request: floor=" + floor
                + " in Car " + elevator.getId() + " ===");
        CabinButtonPanel cabinPanel = new CabinButtonPanel(elevator);
        cabinPanel.pressFloorButton(floor);
    }
    

	/**
	 * Creates a HallwayButtonPanel for a specific floor and wires it to the
	 * dispatch system via an ElevatorDispatchCtrl observer.
	 *
	 * @param floor the floor to create the panel for
	 * @return the wired HallwayButtonPanel ready for button presses
	 */
	public HallwayButtonPanel createButtonPanel(int floor) {
		HallwayButtonPanel panel = new HallwayButtonPanel(floor);
		ElevatorDispatchCtrl ctrl = new ElevatorDispatchCtrl(dispatch, elevators);
		panel.addObserver(ctrl);
		return panel;
	}

	/**
	 * Prints the current state of all elevator cars (useful for debugging).
	 */
	public void printStatus() {
		System.out.println("\n--- System Status ---");
		for (ElevatorCar car : elevators) {
			System.out.println(car);
		}
		System.out.println("---------------------");
	}

	// ------------------------------------------------------------------ //
	// Getters
	// ------------------------------------------------------------------ //

	public List<ElevatorCar> getElevators() {
		return elevators;
	}

	public ElevatorDispatch getDispatch() {
		return dispatch;
	}

	// NEW: anyone who needs to know "which car got assigned to floor X" asks here
	public ElevatorCar getAssignedElevator(int floor) {
		return dispatch.getAssignedElevator(floor);
	}
}
