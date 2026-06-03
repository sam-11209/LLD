package com.system.lld.entity;

;

public interface PaymentProcessor {
	PaymentStatus process(Payment payment);

	PaymentStatus cancel(Payment payment);
}
