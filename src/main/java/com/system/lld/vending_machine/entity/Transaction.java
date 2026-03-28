package com.system.lld.vending_machine.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import lombok.Data;

@Data
public class Transaction {

	private String txnId = UUID.randomUUID().toString();
	private String rackCode;
	private Product product;
	private String txnStatus = TransactionStatus.CREATED.toString();
	private LocalDateTime txnDate;
	private BigDecimal productPrice;
	private BigDecimal amountInserted;
	private BigDecimal change;
}
