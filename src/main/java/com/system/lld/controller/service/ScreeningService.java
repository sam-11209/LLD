package com.system.lld.controller.service;

import java.time.LocalDateTime;
import java.util.UUID;

import com.system.lld.entity.Cinema;
import com.system.lld.entity.Movie;
import com.system.lld.entity.Room;
import com.system.lld.entity.Screening;
import com.system.lld.repo.ScreeningInventoryRepository;
import com.system.lld.repo.ScreeningRepository;

public class ScreeningService {

	private final ScreeningRepository screeningRepo;
	private final ScreeningInventoryRepository screeningInventoryRepository;

	public ScreeningService(ScreeningRepository screeningRepo, ScreeningInventoryRepository inventoryRepo) {
		this.screeningRepo = screeningRepo;
		this.screeningInventoryRepository = inventoryRepo;
	}

	public Screening addScreening(Cinema cinema, Movie movie, Room room, LocalDateTime startTime,
			LocalDateTime endTime) {
		Screening screening = new Screening(UUID.randomUUID().toString(), movie, cinema, room, startTime, endTime);
		screeningRepo.save(screening);
		screeningInventoryRepository.create(screening.getId(), room.getLayout().getAllSeats()); // ✅ initialize
																								// inventory
		return screening;
	}
}
