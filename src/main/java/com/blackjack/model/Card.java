package com.blackjack.model;

import com.blackjack.enums.Rank;
import com.blackjack.enums.Suit;

public class Card {
    public final Rank rank;
    public final Suit suit;

    public Card(Rank rank, Suit suit) {
        if (rank == null || suit == null) {
            throw new IllegalArgumentException("Rank and Suit must not be null");
        }
        this.rank = rank;
        this.suit = suit;
    }

    public int[] getRankValues() {
        return rank.getRankValues();
    }

    @Override
    public String toString() {
        return rank.name() + suit.getSymbol();
    }
}
