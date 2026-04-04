package com.system.lld.elevator;
import java.util.ArrayList;
import java.util.List;

/**
 * HallwayButtonPanel.java
 *
 * <<subject>> — part of the Observer Pattern (new addition in the diagram)
 *
 * Represents the physical UP/DOWN button panel mounted outside
 * an elevator on every floor.
 *
 * Responsibilities:
 *  1. Know which floor it belongs to.
 *  2. Maintain a list of observers (ElevatorObserver).
 *  3. When pressButton() is called (a passenger presses UP or DOWN),
 *     notify ALL registered observers with (floor, direction).
 *
 * Design pattern: OBSERVER
 *  This class is the "Subject" (publisher).  It fires an event and
 *  every observer reacts independently — HallwayButtonPanel does NOT
 *  need to know what the observers do, keeping coupling low.
 *
 * Why Observer here instead of a direct call to ElevatorDispatch?
 *  • Multiple subsystems can react to a button press (dispatch, display,
 *    logging) without HallwayButtonPanel knowing about any of them.
 *  • Observers can be added/removed at runtime (e.g., maintenance mode).
 */
public class HallwayButtonPanel {

    /** The floor this panel is physically located on. */
    private final int floor;

    /** All registered observers that will be notified on button press. */
    private List<ElevatorObserver> observers;

    // ------------------------------------------------------------------ //
    //  Constructor
    // ------------------------------------------------------------------ //

    /**
     * @param floor the floor number this panel belongs to
     */
    public HallwayButtonPanel(int floor) {
        this.floor     = floor;
        this.observers = new ArrayList<>();
    }

    // ------------------------------------------------------------------ //
    //  Observer management
    // ------------------------------------------------------------------ //

    /**
     * Registers a new observer to receive button-press events.
     *
     * @param observer the observer to add
     */
    public void addObserver(ElevatorObserver observer) {
        observers.add(observer);
    }

    /**
     * Removes a previously registered observer.
     *
     * @param observer the observer to remove
     */
    public void removeObserver(ElevatorObserver observer) {
        observers.remove(observer);
    }

    // ------------------------------------------------------------------ //
    //  Core method
    // ------------------------------------------------------------------ //

    /**
     * Simulates a passenger pressing the UP or DOWN button on this panel.
     *
     * Notifies every registered observer with the floor and direction.
     *
     * @param dir Direction.UP or Direction.DOWN (never IDLE from a button)
     */
    public void pressButton(Direction dir) {
        System.out.println("Button pressed on floor " + floor + " [" + dir + "]");
        notifyObservers(dir);
    }

    /**
     * Internal helper — calls update() on every observer.
     */
    private void notifyObservers(Direction dir) {
        for (ElevatorObserver observer : observers) {
            observer.update(floor, dir);
        }
    }

    // ------------------------------------------------------------------ //
    //  Getter
    // ------------------------------------------------------------------ //

    public int getFloor() {
        return floor;
    }
}
