package com.system.lld.calude_design_complete_flow.grocery.discount.criteria;

import com.system.lld.calude_design_complete_flow.grocery.model.Item;

/**
 * Applies a discount to ONE specific item identified by its barcode.
 *
 * EXAMPLE:
 *   criteria = new ItemBasedCriteria("B001")
 *   item = new Item("Pepsi", "B001", "Beverages", ...)
 *   criteria.isApplicable(item) → true   ✓ only THIS item gets the discount
 *
 *   item2 = new Item("Sprite", "B002", "Beverages", ...)
 *   criteria.isApplicable(item2) → false  ✗ different barcode
 */
public class ItemBasedCriteria implements DiscountCriteria {

    private final String barcode;

    public ItemBasedCriteria(String barcode) {
        if (barcode == null || barcode.isBlank())
            throw new IllegalArgumentException("Barcode cannot be blank");
        this.barcode = barcode;
    }

    @Override
    public boolean isApplicable(Item item) {
        return barcode.equals(item.getBarcode());
    }

    public String getBarcode() { return barcode; }
}
