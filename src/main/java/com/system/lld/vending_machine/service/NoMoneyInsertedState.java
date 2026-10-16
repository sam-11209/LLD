package com.system.lld.vending_machine.service;

import java.math.BigDecimal;

import com.system.lld.vending_machine.entity.Transaction;
import com.system.lld.vending_machine.exception.VMException;

public class NoMoneyInsertedState implements VendingMachineState {

	@Override
	public void insertMoney(VendingMachine vm, BigDecimal amount) {
		vm.getPaymentProcessor().addMoney(amount);
		vm.getTransaction().setAmountInserted(amount);
		vm.setMachineState(new MoneyInsertedState());

	}

	@Override
	public void selectProduct(VendingMachine vm, String rackCode) {
		throw new VMException(4001, "Please insert the money first then select the product");
	}

	@Override
	public Transaction dispenseProduct(VendingMachine vm) {
		throw new VMException(4002, "Please add money first");
	}

	@Override
	public String getDescription() {
		return "Please insert the money";
	}

}
