package com.system.lld.entity;

import java.math.BigDecimal;

import org.springframework.stereotype.Component;

@Component("PERCENT10")
public class PercentageDiscount implements DiscountStrategy {
	
	@Override
	public BigDecimal apply(BigDecimal originalPrice, Booking booking) {
		return originalPrice.multiply(BigDecimal.valueOf(0.9)); // 10% off
	}

	@Override
	public String getName() {
		return "10% Off";
	}
}