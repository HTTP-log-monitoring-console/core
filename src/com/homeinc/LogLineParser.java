package com.homeinc;

import com.clfparser.CLFLogEntry;
import com.clfparser.CLFLogParser;
import com.filereader.LogFileTailerListener;

public class LogLineParser implements LogFileTailerListener {
    @Override
    public void newLogFileLine(String line) {
        CLFLogEntry entry = null;
        try {
             entry = CLFLogParser.parse(line);
        } catch (Exception exception) {
        }
        System.out.println(entry);
    }
}
