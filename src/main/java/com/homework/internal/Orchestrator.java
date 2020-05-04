package com.homework.internal;

import com.filereader.FileTailReader;

import java.io.File;

public class Orchestrator {
    private final static Integer SAMPLE_INTERVAL = 1000; // in milliseconds.

    public Orchestrator(final String inputFilename) {
        // creating new entry manager to send new entries to all listeners
        LogEntryManager logEntryManager = new LogEntryManager();

        // creating line parser listener to convert new string lines into log entries
        LogLineParserListener logLineParserListener = new LogLineParserListener(logEntryManager);

        // creating file tail reader to read new lines from the text file.
        FileTailReader tailReader = new FileTailReader(new File(inputFilename), SAMPLE_INTERVAL, false);
        tailReader.addLogFileTailerListener(logLineParserListener);

        // starting the thread.
        tailReader.start();
    }
}
