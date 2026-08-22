package com.system.lld.locker.model;

import com.system.lld.locker.exception.NoLockerAvailableException;
import com.system.lld.locker.factory.LockerFactory;
import com.system.lld.locker.strategy.LockerAssignmentStrategy;
import com.system.lld.locker.strategy.SmallestFitLockerAssignmentStrategy;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Represents a physical locker site (hub or station) containing sets of lockers
 * categorized by size. Coordinates package placement using an assignment
 * strategy.
 */
public class Site {
	private final String siteId;
	private final String siteName;
	private final Map<LockerSize, Set<Locker>> lockers = new ConcurrentHashMap<>();
	private final LockerAssignmentStrategy assignmentStrategy;

	/**
	 * Constructs a Site with a specific ID, name, locker capacity configurations,
	 * and locker assignment strategy.
	 *
	 * @param siteId             the unique site identifier
	 * @param siteName           the human-readable name of the site
	 * @param lockerCounts       map indicating the number of lockers to instantiate
	 *                           for each size
	 * @param assignmentStrategy the LockerAssignmentStrategy strategy (defaults to
	 *                           SmallestFit if null)
	 */
	public Site(String siteId, String siteName, Map<LockerSize, Integer> lockerCounts,
			LockerAssignmentStrategy assignmentStrategy) {
		this.siteId = siteId;
		this.siteName = siteName;
		this.assignmentStrategy = assignmentStrategy != null ? assignmentStrategy
				: new SmallestFitLockerAssignmentStrategy();

		for (Map.Entry<LockerSize, Integer> entry : lockerCounts.entrySet()) {
			Set<Locker> lockerSet = Collections.synchronizedSet(new HashSet<>());
			for (int i = 0; i < entry.getValue(); i++) {
				lockerSet.add(LockerFactory.createLocker(entry.getKey()));
			}
			this.lockers.put(entry.getKey(), lockerSet);
		}
	}

	/**
	 * Convenience constructor creating a default Site with ID "SITE-1" and name
	 * "Main Site".
	 *
	 * @param lockerCounts map indicating the number of lockers to create per size
	 */
	public Site(Map<LockerSize, Integer> lockerCounts) {
		this("SITE-1", "Main Site", lockerCounts, new SmallestFitLockerAssignmentStrategy());
	}

	/**
	 * Gets the unique identifier of the site.
	 *
	 * @return the site ID
	 */
	public String getSiteId() {
		return siteId;
	}

	/**
	 * Gets the name of the site.
	 *
	 * @return the site name
	 */
	public String getSiteName() {
		return siteName;
	}

	/**
	 * Returns an unmodifiable view of the lockers map grouped by LockerSize.
	 *
	 * @return map of LockerSize to Set of Locker instances
	 */
	public Map<LockerSize, Set<Locker>> getLockers() {
		return Collections.unmodifiableMap(lockers);
	}

	/**
	 * Delegate method to find an available locker using the configured assignment
	 * strategy.
	 *
	 * @param size the minimum required locker size
	 * @return an available Locker instance, or null if none is available
	 */
	public Locker findAvailableLocker(LockerSize size) {
		return assignmentStrategy.findAvailableLocker(lockers, size);
	}

	/**
	 * Places a package into a fitting, available locker thread-safely for a given
	 * deposit date. Searches starting from the package's minimum required size up
	 * to larger sizes.
	 *
	 * @param pkg  the ShippingPackage to deposit
	 * @param date the deposit timestamp
	 * @return the assigned Locker
	 * @throws IllegalArgumentException   if package is null
	 * @throws NoLockerAvailableException if no suitable locker is currently
	 *                                    available
	 */
	public Locker placePackage(ShippingPackage pkg, Date date) {
		if (pkg == null) {
			throw new IllegalArgumentException("Package cannot be null");
		}

		// This means give you the first lockerSize in which this shipment can fit
		// Say Medium
		LockerSize requiredSize = pkg.getLockerSize();

		// Suppose size are- Extra Small ,Small, Medium, Large, Extra Large
		LockerSize[] sizes = LockerSize.values();

		// this loop will run from medium to Extra Large to find the locker for shipment
		for (int i = requiredSize.ordinal(); i < sizes.length; i++) {
			LockerSize candidateSize = sizes[i];
			Set<Locker> lockerSet = lockers.get(candidateSize);

			if (lockerSet != null) {
				// Synchronize on the set iteration or lock individual lockers atomically
				synchronized (lockerSet) {
					for (Locker locker : lockerSet) {
						locker.getLock().lock();
						try {
							if (locker.isAvailable()) {
								locker.assignPackage(pkg, date);
								pkg.updateShippingStatus(ShippingStatus.IN_LOCKER);
								return locker;
							}
						} finally {
							locker.getLock().unlock();
						}
					}
				}
			}
		}

		throw new NoLockerAvailableException("No suitable locker available for package orderId: " + pkg.getOrderId()
				+ " requiring size: " + requiredSize);
	}
}
