package com.blackjack.model;

import com.blackjack.strategy.DealerDecisionLogic;
import com.blackjack.strategy.PlayerDecisionLogic;

public class DealerPlayer implements Player {
    private final String name = "Dealer";
    private final Hand hand;
    private final PlayerDecisionLogic decisionLogic;

    public DealerPlayer() {
        this.hand = new Hand();
        this.decisionLogic = new DealerDecisionLogic();
    }

    @Override
    public void bet(int bet) {}

    @Override
    public void loseBet() {}

    @Override
    public void returnBet() {}

    @Override
    public void payout() {}

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
        return 0;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getBet() {
        return 0;
    }

    @Override
    public PlayerDecisionLogic getDecisionLogic() {
        return decisionLogic;
    }
}
