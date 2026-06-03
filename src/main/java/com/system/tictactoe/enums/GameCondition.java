package com.system.tictactoe.enums;

/**
 * Represents the current state of the game.
 *
 * Design Note: Using an enum (rather than a boolean or int) makes the state
 * self-documenting and extensible — e.g., we could add PAUSED or ABANDONED later.
 */
public enum GameCondition {
    IN_PROGRESS,
    ENDED
}
