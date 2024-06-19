package com.hww.pojo;

import lombok.Data;

@Data
public class RateLimiter {

    private final long interval;
    private long lastRequestTime;

    public RateLimiter(int permitsPerSecond) {
        this.interval = 1000 / permitsPerSecond;
        this.lastRequestTime = System.currentTimeMillis();
    }

    public synchronized boolean tryAcquire() {
        long now = System.currentTimeMillis();
        if (now - lastRequestTime >= interval) {
            lastRequestTime = now;
            return true;
        }
        return false;
    }
}