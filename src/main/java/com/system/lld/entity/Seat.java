package com.system.lld.entity;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class Seat {

	private final String seatNumber; // e.g., "A10"
	private PricingStrategy pricingStrategy; // can change by layout/row

	public BigDecimal getPrice() {
		return pricingStrategy.getPrice();
	}
}
