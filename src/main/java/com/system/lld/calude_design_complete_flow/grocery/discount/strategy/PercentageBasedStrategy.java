package com.system.lld.calude_design_complete_flow.grocery.discount.strategy;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Applies a percentage reduction to the original price.
 *
 * EXAMPLE:
 *   strategy = new PercentageBasedStrategy(new BigDecimal("10"))  // 10%
 *   originalPrice = $100  →  finalPrice = $90
 *
 *   strategy = new PercentageBasedStrategy(new BigDecimal("20"))  // 20%
 *   originalPrice = $50   →  finalPrice = $40
 *
 * MISTAKE IN ORIGINAL DESIGN:
 * - Used integer division in some implementations — causes precision loss.
 *   We use BigDecimal throughout with explicit HALF_UP rounding.
 * - No validation that percentage is between 0 and 100.
 */
public class PercentageBasedStrategy implements DiscountCalculationStrategy {

    private final BigDecimal discountPercentage; // e.g., 10 means 10%

    public PercentageBasedStrategy(BigDecimal discountPercentage) {
        if (discountPercentage == null
                || discountPercentage.compareTo(BigDecimal.ZERO) <= 0
                || discountPercentage.compareTo(new BigDecimal("100")) > 0) {
            throw new IllegalArgumentException("Discount percentage must be between 1 and 100");
        }
        this.discountPercentage = discountPercentage;
    }

    @Override
    public BigDecimal calculateDiscountedPrice(BigDecimal originalPrice) {
        // multiplier = (100 - percentage) / 100
        BigDecimal multiplier = BigDecimal.ONE
                .subtract(discountPercentage.divide(new BigDecimal("100"), 4, RoundingMode.HALF_UP));
        return originalPrice.multiply(multiplier).setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal getDiscountPercentage() { return discountPercentage; }
}
