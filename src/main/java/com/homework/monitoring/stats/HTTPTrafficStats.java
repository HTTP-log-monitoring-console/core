package com.homework.monitoring.stats;

import com.clfparser.CLFLogEntry;
import com.utils.ConversionUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * The HTTP traffic statistics.
 *
 * The object represents an aggregation of multiple valid CLF log entries; the time over which the entries are aggregated
 * is not described in the stats themselves.
 */
public class HTTPTrafficStats {

    /**
     * The number of requests aggregated in the stats object.
     */
    private int totalNumberOfHTTPRequestsInSnapshot;

    /**
     * The total size (in bytes) of all the requests aggregated in the stats object.
     */
    private int totalNumberOfBytesRequestedInSnapshot;

    /**
     * A list of all the website hits being accessed by the requests aggregated in the stats object.
     */
    private Map<String, Integer> hitsPerWebsiteSections;

    /**
     * Constructor.
     */
    public HTTPTrafficStats() {
        totalNumberOfHTTPRequestsInSnapshot = 0;
        totalNumberOfBytesRequestedInSnapshot = 0;
        hitsPerWebsiteSections = new HashMap<>();
    }

    /**
     * Test constructor.
     * @param numberOfRequests predefined number of requests.
     * @param numberOfBytes predefined size of requests.
     * @param hitsPerSection predefined map of hits.
     */
    public HTTPTrafficStats(final int numberOfRequests,
                            final int numberOfBytes,
                            final Map<String, Integer> hitsPerSection) {
        totalNumberOfBytesRequestedInSnapshot = numberOfBytes;
        totalNumberOfHTTPRequestsInSnapshot = numberOfRequests;
        hitsPerWebsiteSections = hitsPerSection;
    }

    /**
     * Treat a new log entry in CLF log format and aggregate it in the stats object.
     * @param logEntry the valid {@link CLFLogEntry}; must not be null.
     * @return the current traffic stats object (for chaining).
     */
    public HTTPTrafficStats processNewLogEntry(final CLFLogEntry logEntry) {
        totalNumberOfHTTPRequestsInSnapshot++;
        totalNumberOfBytesRequestedInSnapshot += logEntry.getResponseSize();

        final String section = ConversionUtils.extractHTTPSectionFromRequest(logEntry.getResource());

        if (section != null) {
            int count = hitsPerWebsiteSections.getOrDefault(section, 0);
            hitsPerWebsiteSections.put(section, count + 1);
        }
        return this;
    }

    /**
     * @return the number of requests in the aggregated stats.
     */
    public int getTotalNumberOfHTTPRequestsInSnapshot() {
        return totalNumberOfHTTPRequestsInSnapshot;
    }

    public int getTotalNumberOfBytesRequestedInSnapshot() {
        return totalNumberOfBytesRequestedInSnapshot;
    }
}
