package com.clfparser;

import com.utils.ConversionUtils;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import static org.junit.jupiter.api.Assertions.*;

class CLFLogParserTest {
    private static final String INPUT1 = "127.0.0.1 - james [09/May/2018:16:00:39 +0000] \"GET /report HTTP/1.0\" 200 123\n";
    private static final String INPUT2 = "127.0.0.1 - jill [09/May/2018:16:00:41 +0000] \"GET /api/user HTTP/1.0\" 200 234\n";
    private static final String INPUT3 = "127.0.0.1 - frank [09/May/2018:16:00:42 +0000] \"POST /api/user HTTP/1.0\" 200 34\n";
    private static final String INPUT4 = "127.0.0.1 - mary [09/May/2018:16:00:42 +0000] \"POST /api/user HTTP/1.0\" 503 12\n";

    private final CLFLogParser parser = new CLFLogParser();

    @org.junit.jupiter.api.Test
    void parseInput1() throws Exception {
        final CLFLogEntry parseResult = parser.parse(INPUT1);

        CLFLogEntry expected = new CLFLogEntry(
                "127.0.0.1",
                "-",
                "james",
                ZonedDateTime.of(2018, 5, 9, 16, 0, 39, 0, ZoneOffset.ofHours(0)),
                "GET",
                "/report",
                "HTTP/1.0",
                200,
                123
        );

        assertEquals(expected, parseResult);
    }
}