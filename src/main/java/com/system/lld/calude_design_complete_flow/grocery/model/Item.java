package com.system.lld.calude_design_complete_flow.grocery.model;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Represents an individual product in the grocery store.
 *
 * DESIGN NOTE: Item is intentionally a simple data container (no business logic).
 * It is immutable except for price, which can change due to repricing.
 *
 * MISTAKE IN ORIGINAL DESIGN: The original design didn't handle validation.
 * For example, price should never be negative. We add guard clauses here.
 */
public class Item {

    private final String name;
    private final String barcode;   // unique identifier
    private final String category;
    private BigDecimal price;

    public Item(String name, String barcode, String category, BigDecimal price) {
        // FIX: Validate inputs — original design skipped this entirely
        if (name == null || name.isBlank())
            throw new IllegalArgumentException("Item name cannot be empty");
        if (barcode == null || barcode.isBlank())
            throw new IllegalArgumentException("Barcode cannot be empty");
        if (price == null || price.compareTo(BigDecimal.ZERO) < 0)
            throw new IllegalArgumentException("Price must be non-negative");

        this.name = name;
        this.barcode = barcode;
        this.category = category;
        this.price = price;
    }

    public String getName()     { return name; }
    public String getBarcode()  { return barcode; }
    public String getCategory() { return category; }
    public BigDecimal getPrice(){ return price; }

    public void setPrice(BigDecimal price) {
        if (price == null || price.compareTo(BigDecimal.ZERO) < 0)
            throw new IllegalArgumentException("Price must be non-negative");
        this.price = price;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Item)) return false;
        Item item = (Item) o;
        return Objects.equals(barcode, item.barcode);
    }

    @Override
    public int hashCode() { return Objects.hash(barcode); }

    @Override
    public String toString() {
        return String.format("Item{name='%s', barcode='%s', category='%s', price=%s}",
                name, barcode, category, price);
    }
}
