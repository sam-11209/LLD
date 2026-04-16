package com.system.lld.calude_design_complete_flow.grocery.discount;


import java.math.BigDecimal;

import com.system.lld.calude_design_complete_flow.grocery.discount.criteria.DiscountCriteria;
import com.system.lld.calude_design_complete_flow.grocery.discount.strategy.DiscountCalculationStrategy;
import com.system.lld.calude_design_complete_flow.grocery.model.Item;
import com.system.lld.calude_design_complete_flow.grocery.order.OrderItem;

/**
 * Models a single promotional discount campaign.
 *
 * ══════════════════════════════════════════════════════════════
 *  HOW THE FULL DISCOUNT FLOW WORKS — STEP BY STEP
 * ══════════════════════════════════════════════════════════════
 *
 * Imagine these two active campaigns:
 *   Campaign A: 10% off ALL "Beverages"
 *   Campaign B: $3 off item with barcode "PEPSI_001"
 *
 * Cashier scans: Pepsi (barcode=PEPSI_001, category=Beverages, price=$10, qty=2)
 *   → OrderItem created: quantity=2, base price = $10 * 2 = $20
 *
 * Checkout loops through campaigns:
 *
 *   Campaign A:
 *     criteria = CategoryBasedCriteria("Beverages")
 *     criteria.isApplicable(pepsiItem)? → category matches → TRUE
 *     → campaign.calculateDiscount(orderItem):
 *         calculationStrategy.calculateDiscountedPrice($20) → 10% off → $18.00
 *     → appliedDiscount for Pepsi = Campaign A, discountedPrice = $18.00
 *
 *   Campaign B:
 *     criteria = ItemBasedCriteria("PEPSI_001")
 *     criteria.isApplicable(pepsiItem)? → barcode matches → TRUE
 *     → campaign.calculateDiscount(orderItem):
 *         calculationStrategy.calculateDiscountedPrice($20) → subtract $3 → $17.00
 *     → $17.00 < $18.00? YES → Campaign B gives MORE savings
 *     → appliedDiscount for Pepsi UPDATED to Campaign B, discountedPrice = $17.00
 *
 * Final: Pepsi charges $17.00 (saved $3 from the $20 base)
 *
 * ──────────────────────────────────────────────────────────────
 * DESIGN NOTE:
 * DiscountCampaign uses COMPOSITION not inheritance:
 *   - criteria       = pluggable "who qualifies" rule
 *   - calculationStrategy = pluggable "how much" rule
 * This makes every combination possible without new subclasses.
 */
public class DiscountCampaign {

    private final String discountId;
    private final String name;
    private final DiscountCriteria criteria;
    private final DiscountCalculationStrategy calculationStrategy;

    public DiscountCampaign(String discountId, String name,
                            DiscountCriteria criteria,
                            DiscountCalculationStrategy calculationStrategy) {
        this.discountId = discountId;
        this.name = name;
        this.criteria = criteria;
        this.calculationStrategy = calculationStrategy;
    }

    /**
     * Delegates to the criteria to decide if this campaign applies to the given item.
     */
    public boolean isApplicable(Item item) {
        return criteria.isApplicable(item);
    }

    /**
     * Delegates to the calculation strategy.
     * Returns the FINAL PRICE (after discount) for the given order item.
     *
     * NOTE: input is the total price of the OrderItem (unit price × quantity),
     * not just the unit price.
     */
    public BigDecimal calculateDiscount(OrderItem orderItem) {
        return calculationStrategy.calculateDiscountedPrice(orderItem.calculatePrice());
    }

    public String getDiscountId()   { return discountId; }
    public String getName()         { return name; }

    @Override
    public String toString() {
        return String.format("DiscountCampaign{id='%s', name='%s'}", discountId, name);
    }
}
