package com.system.lld.grocery.my_design;

import java.math.BigDecimal;

public class PercentageBasedDiscountCalculation implements DiscountCalculationStrategy {

	private BigDecimal percentage;

	public PercentageBasedDiscountCalculation(BigDecimal percentage) {
		this.percentage = percentage;
	}

	@Override
	public BigDecimal applyDiscount(BigDecimal price) throws Throwable {

		if (percentage.compareTo(BigDecimal.ZERO) <= 0) {
			return price;
		}

		return price.subtract(((price.multiply(percentage)).divide(BigDecimal.valueOf(100))));

	}

}
