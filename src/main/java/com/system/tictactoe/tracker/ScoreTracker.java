package com.system.tictactoe.tracker;


import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import com.system.tictactoe.model.Player;

/**
 * Tracks player scores across multiple Tic-Tac-Toe games.
 *
 * ── Design Responsibilities (SRP) ──────────────────────────────────────────
 *  ✔  Maintain per-player win/loss/draw counts
 *  ✔  Provide ranked leaderboard queries
 *  ✗  Does NOT control game flow          → Game
 *  ✗  Does NOT validate moves             → Board
 *
 * ── Why scores live here, not in Player ────────────────────────────────────
 *  Ratings are *contextual* — they exist relative to a group of players.
 *  The same player could have different ratings in different leagues.
 *  Decoupling rating from Player enables multi-league support in the future.
 *
 * ── Thread Safety ──────────────────────────────────────────────────────────
 *  • ConcurrentHashMap: allows concurrent reads / writes to different player
 *    entries without a global lock — important for a game server handling
 *    many simultaneous matches.
 *
 *  • AtomicInteger per player: score increments are CAS-based (compare-and-swap),
 *    eliminating the need for synchronized blocks on individual counters.
 *    This is significantly faster under high contention than locking the entire map.
 *
 * ── Design Change from Original ────────────────────────────────────────────
 *  Original: HashMap<Player, Integer> — not thread-safe; requires external sync.
 *  New:      ConcurrentHashMap<Player, AtomicInteger> — lock-free score updates.
 *
 * ── Scoring Algorithm ──────────────────────────────────────────────────────
 *  Win  → +1 point
 *  Loss → -1 point
 *  Draw →  0 points (no change)
 *  Extendable: swap out the algorithm inside reportGameResult() without touching
 *  any other class (Open/Closed Principle).
 */
public class ScoreTracker {

    // ConcurrentHashMap: thread-safe, no global lock needed for reads/writes
    // AtomicInteger: each player's score updated atomically — no race conditions
    private final ConcurrentHashMap<Player, AtomicInteger> playerRatings = new ConcurrentHashMap<>();

    /**
     * Updates scores after a game ends.
     *
     * @param player1       First player
     * @param player2       Second player
     * @param winningPlayer The winner, or Optional.empty() for a draw
     */
    public void reportGameResult(Player player1, Player player2, Optional<Player> winningPlayer) {
        // Ensure both players exist in the map (putIfAbsent is atomic on ConcurrentHashMap)
        playerRatings.putIfAbsent(player1, new AtomicInteger(0));
        playerRatings.putIfAbsent(player2, new AtomicInteger(0));

        winningPlayer.ifPresent(winner -> {
            Player loser = winner.equals(player1) ? player2 : player1;
            // AtomicInteger.incrementAndGet() / decrementAndGet() are lock-free CAS operations
            playerRatings.get(winner).incrementAndGet();  // +1 for winner
            playerRatings.get(loser).decrementAndGet();   // -1 for loser
            // Draw: no score change — intentionally left empty
        });
    }

    /**
     * Returns all players sorted by score descending.
     *
     * @return Ordered map: Player → score
     */
    public Map<Player, Integer> getTopPlayers() {
        return playerRatings.entrySet().stream()
                .sorted((a, b) -> Integer.compare(b.getValue().get(), a.getValue().get()))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> e.getValue().get(),
                        (e1, e2) -> e1,
                        LinkedHashMap::new   // Preserve insertion order after sort
                ));
    }

    /**
     * Returns the 1-based rank of a player (1 = highest score).
     *
     * @param player The player to rank
     * @return Rank (1-based), or -1 if player is unregistered
     */
    public int getRank(Player player) {
        List<Player> ranked = playerRatings.entrySet().stream()
                .sorted((a, b) -> Integer.compare(b.getValue().get(), a.getValue().get()))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        int idx = ranked.indexOf(player);
        return idx == -1 ? -1 : idx + 1;
    }

    /**
     * Returns the current score of a player, or 0 if not registered.
     */
    public int getScore(Player player) {
        AtomicInteger score = playerRatings.get(player);
        return score == null ? 0 : score.get();
    }

    /**
     * Prints a formatted leaderboard to stdout.
     */
    public String getLeaderboard() {
        StringBuilder sb = new StringBuilder();
        sb.append("\n╔══════════════════════════════╗\n");
        sb.append("║        LEADERBOARD           ║\n");
        sb.append("╠══════════════════════════════╣\n");

        int rank = 1;
        for (Map.Entry<Player, Integer> entry : getTopPlayers().entrySet()) {
            sb.append(String.format("║  #%d  %-15s  %+4d pts  ║\n",
                    rank++, entry.getKey().getName(), entry.getValue()));
        }
        sb.append("╚══════════════════════════════╝\n");
        return sb.toString();
    }
}
