package com.system.lld.grocery.my_design;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import lombok.Getter;

@Getter
public class Order {

	private String orderId;
	private List<OrderItem> orderItems;
	private List<DiscountCampaign> discountCampaigns;
	private LocalDateTime date;
	
	//We are putting here so that we do not need to create payment entity else we have to create it in real scenarios
	private BigDecimal PaymentAmount;

	public Order(List<DiscountCampaign> campaigns) {
		this.discountCampaigns = campaigns;
		this.orderId = UUID.randomUUID().toString().substring(6);
		this.date = LocalDateTime.now();
	}

	public void addItem(OrderItem orderItem) {
		if (orderItems == null) {
			orderItems = new ArrayList<>();
		}
		orderItems.add(orderItem);
	}

	public BigDecimal calculatePriceWithoutDiscount() {
		BigDecimal totalPrice = BigDecimal.ZERO;
		for (OrderItem oi : orderItems) {
			totalPrice = totalPrice.add(oi.getTotalPrice());
		}
		return totalPrice;
	}

	public BigDecimal calculatePriceWithBestDiscount() throws Throwable {
		BigDecimal totalPriceAfterDiscount = BigDecimal.ZERO;

		for (OrderItem oi : orderItems) {
			BigDecimal bestPrice = oi.getTotalPrice();

			for (DiscountCampaign discountCampaign : discountCampaigns) {
				if (discountCampaign.isApplicable(oi.getItem())) {
					BigDecimal discountedPrice = discountCampaign.applyDiscount(oi.getTotalPrice());
					if (discountedPrice.compareTo(bestPrice) < 0) {
						bestPrice = discountedPrice;
					}
				}
			}
			totalPriceAfterDiscount.add(bestPrice);
		}
		return totalPriceAfterDiscount;
	}

}
