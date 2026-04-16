package com.system.lld.calude_design_complete_flow.grocery.discount.strategy;

import java.math.BigDecimal;

/**
 * Strategy interface for computing the final (discounted) price.
 *
 * HOW IT FITS IN THE DISCOUNT FLOW:
 * ──────────────────────────────────
 * Once criteria confirms a discount is applicable, the campaign calls:
 *
 *     calculationStrategy.calculateDiscountedPrice(originalPrice)
 *
 * This returns the FINAL price after the discount — NOT the discount amount itself.
 *
 * EXAMPLE:
 *   Original price = $100
 *   AmountBasedStrategy(discountAmount=$20) → returns $80
 *   PercentageBasedStrategy(10%)            → returns $90
 *
 * The Strategy Pattern means we can swap calculation methods freely.
 */
public interface DiscountCalculationStrategy {
    /**
     * @param originalPrice the pre-discount price of the order item
     * @return the final price after applying the discount
     */
    BigDecimal calculateDiscountedPrice(BigDecimal originalPrice);
}
