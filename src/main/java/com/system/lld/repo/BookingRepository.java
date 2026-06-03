package com.system.lld.repo;

import java.util.Optional;

import com.system.lld.entity.Booking;

public interface BookingRepository {

	void save(Booking b);

	Optional<Booking> findById(String id);
}
