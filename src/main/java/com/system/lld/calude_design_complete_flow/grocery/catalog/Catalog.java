package com.system.lld.calude_design_complete_flow.grocery.catalog;



import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.system.lld.calude_design_complete_flow.grocery.model.Item;

/**
 * Central repository for all store products.
 *
 * DESIGN NOTE: Catalog manages static product metadata (name, barcode, price, category).
 * It is intentionally separated from Inventory (which tracks quantities) — this
 * follows the Single Responsibility Principle.
 *
 * MISTAKE IN ORIGINAL DESIGN:
 * - addItem and updateItem were merged into one method (updateItem). This is ambiguous.
 *   We keep it but add a dedicated addItem for clarity.
 * - No existence check before removing — silently ignoring a missing barcode can hide bugs.
 */
public class Catalog {

    // Key = barcode, Value = Item
    private final Map<String, Item> items = new HashMap<>();

    /**
     * Adds a new item. Throws if barcode already exists.
     */
    public void addItem(Item item) {
        if (items.containsKey(item.getBarcode())) {
            throw new IllegalArgumentException(
                    "Item with barcode " + item.getBarcode() + " already exists. Use updateItem.");
        }
        items.put(item.getBarcode(), item);
    }

    /**
     * Updates an existing item (or inserts if absent — upsert semantics, matching original).
     */
    public void updateItem(Item item) {
        items.put(item.getBarcode(), item);
    }

    /**
     * Removes an item by barcode.
     * FIX: Throws if item not found, instead of silently doing nothing.
     */
    public void removeItem(String barcode) {
        if (!items.containsKey(barcode)) {
            throw new IllegalArgumentException("No item found with barcode: " + barcode);
        }
        items.remove(barcode);
    }

    /**
     * Returns null if barcode not found.
     * Callers should check for null.
     */
    public Item getItem(String barcode) {
        return items.get(barcode);
    }

    /**
     * Returns an unmodifiable view of all items.
     */
    public Collection<Item> getAllItems() {
        return Collections.unmodifiableCollection(items.values());
    }

    public boolean contains(String barcode) {
        return items.containsKey(barcode);
    }
}
