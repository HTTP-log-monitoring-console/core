package com.homework.internal;

import com.clfparser.CLFLogEntry;
import com.clfparser.CLFLogParser;
import com.filereader.LogFileTailerListener;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LogLineParserListener implements LogFileTailerListener {
    /**
     * Logger.
     */
    Logger logger = LogManager.getLogger(LogLineParserListener.class);

    /**
     * The manager object broadcasting new entries to listeners.
     */
    final LogEntryManager logEntryManager;

    /**
     * Constructor
     * @param logEntryManager the manager object disseminating entries to all listeners.
     */
    public LogLineParserListener(final LogEntryManager logEntryManager) {
        if (logEntryManager == null) {
            logger.error("LogEntryManager is null ! Stopping application.");
            System.exit(1);
        }

        this.logEntryManager = logEntryManager;
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
            logEntryManager.notifyEntry(entry);
        }
    }
}
