package com.system.lld.elevator;
import java.util.List;

/**
 * DispatchingStrategy.java
 *
 * <<interface>>
 *
 * Defines the contract for any elevator selection algorithm.
 * This is the Strategy Pattern — the concrete algorithm
 * (FirstComeFirstServe, ShortestSeekTimeFirst, etc.) is swapped in
 * at runtime without changing ElevatorDispatch.
 *
 * Benefits:
 *  • Open/Closed Principle: add new strategies without touching existing code.
 *  • Easy to unit-test each algorithm in isolation.
 *  • ElevatorDispatch stays simple — it just delegates here.
 */
public interface DispatchingStrategy {

    /**
     * Choose the best elevator car for a new hallway request.
     *
     * @param elevators list of all available elevator cars
     * @param floor     the floor where the button was pressed
     * @param dir       the direction the waiting passenger wants to travel
     * @return          the chosen ElevatorCar, or null if none is suitable
     */
    ElevatorCar selectElevator(List<ElevatorCar> elevators, int floor, Direction dir);
}
