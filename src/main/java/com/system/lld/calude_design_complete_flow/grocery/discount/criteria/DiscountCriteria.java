package com.system.lld.calude_design_complete_flow.grocery.discount.criteria;

import com.system.lld.calude_design_complete_flow.grocery.model.Item;

/**
 * Strategy interface for determining whether a discount applies to an item.
 *
 * HOW IT FITS IN THE DISCOUNT FLOW:
 * ──────────────────────────────────
 * When a cashier scans an item, the Checkout class loops through all
 * active DiscountCampaigns. For each campaign, it calls:
 *
 *     campaign.isApplicable(item)
 *       └─→ delegates to: criteria.isApplicable(item)
 *
 * If this returns true, the discount is eligible to be applied.
 *
 * This interface allows plugging in ANY rule without changing DiscountCampaign.
 */
public interface DiscountCriteria {
    boolean isApplicable(Item item);
}
