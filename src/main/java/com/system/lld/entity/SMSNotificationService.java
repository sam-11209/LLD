package com.system.lld.entity;

public class SMSNotificationService implements NotificationObserver {
	@Override
	public void onBookingEvent(BookingEvent event) {
		System.out.println("📱 Sending SMS to User: " + event.getUserId() + " | Booking: " + event.getBookingId()
				+ " | Message: " + event.getMessage());
	}
}
