package com.system.lld.grocery.my_design;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DiscountCampaign {

	private Integer id;
	private String name;
	private DiscountCriteria discountCriteria;
	private DiscountCalculationStrategy discountCalculationStrategy;

	// Creates a new discount campaign with the specified details
	public DiscountCampaign(Integer discountId, String name, DiscountCriteria criteria,
			DiscountCalculationStrategy calculationStrategy) {
		this.id = discountId;
		this.name = name;
		this.discountCriteria = criteria;
		this.discountCalculationStrategy = calculationStrategy;
	}

	public boolean isApplicable(Item item) {
		return discountCriteria.isApplicable(item);
	}

	public BigDecimal applyDiscount(BigDecimal price) throws Throwable {
		return discountCalculationStrategy.applyDiscount(price);
	}
}
