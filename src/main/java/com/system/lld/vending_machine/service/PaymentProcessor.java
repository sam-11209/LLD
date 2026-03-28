package com.system.lld.vending_machine.service;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;

import com.system.lld.vending_machine.exception.VMException;

import lombok.Getter;

@Service
@Getter
public class PaymentProcessor {

	// BigDecimal is immutable
	private BigDecimal amount = BigDecimal.ZERO;

	public void addMoney(BigDecimal insertedAmount) {
		if (insertedAmount == null || insertedAmount.compareTo(BigDecimal.ZERO) < 0) {
			throw new VMException(4010, "Amount must be positive");
		}
		// BigDecimal is immutable so assign the value to new variable just like string
		amount = amount.add(insertedAmount);
	}

	public void charge(BigDecimal productPrice) {
		sufficientCheck(productPrice);
		amount = amount.subtract(productPrice).setScale(2);
	}

	private void sufficientCheck(BigDecimal productPrice) {
		if (amount.compareTo(productPrice) < 0) {
			throw new VMException(4011, "Need " + productPrice.toPlainString() + ", have " + amount.toPlainString());
		}
	}

	public BigDecimal returnchange() {
		BigDecimal curAmount = amount;
		amount = BigDecimal.ZERO;
		return curAmount;
	}
}
