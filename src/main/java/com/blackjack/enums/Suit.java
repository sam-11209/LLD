package com.blackjack.enums;

public enum Suit {
    HEARTS("♥"),
    SPADES("♠"),
    CLUBS("♣"),
    DIAMONDS("♦");

    private final String symbol;

    Suit(String symbol) {
        this.symbol = symbol;
    }

    public String getSymbol() {
        return symbol;
    }
}
