package com.system.lld.calude_design_complete_flow.grocery.order;



import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import com.system.lld.calude_design_complete_flow.grocery.discount.DiscountCampaign;

/**
 * Generates a formatted receipt for a completed transaction.
 *
 * DESIGN NOTE: Receipt is purely a presentation layer.
 * All financial logic stays in Order/OrderItem — Receipt only reads from them.
 *
 * MISTAKE IN ORIGINAL DESIGN:
 * - Used java.util.Date (legacy). We use java.time.LocalDateTime instead.
 * - printReceipt() was declared to return a String but had no implementation.
 *   We implement it properly here.
 */
public class Receipt {

    private final String receiptId;
    private final Order order;
    private final LocalDateTime issueDate;

    public Receipt(Order order) {
        this.receiptId = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        this.order = order;
        this.issueDate = LocalDateTime.now();
    }

    /**
     * Builds and returns a human-readable receipt string.
     */
    public String printReceipt() {
        StringBuilder sb = new StringBuilder();
        String separator = "─".repeat(42);

        sb.append("\n").append(separator).append("\n");
        sb.append("         GROCERY STORE RECEIPT\n");
        sb.append(separator).append("\n");
        sb.append(String.format("Receipt ID : %s%n", receiptId));
        sb.append(String.format("Order ID   : %s%n", order.getOrderId().substring(0, 8)));
        sb.append(String.format("Date       : %s%n",
                issueDate.format(DateTimeFormatter.ofPattern("dd-MMM-yyyy HH:mm"))));
        sb.append(separator).append("\n");
        sb.append(String.format("%-20s %6s %8s%n", "Item", "Qty", "Price"));
        sb.append(separator).append("\n");

        for (OrderItem orderItem : order.getItems()) {
            String name = orderItem.getItem().getName();
            int qty = orderItem.getQuantity();
            BigDecimal basePrice = orderItem.calculatePrice();

            DiscountCampaign discount = order.getAppliedDiscounts().get(orderItem);
            if (discount != null) {
                BigDecimal discountedPrice = orderItem.calculatePriceWithDiscount(discount);
                sb.append(String.format("%-20s %6d %8.2f%n", name, qty, discountedPrice));
                sb.append(String.format("  %-18s %6s %8.2f%n",
                        "  [" + discount.getName() + "]", "",
                        discountedPrice.subtract(basePrice))); // negative = savings
            } else {
                sb.append(String.format("%-20s %6d %8.2f%n", name, qty, basePrice));
            }
        }

        sb.append(separator).append("\n");
        sb.append(String.format("%-28s %8.2f%n", "Subtotal (before discounts):",
                order.calculateSubtotal()));
        sb.append(String.format("%-28s %8.2f%n", "Total (after discounts):",
                order.calculateTotal()));
        sb.append(String.format("%-28s %8.2f%n", "Payment:", order.getPaymentAmount()));
        sb.append(String.format("%-28s %8.2f%n", "Change:", order.calculateChange()));
        sb.append(separator).append("\n");
        sb.append("       Thank you for shopping with us!\n");
        sb.append(separator).append("\n");

        return sb.toString();
    }
}
