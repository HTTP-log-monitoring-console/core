package com.clfparser;

import com.utils.ConversionUtils;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import static org.junit.jupiter.api.Assertions.*;

class CLFLogParserTest {
    private static final String[] INPUTS = {
            "127.0.0.1 - james [09/May/2018:16:00:39 +0000] \"GET /report HTTP/1.0\" 200 123\n",
            "127.0.0.1 - jill [09/May/2018:16:00:41 +0000] \"GET /api/user HTTP/1.0\" 200 234\n",
            "127.0.0.1 - frank [09/May/2018:16:00:42 +0000] \"POST /api/user HTTP/1.0\" 200 34\n",
            "127.0.0.1 - mary [09/May/2018:16:00:42 +0000] \"POST /api/user HTTP/1.0\" 503 12\n",
            "127.0.0.1 - james [09/janv./2018:16:00:39 +0000] \"GET /report HTTP/1.0\" 200 123\n",
            "127.0.0.1 - jill [09/févr./2018:16:00:41 +0000] \"GET /api/user HTTP/1.0\" 200 234\n",
            "127.0.0.1 - jill [09/fév/2018:16:00:41 +0000] \"GET /api/user HTTP/1.0\" 200 234",
            "127.0.0.1 - frank [09/mars/2018:16:00:42 +0000] \"POST /api/user HTTP/1.0\" 200 34",
            "127.0.0.1 - mary [09/avr./2018:16:00:42 +0000] \"POST /api/user HTTP/1.0\" 503 12",
            "127.0.0.1 - frank [09/déc/2018:16:00:42 +0000] \"POST /api/user HTTP/1.0\" 200 34",
            "127.0.0.1 - jill [09/mars/2018:16:00:41 +0000] \"GET /api/user HTTP/1.0\" 200 234"
    };

    private final CLFLogParser parser = new CLFLogParser();

    @org.junit.jupiter.api.Test
    void parseInput1() throws Exception {

        assertEquals(new CLFLogEntry(
                "127.0.0.1",
                "-",
                "james",
                ZonedDateTime.of(2018, 5, 9, 16, 0, 39, 0, ZoneOffset.ofHours(0)),
                "GET",
                "/report",
                "HTTP/1.0",
                200,
                123
        ), parser.parse(INPUTS[0]));

        /* //test for French locale (using accents)
        assertEquals(new CLFLogEntry(
                        "127.0.0.1",
                        "-",
                        "jill",
                        ZonedDateTime.of(2018, 2, 9, 16, 0, 41, 0, ZoneOffset.ofHours(0)),
                        "GET",
                        "/api/user",
                        "HTTP/1.0",
                        200,
                        234
                ), parser.parse(INPUTS[5]));
         */
    }
}