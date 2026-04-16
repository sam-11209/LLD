package com.system.lld.calude_design_complete_flow.grocery.discount.strategy;

import java.math.BigDecimal;

/**
 * Subtracts a fixed dollar amount from the original price.
 *
 * EXAMPLE:
 *   strategy = new AmountBasedStrategy(new BigDecimal("20.00"))
 *   originalPrice = $100  →  finalPrice = $80
 *
 * MISTAKE IN ORIGINAL DESIGN:
 * No guard against the discount exceeding the price.
 * If discount > price, result would be negative — which is impossible in a store.
 * We clamp the result to $0 minimum.
 */
public class AmountBasedStrategy implements DiscountCalculationStrategy {

    private final BigDecimal discountAmount;

    public AmountBasedStrategy(BigDecimal discountAmount) {
        if (discountAmount == null || discountAmount.compareTo(BigDecimal.ZERO) <= 0)
            throw new IllegalArgumentException("Discount amount must be positive");
        this.discountAmount = discountAmount;
    }

    @Override
    public BigDecimal calculateDiscountedPrice(BigDecimal originalPrice) {
        BigDecimal result = originalPrice.subtract(discountAmount);
        // FIX: Price can never go below zero
        return result.compareTo(BigDecimal.ZERO) < 0 ? BigDecimal.ZERO : result;
    }

    public BigDecimal getDiscountAmount() { return discountAmount; }
}
