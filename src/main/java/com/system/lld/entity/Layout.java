package com.system.lld.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Data;

@Data
public class Layout {

	private final int rows;
	private final int cols;
	private final Map<String, Seat> seatsByNumber = new HashMap<>();
	private final Map<Integer, Map<Integer, Seat>> seatsByPosition = new HashMap<>();

	public List<Seat> getAllSeats() {
		return new ArrayList<>(seatsByNumber.values());
	}
}
