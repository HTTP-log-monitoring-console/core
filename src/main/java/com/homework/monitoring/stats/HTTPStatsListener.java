package com.homework.monitoring.stats;

/**
 * Interface for a new traffic stats listener.
 */
public interface HTTPStatsListener {
    /**
     * Treat the newly raised traffic stats covering the last X seconds the manager has aggregated.
     * @param httpTrafficStats the aggregated stats.
     *
     * @Note the interval over which the stats are aggregated depends on the contract of the stats'
     * emitter.
     */
    void processTrafficStatistics(final HTTPTrafficStats httpTrafficStats);
}
