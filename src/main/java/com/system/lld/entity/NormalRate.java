package com.system.lld.entity;

import java.math.BigDecimal;

final class NormalRate implements PricingStrategy {
	
	public BigDecimal getPrice() {
		return BigDecimal.valueOf(200);
	}
}