package com.system.lld.controller.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.system.lld.entity.Screening;
import com.system.lld.repo.ScreeningRepository;

@Service
public class SearchService {

	private final ScreeningRepository screeningRepo;

	SearchService(ScreeningRepository screeningRepo) {
		this.screeningRepo = screeningRepo;
	}

	public List<Screening> findScreenings(String movieId, String city) {
		return screeningRepo.findByMovieInCity(movieId, city);
	}
}
