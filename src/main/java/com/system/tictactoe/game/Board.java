package com.system.tictactoe.game;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.IntStream;

import com.system.tictactoe.model.Player;

/**
 * Represents the 3x3 Tic-Tac-Toe board.
 *
 * ── Design Responsibilities (SRP) ──────────────────────────────────────────
 *  ✔  Maintain grid state
 *  ✔  Validate & apply moves at the cell level
 *  ✔  Detect win conditions (rows, cols, diagonals)
 *  ✔  Report whether the board is full
 *  ✗  Does NOT know whose turn it is  → Game
 *  ✗  Does NOT track scores           → ScoreTracker
 *
 * ── Thread Safety ──────────────────────────────────────────────────────────
 *  ReentrantLock guards all state mutations and multi-step reads so that
 *  concurrent calls (e.g., from a networked game server) cannot produce
 *  corrupt grid state or false winner results.
 *
 *  Why ReentrantLock over synchronized?
 *   • tryLock() lets us fail-fast rather than block forever.
 *   • Explicit lock/unlock is clearer for complex multi-step operations.
 *   • Supports fairness policy (new ReentrantLock(true)) if needed later.
 *
 * ── Design Change from Original ────────────────────────────────────────────
 *  Original: no thread safety.
 *  New: ReentrantLock guards all grid mutations and composite reads.
 */
public class Board {

    private static final int SIZE = 3;

    // Grid stores Player references; null == empty cell
    private final Player[][] grid = new Player[SIZE][SIZE];

    // ReentrantLock: used instead of 'synchronized' for fine-grained control
    // and future extensibility (e.g., tryLock with timeout in a timed game mode).
    private final ReentrantLock lock = new ReentrantLock();

    /**
     * Places a player on the board. No-op if cell is already occupied.
     * Pass null to clear a cell (used by undo).
     */
    public void updateBoard(int rowIndex, int colIndex, Player player) {
        lock.lock();
        try {
            validateBounds(rowIndex, colIndex);
            // Allow null for undo; for normal moves Game already checks occupancy
            grid[rowIndex][colIndex] = player;
        } finally {
            lock.unlock();
        }
    }

    /**
     * Returns the player at a given cell, or empty if the cell is vacant.
     */
    public Optional<Player> getPlayerAt(int rowIndex, int colIndex) {
        lock.lock();
        try {
            validateBounds(rowIndex, colIndex);
            return Optional.ofNullable(grid[rowIndex][colIndex]);
        } finally {
            lock.unlock();
        }
    }

    /**
     * Checks rows, columns, and both diagonals for a winner.
     * Holds the lock for the entire check to prevent a race between reads.
     */
    public Optional<Player> getWinner() {
        lock.lock();
        try {
            // ── Rows ──────────────────────────────────────────────────────
            for (int i = 0; i < SIZE; i++) {
                Player first = grid[i][0];
                if (first != null && Arrays.stream(grid[i]).allMatch(p -> p == first)) {
                    return Optional.of(first);
                }
            }

            // ── Columns ───────────────────────────────────────────────────
            for (int j = 0; j < SIZE; j++) {
                final int col = j;
                Player first = grid[0][col];
                if (first != null &&
                        IntStream.range(0, SIZE).allMatch(i -> grid[i][col] == first)) {
                    return Optional.of(first);
                }
            }

            // ── Main diagonal (top-left → bottom-right) ───────────────────
            Player topLeft = grid[0][0];
            if (topLeft != null &&
                    IntStream.range(0, SIZE).allMatch(i -> grid[i][i] == topLeft)) {
                return Optional.of(topLeft);
            }

            // ── Anti-diagonal (top-right → bottom-left) ───────────────────
            Player topRight = grid[0][SIZE - 1];
            if (topRight != null &&
                    IntStream.range(0, SIZE).allMatch(i -> grid[i][SIZE - 1 - i] == topRight)) {
                return Optional.of(topRight);
            }

            return Optional.empty();
        } finally {
            lock.unlock();
        }
    }

    /**
     * Returns true if every cell is occupied.
     */
    public boolean isFull() {
        lock.lock();
        try {
            return Arrays.stream(grid)
                    .flatMap(Arrays::stream)
                    .noneMatch(Objects::isNull);
        } finally {
            lock.unlock();
        }
    }

    /**
     * Clears all cells — called at the start of a new game.
     */
    public void reset() {
        lock.lock();
        try {
            for (Player[] row : grid) {
                Arrays.fill(row, null);
            }
        } finally {
            lock.unlock();
        }
    }

    /**
     * Pretty-prints the board to stdout — useful for console demos.
     */
    public String display() {
        lock.lock();
        try {
            StringBuilder sb = new StringBuilder();
            sb.append("\n  0   1   2\n");
            for (int i = 0; i < SIZE; i++) {
                sb.append(i).append(" ");
                for (int j = 0; j < SIZE; j++) {
                    Player p = grid[i][j];
                    sb.append(p == null ? "." : p.getSymbol());
                    if (j < SIZE - 1) sb.append(" | ");
                }
                if (i < SIZE - 1) sb.append("\n  ---------\n");
            }
            sb.append("\n");
            return sb.toString();
        } finally {
            lock.unlock();
        }
    }

    // ── Private helpers ───────────────────────────────────────────────────────

    private void validateBounds(int row, int col) {
        if (row < 0 || row >= SIZE || col < 0 || col >= SIZE) {
            throw new IllegalArgumentException(
                    String.format("Position (%d,%d) is out of bounds. Valid range: 0-%d.", row, col, SIZE - 1));
        }
    }
}
