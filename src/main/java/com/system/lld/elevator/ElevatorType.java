package com.system.lld.elevator;

/**
 * ElevatorType.java
 *
 * Defines the types of elevator cars that can be produced by ElevatorFactory:
 *  - STANDARD: Standard passenger elevator serving all floors.
 *  - EXPRESS: High-speed car serving specific key floors (e.g., lobby, sky lobbies, penthouses).
 *  - SERVICE: Freight / maintenance car with specific access restrictions.
 */
public enum ElevatorType {
    STANDARD,
    EXPRESS,
    SERVICE
}
