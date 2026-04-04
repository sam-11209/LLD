package com.system.lld.elevator;
/**
 * ElevatorStatus.java
 *
 * A simple data/state holder class that tracks the real-time
 * position and direction of an ElevatorCar.
 *
 * Responsibilities:
 *  - Store the current floor the elevator is on.
 *  - Store the current direction (UP / DOWN / IDLE).
 *
 * This is kept as a separate class (not fields directly on ElevatorCar)
 * so that status can be read/shared without exposing internal scheduling
 * details of ElevatorCar.
 */
public class ElevatorStatus {

    /** The floor the elevator is currently at. */
    private int currentFloor;

    /** The direction the elevator is currently moving (or IDLE). */
    private Direction currentDirection;

    // ------------------------------------------------------------------ //
    //  Constructor
    // ------------------------------------------------------------------ //

    /**
     * Creates a new ElevatorStatus starting at floor 0 and IDLE.
     */
    public ElevatorStatus() {
        this.currentFloor     = 0;
        this.currentDirection = Direction.IDLE;
    }

    /**
     * Creates a new ElevatorStatus with specific initial values.
     *
     * @param currentFloor     starting floor number
     * @param currentDirection starting direction
     */
    public ElevatorStatus(int currentFloor, Direction currentDirection) {
        this.currentFloor     = currentFloor;
        this.currentDirection = currentDirection;
    }

    // ------------------------------------------------------------------ //
    //  Getters & Setters
    // ------------------------------------------------------------------ //

    public int getCurrentFloor() {
        return currentFloor;
    }

    public void setCurrentFloor(int currentFloor) {
        this.currentFloor = currentFloor;
    }

    public Direction getCurrentDirection() {
        return currentDirection;
    }

    public void setCurrentDirection(Direction currentDirection) {
        this.currentDirection = currentDirection;
    }

    @Override
    public String toString() {
        return "ElevatorStatus{floor=" + currentFloor
                + ", direction=" + currentDirection + "}";
    }
}
