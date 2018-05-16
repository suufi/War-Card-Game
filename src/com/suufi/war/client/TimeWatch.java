package com.suufi.war.client;

import java.util.concurrent.TimeUnit;

public class TimeWatch {

    long startTime;

    /**
     * Reset timeWatch when ever it is constructed
     */
    TimeWatch() {
        reset();
    }

    /**
     * Creates and returns a new instance of TimeWatch when start is called
     * @return a new instance of TimeWatch
     */
    public static TimeWatch start() {
        return new TimeWatch();
    }

    /**
     * Resets startTime to time right now in nanoseconds
     */
    public TimeWatch reset() {
    	startTime = System.nanoTime();
        return this;
    }

    /**
     * Gets the time elapsed between currentTime (in nano) and startTime
     * @return time elapsed
     */
    public long time() {
        long currentTime = System.nanoTime();
        return currentTime - startTime;
    }

    /**
     * Gets the time elapsed between currentTime and startTime in a certain TimeUnit by converting
     * @param unit - TimeUnit to convert to
     * @return time elapsed in TimeUnit
     */
    public long time(TimeUnit unit) {
        return unit.convert(time(), TimeUnit.NANOSECONDS);
    }

    /**
     * Returns a String formatted similar to "3 min, 42 sec" referring to the time elapsed
     * @return the String that was formatted
     */
    public String toMinuteSeconds() {
        return String.format("%d min, %d sec", time(TimeUnit.MINUTES),
                time(TimeUnit.SECONDS) - time(TimeUnit.MINUTES));
    }
    
    /**
     * Returns a String formatted similar to "03:42" referring to time elapsed
     * @return the String that was formatted
     */
    public String toString() {
    		return String.format("%02d:%02d", time(TimeUnit.SECONDS)/60,
                time(TimeUnit.SECONDS) % 60);
    }
}

