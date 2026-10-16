package com.system.lld.vending_machine.service;

import java.math.BigDecimal;

import com.system.lld.vending_machine.entity.Transaction;

public interface VendingMachineState {

	void insertMoney(VendingMachine vm, BigDecimal amount);

	void selectProduct(VendingMachine vm, String rackCode);

	Transaction dispenseProduct(VendingMachine vm);

	String getDescription();

}
