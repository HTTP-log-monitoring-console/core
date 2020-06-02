package com.filereader;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.util.*;

public class FileReader extends Thread {
    /**
     * Logger.
     */
    Logger logger = LogManager.getLogger(FileReader.class);

    /**
     * File sampling interval.
     */
    private long sampleInterval = 1000; // in milliseconds.

    /**
     * The log file to read.
     */
    private File logfile;

    /**
     * Set of listeners
     */
    private Set<LogLineListener> listeners = new HashSet<LogLineListener>();

    /**
     * Creates a new log file reader.
     *
     * @param file           The file to tail
     * @param sampleInterval How often to check for updates to the log file
     */
    public FileReader(final File file, final long sampleInterval) {
        this.logfile = file;
        this.sampleInterval = sampleInterval;
    }

    /**
     * Register a new line listener.
     *
     * @param l
     */
    public void registerLogFileTailerListener(final LogLineListener l) {
        this.listeners.add(l);
    }

    /**
     * Notify new lines to listeners.
     *
     * @param line string line, cannot be null.
     */
    protected void notifyNewLine(final String line) {
        if (line == null) {
            logger.error("Null line read from file, possible app error.");
            return;
        }

        for (final LogLineListener listener : this.listeners) {
            listener.newLogFileLine(line);
        }
    }

    private String convertToUTF8(final String fakeLine) {
        if (fakeLine == null)
            return null;

        byte[] bytes = new byte[fakeLine.length()];
        for (int i = 0; i < fakeLine.length(); i++)
            bytes[i] = (byte) fakeLine.charAt(i);
        try {
            return new String(bytes, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            logger.error("UTF8 parsing error : " + fakeLine);
        }
        return fakeLine;
    }

    /**
     * Start the file reader.
     */
    public void run() {
        // The file pointer keeps track of where we are in the file
        long lastPosition = this.logfile.length();

        RandomAccessFile accessFile = null;
        try {
            accessFile = new RandomAccessFile(logfile, "r");
            while (true) {
                try {
                    long fileLength = this.logfile.length();
                    if (fileLength < lastPosition) {
                        accessFile = new RandomAccessFile(logfile, "r");
                        lastPosition = 0;
                    }

                    if (fileLength > lastPosition) {
                        accessFile.seek(lastPosition);
                        String line = convertToUTF8(accessFile.readLine());

                        while (line != null) {
                            if (line.trim().length() > 0) {
                                this.notifyNewLine(line);
                            }
                            line = convertToUTF8(accessFile.readLine());
                        }
                        lastPosition = accessFile.getFilePointer();
                    }

                    sleep(this.sampleInterval);
                } catch (IOException e) {
                    logger.error("Problem while reading from file " + logfile.getAbsolutePath() + "; quitting.");
                } catch (InterruptedException e) {
                    logger.warn("File reader thread was interrupted, ignoring.");
                }
            }
        } catch (final FileNotFoundException e) {
            final String errorMessage = "Cannot find file " + logfile.getAbsolutePath() +
                    ". Please pass the correct file as an argument when launching the application's jar file.";
            logger.error(errorMessage);
            System.out.println(errorMessage);
            System.exit(1);
        } finally {
            if (accessFile != null) {
                try {
                    accessFile.close();
                } catch (final IOException e) {
                    logger.error("Cannot close file correctly.");
                }
            }
        }
    }
}