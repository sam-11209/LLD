package com.system.lld.calude_design_complete_flow.grocery.discount.decorator;



import java.math.BigDecimal;

import com.system.lld.calude_design_complete_flow.grocery.discount.strategy.DiscountCalculationStrategy;

/**
 * The BASE (identity) strategy in a decorator chain.
 *
 * Returns the price unchanged — acts as the innermost layer of the
 * decorator stack. Every decorator chain starts with this.
 *
 * WHY THIS IS NEEDED:
 * Decorators need something to wrap. Rather than passing null or
 * a concrete strategy as the "start", we use this no-op base.
 *
 * ANALOGY: In text formatting, this is "plain text" — the undecorated base.
 * Bold wraps it → BoldDecorator(PlainText)
 * Italic + Bold → ItalicDecorator(BoldDecorator(PlainText))
 */
public class NoDiscountStrategy implements DiscountCalculationStrategy {
    @Override
    public BigDecimal calculateDiscountedPrice(BigDecimal originalPrice) {
        return originalPrice; // pass-through, no change
    }
}
