package com.system.tictactoe.game;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

import com.system.tictactoe.enums.GameCondition;
import com.system.tictactoe.exception.InvalidMoveException;
import com.system.tictactoe.history.MoveHistory;
import com.system.tictactoe.model.Move;
import com.system.tictactoe.model.Player;
import com.system.tictactoe.tracker.ScoreTracker;

/**
 * Central coordinator of a Tic-Tac-Toe game session.
 *
 * ── Memento Pattern Role: ORIGINATOR ───────────────────────────────────────
 *  The Game creates Move objects (Mementos) on each makeMove() and delegates
 *  their storage to MoveHistory (Caretaker). On undoMove(), it requests the
 *  last Memento from MoveHistory and uses it to restore the board state.
 *
 * ── Design Responsibilities (SRP) ──────────────────────────────────────────
 *  ✔  Orchestrate turns between players
 *  ✔  Validate moves (game-level rules: correct player, game not ended)
 *  ✔  Trigger score updates when game ends
 *  ✔  Provide undo capability via MoveHistory
 *  ✗  Grid-level validation (occupied cell, bounds) → Board
 *  ✗  Score calculation                             → ScoreTracker
 *  ✗  Move history storage                          → MoveHistory
 *
 * ── Thread Safety ──────────────────────────────────────────────────────────
 *  • ReentrantLock: guards the entire makeMove / undoMove critical section so
 *    two threads cannot simultaneously mutate turn state and board state.
 *    A single lock at the Game level is correct here because these operations
 *    span multiple objects (Board + MoveHistory + currentPlayerIndex) and must
 *    be atomic as a whole.
 *
 *  • AtomicInteger for currentPlayerIndex: in a single-threaded context this is
 *    overkill, but it documents intent and makes the turn counter safe if we ever
 *    allow async move submission (e.g., websocket handlers on separate threads).
 *    The ReentrantLock still wraps the composite operation; AtomicInteger gives us
 *    correct visibility for free even outside the lock (e.g., getCurrentPlayer()
 *    reads without locking).
 *
 * ── Design Changes from Original ───────────────────────────────────────────
 *  1. Added MoveHistory for undo support (Memento Pattern).
 *  2. ReentrantLock guards makeMove/undoMove/startNewGame critical sections.
 *  3. AtomicInteger for currentPlayerIndex.
 *  4. Custom InvalidMoveException instead of raw IllegalArgumentException.
 *  5. Player validation via UUID equals() rather than reference equality (==),
 *     which would break after deserialization or network transfer.
 */
public class Game {

    private final Board board;
    private final ScoreTracker scoreTracker;
    private final MoveHistory moveHistory;

    private Player[] players;

    // AtomicInteger documents that this counter must be mutation-safe
    private final AtomicInteger currentPlayerIndex = new AtomicInteger(0);

    // ReentrantLock: guards all multi-step state mutations
    private final ReentrantLock gameLock = new ReentrantLock();

    // ── Constructor ───────────────────────────────────────────────────────────

    public Game(Player playerOne, Player playerTwo) {
        this.board        = new Board();
        this.scoreTracker = new ScoreTracker();
        this.moveHistory  = new MoveHistory();
        startNewGame(playerOne, playerTwo);
    }

    // ── Public API ────────────────────────────────────────────────────────────

    /**
     * Resets the game for a new session with two players.
     */
    public void startNewGame(Player playerOne, Player playerTwo) {
        gameLock.lock();
        try {
            if (playerOne.getSymbol() == playerTwo.getSymbol()) {
                throw new IllegalArgumentException("Players must have different symbols.");
            }
            this.players = new Player[]{ playerOne, playerTwo };
            currentPlayerIndex.set(0);
            board.reset();
            moveHistory.clearHistory();
        } finally {
            gameLock.unlock();
        }
    }

    /**
     * Processes a player's move.
     *
     * ── Validations (in order) ──────────────────────────────────────────────
     *  1. Game must be IN_PROGRESS
     *  2. It must be this player's turn
     *  3. Target cell must be empty (delegated to Board)
     *
     * @param rowIndex 0-based row
     * @param colIndex 0-based column
     * @param player   The player making the move
     */
    public void makeMove(int rowIndex, int colIndex, Player player) {
        gameLock.lock();
        try {
            // Guard 1: Game must still be active
            if (getGameStatus() == GameCondition.ENDED) {
                throw new InvalidMoveException("Cannot make a move — the game has already ended.");
            }

            // Guard 2: Correct player must be moving
            // Using equals() (UUID-based) — not == (reference equality)
            if (!players[currentPlayerIndex.get()].equals(player)) {
                throw new InvalidMoveException(
                        String.format("It is %s's turn, not %s's.",
                                players[currentPlayerIndex.get()].getName(), player.getName()));
            }

            // Guard 3: Cell must be unoccupied (Board validates bounds too)
            if (board.getPlayerAt(rowIndex, colIndex).isPresent()) {
                throw new InvalidMoveException(
                        String.format("Cell (%d, %d) is already occupied.", rowIndex, colIndex));
            }

            // ── Apply the move ───────────────────────────────────────────────
            board.updateBoard(rowIndex, colIndex, player);

            // ── Record in history (Memento save) ────────────────────────────
            Move move = new Move(rowIndex, colIndex, player);
            moveHistory.recordMove(move);

            // ── Advance turn ────────────────────────────────────────────────
            currentPlayerIndex.set((currentPlayerIndex.get() + 1) % players.length);

            // ── Check end condition and update scores ────────────────────────
            if (getGameStatus() == GameCondition.ENDED) {
                scoreTracker.reportGameResult(players[0], players[1], board.getWinner());
            }

        } finally {
            gameLock.unlock();
        }
    }

    /**
     * Reverts the last move (Memento restore).
     *
     * Design Note: Undo is disallowed once the game has ended and the score has
     * been reported — rolling back a scored result would corrupt the leaderboard.
     * Extend this with a "pending" state if post-game undo is required.
     */
    public void undoMove() {
        gameLock.lock();
        try {
            if (getGameStatus() == GameCondition.ENDED) {
                throw new InvalidMoveException(
                        "Cannot undo — the game has ended and scores have been recorded.");
            }
            if (!moveHistory.hasHistory()) {
                throw new InvalidMoveException("No moves to undo.");
            }

            // Restore previous player's turn
            int prevIndex = (currentPlayerIndex.get() - 1 + players.length) % players.length;
            currentPlayerIndex.set(prevIndex);

            // Clear the board cell for that move (Memento restore)
            Move lastMove = moveHistory.undoMove();
            board.updateBoard(lastMove.getRowIndex(), lastMove.getColIndex(), null);

        } finally {
            gameLock.unlock();
        }
    }

    /**
     * Returns the current game status.
     */
    public GameCondition getGameStatus() {
        Optional<Player> winner = board.getWinner();
        if (winner.isPresent()) return GameCondition.ENDED;
        return board.isFull() ? GameCondition.ENDED : GameCondition.IN_PROGRESS;
    }

    /**
     * Returns the player whose turn it currently is.
     */
    public Player getCurrentPlayer() {
        // AtomicInteger.get() provides a safe read even without the gameLock
        return players[currentPlayerIndex.get()];
    }

    /**
     * Returns the winner if one exists.
     */
    public Optional<Player> getWinner() {
        return board.getWinner();
    }

    public ScoreTracker getScoreTracker()  { return scoreTracker; }
    public Board getBoard()                { return board; }
    public MoveHistory getMoveHistory()    { return moveHistory; }

    /**
     * Convenience method: prints the board to stdout.
     */
    public void printBoard() {
        System.out.println(board.display());
    }
}
