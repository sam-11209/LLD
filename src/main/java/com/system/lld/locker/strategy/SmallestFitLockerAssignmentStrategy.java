package com.system.lld.locker.strategy;

import com.system.lld.locker.model.Locker;
import com.system.lld.locker.model.LockerSize;

import java.util.Map;
import java.util.Set;

/**
 * Implementation of {@link LockerAssignmentStrategy} that uses a "Smallest-Fit" allocation strategy.
 * Attempts to assign the exact required locker size first, and if full, upgrades to the next smallest available locker size.
 */
public class SmallestFitLockerAssignmentStrategy implements LockerAssignmentStrategy {

    /**
     * {@inheritDoc}
     * Iterates from the minimum required size up through larger sizes to locate the first available locker.
     */
    @Override
    public Locker findAvailableLocker(Map<LockerSize, Set<Locker>> lockers, LockerSize requiredSize) {
        if (lockers == null || requiredSize == null) {
            return null;
        }

        // Start from requiredSize and check candidate sizes in ascending order
        LockerSize[] sizes = LockerSize.values();
        for (int i = requiredSize.ordinal(); i < sizes.length; i++) {
            LockerSize candidateSize = sizes[i];
            Set<Locker> lockerSet = lockers.get(candidateSize);
            if (lockerSet != null) {
                for (Locker locker : lockerSet) {
                    if (locker.isAvailable()) {
                        return locker;
                    }
                }
            }
        }
        return null;
    }
}

