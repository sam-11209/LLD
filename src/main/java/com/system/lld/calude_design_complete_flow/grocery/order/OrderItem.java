package com.system.lld.calude_design_complete_flow.grocery.order;


import java.math.BigDecimal;

import com.system.lld.calude_design_complete_flow.grocery.discount.DiscountCampaign;
import com.system.lld.calude_design_complete_flow.grocery.model.Item;

/**
 * Represents one line in an order: a specific item + how many of it.
 *
 * DESIGN NOTE: OrderItem is the bridge between the product world (Item)
 * and the checkout world (Order). It encapsulates per-line pricing logic.
 *
 * MISTAKE IN ORIGINAL DESIGN:
 * - quantity was not validated — zero or negative quantities would silently
 *   produce wrong prices.
 */
public class OrderItem {

    private final Item item;
    private final int quantity;

    public OrderItem(Item item, int quantity) {
        if (item == null)
            throw new IllegalArgumentException("Item cannot be null");
        if (quantity <= 0)
            throw new IllegalArgumentException("Quantity must be at least 1");
        this.item = item;
        this.quantity = quantity;
    }

    /**
     * Returns unit price × quantity — the base price with NO discount.
     *
     * EXAMPLE:
     *   item.price = $5.00, quantity = 3
     *   calculatePrice() = $15.00
     */
    public BigDecimal calculatePrice() {
        return item.getPrice().multiply(BigDecimal.valueOf(quantity));
    }

    /**
     * Returns the price after applying the given discount campaign.
     *
     * EXAMPLE:
     *   item.price = $5.00, quantity = 3, base = $15.00
     *   campaign = 10% off → calculatePriceWithDiscount() = $13.50
     */
    public BigDecimal calculatePriceWithDiscount(DiscountCampaign campaign) {
        return campaign.calculateDiscount(this);
    }

    public Item getItem()     { return item; }
    public int getQuantity()  { return quantity; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OrderItem)) return false;
        OrderItem other = (OrderItem) o;
        // Two OrderItems are equal if they're literally the same object
        // (important for HashMap key behavior in Order)
        return false; // identity equality — each scan creates a distinct line
    }

    @Override
    public int hashCode() {
        return System.identityHashCode(this);
    }

    @Override
    public String toString() {
        return String.format("OrderItem{item=%s, qty=%d, basePrice=%s}",
                item.getName(), quantity, calculatePrice());
    }
}
