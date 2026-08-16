package com.blackjack.strategy;

import com.blackjack.enums.Action;
import com.blackjack.model.Hand;

public class DealerDecisionLogic implements PlayerDecisionLogic {
    @Override
    public Action decideAction(Hand hand) {
        if (hand.isBust()) return Action.STAND;
        // Blackjack rule: Dealer must hit under 17, stand on 17 or higher
        return hand.getBestValidScore() < 17 ? Action.HIT : Action.STAND;
    }
}
