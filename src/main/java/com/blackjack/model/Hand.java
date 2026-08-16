package com.blackjack.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Thread-safe Hand holding dynamic card list and precomputing Ace values.
 * Uses ReentrantReadWriteLock for high concurrency during score checks.
 */
public class Hand {
    private final List<Card> handCards = new ArrayList<>();
    private final SortedSet<Integer> possibleValues = new TreeSet<>();
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    public Hand() {}

    public void addCard(Card card) {
        if (card == null) {
            throw new IllegalArgumentException("Cannot add null card to hand");
        }

        lock.writeLock().lock();
        try {
            handCards.add(card);

            if (possibleValues.isEmpty()) {
                for (int value : card.getRankValues()) {
                    possibleValues.add(value);
                }
            } else {
                SortedSet<Integer> newPossibleValues = new TreeSet<>();
                for (int existingTotal : possibleValues) {
                    for (int cardValue : card.getRankValues()) {
                        newPossibleValues.add(existingTotal + cardValue);
                    }
                }
                possibleValues.clear();
                possibleValues.addAll(newPossibleValues);
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    public List<Card> getCards() {
        lock.readLock().lock();
        try {
            return Collections.unmodifiableList(new ArrayList<>(handCards));
        } finally {
            lock.readLock().unlock();
        }
    }

    public SortedSet<Integer> getPossibleValues() {
        lock.readLock().lock();
        try {
            return Collections.unmodifiableSortedSet(new TreeSet<>(possibleValues));
        } finally {
            lock.readLock().unlock();
        }
    }

    public int getBestValidScore() {
        lock.readLock().lock();
        try {
            int best = -1;
            for (int val : possibleValues) {
                if (val <= 21 && val > best) {
                    best = val;
                }
            }
            // If all exceed 21, return the lowest bust score
            return best != -1 ? best : possibleValues.first();
        } finally {
            lock.readLock().unlock();
        }
    }

    public void clear() {
        lock.writeLock().lock();
        try {
            handCards.clear();
            possibleValues.clear();
        } finally {
            lock.writeLock().unlock();
        }
    }

    public boolean isBust() {
        lock.readLock().lock();
        try {
            if (possibleValues.isEmpty()) return false;
            return possibleValues.first() > 21;
        } finally {
            lock.readLock().unlock();
        }
    }
}
