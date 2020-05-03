package com.homework;

import com.clfparser.CLFLogEntry;
import com.clfparser.CLFLogParser;
import com.filereader.LogFileTailerListener;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LogLineParser implements LogFileTailerListener {
    Logger logger = LogManager.getLogger(LogLineParser.class);

    @Override
    public void newLogFileLine(String line) {
        CLFLogEntry entry = null;
        try {
             entry = CLFLogParser.parse(line);
        } catch (Exception exception) {
        }
        if (entry != null) {
            logger.debug(entry);
        }
    }
}
