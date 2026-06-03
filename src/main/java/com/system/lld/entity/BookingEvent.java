package com.system.lld.entity;

public class BookingEvent {
	private final String bookingId;
	private final String userId;
	private final String message;

	public BookingEvent(String bookingId, String userId, String message) {
		this.bookingId = bookingId;
		this.userId = userId;
		this.message = message;
	}

	public String getBookingId() {
		return bookingId;
	}

	public String getUserId() {
		return userId;
	}

	public String getMessage() {
		return message;
	}
}
