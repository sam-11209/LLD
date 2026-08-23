package com.system.lld.elevator;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Main.java — Demo for ElevatorSystem V2 (pending queue + retry)
 *
 * Scenario:
 *  1. Only 1 elevator car exists and it is busy with a UP trip.
 *  2. A second request arrives while the car is occupied.
 *     → Strategy returns null → request is QUEUED (not lost).
 *  3. The car finishes its stop → retryPending() fires automatically
 *     → the queued request is now assigned successfully.
 *
 * This proves no request is ever dropped, even under full load.
 */
public class Main {

    public static void main(String[] args) {

        // ── Setup ────────────────────────────────────────────────────────
        // 1 car starting at floor 0, no floor restrictions
        List<ElevatorCar> elevators = new ArrayList<>();
        elevators.add(new ElevatorCar(1, 0, new HashSet<>()));

        ElevatorDispatch dispatch = new ElevatorDispatch(new ShortestSeekTimeFirst());

        System.out.println("=== Elevator System V2 — Pending Queue Demo ===\n");

        // ── Step 1: First request — car is free, assigned immediately ────
        System.out.println("--- Step 1: Request floor 10 UP ---");
        dispatch.dispatchElevatorCar(elevators, 10, Direction.UP);
        printStatus(elevators, dispatch);

        // ── Step 2: Second request arrives while car is still going up ───
        // ShortestSeekTimeFirst will NOT reassign a car already committed
        // to floor 10 for a completely different new request at floor 3 DOWN.
        // To demonstrate a "no car available" scenario we create a
        // restricted-access car set (empty list) temporarily.
        System.out.println("\n--- Step 2: Request floor 3 DOWN (all cars busy) ---");

        // Simulate "all cars busy" by using an empty list for this call:
        List<ElevatorCar> noCars = new ArrayList<>(); // empty → strategy returns null
        dispatch.dispatchElevatorCar(noCars, 3, Direction.DOWN);
        // ↑ pendingRequests now has 1 item

        System.out.println("Pending queue size: " + dispatch.pendingCount());

        // ── Step 3: Car 1 finishes its stop at floor 10 ──────────────────
        // arriveAt() calls retryPending() with the REAL elevators list,
        // so the queued request now gets assigned.
        System.out.println("\n--- Step 3: Car 1 arrives at floor 10 ---");
        ElevatorCar car1 = elevators.get(0);
        dispatch.setElevators(elevators);
        car1.arriveAt(10, dispatch); // ← triggers retry internally
        // ↑ retryPending fires → floor 3 DOWN is now assigned to car 1

        System.out.println("\nPending queue size after retry: " + dispatch.pendingCount());
        printStatus(elevators, dispatch);

        // ── Step 4: Verify car 1 has floor 3 in its downStops ────────────
        System.out.println("\n--- Step 4: Car 1 next stop ---");
        System.out.println("Next stop for Car 1: " + car1.getNextStop());
    }

    private static void printStatus(List<ElevatorCar> elevators,
                                    ElevatorDispatch dispatch) {
        System.out.println("\n[STATUS]");
        for (ElevatorCar car : elevators) System.out.println("  " + car);
        System.out.println("  Pending queue: " + dispatch.getPendingRequests());
    }
}
