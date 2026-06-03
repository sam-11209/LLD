package com.system.lld.controller.service;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.system.lld.entity.Booking;
import com.system.lld.entity.BookingEvent;
import com.system.lld.entity.DiscountFactory;
import com.system.lld.entity.DiscountStrategy;
import com.system.lld.entity.NotificationPublisher;
import com.system.lld.entity.Order;
import com.system.lld.entity.Payment;
import com.system.lld.entity.PaymentMethod;
import com.system.lld.entity.PaymentStatus;
import com.system.lld.entity.Screening;
import com.system.lld.entity.ScreeningInventoryPessimistic;
import com.system.lld.entity.Seat;
import com.system.lld.entity.Ticket;
import com.system.lld.repo.BookingRepository;
import com.system.lld.repo.OrderRepository;
import com.system.lld.repo.ScreeningInventoryRepository;
import com.system.lld.repo.ScreeningRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class BookingService {

	private static final Duration HOLD_TTL = Duration.ofMinutes(5);

	private final ScreeningRepository screeningRepo;
	private final ScreeningInventoryRepository screeningInventoryRepo;
	private final OrderRepository orderRepo;
	private final BookingRepository bookingRepo;
	private final PaymentService paymentService;
	private final NotificationPublisher notificationPublisher;
	private final DiscountFactory discountFactory;

	public BookingService(BookingRepository bookingRepo, ScreeningRepository screeningRepo,
			ScreeningInventoryRepository screeningInventoryRepo, OrderRepository orderRepo,
			PaymentService paymentService, NotificationPublisher notificationPublisher,
			DiscountFactory discountFactory) {
		this.bookingRepo = bookingRepo;
		this.screeningRepo = screeningRepo;
		this.screeningInventoryRepo = screeningInventoryRepo;
		this.orderRepo = orderRepo;
		this.paymentService = paymentService;
		this.notificationPublisher = notificationPublisher;
		this.discountFactory = discountFactory;
	}

	/** 1) place a temporary hold */
	public Booking startBooking(String userId, String screeningId, List<String> seatNumbers) {

		Screening screening = screeningRepo.findById(screeningId)
				.orElseThrow(() -> new IllegalArgumentException("screening not found"));

//		getOrCreate(screeningId, seats) is meant to give you the seat inventory for that screening.
//		If the screening already exists in memory → just return it.
//		If it doesn’t → create a new ScreeningInventory initialized with all the seats.
//		This is a convenience method to avoid null checks in the service layer.

		// ScreeningInventory inv = inventoryRepo.getOrCreate(screeningId,
		// screening.getRoom().getLayout().getAllSeats());

		ScreeningInventoryPessimistic screeningInventory = screeningInventoryRepo.get(screeningId);
		if (screeningInventory == null) {
			throw new IllegalStateException("Inventory not initialized for screening");
		}
		screeningInventory.lockSeats(seatNumbers, userId, HOLD_TTL);

		Booking booking = new Booking(/* id */ UUID.randomUUID().toString(), userId, screeningId, seatNumbers,
				PaymentStatus.INITIATED, null);
		bookingRepo.save(booking);
		return booking;
	}

	/**
	 * 2) confirm purchase -> apply discount, charge final price, create order, mark
	 * seats SOLD
	 */
	public Order confirmBooking(String bookingId, String promoCode) {
		Booking booking = bookingRepo.findById(bookingId).orElseThrow();
		Screening screening = screeningRepo.findById(booking.getScreeningId()).orElseThrow();

		// 1) Calculate total price from seat pricing at this screening
		BigDecimal total = booking.calculateTotalPrice(screening);

		// 2) Apply discount (no-op if promoCode is null/blank/unknown)
		DiscountStrategy discount = discountFactory
				.getStrategy((promoCode == null || promoCode.isBlank()) ? "NONE" : promoCode);
		BigDecimal finalPrice = discount.apply(total, booking);
		if (finalPrice.compareTo(BigDecimal.ZERO) < 0)
			finalPrice = BigDecimal.ZERO;

		// 3) Charge the FINAL amount only
		Payment payment = paymentService.processPayment(bookingId, PaymentMethod.UPI, finalPrice);
		if (payment.getStatus() != PaymentStatus.SUCCESS) {
			booking.setPaymentStatus(payment.getStatus());
			bookingRepo.save(booking);
			throw new RuntimeException("Payment failed with status: " + payment.getStatus());
		}

		// 4) Create order + tickets (snapshot per-seat price; discount tracked at order
		// level if you add fields)
		Map<String, Seat> seatsByNo = screening.getRoom().getLayout().getAllSeats().stream()
				.collect(Collectors.toMap(Seat::getSeatNumber, s -> s));

		Order order = new Order(UUID.randomUUID().toString(), booking.getUserId());
		for (String seatNo : booking.getSeatNumbers()) {
			BigDecimal seatPrice = seatsByNo.get(seatNo).getPricingStrategy().getPrice();
			order.addTicket(new Ticket(UUID.randomUUID().toString(), screening.getId(), seatNo, seatPrice));
		}

		// 5) Atomically mark seats as SOLD (after successful payment)
		ScreeningInventoryPessimistic inv = screeningInventoryRepo.getOrCreate(screening.getId(), seatsByNo.values());
		inv.markSold(booking.getSeatNumbers(), booking.getUserId());

		// 6) Persist state
		orderRepo.save(order);
		booking.setPaymentStatus(PaymentStatus.SUCCESS);
		bookingRepo.save(booking);

		// 7) Fire-and-forget notifications (don’t break the booking flow if they fail)
		BookingEvent event = new BookingEvent(bookingId, booking.getUserId(), "Your booking is confirmed!");
		CompletableFuture.runAsync(() -> {
			try {
				notificationPublisher.publish(event);
			} catch (Exception e) {
				System.out.println("⚠️ Notification async failed for booking " + bookingId);
			}
		});

		return order;
	}

	/** 3) cancel / timeout -> release locks */
	public void cancelBooking(String bookingId) {
		Booking booking = bookingRepo.findById(bookingId).orElseThrow();
		ScreeningInventoryPessimistic inv = screeningInventoryRepo.getOrCreate(booking.getScreeningId(), List.of());
		inv.releaseLocks(booking.getSeatNumbers(), booking.getUserId());
		booking.setPaymentStatus(PaymentStatus.CANCELLED);
		bookingRepo.save(booking);
	}
	
//	/** 2) confirm purchase -> create order + tickets, mark seats SOLD */
//	public Order confirmBooking(String bookingId, String promoCode) {
//		Booking booking = bookingRepo.findById(bookingId).orElseThrow();
//		Screening screening = screeningRepo.findById(booking.getScreeningId()).orElseThrow();
//
//		// 🧾 Calculate total price first
//		BigDecimal total = booking.calculateTotalPrice();
//
//		// 🎟 Apply discount if promo code given
//		DiscountStrategy discount = discountFactory.getStrategy(promoCode);
//		BigDecimal finalPrice = discount.apply(total, booking);
//
//		// 💰 Step 1: Try payment
//		Payment payment = paymentService.processPayment(bookingId, PaymentMethod.UPI);
//
//		if (payment.getStatus() != PaymentStatus.SUCCESS) {
//			booking.setPaymentStatus(payment.getStatus());
//			bookingRepo.save(booking);
//			throw new RuntimeException("Payment failed with status: " + payment.getStatus());
//		}
//
//		// 💳 Step 2: Payment succeeded → create order
//		Map<String, Seat> seats = screening.getRoom().getLayout().getAllSeats().stream()
//				.collect(Collectors.toMap(Seat::getSeatNumber, s -> s));
//
//		Order order = new Order(UUID.randomUUID().toString(), booking.getUserId());
//		for (String seatNo : booking.getSeatNumbers()) {
//			BigDecimal p = seats.get(seatNo).getPricingStrategy().getPrice();
//			order.addTicket(new Ticket(UUID.randomUUID().toString(), screening.getId(), seatNo, p));
//		}
//		// ✅ Step 3: Atomically mark seats as SOLD
//		ScreeningInventory inv = screeningInventoryRepo.getOrCreate(screening.getId(), seats.values());
//		inv.markSold(booking.getSeatNumbers(), booking.getUserId());
//
//		orderRepo.save(order);
//		booking.setPaymentStatus(PaymentStatus.SUCCESS);
//		// link order inside booking if you want immutability loosened
//		bookingRepo.save(booking);
//		System.out.println("✅ Booking completed for user " + booking.getUserId());
//
//		// ✅ Step 4: Sending notification which is asyc process
//		BookingEvent event = new BookingEvent(bookingId, booking.getUserId(), "Your booking is confirmed!");
//
//		// fire-and-forget notifications
//		CompletableFuture.runAsync(() -> {
//			try {
//				notificationPublisher.publish(event);
//			} catch (Exception e) {
//				System.err.println("⚠️ Notification async failed for booking " + bookingId);
//			}
//		});
//		return order;
//	}
}
