package com.system.lld.entity;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.system.lld.exception.NotFoundException;

@Component
public class DiscountFactory {
	private final Map<String, DiscountStrategy> strategies;

	public DiscountFactory(Map<String, DiscountStrategy> strategies) {
		this.strategies = strategies;
	}

	public DiscountStrategy getStrategy(String code) {

		DiscountStrategy discountStrategy = strategies.get(code);
		if (discountStrategy == null) {
			throw new NotFoundException("404", "Invalid coupan code :" + code, null);
		}
		return discountStrategy;
	}
}
