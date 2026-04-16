package com.system.lld.grocery.my_design;

import java.math.BigDecimal;

public interface DiscountCalculationStrategy {

	BigDecimal applyDiscount(BigDecimal price) throws Throwable;
}
