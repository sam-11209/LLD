package com.system.lld.entity;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class Cinema {

    private final String id;
    private final String name;
    private final String city;
    private final List<Room> rooms = new ArrayList<>();
}
