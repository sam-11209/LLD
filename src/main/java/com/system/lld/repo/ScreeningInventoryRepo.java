//package com.redis.demo.z.lld.movie_booking_sys.repo;
//
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//import com.redis.demo.z.lld.movie_booking_sys.ScreeningInventory;
//import com.redis.demo.z.lld.movie_booking_sys.Seat;
//
//public class ScreeningInventoryRepo {
//
//	// Key = screeningId, Value = ScreeningInventory
//	private Map<String, ScreeningInventory> inventoryMap = new HashMap<>();
//
//	public ScreeningInventory getOrCreate(String screeningId, List<Seat> allSeats) {
//		// if already exists, just return it
//		if (inventoryMap.containsKey(screeningId)) {
//			return inventoryMap.get(screeningId);
//		}
//
//		// otherwise create new inventory
//		ScreeningInventory inv = new ScreeningInventory(screeningId, allSeats);
//		inventoryMap.put(screeningId, inv);
//		return inv;
//	}
//
//	public ScreeningInventory get(String screeningId) {
//		return inventoryMap.get(screeningId);
//	}
//}
