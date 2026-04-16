package com.system.lld.calude_design_complete_flow.grocery.discount.decorator;



import java.math.BigDecimal;

import com.system.lld.calude_design_complete_flow.grocery.discount.strategy.DiscountCalculationStrategy;

/**
 * ══════════════════════════════════════════════════════════════
 *  DECORATOR PATTERN — EXPLAINED IN DETAIL
 * ══════════════════════════════════════════════════════════════
 *
 * PROBLEM it solves:
 * Suppose you want to apply TWO discounts in sequence on the same item:
 *   1) First subtract a fixed $5
 *   2) Then apply 10% off on the remaining price
 *
 * Without Decorator, you'd need a combined class for every combination:
 *   FixedThenPercentageStrategy, PercentageThenFixedStrategy, etc.
 * → This leads to a class explosion.
 *
 * DECORATOR SOLUTION:
 * Each decorator WRAPS another DiscountCalculationStrategy and adds its own step.
 * You compose them like Russian nesting dolls:
 *
 *   PercentageDiscountDecorator(10%)
 *     └── wraps → FixedDiscountDecorator($5)
 *                   └── wraps → NoDiscountStrategy (base: returns price as-is)
 *
 * Calling calculateDiscountedPrice($100):
 *   1. FixedDiscountDecorator: $100 - $5 = $95
 *   2. PercentageDiscountDecorator: $95 * 0.90 = $85.50
 *
 * KEY INSIGHT: All three classes implement the SAME interface.
 * The outer layer has NO idea what's inside — it just calls the interface method.
 *
 * ──────────────────────────────────────────────────────────────
 * FixedDiscountDecorator: subtracts a fixed amount AFTER the wrapped strategy runs.
 * ──────────────────────────────────────────────────────────────
 */
public class FixedDiscountDecorator implements DiscountCalculationStrategy {

    // The inner strategy (could be a base strategy OR another decorator)
    private final DiscountCalculationStrategy wrappedStrategy;
    private final BigDecimal fixedAmount;

    public FixedDiscountDecorator(DiscountCalculationStrategy wrappedStrategy,
                                  BigDecimal fixedAmount) {
        this.wrappedStrategy = wrappedStrategy;
        this.fixedAmount = fixedAmount;
    }

    @Override
    public BigDecimal calculateDiscountedPrice(BigDecimal originalPrice) {
        // Step 1: Let the inner strategy run first
        BigDecimal afterInnerDiscount = wrappedStrategy.calculateDiscountedPrice(originalPrice);
        // Step 2: Apply OUR additional fixed reduction
        BigDecimal result = afterInnerDiscount.subtract(fixedAmount);
        return result.compareTo(BigDecimal.ZERO) < 0 ? BigDecimal.ZERO : result;
    }
}
