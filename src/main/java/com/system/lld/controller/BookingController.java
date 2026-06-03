package com.system.lld.controller;

import com.system.lld.controller.Dtos.BookingDto;
import com.system.lld.controller.Dtos.ConfirmBookingRequest;
import com.system.lld.controller.Dtos.OrderDto;
import com.system.lld.controller.Dtos.StartBookingRequest;
import com.system.lld.controller.service.BookingService;
import com.system.lld.entity.Booking;
import com.system.lld.entity.Order;

public class BookingController {

	private final BookingService bookingService;

	BookingController(BookingService b) {
		this.bookingService = b;
	}

	// POST /book/start
	public BookingDto startBooking(StartBookingRequest r) {
		Booking b = bookingService.startBooking(r.userId(), r.screeningId(), r.seatNumbers());
		return BookingDto.from(b);
	}

	// POST /book/confirm
	public OrderDto confirm(ConfirmBookingRequest r, String promoCode) {
		Order o = bookingService.confirmBooking(r.bookingId(), promoCode);
		return OrderDto.from(o);
	}

	// POST /book/cancel
	public void cancel(String bookingId) {
		bookingService.cancelBooking(bookingId);
	}
}
