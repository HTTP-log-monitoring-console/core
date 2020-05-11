package com.homework.monitoring;

import com.clfparser.CLFLogEntry;

/**
 * Interface for listening to new log entry objects.
 */
public interface LogEntryListener {
    /**
     * Method for receiving new entries.
     * @param entry the valid entry (we expect only nonnull entries).
     */
    void process(final CLFLogEntry entry);
}
