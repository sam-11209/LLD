package com.system.lld.elevator;



import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Main.java
 *
 * Demonstrates floor-by-floor movement with a mid-route cabin request.
 *
 * Scenario:
 *  1. Car starts at floor 0.
 *  2. Hallway request → floor 10 UP.
 *  3. While car is moving, passenger inside presses floor 5 (cabin button).
 *  4. Simulator moves floor by floor:
 *       0→1→2→3→4→5 (STOP — serves cabin request)→6→7→8→9→10 (STOP)
 */
public class MainWithElevatorSimulator {

    public static void main(String[] args) {

        // Setup
        List<ElevatorCar> elevators = new ArrayList<>();
        ElevatorCar car1 = new ElevatorCar(1, 0, new HashSet<>());
        elevators.add(car1);

        ElevatorDispatch  dispatch  = new ElevatorDispatch(new ShortestSeekTimeFirst());
        ElevatorSimulator simulator = new ElevatorSimulator(dispatch, elevators);
        ElevatorSystem    system    = new ElevatorSystem(dispatch, elevators, simulator);

        System.out.println("=== Floor-by-Floor Simulation Demo ===\n");

        // Step 1: Hallway request — go to floor 10
        System.out.println("--- Hallway: floor 10 UP ---");
        system.requestElevator(10, Direction.UP);
        // upStops = {10}

        // Step 2: Passenger boards and presses floor 5 inside the cabin
        System.out.println("\n--- Cabin: passenger presses floor 5 inside Car 1 ---");
        system.selectFloor(5, car1);
        // upStops = {5, 10}

        System.out.println("\nupStops now: " + car1.getUpStops());

        // Step 3: Run the simulator — moves floor by floor
        // Watch it stop at 5 first, then continue to 10
        System.out.println("\n--- Simulator running Car 1 floor by floor ---");
        simulator.runUntilIdle(car1);

        System.out.println("Final state: " + car1);
    }
}