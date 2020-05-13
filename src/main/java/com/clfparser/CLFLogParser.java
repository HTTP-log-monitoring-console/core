package com.clfparser;

import com.utils.ConversionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.ZonedDateTime;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parser for log lines.
 */
public class CLFLogParser {
    /**
     * Logger.
     */
    private static Logger logger = LogManager.getLogger(CLFLogParser.class);

    /**
     * Pattern used to parse CLF logs; more info on what exactly each part is doing here: https://en.wikipedia.org/wiki/Common_Log_Format#Example
     */
    private static final Pattern REGEX = Pattern
            .compile("^(\\S+) (\\S+) (\\S+) \\[([\\w:/]+\\s[+\\-]\\d{4})\\] \"(\\S+) (\\S+) (\\S+)\" (\\S+) (\\S+)(.)*$");


    /**
     * Run parser and apply pattern
     *
     * @param line a full log line (from first character to line ending character(s)
     * @return an entry object containing all log line parameters
     * @throws Exception
     */
    public static CLFLogEntry parse(final String line) throws Exception {
        logger.debug("Parsing log file [" + line + "]");

        final Matcher matcher = REGEX.matcher(line);
        if (matcher.find()) {
            logger.debug("Log parser found following parameters:");
            for (int i = 0; i <= matcher.groupCount(); i++) {
                logger.debug("Group " + i + ": " + matcher.group(i));
            }

            try {
                return new CLFLogEntry(
                        matcher.group(1),
                        matcher.group(2),
                        matcher.group(3),
                        parseDate(matcher.group(4)),
                        matcher.group(5),
                        matcher.group(6),
                        matcher.group(7),
                        parseInt(matcher.group(8)),
                        parseInt(matcher.group(9)));
            } catch (final Exception exception) {
                logger.warn("Log line failed to parse correctly: " + exception.getMessage());
            }
        }
        return null;
    }

    /**
     * Special treatment for dashes "-".
     * @param value string representation of int.
     * @return int value of string.
     */
    private static Integer parseInt(final String value) {
        if (value == null || "-".equals(value)) {
            return 0;
        }
        return Integer.parseInt(value);
    }

    /**
     * Special treatment for dashes "-".
     * @param value string representation of date.
     * @return date.
     */
    private static ZonedDateTime parseDate(final String value) {
        if (value == null || "-".equals(value)) {
            return null;
        }
        return ConversionUtils.parseDate(value);
    }
}