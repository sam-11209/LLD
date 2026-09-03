package com.system.lld.elevator;

import java.util.HashSet;
import java.util.Set;

/**
 * ElevatorFactory.java
 *
 * Factory Pattern implementation for creating ElevatorCar instances.
 *
 * Responsibilities:
 *  1. Centralize and encapsulate the instantiation logic of ElevatorCar.
 *  2. Provide pre-configured elevator setups (STANDARD, EXPRESS, SERVICE).
 *  3. Support default floor configurations, start floors, and accessible floor sets.
 *  4. Decouple callers (like ElevatorSystem or simulation drivers) from
 *     direct ElevatorCar constructors.
 *
 * Design Pattern: FACTORY PATTERN
 *  - Supports static factory methods for convenient instantiation.
 *  - Supports instance methods for dependency injection.
 */
public class ElevatorFactory {

    /**
     * Creates a standard elevator starting at floor 0 with no floor restrictions.
     *
     * @param id the unique elevator ID
     * @return a new ElevatorCar configured for standard use
     */
    public static ElevatorCar createElevator(int id) {
        return createElevator(id, 0, new HashSet<>());
    }

    /**
     * Creates an elevator with custom starting floor and accessible floors.
     *
     * @param id               the unique elevator ID
     * @param startFloor       initial floor where the elevator starts
     * @param accessibleFloors set of floors this car can service
     * @return a new ElevatorCar configured with custom settings
     */
    public static ElevatorCar createElevator(int id, int startFloor, Set<Integer> accessibleFloors) {
        return new ElevatorCar(id, startFloor, accessibleFloors != null ? accessibleFloors : new HashSet<>());
    }

    /**
     * Creates an elevator based on ElevatorType.
     *
     * @param type             the type of elevator (STANDARD, EXPRESS, SERVICE)
     * @param id               the unique elevator ID
     * @param startFloor       initial floor
     * @param accessibleFloors set of floors (for EXPRESS or SERVICE)
     * @return configured ElevatorCar
     */
    public static ElevatorCar createElevator(ElevatorType type, int id, int startFloor, Set<Integer> accessibleFloors) {
        switch (type) {
            case EXPRESS:
            case SERVICE:
                return new ElevatorCar(id, startFloor, accessibleFloors != null ? accessibleFloors : new HashSet<>());
            case STANDARD:
            default:
                return new ElevatorCar(id, startFloor, new HashSet<>());
        }
    }

    // ------------------------------------------------------------------ //
    //  Instance methods for Dependency Injection
    // ------------------------------------------------------------------ //

    public ElevatorCar create(int id) {
        return createElevator(id);
    }

    public ElevatorCar create(int id, int startFloor, Set<Integer> accessibleFloors) {
        return createElevator(id, startFloor, accessibleFloors);
    }

    public ElevatorCar create(ElevatorType type, int id, int startFloor, Set<Integer> accessibleFloors) {
        return createElevator(type, id, startFloor, accessibleFloors);
    }
}
