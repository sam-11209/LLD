package com.blackjack.model;

import com.blackjack.strategy.PlayerDecisionLogic;
import com.blackjack.strategy.RealPlayerDecisionLogic;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Thread-safe RealPlayer managing atomic balance and bet states.
 */
public class RealPlayer implements Player {
    private final String name;
    private final Hand hand;
    private final AtomicInteger balance;
    private final AtomicInteger currentBet;
    private final PlayerDecisionLogic decisionLogic;

    public RealPlayer(String name, int startBalance) {
        this.name = name;
        this.hand = new Hand();
        this.balance = new AtomicInteger(startBalance);
        this.currentBet = new AtomicInteger(0);
        this.decisionLogic = new RealPlayerDecisionLogic();
    }

    public RealPlayer(String name, int startBalance, PlayerDecisionLogic customDecisionLogic) {
        this.name = name;
        this.hand = new Hand();
        this.balance = new AtomicInteger(startBalance);
        this.currentBet = new AtomicInteger(0);
        this.decisionLogic = customDecisionLogic;
    }

    @Override
    public synchronized void bet(int betAmount) {
        if (betAmount <= 0) {
            throw new IllegalArgumentException("Bet must be positive");
        }
        if (betAmount > balance.get()) {
            throw new IllegalArgumentException("Bet is greater than available balance");
        }
        balance.addAndGet(-betAmount);
        currentBet.set(betAmount);
    }

    @Override
    public synchronized void loseBet() {
        currentBet.set(0);
    }

    @Override
    public synchronized void returnBet() {
        balance.addAndGet(currentBet.get());
        currentBet.set(0);
    }

    @Override
    public synchronized void payout() {
        int winnings = currentBet.get() * 2; // Original bet returned + 1:1 payout
        balance.addAndGet(winnings);
        currentBet.set(0);
    }

    @Override
    public boolean isBust() {
        return hand.isBust();
    }

    @Override
    public Hand getHand() {
        return hand;
    }

    @Override
    public int getBalance() {
        return balance.get();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getBet() {
        return currentBet.get();
    }

    @Override
    public PlayerDecisionLogic getDecisionLogic() {
        return decisionLogic;
    }
}
