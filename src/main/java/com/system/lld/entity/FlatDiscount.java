package com.system.lld.entity;

import java.math.BigDecimal;

import org.springframework.stereotype.Component;

@Component("FLAT100")
public class FlatDiscount implements DiscountStrategy {
	
	@Override
	public BigDecimal apply(BigDecimal originalPrice, Booking booking) {
		return originalPrice.subtract(BigDecimal.valueOf(100)).max(BigDecimal.ZERO);
	}

	@Override
	public String getName() {
		return "Flat ₹100 Off";
	}
}
