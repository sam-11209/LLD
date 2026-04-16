package com.system.lld.grocery.my_design;

import java.util.HashMap;
import java.util.Map;

import javax.management.AttributeNotFoundException;

public class Inventory {

	Map<String, Integer> itemStock = new HashMap<>();

	public Integer getInventory(String code) throws AttributeNotFoundException {
		return itemStock.get(code);

	}

	public void addInventory(String code, Integer quanity) {
		Integer availableInventory = itemStock.get(code);
		if (availableInventory == null) {
			itemStock.put(code, quanity);
		} else {
			itemStock.put(code, quanity + availableInventory);
		}
	}

	public void removeItemFromCatalog(String code) {
		itemStock.remove(code);
	}
}
