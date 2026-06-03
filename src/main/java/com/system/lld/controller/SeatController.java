package com.system.lld.controller;
import java.util.List;

import com.system.lld.controller.service.SeatAvailabilityService;
import com.system.lld.entity.Screening;
import com.system.lld.repo.ScreeningRepository;

final class SeatController {
	
	private final ScreeningRepository screeningRepo;
	private final SeatAvailabilityService availabilityService;

	SeatController(ScreeningRepository r, SeatAvailabilityService a) {
		this.screeningRepo = r;
		this.availabilityService = a;
	}

	// GET /seats/available?screeningId=
	public List<String> getAvailable(String screeningId) {
		Screening s = screeningRepo.findById(screeningId).orElseThrow();
		return availabilityService.getAvailableSeats(s);
	}
}