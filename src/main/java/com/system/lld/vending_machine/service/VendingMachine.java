package com.system.lld.vending_machine.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.system.lld.vending_machine.entity.Transaction;
import com.system.lld.vending_machine.entity.TransactionStatus;

import lombok.Getter;

@Component
@Getter
public class VendingMachine {

	private PaymentProcessor paymentProcessor;
	private InventoryManager inventoryManager;
	private Transaction transaction = new Transaction();
	private List<Transaction> transactionHistory = new ArrayList<>();
	private VendingMachineState machineState = new NoMoneyInsertedState();

	public VendingMachine() {
		this.inventoryManager = new InventoryManager();
		this.paymentProcessor = new PaymentProcessor();
	}

	public void setMachineState(VendingMachineState machineState) {
		this.machineState = machineState;
	}

	// EXPOSE TO PUBLIC
	public void addMoney(BigDecimal amount) {
		machineState.insertMoney(this, amount);
	}

	// EXPOSE TO PUBLIC
	public void selectProduct(String rackCode) {
		machineState.selectProduct(this, rackCode);
	}

	// EXPOSE TO PUBLIC
	public Transaction dispenseProduct() {
		return machineState.dispenseProduct(this);
	}

	public Transaction dispenseProductAndReset() {

		// Decrement Product Count from Inventory
		// Calculate Amount and return change
		// States need to be reset as new again
		// transaction need to be added in txnHistory list

		String rackCode = transaction.getRackCode();
		inventoryManager.dispenceProdcuctFromRack(rackCode);
		paymentProcessor.charge(transaction.getProductPrice());
		BigDecimal returnchange = paymentProcessor.returnchange();
		transaction.setChange(returnchange);
		transaction.setTxnStatus(TransactionStatus.COMPLETED.toString());
		transaction.setTxnDate(LocalDateTime.now());
		transactionHistory.add(transaction);

		Transaction completedTxn = transaction; // save reference first
		// Reset Transaction and State
		transaction = new Transaction();
		machineState = new NoMoneyInsertedState();

		return completedTxn;
	}

	public BigDecimal cancelTxn() {

		BigDecimal returnchange = paymentProcessor.returnchange();
		transaction.setChange(returnchange);
		transaction.setTxnStatus(TransactionStatus.CANCELLED.toString());
		transaction.setTxnDate(LocalDateTime.now());
		transactionHistory.add(transaction);

		Transaction cancelledTxn = transaction; // save reference first if you want to return everything then return
												// this

		// Reset Transaction and State
		transaction = new Transaction();
		machineState = new NoMoneyInsertedState();
		return returnchange;
	}

}
