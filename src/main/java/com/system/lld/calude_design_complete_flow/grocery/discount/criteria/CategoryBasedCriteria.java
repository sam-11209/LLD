package com.system.lld.calude_design_complete_flow.grocery.discount.criteria;

import com.system.lld.calude_design_complete_flow.grocery.model.Item;

/**
 * Applies a discount to ALL items in a specific category.
 *
 * EXAMPLE:
 *   criteria = new CategoryBasedCriteria("Beverages")
 *   item = new Item("Pepsi", "B001", "Beverages", ...)
 *   criteria.isApplicable(item) → true   ✓ discount applies
 *
 *   item2 = new Item("Bread", "F001", "Food", ...)
 *   criteria.isApplicable(item2) → false  ✗ discount does NOT apply
 */
public class CategoryBasedCriteria implements DiscountCriteria {

    private final String category;

    public CategoryBasedCriteria(String category) {
        if (category == null || category.isBlank())
            throw new IllegalArgumentException("Category cannot be blank");
        this.category = category;
    }

    @Override
    public boolean isApplicable(Item item) {
        return category.equalsIgnoreCase(item.getCategory());
    }

    public String getCategory() { return category; }
}
