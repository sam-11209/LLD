package com.system.tictactoe.model;

/**
 * Represents a single move made in the game.
 *
 * Acts as the *Memento* in the Memento Pattern — it is a snapshot of the
 * game state (which cell was played, by whom) at a specific point in time.
 *
 * Bundling row, col, and player into one object avoids messy parameter lists
 * across methods, and makes move history easy to manage as a typed collection.
 *
 * Thread Safety: Immutable after construction — fully thread-safe.
 */
public final class Move {

    private final int rowIndex;
    private final int colIndex;
    private final Player player;

    public Move(int rowIndex, int colIndex, Player player) {
        this.rowIndex = rowIndex;
        this.colIndex = colIndex;
        this.player   = player;
    }

    public int getRowIndex()  { return rowIndex; }
    public int getColIndex()  { return colIndex; }
    public Player getPlayer() { return player; }

    @Override
    public String toString() {
        return String.format("Move{player='%s', row=%d, col=%d}",
                player.getName(), rowIndex, colIndex);
    }
}
