package com.system.lld.calude_design_complete_flow.grocery.checkout;

import java.math.BigDecimal;
import java.util.List;

import com.system.lld.calude_design_complete_flow.grocery.discount.DiscountCampaign;
import com.system.lld.calude_design_complete_flow.grocery.inventory.Inventory;
import com.system.lld.calude_design_complete_flow.grocery.model.Item;
import com.system.lld.calude_design_complete_flow.grocery.order.Order;
import com.system.lld.calude_design_complete_flow.grocery.order.OrderItem;
import com.system.lld.calude_design_complete_flow.grocery.order.Receipt;

/**
 * Orchestrates the entire checkout transaction.
 *
 * RESPONSIBILITIES:
 *  - Creates and manages the active Order
 *  - Adds items (with inventory check)
 *  - Automatically finds and applies the BEST discount for each line
 *  - Processes payment and generates a receipt
 *
 * MISTAKE IN ORIGINAL DESIGN:
 * - addItemToOrder() never checked inventory before adding — a cashier could
 *   sell items that are out of stock. We fix that here.
 * - The "best discount" comparison logic was slightly wrong: it compared
 *   calculatePriceWithDiscount values and applied the higher one. But a HIGHER
 *   price after discount means LESS savings. The logic should pick the campaign
 *   that produces the LOWEST final price (most savings). We fix this.
 */
public class Checkout {

    private Order currentOrder;
    private final List<DiscountCampaign> activeDiscounts;
    private final Inventory inventory; // FIX: added so we can check stock

    public Checkout(List<DiscountCampaign> activeDiscounts, Inventory inventory) {
        this.activeDiscounts = activeDiscounts;
        this.inventory = inventory;
        startNewOrder();
    }

    public void startNewOrder() {
        this.currentOrder = new Order();
    }

    /**
     * Scans an item and adds it to the current order.
     *
     * Steps:
     *  1. Check inventory — throw if insufficient stock
     *  2. Create OrderItem
     *  3. Loop through active campaigns, find the one giving the LOWEST final price
     *  4. Apply that campaign to the order line
     *  5. Reduce inventory
     */
    public void addItemToOrder(Item item, int quantity) {
        // FIX: Check inventory before allowing the sale
        if (!inventory.hasSufficientStock(item.getBarcode(), quantity)) {
            throw new Inventory.InsufficientStockException(
                    "Cannot sell " + quantity + " of '" + item.getName() +
                    "'. Available stock: " + inventory.getStock(item.getBarcode()));
        }

        OrderItem orderItem = new OrderItem(item, quantity);
        currentOrder.addItem(orderItem);

        // Find the campaign that gives the LOWEST final price (= most savings)
        DiscountCampaign bestDiscount = null;
        BigDecimal bestPrice = orderItem.calculatePrice(); // start with full price

        for (DiscountCampaign campaign : activeDiscounts) {
            if (campaign.isApplicable(item)) {
                BigDecimal discountedPrice = orderItem.calculatePriceWithDiscount(campaign);
                // FIX: original compared > 0 (higher price = better?). We want LOWER price.
                if (discountedPrice.compareTo(bestPrice) < 0) {
                    bestPrice = discountedPrice;
                    bestDiscount = campaign;
                }
            }
        }

        if (bestDiscount != null) {
            currentOrder.applyDiscount(orderItem, bestDiscount);
        }

        // Reduce inventory now that the item is confirmed in the order
        inventory.reduceStock(item.getBarcode(), quantity);
    }

    /**
     * Records the customer's payment and returns the change due.
     */
    public BigDecimal processPayment(BigDecimal paymentAmount) {
        currentOrder.setPayment(paymentAmount);
        return currentOrder.calculateChange();
    }

    public BigDecimal getOrderTotal() {
        return currentOrder.calculateTotal();
    }

    public BigDecimal getOrderSubtotal() {
        return currentOrder.calculateSubtotal();
    }

    public Receipt getReceipt() {
        return new Receipt(currentOrder);
    }

    public Order getCurrentOrder() {
        return currentOrder;
    }
}
