package com.system.lld.entity;

public class CardPaymentProcessor implements PaymentProcessor {
	@Override
	public PaymentStatus process(Payment payment) {
		System.out.println("Processing card payment...");
		// Step 1: Validate card
		// Step 2: Block amount
		// Step 3: Ask for OTP → simulate PENDING
		payment.setStatus(PaymentStatus.PENDING);

		// Later, OTP callback received → mark SUCCESS
		// For simplicity, assume OTP always succeeds
		payment.setStatus(PaymentStatus.SUCCESS);
		return payment.getStatus();
	}

	@Override
	public PaymentStatus cancel(Payment payment) {
		System.out.println("Cancelling card payment...");
		payment.setStatus(PaymentStatus.CANCELLED);
		return payment.getStatus();
	}
}
