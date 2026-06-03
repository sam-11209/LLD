package com.system.lld.entity;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Ticket {

	private String id;
	private String screeningId;
	private String seatNumber;
	private BigDecimal priceSnapshot; // immutable at purchase time
}
