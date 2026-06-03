package com.system.tictactoe.history;


import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.locks.ReentrantLock;

import com.system.tictactoe.model.Move;

/**
 * Maintains the history of moves made during a game.
 *
 * ── Memento Pattern Role: CARETAKER ────────────────────────────────────────
 *  The Caretaker holds a collection of Mementos (Move objects) and provides
 *  save / restore operations. It never inspects the internal state of a Memento.
 *
 * ── Thread Safety ──────────────────────────────────────────────────────────
 *  ReentrantLock guards push and pop so concurrent game-server threads cannot
 *  interleave record/undo calls and corrupt the stack order.
 *
 * ── Design Change from Original ────────────────────────────────────────────
 *  Original: no explicit thread safety on the ArrayDeque.
 *  New: ReentrantLock ensures safe access in multi-threaded environments.
 */
public class MoveHistory {

    // ArrayDeque used as a stack (LIFO): push() adds to front, pop() removes from front.
    private final Deque<Move> history = new ArrayDeque<>();
    private final ReentrantLock lock = new ReentrantLock();

    /**
     * Records a move onto the history stack (Memento save).
     */
    public void recordMove(Move move) {
        lock.lock();
        try {
            history.push(move);
        } finally {
            lock.unlock();
        }
    }

    /**
     * Removes and returns the most recent move (Memento restore).
     *
     * @throws java.util.NoSuchElementException if history is empty.
     */
    public Move undoMove() {
        lock.lock();
        try {
            if (history.isEmpty()) {
                throw new IllegalStateException("No moves to undo.");
            }
            return history.pop();
        } finally {
            lock.unlock();
        }
    }

    /**
     * Returns true if there are moves that can be undone.
     */
    public boolean hasHistory() {
        lock.lock();
        try {
            return !history.isEmpty();
        } finally {
            lock.unlock();
        }
    }

    /**
     * Clears all history — called when a new game starts.
     */
    public void clearHistory() {
        lock.lock();
        try {
            history.clear();
        } finally {
            lock.unlock();
        }
    }

    /**
     * Returns the number of moves recorded.
     */
    public int size() {
        lock.lock();
        try {
            return history.size();
        } finally {
            lock.unlock();
        }
    }
}
