package com.system.lld.elevator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ElevatorDispatchCtrl.java
 *
 * Concrete implementation of ElevatorObserver.
 *
 * This is the "bridge" class that connects the Observer pattern
 * (HallwayButtonPanel) to the Strategy pattern (ElevatorDispatch).
 *
 * Responsibilities:
 *  • Receive button-press events from HallwayButtonPanel via update().
 *  • Forward those events to ElevatorDispatch, which selects and assigns
 *    the best elevator car using the configured DispatchingStrategy.
 *
 * Flow:
 *  Passenger presses button
 *    → HallwayButtonPanel.pressButton(dir)
 *      → ElevatorDispatchCtrl.update(floor, dir)      ← this class
 *        → ElevatorDispatch.dispatchElevatorCar(...)
 *          → DispatchingStrategy.selectElevator(...)
 *            → ElevatorCar.addFloorRequest(floor)
 *
 * Why a separate controller and not let HallwayButtonPanel call
 * ElevatorDispatch directly?
 *  Single Responsibility — the button panel should only know how to
 *  broadcast events, not which dispatch system handles them.
 */
public class ElevatorDispatchCtrl implements ElevatorObserver {

    /** The dispatch layer that will assign elevator cars. */
    private final ElevatorDispatch dispatch;

    /** All elevator cars in the building, passed through for dispatching. */
    private final List<ElevatorCar> elevators;
 

    // ------------------------------------------------------------------ //
    //  Constructor
    // ------------------------------------------------------------------ //

    /**
     * @param dispatch  the ElevatorDispatch instance to delegate to
     * @param elevators the full list of elevator cars in the system
     */
    public ElevatorDispatchCtrl(ElevatorDispatch dispatch,
                                List<ElevatorCar> elevators) {
        this.dispatch  = dispatch;
        this.elevators = elevators;
    }

    // ------------------------------------------------------------------ //
    //  Observer callback
    // ------------------------------------------------------------------ //

    /**
	 * Called automatically by HallwayButtonPanel whenever a button is pressed.
	 *
	 * Delegates directly to ElevatorDispatch to pick and assign a car.
	 *
	 * @param floor the floor where the button was pressed
	 * @param dir   the direction the passenger wants to go
	 */
	@Override
	public void update(int floor, Direction dir) {
		System.out.println("ElevatorDispatchCtrl received event: floor=" + floor + ", dir=" + dir);
		dispatch.dispatchElevatorCar(elevators, floor, dir);

	}
}
