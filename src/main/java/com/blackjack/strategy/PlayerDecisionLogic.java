package com.blackjack.strategy;

import com.blackjack.enums.Action;
import com.blackjack.model.Hand;

public interface PlayerDecisionLogic {
    Action decideAction(Hand hand);
}
