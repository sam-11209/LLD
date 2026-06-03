package com.system.tictactoe.model;

import java.util.Objects;
import java.util.UUID;

/**
 * Represents a player in the Tic-Tac-Toe game.
 *
 * Design Note: Player is intentionally kept as a pure data class (no game logic).
 * Win tracking is delegated to ScoreTracker — ratings are contextual and relative,
 * not an intrinsic property of the player.
 *
 * Thread Safety: Immutable after construction — fully thread-safe.
 */
public final class Player {

    private final String id;       // Unique ID — useful for persistence / distributed systems
    private final String name;
    private final char symbol;

    public Player(String name, char symbol) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Player name must not be blank.");
        }
        if (symbol != 'X' && symbol != 'O') {
            throw new IllegalArgumentException("Symbol must be 'X' or 'O'.");
        }
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.symbol = symbol;
    }

    public String getId()     { return id; }
    public String getName()   { return name; }
    public char getSymbol()   { return symbol; }

    // Equality is identity-based (UUID), not name-based.
    // Two players with the same name are different entities.
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Player)) return false;
        return id.equals(((Player) o).id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("Player{name='%s', symbol='%c'}", name, symbol);
    }
}
