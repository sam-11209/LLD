package com.system.lld.grocery.my_design;

import lombok.Getter;

@Getter
public class CategoryDiscountCriteria implements DiscountCriteria {

	private String category;

	public CategoryDiscountCriteria(String category) {
		this.category = category;
	}

	@Override
	public boolean isApplicable(Item item) {
		return item.getCategory().equals(category);
	}

}
