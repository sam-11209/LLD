package com.blackjack.model;

import com.blackjack.enums.Rank;
import com.blackjack.enums.Suit;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Thread-safe 52-card playing deck using AtomicInteger index tracking.
 * Card drawing is an O(1) atomic operation without array shifting.
 */
public class Deck {
    private final List<Card> cards;
    private final AtomicInteger nextCardIndex;

    public Deck() {
        this.cards = new ArrayList<>();
        this.nextCardIndex = new AtomicInteger(0);
        initializeDeck();
    }

    private void initializeDeck() {
        for (Suit suit : Suit.values()) {
            for (Rank rank : Rank.values()) {
                cards.add(new Card(rank, suit));
            }
        }
    }

    public synchronized void shuffle() {
        Collections.shuffle(cards, new Random(System.currentTimeMillis()));
        nextCardIndex.set(0);
    }

    public Card draw() {
        int index = nextCardIndex.getAndIncrement();
        if (index >= cards.size()) {
            throw new IllegalStateException("No more cards left in deck");
        }
        return cards.get(index);
    }

    public int getRemainingCardCount() {
        return Math.max(0, cards.size() - nextCardIndex.get());
    }

    public boolean isEmpty() {
        return getRemainingCardCount() == 0;
    }

    public synchronized void reset() {
        nextCardIndex.set(0);
    }
}
