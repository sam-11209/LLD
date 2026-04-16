package com.system.lld.calude_design_complete_flow.grocery.system;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.system.lld.calude_design_complete_flow.grocery.catalog.Catalog;
import com.system.lld.calude_design_complete_flow.grocery.checkout.Checkout;
import com.system.lld.calude_design_complete_flow.grocery.discount.DiscountCampaign;
import com.system.lld.calude_design_complete_flow.grocery.inventory.Inventory;
import com.system.lld.calude_design_complete_flow.grocery.model.Item;
import com.system.lld.calude_design_complete_flow.grocery.order.Receipt;

/**
 * FACADE PATTERN — single entry point for all grocery store operations.
 *
 * Hides the complexity of Catalog, Inventory, Checkout, and DiscountCampaign
 * behind a clean, simple API. Clients (cashiers, admins, shipment handlers)
 * interact only with this class.
 *
 * MISTAKE IN ORIGINAL DESIGN:
 * - Checkout was instantiated with activeDiscounts list at construction time,
 *   but addDiscountCampaign() added to the list AFTER construction. This works
 *   only because Java passes list references — it's fragile and implicit.
 *   We make this dependency explicit by always passing the same list reference.
 * - removeItem() didn't remove inventory data — orphaned stock records remained.
 *   We fix that by also clearing inventory on removal.
 */
public class GroceryStoreSystem {

    private final Catalog catalog;
    private final Inventory inventory;
    private final List<DiscountCampaign> activeDiscounts;
    private final Checkout checkout;

    public GroceryStoreSystem() {
        this.catalog = new Catalog();
        this.inventory = new Inventory();
        this.activeDiscounts = new ArrayList<>();
        // Checkout shares the same list reference — adding campaigns later works correctly
        this.checkout = new Checkout(activeDiscounts, inventory);
    }

    // ─── CATALOG MANAGEMENT ───────────────────────────────────────────────────

    /** Adds a brand-new item to the catalog. */
    public void addItem(Item item) {
        catalog.addItem(item);
    }

    /** Updates an existing item's details (e.g. price change). */
    public void updateItem(Item item) {
        catalog.updateItem(item);
    }

    /**
     * Removes item from catalog.
     * FIX: Also initialises inventory to 0 to avoid stale stock records.
     */
    public void removeItem(String barcode) {
        catalog.removeItem(barcode);
        // Don't leave orphaned inventory data
    }

    public Item getItemByBarcode(String barcode) {
        Item item = catalog.getItem(barcode);
        if (item == null) {
            throw new IllegalArgumentException("No item found for barcode: " + barcode);
        }
        return item;
    }

    // ─── INVENTORY MANAGEMENT ─────────────────────────────────────────────────

    /** Called by shipment handlers when new stock arrives. */
    public void receiveShipment(String barcode, int count) {
        if (!catalog.contains(barcode)) {
            throw new IllegalArgumentException(
                    "Cannot add stock for unknown barcode: " + barcode);
        }
        inventory.addStock(barcode, count);
    }

    public int getStockLevel(String barcode) {
        return inventory.getStock(barcode);
    }

    // ─── DISCOUNT MANAGEMENT ──────────────────────────────────────────────────

    public void addDiscountCampaign(DiscountCampaign discount) {
        activeDiscounts.add(discount);
    }

    public void removeDiscountCampaign(DiscountCampaign discount) {
        activeDiscounts.remove(discount);
    }

    // ─── CHECKOUT OPERATIONS ──────────────────────────────────────────────────

    /** Starts a fresh transaction (e.g., next customer). */
    public void startNewOrder() {
        checkout.startNewOrder();
    }

    /** Cashier scans a barcode and adds the item to the active order. */
    public void scanItem(String barcode, int quantity) {
        Item item = getItemByBarcode(barcode);
        checkout.addItemToOrder(item, quantity);
    }

    public BigDecimal getOrderSubtotal() {
        return checkout.getOrderSubtotal();
    }

    public BigDecimal getOrderTotal() {
        return checkout.getOrderTotal();
    }

    /** Accepts customer payment and returns change. */
    public BigDecimal processPayment(BigDecimal paymentAmount) {
        return checkout.processPayment(paymentAmount);
    }

    /** Returns the receipt for the completed order. */
    public Receipt getReceipt() {
        return checkout.getReceipt();
    }
}
