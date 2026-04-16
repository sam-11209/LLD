package com.system.lld.calude_design_complete_flow.grocery;

import java.math.BigDecimal;

import com.system.lld.calude_design_complete_flow.grocery.discount.DiscountCampaign;
import com.system.lld.calude_design_complete_flow.grocery.discount.criteria.CategoryBasedCriteria;
import com.system.lld.calude_design_complete_flow.grocery.discount.criteria.CompositeCriteria;
import com.system.lld.calude_design_complete_flow.grocery.discount.criteria.ItemBasedCriteria;
import com.system.lld.calude_design_complete_flow.grocery.discount.criteria.MinPriceCriteria;
import com.system.lld.calude_design_complete_flow.grocery.discount.decorator.FixedDiscountDecorator;
import com.system.lld.calude_design_complete_flow.grocery.discount.decorator.NoDiscountStrategy;
import com.system.lld.calude_design_complete_flow.grocery.discount.decorator.PercentageDiscountDecorator;
import com.system.lld.calude_design_complete_flow.grocery.discount.strategy.AmountBasedStrategy;
import com.system.lld.calude_design_complete_flow.grocery.discount.strategy.DiscountCalculationStrategy;
import com.system.lld.calude_design_complete_flow.grocery.discount.strategy.PercentageBasedStrategy;
import com.system.lld.calude_design_complete_flow.grocery.model.Item;
import com.system.lld.calude_design_complete_flow.grocery.system.GroceryStoreSystem;

/**
 * ══════════════════════════════════════════════════════════════════════
 *  DEMO: Shows all 3 discount scenarios in action
 *
 *  Scenario 1 — Simple category-based percentage discount
 *  Scenario 2 — Item-specific fixed amount discount
 *  Scenario 3 — DECORATOR: stacked fixed + percentage discount
 *  Scenario 4 — COMPOSITE CRITERIA: category AND min-price combined
 * ══════════════════════════════════════════════════════════════════════
 */
public class Main {

