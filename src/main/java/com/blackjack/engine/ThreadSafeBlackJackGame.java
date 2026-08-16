package com.blackjack.engine;

import com.blackjack.enums.Action;
import com.blackjack.enums.GamePhase;
import com.blackjack.model.Card;
import com.blackjack.model.DealerPlayer;
import com.blackjack.model.Deck;
import com.blackjack.model.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Thread-safe Blackjack Game Engine. Synchronizes multi-threaded player
 * actions, turn sequencing, and payout settlement using a fair ReentrantLock
 * and Condition variables.
 */
public class ThreadSafeBlackJackGame {
	private final Deck deck = new Deck();
	private final List<Player> players = new ArrayList<>();
	private final Player dealer = new DealerPlayer();

	private final Map<Player, Action> playerTurnStatusMap = new ConcurrentHashMap<>();
	private volatile GamePhase currentPhase = GamePhase.STARTED;
	private volatile Player currentPlayer = null;

	private final ReentrantLock gameLock = new ReentrantLock(true); // Fair lock
	private final Condition turnCondition = gameLock.newCondition();

	public ThreadSafeBlackJackGame(List<Player> players) {
		if (players == null || players.isEmpty()) {
			throw new IllegalArgumentException("Game requires at least one player");
		}
		for (Player p : players) {
			if (p == null)
				throw new IllegalArgumentException("Player cannot be null");
			this.players.add(p);
			this.playerTurnStatusMap.put(p, Action.HIT); // Default active status
		}
		this.playerTurnStatusMap.put(dealer, Action.HIT);
		deck.shuffle();
	}

	public void startNewRound() {
		gameLock.lock();
		try {
			deck.reset();
			deck.shuffle();

			for (Player p : players) {
				p.getHand().clear();
				playerTurnStatusMap.put(p, Action.HIT);
			}
			dealer.getHand().clear();
			playerTurnStatusMap.put(dealer, Action.HIT);

			currentPlayer = null;
			currentPhase = GamePhase.STARTED;
			turnCondition.signalAll();
		} finally {
			gameLock.unlock();
		}
	}

	public void bet(Player player, int betAmount) {
		gameLock.lock();
		try {
			if (currentPhase != GamePhase.STARTED) {
				throw new IllegalStateException("Bets must be placed at the start of the round");
			}
			player.bet(betAmount);

			// Check if all non-dealer players have placed bets
			boolean allBetsPlaced = players.stream().filter(p -> !(p instanceof DealerPlayer))
					.allMatch(p -> p.getBet() > 0);

			if (allBetsPlaced) {
				currentPhase = GamePhase.BET_PLACED;
			}
		} finally {
			gameLock.unlock();
		}
	}

	public void dealInitialCards() {
		gameLock.lock();
		try {
			if (currentPhase != GamePhase.BET_PLACED) {
				throw new IllegalStateException("All bets must be placed before dealing initial cards");
			}

			// Deal first card to real players then dealer
			for (Player player : players) {
				player.getHand().addCard(deck.draw());
			}
			dealer.getHand().addCard(deck.draw());

			// Deal second card to real players then dealer
			for (Player player : players) {
				player.getHand().addCard(deck.draw());
			}
			dealer.getHand().addCard(deck.draw());

			currentPhase = GamePhase.INITIAL_CARD_DRAWN;
		} finally {
			gameLock.unlock();
		}
	}

