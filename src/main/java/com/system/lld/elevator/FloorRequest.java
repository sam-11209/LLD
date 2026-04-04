package com.system.lld.elevator;
/**
 * FloorRequest.java
 *
 * A simple value object that captures a single hall-call request.
 *
 * Why a separate class?
 *  - Keeps floor + direction bundled together so the pending queue
 *    stores one object per request instead of parallel lists.
 *  - timestamp lets us log or prioritise by wait time in the future
 *    (e.g. "oldest pending request first").
 */
public class FloorRequest {

    private final int floor;
    private final Direction dir;
    private final long timestamp; // when the request was created

    public FloorRequest(int floor, Direction dir) {
        this.floor     = floor;
        this.dir       = dir;
        this.timestamp = System.currentTimeMillis();
    }

    public int       getFloor()     { return floor; }
    public Direction getDir()       { return dir; }
    public long      getTimestamp() { return timestamp; }

    @Override
    public String toString() {
        return "FloorRequest{floor=" + floor + ", dir=" + dir + "}";
    }
}
