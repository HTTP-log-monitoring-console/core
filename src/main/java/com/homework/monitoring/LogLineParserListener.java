package com.homework.monitoring;

import com.clfparser.CLFLogEntry;
import com.clfparser.CLFLogParser;
import com.filereader.LogLineListener;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Log file parser reading line by line the latest updates to the file.
 */
public class LogLineParserListener implements LogLineListener {
    /**
     * Logger.
     */
    Logger logger = LogManager.getLogger(LogLineParserListener.class);

    /**
     * The object broadcasting new entries to listeners.
     */
    final LogEntryBroadcaster logEntryBroadcaster;

    /**
     * Constructor
     * @param logEntryBroadcaster the manager object disseminating entries to all listeners.
     */
    public LogLineParserListener(final LogEntryBroadcaster logEntryBroadcaster) {
        if (logEntryBroadcaster == null) {
            logger.error("LogEntryManager is null ! Stopping application.");
            System.exit(1);
        }

        this.logEntryBroadcaster = logEntryBroadcaster;
    }

    @Override
    public void newLogFileLine(String line) {
        CLFLogEntry entry = null;
        try {
             entry = CLFLogParser.parse(line);
        } catch (Exception exception) {
            logger.warn("Exception encountered while trying to parse log line " + line);
            logger.error("Exception encountered : " + exception.getMessage());
        }
        if (entry != null) {
            logger.debug(entry);
            logEntryBroadcaster.notifyEntry(entry);
        }
    }
}
