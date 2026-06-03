package com.system.lld.repo;

import java.util.Collection;

import com.system.lld.entity.ScreeningInventoryPessimistic;
import com.system.lld.entity.Seat;

public interface ScreeningInventoryRepository {

	// We avoid this method because If inventory not exist we will not prefer to
	// crate for now
	ScreeningInventoryPessimistic getOrCreate(String screeningId, Collection<Seat> seatsForScreening);

	ScreeningInventoryPessimistic get(String screeningId);

	void create(String screeningId, Collection<Seat> seats);
}
