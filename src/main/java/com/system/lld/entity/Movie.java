package com.system.lld.entity;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Movie {

	private final String id;
    private final String title;
    private final String genre;
    private final int durationMinutes;
}
