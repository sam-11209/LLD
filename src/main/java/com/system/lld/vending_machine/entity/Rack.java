package com.system.lld.vending_machine.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Rack {

	private String code;
	private String name;
	private Product product;
	private int quantity;
}
