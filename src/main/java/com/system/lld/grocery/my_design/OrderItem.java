package com.system.lld.grocery.my_design;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class OrderItem {

	private Item item;
	private int quantity;

	public BigDecimal getTotalPrice() {
		return item.getPrice().multiply(BigDecimal.valueOf(quantity));
	}
}
