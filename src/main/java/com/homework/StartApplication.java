package com.homework;

import com.filereader.FileReader;
import com.homework.monitoring.Orchestrator;
import org.apache.commons.cli.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;

/**
 * Implements console-based log file tailing.
 */
public class StartApplication {
    /**
     * The log file tailer
     */
    private FileReader tailer;

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
    }

    /**
     * Parse command line arguments.
     * @param arguments the list of string arguments.
     * @return a CommandLine object.
     */
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