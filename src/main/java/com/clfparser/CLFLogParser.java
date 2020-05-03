package com.clfparser;

import com.utils.ConversionUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CLFLogParser {
    private static final Pattern REGEX = Pattern
            .compile("^(\\S+) (\\S+) (\\S+) \\[([\\w:/]+\\s[+\\-]\\d{4})\\] \"(\\S+) (\\S+) (\\S+)\" (\\S+) (\\S+)(.)*$");

    public static CLFLogEntry parse(final String line) throws Exception {
        final Matcher matcher = REGEX.matcher(line);
        if (matcher.find()) {
            try {
//                for (int i = 0; i <= matcher.groupCount(); i++) {
//                    System.out.println("------------------------------------");
//                    System.out.println("Group " + i + ": " + matcher.group(i));
//                }
                return new CLFLogEntry(
                        matcher.group(1),
                        matcher.group(2),
                        matcher.group(3),
                        ConversionUtils.parseDate(matcher.group(4)),
                        matcher.group(5),
                        matcher.group(6),
                        matcher.group(7),
                        Integer.parseInt(matcher.group(8)),
                        Integer.parseInt(matcher.group(9)));
            } catch (Exception e) {
            }
        }
        return null;
    }
}