package com.system.lld.controller;

import java.time.LocalDateTime;
import java.util.List;

import com.system.lld.controller.Dtos.ScreeningDto;
import com.system.lld.controller.service.ScreeningService;
import com.system.lld.controller.service.SearchService;
import com.system.lld.entity.Cinema;
import com.system.lld.entity.Movie;
import com.system.lld.entity.Room;
import com.system.lld.entity.Screening;



final class MovieController {

	private final ScreeningService screeningService;
	private final SearchService searchService;

	MovieController(SearchService s, ScreeningService screeningService) {
		this.searchService = s;
		this.screeningService = screeningService;
	}

	// POST /add/screening
	public Screening createScreening(Cinema cinema, Movie movie, Room room, LocalDateTime startTime,
			LocalDateTime endTime) {
		return screeningService.addScreening(cinema, movie, room, startTime, endTime);
	}

	// GET /screenings?movieId=&city=
	public List<ScreeningDto> getScreenings(String movieId, String city) {
		return searchService.findScreenings(movieId, city).stream().map(ScreeningDto::from).toList();
	}

}
