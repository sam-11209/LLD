package com.system.lld.repo;

import java.util.List;
import java.util.Optional;

import com.system.lld.entity.Cinema;

public interface CinemaRepository {

	Optional<Cinema> findById(String id);

	List<Cinema> findByCity(String city);

	void save(Cinema c);
}
