package com.system.lld.entity;

import java.math.BigDecimal;

public interface DiscountStrategy {
	
	BigDecimal apply(BigDecimal originalPrice, Booking booking);

	String getName();
}
