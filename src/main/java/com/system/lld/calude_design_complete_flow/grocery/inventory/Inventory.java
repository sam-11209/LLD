package com.system.lld.calude_design_complete_flow.grocery.inventory;

import java.util.HashMap;
import java.util.Map;

/**
 * Tracks real-time stock levels for all items.
 *
 * DESIGN NOTE: Inventory is intentionally decoupled from Catalog.
 * Catalog holds WHAT a product is; Inventory tracks HOW MANY are in stock.
 *
 * MISTAKE IN ORIGINAL DESIGN:
 * - reduceStock() could produce negative stock — no guard against overselling.
 *   We fix that with an explicit check.
 * - No way to check if sufficient stock exists before selling — callers had no API for this.
 */
public class Inventory {

    // Key = barcode, Value = quantity in stock
    private final Map<String, Integer> stock = new HashMap<>();

    /**
     * Increases stock for the given barcode.
     */
    public void addStock(String barcode, int count) {
        if (count <= 0)
            throw new IllegalArgumentException("Stock count to add must be positive");
        stock.put(barcode, stock.getOrDefault(barcode, 0) + count);
    }

    /**
     * Decreases stock when items are sold.
     * FIX: Throws InsufficientStockException if stock would go negative.
     */
    public void reduceStock(String barcode, int count) {
        if (count <= 0)
            throw new IllegalArgumentException("Count to reduce must be positive");

        int current = stock.getOrDefault(barcode, 0);
        if (current < count) {
            throw new InsufficientStockException(
                    "Insufficient stock for barcode " + barcode +
                    ". Available: " + current + ", Requested: " + count);
        }
        stock.put(barcode, current - count);
    }

    /**
     * Returns the current stock for the given barcode (0 if never stocked).
     */
    public int getStock(String barcode) {
        return stock.getOrDefault(barcode, 0);
    }

    /**
     * Convenience method — added to fix the gap in original design.
     */
    public boolean hasSufficientStock(String barcode, int required) {
        return getStock(barcode) >= required;
    }

    // ---------- Inner exception class ----------

    public static class InsufficientStockException extends RuntimeException {
        public InsufficientStockException(String message) {
            super(message);
        }
    }
}
