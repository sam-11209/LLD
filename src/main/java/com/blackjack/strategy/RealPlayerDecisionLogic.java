package com.blackjack.strategy;

import com.blackjack.enums.Action;
import com.blackjack.model.Hand;

public class RealPlayerDecisionLogic implements PlayerDecisionLogic {
    @Override
    public Action decideAction(Hand hand) {
        if (hand.isBust()) return Action.STAND;
        // Hit if best score is under 16, else stand
        return hand.getBestValidScore() < 16 ? Action.HIT : Action.STAND;
    }
}
