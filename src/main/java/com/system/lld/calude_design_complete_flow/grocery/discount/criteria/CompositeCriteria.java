package com.system.lld.calude_design_complete_flow.grocery.discount.criteria;



import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.system.lld.calude_design_complete_flow.grocery.model.Item;

/**
 * Composite Pattern — combines multiple DiscountCriteria with AND logic.
 *
 * WHY THIS IS NEEDED:
 * The original design could only handle simple single-rule criteria.
 * Real promotions often require combined rules. For example:
 *   "Apply 10% off if the item is in the Beverages category AND its price > $5"
 *
 * EXAMPLE:
 *   CompositeCriteria criteria = new CompositeCriteria(
 *       new CategoryBasedCriteria("Beverages"),
 *       new MinPriceCriteria(new BigDecimal("5.00"))
 *   );
 *   // Both must be true for the discount to apply
 *
 * COMPOSITE PATTERN EXPLAINED:
 * ─────────────────────────────
 *  DiscountCriteria (interface)
 *       ├── CategoryBasedCriteria   (leaf)
 *       ├── ItemBasedCriteria       (leaf)
 *       └── CompositeCriteria       (composite — holds a list of DiscountCriteria)
 *
 * Because CompositeCriteria itself implements DiscountCriteria, you can even
 * NEST composites inside composites for deeply complex rules.
 */
public class CompositeCriteria implements DiscountCriteria {

    private final List<DiscountCriteria> criteriaList;

    public CompositeCriteria(DiscountCriteria... criteria) {
        this.criteriaList = new ArrayList<>(Arrays.asList(criteria));
    }

    public CompositeCriteria(List<DiscountCriteria> criteriaList) {
        this.criteriaList = new ArrayList<>(criteriaList);
    }

    /**
     * Returns true only if ALL criteria pass (AND logic).
     */
    @Override
    public boolean isApplicable(Item item) {
        return criteriaList.stream().allMatch(c -> c.isApplicable(item));
    }

    public void addCriteria(DiscountCriteria criteria) {
        criteriaList.add(criteria);
    }

    public void removeCriteria(DiscountCriteria criteria) {
        criteriaList.remove(criteria);
    }
}
