package com.system.lld.calude_design_complete_flow.grocery.discount.criteria;



import java.math.BigDecimal;

import com.system.lld.calude_design_complete_flow.grocery.model.Item;

/**
 * Example of an EXTRA criteria not in the original design.
 * Applies only to items whose price is at or above a minimum threshold.
 *
 * Used to demonstrate how easy it is to add new rules without
 * modifying any existing class (Open/Closed Principle).
 *
 * EXAMPLE:
 *   criteria = new MinPriceCriteria(new BigDecimal("10.00"))
 *   item priced at $15 → isApplicable = true
 *   item priced at $5  → isApplicable = false
 */
public class MinPriceCriteria implements DiscountCriteria {

    private final BigDecimal minimumPrice;

    public MinPriceCriteria(BigDecimal minimumPrice) {
        this.minimumPrice = minimumPrice;
    }

    @Override
    public boolean isApplicable(Item item) {
        return item.getPrice().compareTo(minimumPrice) >= 0;
    }
}
