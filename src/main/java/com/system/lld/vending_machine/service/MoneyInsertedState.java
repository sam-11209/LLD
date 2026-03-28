package com.system.lld.vending_machine.service;

import java.math.BigDecimal;

import com.system.lld.vending_machine.entity.Product;
import com.system.lld.vending_machine.entity.Transaction;
import com.system.lld.vending_machine.exception.VMException;

public class MoneyInsertedState implements VendingMachineState {

	@Override
	public void insertMoney(VendingMachine vm, BigDecimal amount) {
		throw new VMException(4003, "You can not add money in between the process");
	}

	@Override
	public void selectProduct(VendingMachine vm, String rackCode) {

		// Product Selection, Availability verification, Product Price validation , TXN
		// modified, State change
		Product product = vm.getInventoryManager().getProductFromRack(rackCode);
		BigDecimal price = product.getPrice();
		if (vm.getPaymentProcessor().getAmount().compareTo(price) < 0) {
			throw new VMException(4006, "Product price is more than the money inserted");
		}
		vm.getTransaction().setRackCode(rackCode);
		vm.getTransaction().setProduct(product);
		vm.getTransaction().setProductPrice(price);

		vm.setMachineState(new DispenseState());
	}

	@Override
	public Transaction dispenseProduct(VendingMachine vm) {
		throw new VMException(4004, "Please select a product first");
	}

	@Override
	public String getDescription() {
		return "Please select a product";
	}

}
