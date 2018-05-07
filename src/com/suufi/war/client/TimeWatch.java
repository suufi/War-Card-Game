package com.suufi.war.client;

import java.util.concurrent.TimeUnit;

public class TimeWatch {

    long startTime;

    TimeWatch() {
        reset();
    }

    public static TimeWatch start() {
        return new TimeWatch();
    }

    public TimeWatch reset() {
    		startTime = System.nanoTime();
        return this;
    }

    public long time() {
        long currentTime = System.nanoTime();
        return currentTime - startTime;
    }

    public long time(TimeUnit unit) {
        return unit.convert(time(), TimeUnit.NANOSECONDS);
    }

    public String toMinuteSeconds() {
        return String.format("%d min, %d sec", time(TimeUnit.MINUTES),
                time(TimeUnit.SECONDS) - time(TimeUnit.MINUTES));
    }
    
    public String toString() {
    		return String.format("%02d:%02d", time(TimeUnit.SECONDS)/60,
                time(TimeUnit.SECONDS) % 60);
    }
}

