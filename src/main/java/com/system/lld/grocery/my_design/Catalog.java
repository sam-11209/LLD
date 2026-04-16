package com.system.lld.grocery.my_design;

import java.util.HashMap;
import java.util.Map;

import javax.management.AttributeNotFoundException;

public class Catalog {

	private Map<String, Item> items = new HashMap<>();

	public Item getCatalogItem(String code) throws AttributeNotFoundException {
		Item item = items.get(code);
		if (item == null) {
			throw new AttributeNotFoundException("Item Not found");
		}
		return item;
	}

	public void addItemInCatalog(Item item) {
		items.put(item.getCode(), item);
	}

	public void removeItemFromCatalog(String code) {
		items.remove(code);
	}

}
