package com.system.lld.repo;

import java.util.List;
import java.util.Optional;

import com.system.lld.entity.Movie;

public interface MovieRepository {

	Optional<Movie> findById(String id);

	List<Movie> findByCity(String city); // convenience for search

	void save(Movie m);
}
