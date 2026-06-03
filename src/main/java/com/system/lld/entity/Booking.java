package com.system.lld.entity;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@AllArgsConstructor
@Getter
@Setter
public class Booking {

	private String id;
	private String userId;
	private String screeningId;
	private List<String> seatNumbers;
	private PaymentStatus paymentStatus = PaymentStatus.PENDING;
	private Order order; // optional until payment success

	/**
	 * Calculate the total price of this booking by resolving seats against the
	 * given screening's layout and summing their prices.
	 */
	public BigDecimal calculateTotalPrice(Screening screening) {
		// 1) Map seatNumber → Seat
		Map<String, Seat> seatsByNo = screening.getRoom().getLayout().getAllSeats().stream()
				.collect(Collectors.toMap(Seat::getSeatNumber, s -> s));

		// 2) Sum prices of booked seats
		return seatNumbers.stream().map(seatNo -> {
			Seat seat = seatsByNo.get(seatNo);
			if (seat == null) {
				throw new IllegalArgumentException("Seat not found in screening: " + seatNo);
			}
			return seat.getPricingStrategy().getPrice();
		}).reduce(BigDecimal.ZERO, BigDecimal::add);
	}
}
