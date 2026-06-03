package com.system.lld.repo;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import com.system.lld.entity.Payment;

public class InMemoryPaymentRepository implements PaymentRepository {

	private final Map<String, Payment> store = new ConcurrentHashMap<>();

	@Override
	public Payment save(Payment payment) {
		return store.put(payment.getId(), payment);
	}

	@Override
	public Optional<Payment> findById(String paymentId) {
		return Optional.ofNullable(store.get(paymentId));
	}

	@Override
	public List<Payment> findByBookingId(String bookingId) {
		return store.values().stream().filter(p -> p.getBookingId().equals(bookingId)).collect(Collectors.toList());
	}

}
