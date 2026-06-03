package com.system.lld.controller.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.system.lld.entity.Screening;
import com.system.lld.entity.ScreeningInventoryPessimistic;
import com.system.lld.repo.ScreeningInventoryRepository;

@Service
public class SeatAvailabilityService {

	private final ScreeningInventoryRepository screeningInventoryRepository;

	SeatAvailabilityService(ScreeningInventoryRepository inventoryRepo) {
		this.screeningInventoryRepository = inventoryRepo;
	}

	public List<String> getAvailableSeats(Screening screening) {

		// Normally Here we get ScreeningInventory always because it is already created
		// But here we are doing this to avoid NPE
		// So if ScreeningInventory already exist we get it else we create new one
		ScreeningInventoryPessimistic allSeats = screeningInventoryRepository.getOrCreate(screening.getId(),
				screening.getRoom().getLayout().getAllSeats());

		// One way to look for available seats is this

		// Calculates which seats are still available for a screening
//	        List<Seat> allSeats = screening.getRoom().getLayout().getAllSeats();
//	        List<Ticket> bookedTickets = getTicketsForScreening(screening);
//
//	        List<Seat> availableSeats = new ArrayList<>(allSeats);
//	        for (Ticket ticket : bookedTickets) {
//	            availableSeats.remove(ticket.getSeat());
//	        }

		return allSeats.getAvailableSeatNumbers();
	}
}
