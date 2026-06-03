package com.system.lld.entity;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ScreeningInventoryOptimistic {

    private final Map<String, SeatRecord> seatRecords = new ConcurrentHashMap<>();

    public ScreeningInventoryOptimistic(Collection<Seat> seats) {
        for (Seat s : seats) {
            seatRecords.put(s.getSeatNumber(),
                    new SeatRecord(s.getSeatNumber(), SeatStatus.AVAILABLE, 0L, null));
        }
    }

    public List<String> getAvailableSeatNumbers() {
        cleanExpiredLocks();
        return seatRecords.values().stream()
                .filter(r -> r.status == SeatStatus.AVAILABLE)
                .map(r -> r.seatNumber)
                .sorted()
                .toList();
    }

    /** Try to lock seats optimistically */
    public void lockSeats(Collection<String> seatNos, String userId, Duration ttl) {
        for (String s : seatNos) {
            SeatRecord rec = seatRecords.get(s);

            if (rec == null || rec.status != SeatStatus.AVAILABLE) {
                throw new IllegalStateException("Seat not available: " + s);
            }

            long oldVersion = rec.version;
            SeatRecord updated = new SeatRecord(
                    rec.seatNumber,
                    SeatStatus.LOCKED,
                    oldVersion + 1,
                    new SeatLock(s, userId, LocalDateTime.now(), ttl)
            );

            // CAS-like replace: only update if version matches
            boolean success = seatRecords.replace(s, rec, updated);
            if (!success) {
                throw new IllegalStateException("Seat was modified concurrently: " + s);
            }
        }
    }

    /** Confirm seats optimistically */
    public void markSold(Collection<String> seatNos, String userId) {
        for (String s : seatNos) {
            SeatRecord rec = seatRecords.get(s);
            if (rec == null || rec.lock == null || rec.lock.isExpired() || 
            		!rec.lock.getUserId().equals(userId)) {
                throw new IllegalStateException("Seat not locked by user: " + s);
            }

            long oldVersion = rec.version;
            SeatRecord updated = new SeatRecord(
                    rec.seatNumber,
                    SeatStatus.SOLD,
                    oldVersion + 1,
                    null
            );
            boolean success = seatRecords.replace(s, rec, updated);
            if (!success) {
                throw new IllegalStateException("Seat was modified concurrently: " + s);
            }
        }
    }

    /** Release seats optimistically */
    public void releaseLocks(Collection<String> seatNos, String userId) {
        for (String s : seatNos) {
            SeatRecord rec = seatRecords.get(s);
            if (rec == null || rec.lock == null) continue;
            if (userId == null || userId.equals(rec.lock.getUserId())) {
                long oldVersion = rec.version;
                SeatRecord updated = new SeatRecord(
                        rec.seatNumber,
                        SeatStatus.AVAILABLE,
                        oldVersion + 1,
                        null
                );
                seatRecords.replace(s, rec, updated);
            }
        }
    }

    private void cleanExpiredLocks() {
        for (Map.Entry<String, SeatRecord> e : seatRecords.entrySet()) {
            SeatRecord rec = e.getValue();
            if (rec.lock != null && rec.lock.isExpired()) {
                SeatRecord updated = new SeatRecord(
                        rec.seatNumber,
                        SeatStatus.AVAILABLE,
                        rec.version + 1,
                        null
                );
                seatRecords.replace(e.getKey(), rec, updated);
            }
        }
    }

	/** Internal wrapper holding version */
	private record SeatRecord(String seatNumber, SeatStatus status, long version, SeatLock lock) {
	}
}

