package com.system.lld.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Order {

	private String id;
	private String userId;
	private List<Ticket> tickets = new ArrayList<>();
	private LocalDateTime createdAt = LocalDateTime.now();
	// addTicket(), totalPrice()

	public Order(String id, String userId) {
		this.id = id;
		this.userId = userId;
	}

	// ✅ Add a ticket to the order
	public void addTicket(Ticket ticket) {
		if (ticket != null) {
			tickets.add(ticket);
		}
	}

	// ✅ Calculate total price of all tickets in this order
	public BigDecimal totalPrice() {
		return tickets.stream().map(Ticket::getPriceSnapshot) // Stream<BigDecimal>
				.reduce(BigDecimal.ZERO, BigDecimal::add);
	}

	// Getters
	public String getId() {
		return id;
	}

	public String getUserId() {
		return userId;
	}

	public List<Ticket> getTickets() {
		return tickets;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}
}
