package com.system.lld.controller.service;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;

import com.system.lld.entity.Payment;
import com.system.lld.entity.PaymentMethod;

@Service
public interface PaymentService {

	Payment processPayment(String bookingId, PaymentMethod method, BigDecimal amount);

	void cancelPayment(String paymentId);

}
