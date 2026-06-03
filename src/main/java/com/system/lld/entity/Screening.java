package com.system.lld.entity;

import java.time.Duration;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Screening {

	private final String id;
	private final Movie movie;
	private final Cinema cinema;
	private final Room room;
	private final LocalDateTime startTime;
	private final LocalDateTime endTime;

	public Duration getDuration() {
		return Duration.between(startTime, endTime);
	}
}
