package com.system.lld.repo;

import java.util.List;
import java.util.Optional;

import com.system.lld.entity.Payment;

public interface PaymentRepository {

	Payment save(Payment payment);

	Optional<Payment> findById(String paymentId);

	List<Payment> findByBookingId(String bookingId);
}
