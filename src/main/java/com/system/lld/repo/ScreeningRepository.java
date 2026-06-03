package com.system.lld.repo;

import java.util.List;
import java.util.Optional;

import com.system.lld.entity.Screening;

public interface ScreeningRepository {

	Optional<Screening> findById(String id);

	List<Screening> findByMovieInCity(String movieId, String city);

	void save(Screening s);
}
