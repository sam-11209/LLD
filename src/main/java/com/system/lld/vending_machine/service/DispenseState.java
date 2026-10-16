package com.system.lld.vending_machine.service;

import java.math.BigDecimal;

import com.system.lld.vending_machine.entity.Transaction;
import com.system.lld.vending_machine.exception.VMException;

public class DispenseState implements VendingMachineState {

	@Override
	public void insertMoney(VendingMachine vm, BigDecimal amount) {
		throw new VMException(4007, "Can't insert the money in between the transcation");
	}

	@Override
	public void selectProduct(VendingMachine vm, String rackCode) {
		throw new VMException(4008, "Can't select a product in between the transcation");

	}

	@Override
	public Transaction dispenseProduct(VendingMachine vm) {
		return vm.dispenseProductAndReset();
	}

	@Override
	public String getDescription() {
		return "Please collect the product and change";
	}

}
