package com.system.lld.repo;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.system.lld.entity.ScreeningInventoryPessimistic;
import com.system.lld.entity.Seat;

final class InMemoryInventoryRepo implements ScreeningInventoryRepository {

	private final Map<String, ScreeningInventoryPessimistic> store = new ConcurrentHashMap<>();

	@Override
	public ScreeningInventoryPessimistic getOrCreate(String screeningId, Collection<Seat> seats) {
		return store.computeIfAbsent(screeningId, k -> new ScreeningInventoryPessimistic(seats));
	}

	@Override
	public ScreeningInventoryPessimistic get(String screeningId) {
		return store.get(screeningId);
	}

	@Override
	public void create(String screeningId, Collection<Seat> seats) {
		store.putIfAbsent(screeningId, new ScreeningInventoryPessimistic(seats));
	}
}
