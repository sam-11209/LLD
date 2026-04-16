package com.system.lld.calude_design_complete_flow.grocery.discount.decorator;



import java.math.BigDecimal;
import java.math.RoundingMode;

import com.system.lld.calude_design_complete_flow.grocery.discount.strategy.DiscountCalculationStrategy;

/**
 * DECORATOR PATTERN — Percentage layer.
 *
 * Applies a percentage reduction ON TOP of whatever the wrapped strategy already computed.
 *
 * CONCRETE EXAMPLE (used in Main demo):
 * ─────────────────────────────────────
 * Scenario: "Weekend Sale" — 10% off all beverages, PLUS an extra $2 off.
 *
 *   DiscountCalculationStrategy strategy =
 *       new PercentageDiscountDecorator(
 *           new FixedDiscountDecorator(
 *               new NoDiscountStrategy(),   // base: returns price unchanged
 *               new BigDecimal("2.00")      // step 1: subtract $2
 *           ),
 *           new BigDecimal("10")            // step 2: subtract 10%
 *       );
 *
 * For a $20 item:
 *   NoDiscountStrategy: $20.00
 *   FixedDiscountDecorator: $20.00 - $2.00 = $18.00
 *   PercentageDiscountDecorator: $18.00 * 0.90 = $16.20
 */
public class PercentageDiscountDecorator implements DiscountCalculationStrategy {

    private final DiscountCalculationStrategy wrappedStrategy;
    private final BigDecimal percentage; // e.g., 10 = 10%

    public PercentageDiscountDecorator(DiscountCalculationStrategy wrappedStrategy,
                                       BigDecimal percentage) {
        this.wrappedStrategy = wrappedStrategy;
        this.percentage = percentage;
    }

    @Override
    public BigDecimal calculateDiscountedPrice(BigDecimal originalPrice) {
        // Step 1: Let the inner strategy run first
        BigDecimal afterInnerDiscount = wrappedStrategy.calculateDiscountedPrice(originalPrice);
        // Step 2: Apply OUR percentage reduction
        BigDecimal multiplier = BigDecimal.ONE
                .subtract(percentage.divide(new BigDecimal("100"), 4, RoundingMode.HALF_UP));
        return afterInnerDiscount.multiply(multiplier).setScale(2, RoundingMode.HALF_UP);
    }
}
