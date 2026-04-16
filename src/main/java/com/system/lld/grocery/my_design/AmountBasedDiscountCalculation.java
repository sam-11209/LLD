package com.system.lld.grocery.my_design;

import java.math.BigDecimal;

public class AmountBasedDiscountCalculation implements DiscountCalculationStrategy {

	private BigDecimal amount;

	public AmountBasedDiscountCalculation(BigDecimal amount) {
		this.amount = amount;
	}

	@Override
	public BigDecimal applyDiscount(BigDecimal price) throws Throwable {

		if (amount.compareTo(BigDecimal.ZERO) <= 0)
			return price;

		if (price.compareTo(amount) < 0) {
			throw new Exception("Not applicable");
		}

		return price.subtract(amount);
	}

}
