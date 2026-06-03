package com.system.lld.entity;

import java.math.BigDecimal;

final class PremiumRate implements PricingStrategy {
	public BigDecimal getPrice() {
		return BigDecimal.valueOf(350);
	}
}
