package com.homework;

import com.filereader.FileTailReader;
import com.homework.internal.LogLineParserListener;
import com.homework.internal.Orchestrator;
import com.homework.ui.ApplicationWindow;
import org.apache.commons.cli.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

/**
 * Implements console-based log file tailing.
 */
public class StartApplication {
    /**
     * The log file tailer
     */
    private FileTailReader tailer;

    final static Logger logger = LogManager.getLogger(StartApplication.class);

    /**
     * Command-line launcher
     */
    public static void main(String[] args) {
        logger.info("Starting application ...");

        CommandLine commandLine = parseCommandLine(args);

        if (commandLine == null) {
            logger.error("Command line arguments parsing has failed ! Exiting application ...");
        }
        final String inputLogFile = commandLine.getOptionValue("input");
        logger.debug("Input file path [" + inputLogFile + "]");
        Orchestrator orchestrator = new Orchestrator(inputLogFile);

        // The UI execution is performed asynchronously using a separate thread.
        new Thread(() -> {
            ApplicationWindow uiWindow = new ApplicationWindow();
            try {
                uiWindow.launch();
            } catch (IOException e) {
                logger.error("failed to create console UI; exiting ...");
                logger.error(e.getMessage(), e);
                System.exit(1);
            }
        }, "ui-thread").start();
    }

    private static CommandLine parseCommandLine(final String[] arguments) {
        logger.debug("Parsing command line arguments : " + Arrays.toString(arguments));
        final Options options = new Options();

        final Option input = new Option("i", "input", true, "input log file path");
        input.setRequired(true);
        options.addOption(input);

        final Option output = new Option("o", "output", true, "output file");
        output.setRequired(false);
        options.addOption(output);

        final CommandLineParser parser = new DefaultParser();
        final HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd = null;

        try {
            cmd = parser.parse(options, arguments);
        } catch (final ParseException parseException) {
            logger.error("Cannot parse command line arguments : " + parseException.getMessage());
            formatter.printHelp("utility-name", options);
            System.exit(1);
        }
        return cmd;
    }
}