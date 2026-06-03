package com.system.lld.repo;

import java.util.Optional;

import com.system.lld.entity.Order;

public interface OrderRepository {

	void save(Order o);

	Optional<Order> findById(String id);
}
