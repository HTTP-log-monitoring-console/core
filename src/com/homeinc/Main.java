package com.homeinc;

import java.io.File;
import java.util.Arrays;

import com.filereader.FileTailReader;
import com.filereader.LogFileTailerListener;

/**
 * Implements console-based log file tailing, or more specifically, tail
 * following: it is somewhat equivalent to the unix command "tail -f"
 */
public class Main implements LogFileTailerListener {
    /**
     * The log file tailer
     */
    private FileTailReader tailer;

    /**
     * Creates a new Tail instance to follow the specified file
     */
    public Main(String filename) {
        tailer = new FileTailReader(new File(filename), 1000, false);
        tailer.addLogFileTailerListener(this);
        tailer.addLogFileTailerListener(new LogLineParser());
        tailer.start();
    }

    /**
     * A new line has been added to the tailed log file
     * 
     * @param line The new line that has been added to the tailed log file
     */
    public void newLogFileLine(String line) {
        System.out.println(line);
    }

    /**
     * Command-line launcher
     */
    public static void main(String[] args) {
        System.out.println(Arrays.toString(args));
        if (args.length < 1) {
            System.out.println("Usage: Tail <filename>");
            System.exit(0);
        }
        Main tail = new Main(args[0]);
    }
}