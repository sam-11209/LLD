package com.system.lld.elevator;
/**
 * ElevatorObserver.java
 *
 * <<interface>> — part of the Observer Pattern
 *
 * Any class that wants to react to a hallway button press implements
 * this interface.
 *
 * In the class diagram the only concrete observer is ElevatorDispatchCtrl,
 * but the interface makes it easy to add more (e.g., a display panel,
 * a logging service, an alert system) without changing HallwayButtonPanel.
 *
 * Design pattern: OBSERVER (also called Publish-Subscribe)
 *  Subject  → HallwayButtonPanel
 *  Observer → ElevatorObserver (this interface)
 */
public interface ElevatorObserver {

    /**
     * Called by the subject (HallwayButtonPanel) whenever a button is pressed.
     *
     * @param floor the floor on which the button was pressed
     * @param dir   the direction requested (UP or DOWN)
     */
    void update(int floor, Direction dir);
}
