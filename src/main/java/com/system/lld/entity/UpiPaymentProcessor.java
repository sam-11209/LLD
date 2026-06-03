package com.system.lld.entity;

public class UpiPaymentProcessor implements PaymentProcessor {
	@Override
	public PaymentStatus process(Payment payment) {
		System.out.println("Processing UPI payment...");
		// Step 1: Generate UPI request
		// Step 2: User approves in UPI app → simulate async
		payment.setStatus(PaymentStatus.PENDING);

		// Simulate callback after few seconds
		payment.setStatus(PaymentStatus.SUCCESS);
		return payment.getStatus();
	}

	@Override
	public PaymentStatus cancel(Payment payment) {
		System.out.println("Cancelling UPI payment...");
		payment.setStatus(PaymentStatus.CANCELLED);
		return payment.getStatus();
	}
}
