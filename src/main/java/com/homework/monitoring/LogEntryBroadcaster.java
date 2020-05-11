package com.homework.monitoring;

import com.clfparser.CLFLogEntry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Broadcaster object managing a list of listeners and disseminating new entries to registered clients.
 */
public class LogEntryBroadcaster {
    /**
     * Logger.
     */
    Logger logger = LogManager.getLogger(LogEntryBroadcaster.class);

    /**
     * Internal list of listeners subscribed to new entry log events.
     */
    private List<LogEntryListener> listeners;

    /**
     * Constructor.
     */
    public LogEntryBroadcaster() {
        logger.debug("Initializing LogEntryManager ...");
        listeners = new ArrayList<>();
        logger.debug("Initializing LogEntryManager ... DONE !");
    }

    /**
     * Register new {@link LogEntryListener}.
     * @param logEntryListener the listener; must not be null.
     *
     * @Note duplicated listeners will be registered only once.
     */
    public void registerListener(final LogEntryListener logEntryListener) {
        if (logEntryListener == null) {
            logger.error("Trying to register null entry lister");
            return;
        }
        if (!listeners.contains(logEntryListener)) {
            listeners.add(logEntryListener);
        } else {
            logger.warn("Log entry listener already registered; the second instance will NOT be registered.");
        }
    }

    /**
     * Notify all registered listeners when a new {@link CLFLogEntry} has arrived.
     * @param entry the entry; must not be null.
     */
    public void notifyEntry(final CLFLogEntry entry) {
        if (entry == null) {
            logger.error("Received null log entry; discarding");
            return;
        }

        if (!listeners.isEmpty()) {
            for (final LogEntryListener listener : listeners) {
                listener.process(entry);
            }
        }
    }
}
