package com.system.lld.controller.service;

import java.math.BigDecimal;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.system.lld.entity.Payment;
import com.system.lld.entity.PaymentMethod;
import com.system.lld.entity.PaymentStatus;
import com.system.lld.repo.PaymentRepository;

@Service
public class PaymentServiceImpl implements PaymentService {

	private final PaymentRepository paymentRepo;

	public PaymentServiceImpl(PaymentRepository paymentRepo) {
		this.paymentRepo = paymentRepo;
	}

	@Override
	public Payment processPayment(String bookingId, PaymentMethod method, BigDecimal amount) {
		Payment payment = new Payment(UUID.randomUUID().toString(), bookingId, method, amount);

		// simulate multi-step process (in real system call gateway API)
		try {
			System.out.println("💳 Processing payment of " + amount + " via " + method);
			Thread.sleep(500); // simulate external call latency

			payment.setStatus(PaymentStatus.SUCCESS);
		} catch (Exception e) {
			payment.setStatus(PaymentStatus.FAILED);
		}

		return paymentRepo.save(payment);
	}

	@Override
	public void cancelPayment(String paymentId) {
		Payment payment = paymentRepo.findById(paymentId).orElseThrow(() -> new RuntimeException("Payment not found"));
		payment.setStatus(PaymentStatus.CANCELLED);
		paymentRepo.save(payment);
		System.out.println("❌ Payment cancelled: " + paymentId);
	}
}
