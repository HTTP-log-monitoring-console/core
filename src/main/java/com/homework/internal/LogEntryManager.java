package com.homework.internal;

import com.clfparser.CLFLogEntry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class LogEntryManager {
    Logger logger = LogManager.getLogger(LogEntryManager.class);

    /**
     * Internal list of listeners subscribed to new entry log events.
     */
    private List<EntryListener> listeners;

    public LogEntryManager() {
        logger.debug("Initializing LogEntryManager ...");
        listeners = new ArrayList<>();
        logger.debug("Initializing LogEntryManager ... DONE !");
    }

    public void registerListener(final EntryListener entryListener) {
        if (entryListener == null) {
            logger.error("Trying to register null entry lister");
            return;
        }
        if (!listeners.contains(entryListener)) {
            listeners.add(entryListener);
        } else {
            logger.warn("Log entry listener already registered; the second instance will NOT be registered.");
        }
    }

    public void notifyEntry(final CLFLogEntry entry) {
        if (entry == null) {
            logger.error("Received null log entry; discarding");
            return;
        }

        if (!listeners.isEmpty()) {
            for (final EntryListener listener : listeners) {
                listener.process(entry);
            }
        }
    }
}
