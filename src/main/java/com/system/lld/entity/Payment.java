package com.system.lld.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Data;

@Data
public class Payment {
	private final String id;
	private final String bookingId;
	private final PaymentMethod method;
	private PaymentStatus status;
	private final BigDecimal amount;
	private final LocalDateTime createdAt = LocalDateTime.now();

	public Payment(String id, String bookingId, PaymentMethod method, BigDecimal amount) {
		this.id = id;
		this.bookingId = bookingId;
		this.method = method;
		this.amount = amount;
	}

	public String getId() {
		return id;
	}

	public String getBookingId() {
		return bookingId;
	}

	public PaymentMethod getMethod() {
		return method;
	}

	public PaymentStatus getStatus() {
		return status;
	}

	public void setStatus(PaymentStatus status) {
		this.status = status;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}
}
