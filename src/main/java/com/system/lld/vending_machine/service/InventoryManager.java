package com.system.lld.vending_machine.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.system.lld.vending_machine.entity.Product;
import com.system.lld.vending_machine.entity.Rack;
import com.system.lld.vending_machine.exception.VMException;

import lombok.Data;

@Service
@Data
public class InventoryManager {

	Map<String, Rack> rackCodeRackMap = new HashMap<>();
	

	public Rack getRack(String rackCode) {

		Rack rack = rackCodeRackMap.get(rackCode);
		if (rack == null) {
			throw new VMException(4005, "Invalid rack selected");
		}
		return rack;
	}

	public Product getProductFromRack(String rackCode) {

		Rack rack = getRack(rackCode);
		Product product = rack.getProduct();
		if (product == null || rack.getQuantity() == 0) {
			throw new VMException(4006, "Product is not available this time, Please try later");
		}
		return product;
	}

	public void dispenceProdcuctFromRack(String rackCode) {

		Rack rack = getRack(rackCode);
		if (rack.getQuantity() <= 0) {
			throw new VMException(4009, "Product not available can't dispense it");
		}

		rack.setQuantity(rack.getQuantity() - 1);
	}

}
