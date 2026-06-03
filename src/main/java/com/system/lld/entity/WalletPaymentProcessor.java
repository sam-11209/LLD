package com.system.lld.entity;

public class WalletPaymentProcessor implements PaymentProcessor {
	@Override
	public PaymentStatus process(Payment payment) {
		System.out.println("Processing wallet payment...");
		// Check wallet balance instantly
		boolean balanceAvailable = true;

		if (balanceAvailable) {
			payment.setStatus(PaymentStatus.SUCCESS);
		} else {
			payment.setStatus(PaymentStatus.FAILED);
		}
		return payment.getStatus();
	}

	@Override
	public PaymentStatus cancel(Payment payment) {
		System.out.println("Cancelling wallet payment...");
		payment.setStatus(PaymentStatus.CANCELLED);
		return payment.getStatus();
	}
}
