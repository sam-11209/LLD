package com.system.lld.controller;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.system.lld.entity.Booking;
import com.system.lld.entity.Order;
import com.system.lld.entity.PaymentStatus;
import com.system.lld.entity.Screening;
import com.system.lld.entity.Ticket;

public class Dtos {

	record StartBookingRequest(String userId, String screeningId, List<String> seatNumbers) {
	}

	record ConfirmBookingRequest(String bookingId) {
	}

	record ScreeningDto(String id, String movieTitle, String cinemaName, String room, LocalDateTime startTime) {
		static ScreeningDto from(Screening s) {
			return new ScreeningDto(s.getId(), s.getMovie().getTitle(), s.getCinema().getName(),
					s.getRoom().getRoomNumber(), s.getStartTime());
		}
	}

	record BookingDto(String id, String userId, String screeningId, List<String> seats, PaymentStatus status) {
		static BookingDto from(Booking b) {
			return new BookingDto(b.getId(), b.getUserId(), b.getScreeningId(), b.getSeatNumbers(),
					b.getPaymentStatus());
		}
	}

	record OrderDto(String id, List<TicketDto> tickets, BigDecimal total) {
		static OrderDto from(Order o) {
			return new OrderDto(o.getId(), o.getTickets().stream().map(TicketDto::from).toList(), o.totalPrice());
		}
	}

	record TicketDto(String id, String seatNumber, BigDecimal price) {
		static TicketDto from(Ticket t) {
			return new TicketDto(t.getId(), t.getSeatNumber(), t.getPriceSnapshot());
		}
	}

}
