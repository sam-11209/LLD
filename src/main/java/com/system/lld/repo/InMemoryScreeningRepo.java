package com.system.lld.repo;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import com.system.lld.entity.Screening;

final class InMemoryScreeningRepo implements ScreeningRepository {
	private final Map<String, Screening> store = new ConcurrentHashMap<>();

	@Override
	public Optional<Screening> findById(String id) {
		return Optional.ofNullable(store.get(id));
	}

	@Override
	public List<Screening> findByMovieInCity(String movieId, String city) {
		return store.values().stream()
				.filter(s -> s.getMovie().getId().equals(movieId) && s.getCinema().getCity().equalsIgnoreCase(city))
				.sorted(Comparator.comparing(Screening::getStartTime)).toList();
	}

	@Override
	public void save(Screening s) {
		store.put(s.getId(), s);
	}
}
