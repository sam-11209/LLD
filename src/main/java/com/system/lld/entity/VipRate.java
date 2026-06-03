package com.system.lld.entity;

import java.math.BigDecimal;

final class VipRate implements PricingStrategy {
	
	public BigDecimal getPrice() {
		return BigDecimal.valueOf(500);
	}
}
