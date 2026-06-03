package com.system.lld.entity;

import java.time.Duration;
import java.time.LocalDateTime;

import lombok.Data;

@Data
public class SeatLock {

	private final String seatNumber;
	private final String userId; // who locked it
	private final LocalDateTime lockedAt;
	private final Duration ttl;

	public boolean isExpired() {
		return lockedAt.plus(ttl).isBefore(LocalDateTime.now());
	}
}
