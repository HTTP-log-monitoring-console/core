package com.homework.monitoring;

import com.filereader.FileReader;
import com.homework.monitoring.alerts.HTTPTrafficAlertsManager;
import com.homework.monitoring.stats.HTTPTrafficStatsManager;
import com.homework.ui.ApplicationUI;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;

/**
 * Object managing all interactions between the different parts of the application.
 */
public class Orchestrator {
    /**
     * Logger.
     */
    private Logger logger = LogManager.getLogger(Orchestrator.class);

    /**
     * Default period of sampling the log file for new lines, in milliseconds.
     */
    private final static Integer DEFAULT_FILE_SAMPLE_INTERVAL = 1000; // in milliseconds.

    /**
     * Default length of the monitoring window during which alerts will be raised or cancelled if the number of requests
     * goes over or below the alert threshold.
     */
    private final static Integer DEFAULT_ALERT_MONITORING_WINDOW_LENGTH = 2 * 60; // in seconds // default 2 minutes.

    /**
     * Default number of requests representing the alert threshold for the monitoring window.
     */
    private final static Integer DEFAULT_ALERT_NUMBER_REQUESTS_THRESHOLD = 5;

    /**
     * Default period for creating new aggregated stats from the read & parsed new CLF log file lines, in seconds.
     */
    private final static Integer DEFAULT_STATS_CREATION_INTERVAL = 10; // in seconds.

    /**
     * Constructor.
     * @param inputFilename path towards the log file on disk.
     */
    public Orchestrator(final String inputFilename) {
        if (inputFilename == null || inputFilename.isEmpty()) {
            logger.error("Input log file is not valid [" + inputFilename + "]; stopping the application.");
            System.exit(1);
        }

        // construct the UI window and launch it in a different thread.
        ApplicationUI appUI = new ApplicationUI();

        new Thread(() -> {
            try {
                appUI.create();
            } catch (IOException e) {
                logger.error("failed to create console UI; exiting ...");
                logger.error(e.getMessage(), e);
                System.exit(1);
            }
        }, "ui-thread").start();

        // construct the traffic alerts manager (responsible for raising / cancelling alerts)
        HTTPTrafficAlertsManager httpTrafficAlertsManager =
                new HTTPTrafficAlertsManager(DEFAULT_ALERT_MONITORING_WINDOW_LENGTH, DEFAULT_ALERT_NUMBER_REQUESTS_THRESHOLD);
        // register the UI as a listener for alerts
        httpTrafficAlertsManager.registerAlertsListener(appUI);

        // construct the stats manager (responsible sending out aggregated stats every
        HTTPTrafficStatsManager httpTrafficStatsManager = new HTTPTrafficStatsManager(DEFAULT_STATS_CREATION_INTERVAL);
        // register the alerts manager as a listener for new stats
        httpTrafficStatsManager.registerStatsListener(httpTrafficAlertsManager);
        // register the UI as a listener for new stats so visual updates are being shown.
        httpTrafficStatsManager.registerStatsListener(appUI);

        // creating new entry manager to send new entries to all listeners
        LogEntryBroadcaster logEntryBroadcaster = new LogEntryBroadcaster();
        // register the stats manager as a listener of new CLF log entries
        logEntryBroadcaster.registerListener(httpTrafficStatsManager);

        // creating line parser listener to convert new string lines into log entries
        LogLineParserListener logLineParserListener = new LogLineParserListener(logEntryBroadcaster);

        // creating file tail reader to read new lines from the text file.
        FileReader tailReader = new FileReader(new File(inputFilename), DEFAULT_FILE_SAMPLE_INTERVAL);
        // register the log line parser as listener of new on-disk file lines.
        tailReader.registerLogFileTailerListener(logLineParserListener);

        // starting the on-disk file reader.
        tailReader.start();
    }
}
