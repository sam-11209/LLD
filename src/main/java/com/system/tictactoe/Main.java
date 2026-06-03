package com.system.tictactoe;

import com.system.tictactoe.enums.GameCondition;
import com.system.tictactoe.exception.InvalidMoveException;
import com.system.tictactoe.game.Game;
import com.system.tictactoe.model.Player;

/**
 * Demo runner — exercises the full game flow:
 *  1. Normal win
 *  2. Undo functionality
 *  3. Draw detection
 *  4. Invalid-move rejection
 *  5. Leaderboard after multiple games
 */
public class Main {

    public static void main(String[] args) {
        Player alice = new Player("Alice", 'X');
        Player bob   = new Player("Bob",   'O');

        System.out.println("=== GAME 1: Alice wins diagonally ===");
        Game game1 = new Game(alice, bob);
        playAndPrint(game1, alice, 0, 0);   // Alice: top-left
        playAndPrint(game1, bob,   0, 1);   // Bob
        playAndPrint(game1, alice, 1, 1);   // Alice: center
        playAndPrint(game1, bob,   0, 2);   // Bob
        playAndPrint(game1, alice, 2, 2);   // Alice: bottom-right → diagonal win

        game1.printBoard();
        printResult(game1);
        System.out.println(game1.getScoreTracker().getLeaderboard());

        // ─────────────────────────────────────────────────────────────────────
        System.out.println("\n=== GAME 2: Undo demo ===");
        game1.startNewGame(alice, bob);
        playAndPrint(game1, alice, 0, 0);
        playAndPrint(game1, bob,   1, 1);

        System.out.println("Alice undoes her last move... (wait, it's Bob's last move that's undone)");
        // Only the most recent move (Bob's) can be undone when it's Alice's turn again
        // Let's undo Bob's move:
        game1.undoMove();
        System.out.println("After undo:");
        game1.printBoard();
        System.out.println("Current player: " + game1.getCurrentPlayer().getName());

        // ─────────────────────────────────────────────────────────────────────
        System.out.println("\n=== GAME 3: Draw ===");
        Game game2 = new Game(alice, bob);
        // X O X
        // X X O
        // O X O  → draw
        playAndPrint(game2, alice, 0, 0);
        playAndPrint(game2, bob,   0, 1);
        playAndPrint(game2, alice, 0, 2);
        playAndPrint(game2, bob,   1, 2);
        playAndPrint(game2, alice, 1, 0);
        playAndPrint(game2, bob,   2, 0);
        playAndPrint(game2, alice, 1, 1);
        playAndPrint(game2, bob,   2, 2);
        playAndPrint(game2, alice, 2, 1);

        game2.printBoard();
        printResult(game2);

        // ─────────────────────────────────────────────────────────────────────
        System.out.println("\n=== Invalid move demo ===");
        Game game3 = new Game(alice, bob);
        playAndPrint(game3, alice, 0, 0);

        try {
            // Attempt to play in occupied cell
            game3.makeMove(0, 0, bob);
        } catch (InvalidMoveException e) {
            System.out.println("Caught expected error: " + e.getMessage());
        }

        try {
            // Wrong player tries to move
            game3.makeMove(1, 1, alice);
        } catch (InvalidMoveException e) {
            System.out.println("Caught expected error: " + e.getMessage());
        }

        // ─────────────────────────────────────────────────────────────────────
        System.out.println("\n=== Final Leaderboard (Game 1 only — others drawn/incomplete) ===");
        System.out.println(game1.getScoreTracker().getLeaderboard());
        System.out.println("Alice's rank: " + game1.getScoreTracker().getRank(alice));
        System.out.println("Bob's rank:   " + game1.getScoreTracker().getRank(bob));
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private static void playAndPrint(Game game, Player player, int row, int col) {
        System.out.printf("%s plays at (%d,%d)%n", player.getName(), row, col);
        game.makeMove(row, col, player);
    }

    private static void printResult(Game game) {
        if (game.getGameStatus() == GameCondition.ENDED) {
            game.getWinner().ifPresentOrElse(
                    w -> System.out.println("🏆 Winner: " + w.getName()),
                    ()  -> System.out.println("🤝 It's a draw!")
            );
        } else {
            System.out.println("Game is still in progress.");
        }
    }
}
