package com.system.lld.entity;

import lombok.Data;

@Data
public class Room {

	private final String id;
    private final String roomNumber;
    private final Layout layout;
}
