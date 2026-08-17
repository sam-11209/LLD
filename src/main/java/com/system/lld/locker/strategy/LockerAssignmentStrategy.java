package com.system.lld.locker.strategy;

import com.system.lld.locker.model.Locker;
import com.system.lld.locker.model.LockerSize;

import java.util.Map;
import java.util.Set;

/**
 * Strategy interface defining locker assignment algorithm for finding available lockers.
 */
public interface LockerAssignmentStrategy {
    /**
     * Finds an available locker from the site's lockers map based on the required package size.
     *
     * @param lockers      map of available lockers grouped by LockerSize
     * @param requiredSize the minimum LockerSize required for the package
     * @return an available Locker instance, or null if no suitable locker is available
     */
    Locker findAvailableLocker(Map<LockerSize, Set<Locker>> lockers, LockerSize requiredSize);
}

