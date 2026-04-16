package com.system.lld.calude_design_complete_flow.grocery.order;



import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.system.lld.calude_design_complete_flow.grocery.discount.DiscountCampaign;

/**
 * Tracks the active transaction during a customer checkout.
 *
 * RESPONSIBILITY: Aggregates OrderItems, tracks which discount applies to each,
 * and computes subtotals, totals, and change.
 *
 * MISTAKE IN ORIGINAL DESIGN:
 * - processPayment / setPayment lived in both Order and Checkout inconsistently.
 *   We keep paymentAmount here (it's transaction data) and expose setPayment clearly.
 * - calculateChange() would return a negative number if the customer underpaid —
 *   this should throw, not silently return a negative value.
 */
public class Order {

    private final String orderId;
    private final List<OrderItem> items = new ArrayList<>();

    // Maps each order line to its best applicable discount
    private final Map<OrderItem, DiscountCampaign> appliedDiscounts = new HashMap<>();

    private BigDecimal paymentAmount = BigDecimal.ZERO;

    public Order() {
        this.orderId = UUID.randomUUID().toString();
    }

    public void addItem(OrderItem item) {
        items.add(item);
    }

    /**
     * Sum of all items at FULL price (before any discounts).
     * Useful for showing the customer what they saved.
     */
    public BigDecimal calculateSubtotal() {
        return items.stream()
                .map(OrderItem::calculatePrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Sum of all items AFTER applying their best discount.
     * Items with no discount are charged at full price.
     */
    public BigDecimal calculateTotal() {
        return items.stream()
                .map(item -> {
                    DiscountCampaign discount = appliedDiscounts.get(item);
                    return (discount != null)
                            ? item.calculatePriceWithDiscount(discount)
                            : item.calculatePrice();
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Records the best discount for a given order line.
     */
    public void applyDiscount(OrderItem item, DiscountCampaign discount) {
        appliedDiscounts.put(item, discount);
    }

    /**
     * FIX: Throws if customer underpaid.
     */
    public BigDecimal calculateChange() {
        BigDecimal total = calculateTotal();
        if (paymentAmount.compareTo(total) < 0) {
            throw new IllegalStateException(
                    "Payment of " + paymentAmount + " is less than total " + total);
        }
        return paymentAmount.subtract(total);
    }

    public void setPayment(BigDecimal paymentAmount) {
        if (paymentAmount == null || paymentAmount.compareTo(BigDecimal.ZERO) < 0)
            throw new IllegalArgumentException("Payment must be non-negative");
        this.paymentAmount = paymentAmount;
    }

    public String getOrderId()                                  { return orderId; }
    public List<OrderItem> getItems()                           { return Collections.unmodifiableList(items); }
    public Map<OrderItem, DiscountCampaign> getAppliedDiscounts(){ return appliedDiscounts; }
    public BigDecimal getPaymentAmount()                        { return paymentAmount; }
}
