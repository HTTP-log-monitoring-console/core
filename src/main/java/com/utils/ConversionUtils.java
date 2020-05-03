package com.utils;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class ConversionUtils {
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MMM/yyyy:HH:mm:ss Z");

    public static ZonedDateTime parseDate(String date) {
        System.out.println("Parsing date " + date);
        return ZonedDateTime.parse(date, DATE_TIME_FORMATTER);
    }
}