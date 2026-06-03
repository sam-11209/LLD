package com.system.lld.entity;

public class EmailNotificationService implements NotificationObserver {
	@Override
	public void onBookingEvent(BookingEvent event) {
		System.out.println("📧 Sending EMAIL to User: " + event.getUserId() + " | Booking: " + event.getBookingId()
				+ " | Message: " + event.getMessage());
	}
}