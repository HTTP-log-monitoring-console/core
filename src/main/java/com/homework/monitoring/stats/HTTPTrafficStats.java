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
    private int totalNumberOfHTTPRequests;

    /**
     * The total size (in bytes) of all the requests aggregated in the stats object.
     */
    private int totalSizeOfRequestsInBytes;

    /**
     * The total number of valid HTTP requests (with HTTP statuses varying between [200, 400))
     */
    private int totalNumberOfValidHTTPRequests;

    /**
     * The total number of client error HTTP requests (with HTTP statuses varying between [400,500))
     */
    private int totalNumberOfClientErrorHTTPRequests;

    /**
     * The total number of client error HTTP requests (with HTTP statuses varying between [500,600))
     */
    private int totalNumberOfServerErrorHTTPRequests;

    /**
     * A list of all the website hits being accessed by the requests aggregated in the stats object.
     */
    private Map<String, Integer> hitsPerWebsiteSections;

    /**
     * Constructor.
     */
    public HTTPTrafficStats() {
        totalNumberOfHTTPRequests = 0;
        totalSizeOfRequestsInBytes = 0;
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
        totalSizeOfRequestsInBytes = numberOfBytes;
        totalNumberOfHTTPRequests = numberOfRequests;
        hitsPerWebsiteSections = hitsPerSection;
    }

    /**
     * Treat a new log entry in CLF log format and aggregate it in the stats object.
     * @param logEntry the valid {@link CLFLogEntry}; must not be null.
     * @return the current traffic stats object (for chaining).
     */
    public HTTPTrafficStats processNewLogEntry(final CLFLogEntry logEntry) {
        totalNumberOfHTTPRequests++;
        totalSizeOfRequestsInBytes += logEntry.getResponseSize();

        final int httpStatusCode = logEntry.getHttpStatusCode();
        if (httpStatusCode >= 200 && httpStatusCode < 400) {
            totalNumberOfValidHTTPRequests++;
        } else if (httpStatusCode >= 400 && httpStatusCode < 500) {
            totalNumberOfClientErrorHTTPRequests++;
        } else if (httpStatusCode >= 500 && httpStatusCode < 600) {
            totalNumberOfServerErrorHTTPRequests++;
        }

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
    public int getTotalNumberOfHTTPRequests() {
        return totalNumberOfHTTPRequests;
    }

    /**
     * @return the total size of all requests.
     */
    public int getTotalSizeOfRequestsInBytes() {
        return totalSizeOfRequestsInBytes;
    }

    /**
     * @return the total number of valid requests (2xx & 3xx : https://www.restapitutorial.com/httpstatuscodes.html).
     */
    public int getTotalNumberOfValidHTTPRequests() {
        return totalNumberOfValidHTTPRequests;
    }

    /**
     * @return total number of client error requests (4xx : https://www.restapitutorial.com/httpstatuscodes.html).
     */
    public int getTotalNumberOfClientErrorHTTPRequests() {
        return totalNumberOfClientErrorHTTPRequests;
    }

    /**
     * @return total number of server error requests (5xx : https://www.restapitutorial.com/httpstatuscodes.html).
     */
    public int getTotalNumberOfServerErrorHTTPRequests() {
        return totalNumberOfServerErrorHTTPRequests;
    }

    /**
     * @return the map of hits per section.
     *
     * @Note using Guava's ImmutableMap we can protect this object from being modified (and linked with its parent).
     */
    public Map<String,Integer> getHitsPerWebsiteSections() {
        return hitsPerWebsiteSections;
    }
}
