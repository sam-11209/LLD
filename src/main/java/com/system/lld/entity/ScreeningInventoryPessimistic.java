package com.system.lld.entity;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

public class ScreeningInventoryPessimistic {

	private String screeningId;
	private final Map<String, SeatStatus> seatNoAndstatusMap = new ConcurrentHashMap<>();
	private final Map<String, SeatLock> seatNoAndSeatlockMap = new ConcurrentHashMap<>();
	private final ReentrantLock guard = new ReentrantLock(true);

	public ScreeningInventoryPessimistic(Collection<Seat> seats) {
		for (Seat s : seats)
			seatNoAndstatusMap.put(s.getSeatNumber(), SeatStatus.AVAILABLE);
	}

	public List<String> getAvailableSeatNumbers() {
		cleanExpiredLocks();
		return seatNoAndstatusMap.entrySet().stream().filter(e -> e.getValue() == SeatStatus.AVAILABLE)
				.map(Map.Entry::getKey).sorted().toList();
	}

	/** atomically place a hold; throws if any requested seat not available */
	public void lockSeats(Collection<String> seatNos, String userId, Duration ttl) {
		guard.lock();
		try {
			cleanExpiredLocks();
			for (String s : seatNos) {
				if (seatNoAndstatusMap.getOrDefault(s, SeatStatus.SOLD) != SeatStatus.AVAILABLE)
					throw new IllegalStateException("Seat not available: " + s);
			}
			for (String s : seatNos) {
				seatNoAndstatusMap.put(s, SeatStatus.LOCKED);
				seatNoAndSeatlockMap.put(s, new SeatLock(s, userId, LocalDateTime.now(), ttl));
			}
		} finally {
			guard.unlock();
		}
	}

	/** confirm purchase; only caller who locked them can confirm */
	public void markSold(Collection<String> seatNos, String userId) {
		guard.lock();
		try {
			for (String s : seatNos) {
				SeatLock lock = seatNoAndSeatlockMap.get(s);
				if (lock == null || lock.isExpired() || !lock.getUserId().equals(userId))
					throw new IllegalStateException("Seat not locked by user: " + s);
			}
			for (String s : seatNos) {
				seatNoAndSeatlockMap.remove(s);
				seatNoAndstatusMap.put(s, SeatStatus.SOLD);
			}
		} finally {
			guard.unlock();
		}
	}

	/** release holds (on cancel/timeout) */
	public void releaseLocks(Collection<String> seatNos, String userId) {
		guard.lock();
		try {
			for (String s : seatNos) {
				SeatLock lock = seatNoAndSeatlockMap.get(s);
				if (lock != null && (userId == null || userId.equals(lock.getUserId()))) {
					seatNoAndSeatlockMap.remove(s);
					seatNoAndstatusMap.put(s, SeatStatus.AVAILABLE);
				}
			}
		} finally {
			guard.unlock();
		}
	}

	private void cleanExpiredLocks() {
		List<String> expired = seatNoAndSeatlockMap.values().stream()//
				.filter(SeatLock::isExpired).map(SeatLock::getSeatNumber)
				.toList();
		for (String s : expired) {
			seatNoAndSeatlockMap.remove(s);
			seatNoAndstatusMap.put(s, SeatStatus.AVAILABLE);
		}
	}
}
