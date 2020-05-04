package com.homework.internal;

import com.clfparser.CLFLogEntry;

/**
 * Interface for listening to new log entry objects.
 */
public interface EntryListener {
    /**
     * Method for receiving new entries.
     * @param entry the valid entry (we expect only nonnull entries).
     */
    void process(final CLFLogEntry entry);
}
