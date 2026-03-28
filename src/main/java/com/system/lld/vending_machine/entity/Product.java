package com.system.lld.vending_machine.entity;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Product {

	private String code;
	private String name;
	private String desc;
	private BigDecimal price;
}
