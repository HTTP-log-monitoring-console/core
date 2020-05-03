package com.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Class used to host useful methods for converting values.
 */
public class ConversionUtils {
    /**
     * Logger.
     */
    private static Logger logger = LogManager.getLogger(ConversionUtils.class);

    /**
     * Date formatter according to CLF log format.
     */
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MMM/yyyy:HH:mm:ss Z");

    /**
     * Parse a date in string format according to CLF log date format.
     * @param date string date.
     * @return the date in Java format (including timezone).
     */
    public static ZonedDateTime parseDate(final String date) {
        final ZonedDateTime objectDate = ZonedDateTime.parse(date, DATE_TIME_FORMATTER);
        logger.debug("Parsed date from string [" + date + "] to Java object " + objectDate.toString());
        return objectDate;
    }
}