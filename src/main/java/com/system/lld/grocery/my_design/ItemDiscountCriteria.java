package com.system.lld.grocery.my_design;

import lombok.Getter;

@Getter
public class ItemDiscountCriteria implements DiscountCriteria {

	private String itemCode;

	public ItemDiscountCriteria(String itemCode) {
		this.itemCode = itemCode;
	}

	@Override
	public boolean isApplicable(Item item) {
		return item.getCode().equals(itemCode);
	}

}