    public static void main(String[] args) {

        // ── Setup the store ──────────────────────────────────────────────────
        GroceryStoreSystem store = new GroceryStoreSystem();

        // Add items to catalog
        Item pepsi    = new Item("Pepsi 500ml",     "BEV001", "Beverages", new BigDecimal("12.00"));
        Item sprite   = new Item("Sprite 500ml",    "BEV002", "Beverages", new BigDecimal("10.00"));
        Item bread    = new Item("Whole Grain Bread","FOD001", "Food",      new BigDecimal("5.00"));
        Item chocolate= new Item("Dark Chocolate",  "FOD002", "Food",      new BigDecimal("8.00"));

        store.addItem(pepsi);
        store.addItem(sprite);
        store.addItem(bread);
        store.addItem(chocolate);

        // Add stock
        store.receiveShipment("BEV001", 50);
        store.receiveShipment("BEV002", 30);
        store.receiveShipment("FOD001", 100);
        store.receiveShipment("FOD002", 40);

        System.out.println("======================================================");
        System.out.println("  SCENARIO 1: Simple 10% off ALL Beverages (category)");
        System.out.println("======================================================");

        // Campaign: 10% off all beverages
        DiscountCampaign beverageDiscount = new DiscountCampaign(
                "CAMP001",
                "10% Off Beverages",
                new CategoryBasedCriteria("Beverages"),
                new PercentageBasedStrategy(new BigDecimal("10"))
        );
        store.addDiscountCampaign(beverageDiscount);

        store.startNewOrder();
        store.scanItem("BEV001", 2);  // 2x Pepsi @ $12 = $24 → 10% off → $21.60
        store.scanItem("FOD001", 1);  // 1x Bread @ $5  → no discount

        BigDecimal change = store.processPayment(new BigDecimal("30.00"));
        System.out.println(store.getReceipt().printReceipt());

        // ─────────────────────────────────────────────────────────────────────
        System.out.println("======================================================");
        System.out.println("  SCENARIO 2: $3 off a specific item (ItemBasedCriteria)");
        System.out.println("  + best-discount selection when BOTH campaigns apply");
        System.out.println("======================================================");

        // Campaign: $3 off specifically Pepsi
        DiscountCampaign pepsiDeal = new DiscountCampaign(
                "CAMP002",
                "$3 Off Pepsi",
                new ItemBasedCriteria("BEV001"),
                new AmountBasedStrategy(new BigDecimal("3.00"))
        );
        store.addDiscountCampaign(pepsiDeal);
        // Now both CAMP001 (10% off beverages) and CAMP002 ($3 off Pepsi) apply to Pepsi.
        // For 1x Pepsi @ $12:
        //   CAMP001: 10% off → $10.80
        //   CAMP002: -$3    → $9.00  ← BEST (lower final price)
        // System should auto-select CAMP002.

        store.startNewOrder();
        store.scanItem("BEV001", 1);  // 1x Pepsi — both campaigns apply, $3 off wins
        store.scanItem("BEV002", 1);  // 1x Sprite — only 10% beverage discount applies

        store.processPayment(new BigDecimal("25.00"));
        System.out.println(store.getReceipt().printReceipt());

        // ─────────────────────────────────────────────────────────────────────
        System.out.println("======================================================");
        System.out.println("  SCENARIO 3: DECORATOR PATTERN");
        System.out.println("  'Weekend Mega Deal' — $2 off THEN 15% off (stacked)");
        System.out.println("======================================================");

        /*
         * DECORATOR CHAIN EXPLAINED:
         *
         *   PercentageDiscountDecorator(15%)
         *       └── wraps FixedDiscountDecorator($2)
         *                   └── wraps NoDiscountStrategy (identity/base)
         *
         * For Sprite @ $10:
         *   Step 1 — NoDiscountStrategy:         $10.00
         *   Step 2 — FixedDiscountDecorator:     $10.00 - $2.00 = $8.00
         *   Step 3 — PercentageDiscountDecorator: $8.00 * 0.85  = $6.80
         */
        DiscountCalculationStrategy weekendStrategy =
                new PercentageDiscountDecorator(
                        new FixedDiscountDecorator(
                                new NoDiscountStrategy(),
                                new BigDecimal("2.00")   // inner: subtract $2 first
                        ),
                        new BigDecimal("15")             // outer: then 15% off
                );

        DiscountCampaign weekendDeal = new DiscountCampaign(
                "CAMP003",
                "Weekend Mega Deal (-$2 then -15%)",
                new ItemBasedCriteria("BEV002"),         // only Sprite
                weekendStrategy
        );

        // Temporarily remove other campaigns so CAMP003 is the only one for this demo
        store.removeDiscountCampaign(beverageDiscount);
        store.removeDiscountCampaign(pepsiDeal);
        store.addDiscountCampaign(weekendDeal);

        store.startNewOrder();
        store.scanItem("BEV002", 1);  // Sprite $10 → -$2 → $8 → -15% → $6.80

        store.processPayment(new BigDecimal("10.00"));
        System.out.println(store.getReceipt().printReceipt());

        // ─────────────────────────────────────────────────────────────────────
        System.out.println("======================================================");
        System.out.println("  SCENARIO 4: COMPOSITE CRITERIA");
        System.out.println("  '20% off Food items priced >= $7'");
        System.out.println("======================================================");

        /*
         * CompositeCriteria combines TWO rules with AND logic:
         *   - Must be in "Food" category
         *   - Must cost at least $7.00
         *
         * Chocolate @ $8 → qualifies (Food AND >= $7)
         * Bread      @ $5 → does NOT qualify (Food but < $7)
         */
        DiscountCampaign premiumFoodDiscount = new DiscountCampaign(
                "CAMP004",
                "20% Off Premium Food",
                new CompositeCriteria(
                        new CategoryBasedCriteria("Food"),
                        new MinPriceCriteria(new BigDecimal("7.00"))
                ),
                new PercentageBasedStrategy(new BigDecimal("20"))
        );

        store.removeDiscountCampaign(weekendDeal);
        store.addDiscountCampaign(premiumFoodDiscount);

        store.startNewOrder();
        store.scanItem("FOD002", 2);  // 2x Chocolate @ $8 = $16 → 20% off → $12.80
        store.scanItem("FOD001", 1);  // 1x Bread @ $5 → no discount (below $7)

        store.processPayment(new BigDecimal("20.00"));
        System.out.println(store.getReceipt().printReceipt());

        System.out.println("\nAll scenarios completed successfully.");
    }
}