	public Player getNextEligiblePlayer() {
		gameLock.lock();
		try {
			// Check if current player can continue taking actions
			if (currentPlayer != null && !Action.STAND.equals(playerTurnStatusMap.get(currentPlayer))
					&& !currentPlayer.isBust()) {
				return currentPlayer;
			}

			// Find first player who has not stood or bust
			if (currentPlayer == null) {
				for (Player player : players) {
					if (!Action.STAND.equals(playerTurnStatusMap.get(player)) && !player.isBust()) {
						currentPlayer = player;
						return currentPlayer;
					}
				}
				// If all real players stood or bust, dealer's turn
				if (!Action.STAND.equals(playerTurnStatusMap.get(dealer))) {
					currentPlayer = dealer;
					return dealer;
				}
			} // else, find the next player after the current one who hasn't stood or bust
				// (means cur player is on stand need to find new player)
			else {
				int currentIndex = players.indexOf(currentPlayer);
				for (int i = currentIndex + 1; i < players.size(); i++) {
					Player player = players.get(i);
					if (!Action.STAND.equals(playerTurnStatusMap.get(player)) && !player.isBust()) {
						currentPlayer = player;
						return currentPlayer;
					}
				}
				// Dealer turn check
				if (currentPlayer != dealer && !Action.STAND.equals(playerTurnStatusMap.get(dealer))) {
					currentPlayer = dealer;
					return dealer;
				}
			}
			return null; // All players and dealer complete
		} finally {
			gameLock.unlock();
		}
	}

	public void hit(Player player) {
		gameLock.lock();
		try {
			if (Action.STAND.equals(playerTurnStatusMap.get(player))) {
				throw new IllegalStateException(player.getName() + " has already stood");
			}
			if (player.isBust()) {
				throw new IllegalStateException(player.getName() + " is already bust");
			}

			Card drawn = deck.draw();
			player.getHand().addCard(drawn);
			playerTurnStatusMap.put(player, Action.HIT);

//            if (player.isBust()) {
//                playerTurnStatusMap.put(player, Action.STAND);
//            }
			turnCondition.signalAll();
		} finally {
			gameLock.unlock();
		}
	}

	public void stand(Player player) {
		gameLock.lock();
		try {
			if (Action.STAND.equals(playerTurnStatusMap.get(player))) {
				throw new IllegalStateException(player.getName() + " has already stood");
			}

			if (player.isBust()) {
				throw new IllegalStateException(player.getName() + " is already bust");
			}

			playerTurnStatusMap.put(player, Action.STAND);
			turnCondition.signalAll();
		} finally {
			gameLock.unlock();
		}
	}

	public void executeDealerTurn() {
		gameLock.lock();
		try {
			currentPhase = GamePhase.DEALER_TURN;
			while (dealer.getHand().getBestValidScore() < 17 && !dealer.isBust()) {
				Card drawn = deck.draw();
				dealer.getHand().addCard(drawn);
			}
			playerTurnStatusMap.put(dealer, Action.STAND);
			settleBetsAndFinalize();
		} finally {
			gameLock.unlock();
		}
	}

	public void playTurnForCurrentPlayer() {
		gameLock.lock();
		try {
			Player p = getNextEligiblePlayer();
			if (p == null) {
				executeDealerTurn();
				return;
			}

			if (p == dealer) {
				executeDealerTurn();
				return;
			}

			Action action = p.getDecisionLogic().decideAction(p.getHand());
			if (action == Action.HIT) {
				hit(p);
			} else {
				stand(p);
			}
		} finally {
			gameLock.unlock();
		}
	}

	private void settleBetsAndFinalize() {
		int dealerScore = dealer.getHand().getBestValidScore();
		boolean dealerBusts = dealer.isBust();

		for (Player player : players) {
			if (player.isBust()) {
				player.loseBet();
			} else {
				int playerScore = player.getHand().getBestValidScore();
				if (dealerBusts || playerScore > dealerScore) {
					player.payout();
				} else if (playerScore == dealerScore) {
					player.returnBet();
				} else {
					player.loseBet();
				}
			}
		}
		currentPhase = GamePhase.END;
	}

	// Getters for thread-safe state inspection
	public GamePhase getCurrentPhase() {
		return currentPhase;
	}

	public Player getDealer() {
		return dealer;
	}

	public List<Player> getPlayers() {
		return Collections.unmodifiableList(players);
	}
}
