package com.system.tictactoe.exception;

/**
 * Thrown when a player attempts a move that violates game rules.
 *
 * Using a specific exception (rather than IllegalArgumentException) gives
 * callers the ability to catch game-domain errors distinctly.
 */
public class InvalidMoveException extends RuntimeException {
    public InvalidMoveException(String message) {
        super(message);
    }
}
