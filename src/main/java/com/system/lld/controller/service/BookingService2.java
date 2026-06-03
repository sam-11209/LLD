package com.system.lld.controller.service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

import com.redis.demo.exception.NotFoundException;
import com.system.lld.entity.Booking;
import com.system.lld.entity.Order;
import com.system.lld.entity.PaymentStatus;
import com.system.lld.entity.Screening;
import com.system.lld.entity.ScreeningInventoryPessimistic;
import com.system.lld.entity.Seat;
import com.system.lld.entity.SeatLock;
import com.system.lld.entity.SeatStatus;
import com.system.lld.entity.Ticket;
import com.system.lld.repo.BookingRepository;
import com.system.lld.repo.ScreeningInventoryRepository;
import com.system.lld.repo.ScreeningRepository;

public class BookingService2 {

	ScreeningRepository screeningRepository;

	ScreeningInventoryRepository screeningInventoryRepository;

	BookingRepository bookingRepository;

	Map<String, SeatStatus> seatNoStatusMap = new ConcurrentHashMap<>();
	Map<String, SeatLock> seatNoSeatLockMap = new ConcurrentHashMap<>();
	private final ReentrantLock guard = new ReentrantLock(true);

	public String startBooking(String userId, List<String> seatNos, String screeningId) {

		Screening screening = screeningRepository.findById(screeningId)
				.orElseThrow(() -> new NotFoundException(null, "", null));

		ScreeningInventoryPessimistic screeningInventory = screeningInventoryRepository.get(screening.getId());

		// lock seats
		lockSeat(userId, Duration.ofMinutes(4), seatNos);

		String bookingId = "BookingId" + UUID.randomUUID();
		Booking booking = new Booking(bookingId, userId, screeningId, seatNos, PaymentStatus.INITIATED, null);
		bookingRepository.save(booking);

		return bookingId;
	}

	public void lockSeat(String userId, Duration till, List<String> seats) {
		guard.lock();
		try {
			clearExpiredLocks();
			for (String seatNo : seats) {
				if (SeatStatus.AVAILABLE != seatNoStatusMap.get(seatNo)) {
					throw new NotFoundException(userId, "Seat is not aaialble " + seatNo, null);
				}
			}
			for (String seatNo : seats) {
				seatNoStatusMap.put(seatNo, SeatStatus.LOCKED);
				SeatLock seatLock = new SeatLock(seatNo, userId, LocalDateTime.now(), till);
				seatNoSeatLockMap.put(seatNo, seatLock);
			}
		} finally {
			guard.unlock();
		}
	}

	private void clearExpiredLocks() {
		List<String> lockExpiredSeats = seatNoSeatLockMap.values().stream().filter(sl -> sl.isExpired())
				.map(x -> x.getSeatNumber()).toList();

		lockExpiredSeats.forEach(seatNo -> {
			seatNoSeatLockMap.remove(seatNo);
			seatNoStatusMap.put(seatNo, SeatStatus.AVAILABLE);
		});
	}

	public Booking confirmBooking(String bookingId) {
		Booking booking = bookingRepository.findById(bookingId)
				.orElseThrow(() -> new NotFoundException(bookingId, bookingId, null));
		Screening screening = screeningRepository.findById(bookingId)
				.orElseThrow(() -> new NotFoundException(bookingId, bookingId, null));

		ScreeningInventoryPessimistic screeningInventory = screeningInventoryRepository.get(screening.getId());

		// screeningInventory.
		markSeatsSold(booking.getUserId(), booking.getSeatNumbers());

		// get All Seats
		Map<String, Seat> seatNoSeatObjMap = screening.getRoom().getLayout().getAllSeats().stream()
				.collect(Collectors.toMap(x -> x.getSeatNumber(), y -> y));

		Order order = new Order();
		order.setUserId(booking.getUserId());
		order.setCreatedAt(LocalDateTime.now());

		for (String seatNo : booking.getSeatNumbers()) {
			Ticket ticket = new Ticket();
			ticket.setScreeningId(screening.getId());
			ticket.setSeatNumber(seatNo);
			ticket.setPriceSnapshot(seatNoSeatObjMap.get(seatNo).getPricingStrategy().getPrice());
			order.addTicket(ticket);
		}

		booking.setPaymentStatus(PaymentStatus.SUCCESS);
		booking.setOrder(order);
		return booking;

	}

	private void markSeatsSold(String userId, List<String> seatNumbers) {
		guard.lock();
		try {
			for (String seatNo : seatNumbers) {
				SeatLock seatLock = seatNoSeatLockMap.get(seatNo);
				if (SeatStatus.LOCKED != seatNoStatusMap.get(seatNo) || seatLock == null
						|| seatLock.getUserId() != userId || seatLock.isExpired()) {
					throw new NotFoundException(userId, "Seat is not locked", null);
				}
			}
			for (String seatNo : seatNumbers) {
				seatNoStatusMap.put(seatNo, SeatStatus.SOLD);
				seatNoSeatLockMap.remove(seatNo);
			}
		} finally {
			guard.unlock();
		}
	}

	private void clearLocks(String userId, List<String> seatNumbers) {
		guard.lock();
		try {
			for (String seatNo : seatNumbers) {

				SeatLock seatLock = seatNoSeatLockMap.get(seatNo);
				if (seatLock != null && userId == seatLock.getUserId()) {
					seatNoSeatLockMap.remove(seatNo);
					seatNoStatusMap.put(seatNo, SeatStatus.AVAILABLE);
				}
			}
		} finally {
			guard.unlock();
		}
	}
}
