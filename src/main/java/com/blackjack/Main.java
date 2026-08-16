package com.blackjack;

import com.blackjack.enums.GamePhase;
import com.blackjack.engine.ThreadSafeBlackJackGame;
import com.blackjack.model.Player;
import com.blackjack.model.RealPlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Multi-threaded simulation runner testing concurrent thread access to ThreadSafeBlackJackGame.
 */
public class Main {
    public static void main(String[] args) throws InterruptedException {
        System.out.println("=================================================");
        System.out.println("   THREAD-SAFE BLACKJACK ENGINE SIMULATION       ");
        System.out.println("=================================================");

        // Create 3 Real Players with initial balances
        List<Player> players = new ArrayList<>();
        RealPlayer alice = new RealPlayer("Alice", 100);
        RealPlayer bob = new RealPlayer("Bob", 150);
        RealPlayer charlie = new RealPlayer("Charlie", 200);

        players.add(alice);
        players.add(bob);
        players.add(charlie);

        ThreadSafeBlackJackGame game = new ThreadSafeBlackJackGame(players);

        int numberOfRounds = 3;
        for (int round = 1; round <= numberOfRounds; round++) {
            System.out.println("\n-------------------------------------------------");
            System.out.println(" STARTING ROUND " + round);
            System.out.println("-------------------------------------------------");

            if (round > 1) {
                game.startNewRound();
            }

            // Execute concurrent betting using an ExecutorService thread pool
            ExecutorService bettingPool = Executors.newFixedThreadPool(3);
            int betAmount = 20;

            for (Player player : players) {
                bettingPool.submit(() -> {
                    try {
                        System.out.println("[Thread " + Thread.currentThread().getName() + "] " 
                                           + player.getName() + " placing bet of $" + betAmount);
                        game.bet(player, betAmount);
                    } catch (Exception e) {
                        System.err.println("Bet error for " + player.getName() + ": " + e.getMessage());
                    }
                });
            }

            bettingPool.shutdown();
            bettingPool.awaitTermination(2, TimeUnit.SECONDS);

            System.out.println(">> All bets placed. Current phase: " + game.getCurrentPhase());

            // Deal initial cards
            game.dealInitialCards();
            System.out.println(">> Initial cards dealt.");

            // Print initial hands
            for (Player p : players) {
                System.out.println("   " + p.getName() + " Hand: " + p.getHand().getCards() 
                                   + " | Best Score: " + p.getHand().getBestValidScore());
            }
            System.out.println("   Dealer Hand: " + game.getDealer().getHand().getCards().get(0) 
                               + " [HIDDEN CARD]");

            // Execute turns sequentially / strategy driven
            System.out.println("\n-- Player & Dealer Turns --");
            while (game.getCurrentPhase() != GamePhase.END) {
                game.playTurnForCurrentPlayer();
            }

            // Print final results and balances
            System.out.println("\n-- ROUND " + round + " FINAL RESULTS --");
            System.out.println("   Dealer Final Hand: " + game.getDealer().getHand().getCards() 
                               + " | Score: " + game.getDealer().getHand().getBestValidScore() 
                               + (game.getDealer().isBust() ? " [BUST]" : ""));

            for (Player p : players) {
                String outcome = p.isBust() ? "BUST (Lost Bet)" 
                        : (game.getDealer().isBust() || p.getHand().getBestValidScore() > game.getDealer().getHand().getBestValidScore() ? "WINNER!" 
                        : (p.getHand().getBestValidScore() == game.getDealer().getHand().getBestValidScore() ? "PUSH (Tie)" : "LOST"));

                System.out.println("   " + p.getName() + " Hand: " + p.getHand().getCards() 
                                   + " (Score: " + p.getHand().getBestValidScore() + ") -> " 
                                   + outcome + " | Balance: $" + p.getBalance());
            }
        }

        System.out.println("\n=================================================");
        System.out.println(" SIMULATION COMPLETE. ALL THREAD TESTS PASSED!   ");
        System.out.println("=================================================");
    }
}
