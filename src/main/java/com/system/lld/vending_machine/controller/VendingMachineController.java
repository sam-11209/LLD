package com.system.lld.vending_machine.controller;

import java.math.BigDecimal;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.system.lld.vending_machine.entity.Product;
import com.system.lld.vending_machine.entity.Rack;
import com.system.lld.vending_machine.entity.Transaction;
import com.system.lld.vending_machine.service.VendingMachine;

import jakarta.annotation.PostConstruct;

@RestController
@RequestMapping("/vm")
public class VendingMachineController {

	@Autowired
	private VendingMachine vendingMachine;
	Map<String, Rack> rackCodeRackMap;

	@PostConstruct
	public void init() {
		rackCodeRackMap = vendingMachine.getInventoryManager().getRackCodeRackMap();

		Product p1 = new Product("P1", "P1-Product", "for health1", new BigDecimal(1));
		Rack r1 = new Rack("R1", "R1-Rack", p1, 4);

		Product p2 = new Product("P2", "P2-Product", "for health2", new BigDecimal(2));
		Rack r2 = new Rack("R2", "R2-Rack", p2, 3);

		Product p3 = new Product("P3", "P3-Product", "for health3", new BigDecimal(3));
		Rack r3 = new Rack("R3", "R3-Rack", p3, 2);

		Product p4 = new Product("P4", "P4-Product", "for health4", new BigDecimal(4));
		Rack r4 = new Rack("R4", "R4-Rack", p4, 1);

		rackCodeRackMap.put("R1", r1);
		rackCodeRackMap.put("R2", r2);
		rackCodeRackMap.put("R3", r3);
		rackCodeRackMap.put("R4", r4);

		vendingMachine.getInventoryManager().setRackCodeRackMap(rackCodeRackMap);

	}

	@PostMapping("/add-money/{amount}")
	public String addMoney(@PathVariable BigDecimal amount) {
		vendingMachine.addMoney(amount);
		return "Amount " + amount + " added into VM";
	}

	@GetMapping("/products")
	public Map<String, Rack> products() {
		return rackCodeRackMap;
	}

	@PostMapping("/select-product/{rackCode}")
	public String selectProduct(@PathVariable String rackCode) {
		vendingMachine.selectProduct(rackCode);
		return "Product selected from the rack " + rackCode;
	}

	@PostMapping("/dispense")
	public Transaction dispenseProduct() {
		return vendingMachine.dispenseProduct();
	}

	@PostMapping("/cancel")
	public String cancelTxn() {
		BigDecimal returnAmount = vendingMachine.cancelTxn();
		return "Transaction is cancelled please collect the returned Amount : " + returnAmount;
	}

}
