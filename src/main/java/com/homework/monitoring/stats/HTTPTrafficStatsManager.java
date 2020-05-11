package com.homework.monitoring.stats;

import com.clfparser.CLFLogEntry;
import com.homework.monitoring.LogEntryListener;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * The traffic statistics manager will listen for new CLF log entries and store them internally;
 * every 10 seconds it will aggregate the log entries into traffic statistics and then send them
 * to all registered traffic stats listeners.
 */
public class HTTPTrafficStatsManager implements LogEntryListener {
    /**
     * Logger.
     */
    Logger logger = LogManager.getLogger(HTTPTrafficStatsManager.class);

    /**
     * Default interval of computing statistics of 10 seconds; the stats manager will emit
     * new stats every 10 seconds and the other components (link the alert manager) will use
     * this period as immutable.
     */
    private final static Integer DEFAULT_STATS_CREATION_INTERVAL = 10; // in seconds

    /**
     * Thread safe queue used for storing & processing incoming HTTP requests.
     */
    Queue<CLFLogEntry> activeEntries = new ConcurrentLinkedQueue<CLFLogEntry>();

    /**
     * Listeners waiting for new statistics notifications
     */
    List<HTTPStatsListener> httpStatsListeners = new ArrayList<>();

    /**
     * Constructor.
     */
    public HTTPTrafficStatsManager(final int statsCreationInterval) {
        final int appStatsCreationInterval;
        if (statsCreationInterval < 10 || statsCreationInterval > 60) {
            logger.error("Invalid stats creation interval, going with default of " + DEFAULT_STATS_CREATION_INTERVAL + " seconds.");
            appStatsCreationInterval = DEFAULT_STATS_CREATION_INTERVAL;
        } else {
            appStatsCreationInterval = statsCreationInterval;
        }
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        executor.scheduleAtFixedRate(new Runnable() {
               public void run() { refreshStats(); }
             }, 0, appStatsCreationInterval, TimeUnit.SECONDS);
    }

    /**
     * At regular intervals consume all new CLF entries and aggregate them into a stats object to send to listeners (UI,
     * alerts etc).
     */
    private void refreshStats() {
        logger.info("Refreshing stats ...");
        final HTTPTrafficStats statistics = new HTTPTrafficStats();

        CLFLogEntry logEntry = activeEntries.peek();
        final Instant currentMoment = Instant.now();

        while (logEntry != null && logEntry.getTimestamp().toInstant().isBefore(currentMoment)) {
            statistics.processNewLogEntry(logEntry);
            logEntry = activeEntries.poll();
        }

        notifyStatsListeners(statistics);
    }

    @Override
    public void process(CLFLogEntry entry) {
        if (entry != null) {
            activeEntries.add(entry);
        }
    }

    /**
     * Register a new stats {@link HTTPStatsListener}.
     * @param httpStatsListener the listener; must be nonnull.
     *
     * @Note duplicated listeners will be registered only once.
     */
    public void registerStatsListener(final HTTPStatsListener httpStatsListener) {
        if (httpStatsListener != null) {
            if (httpStatsListeners.contains(httpStatsListener)) {
                logger.warn("HTTP stats listener already registered; skipping second registration.");
            } else {
                httpStatsListeners.add(httpStatsListener);
            }
        } else {
            logger.error("Null HTTP stats listener trying to be registered.");
        }
    }

    /**
     * Notify all registered listeners when a new aggregated stats object has been constructed.
     * @param newStats the stats.
     */
    private void notifyStatsListeners(final HTTPTrafficStats newStats) {
        for (final HTTPStatsListener listener : httpStatsListeners) {
            listener.processTrafficStatistics(newStats);
        }
    }
}
