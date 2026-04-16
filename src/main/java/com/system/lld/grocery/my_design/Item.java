package com.system.lld.grocery.my_design;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Item {

	private Integer id;
	private String name;
	private String code;
	private String category;
	private BigDecimal price;

}
