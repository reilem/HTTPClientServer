package com.reinert.common.HTTP;

import java.time.*;
import java.time.format.DateTimeFormatter;

public class HTTPTime {

    // Use DateTimeFormatter for better thread safety
    public static final DateTimeFormatter FORMAT = DateTimeFormatter.RFC_1123_DATE_TIME;

    public final ZonedDateTime time;

    public HTTPTime(Long time) {
        Instant i = Instant.ofEpochMilli(time);
        this.time = ZonedDateTime.ofInstant(i, ZoneId.of("GMT"));
    }

    public HTTPTime(String timeStr) {
        this.time = ZonedDateTime.parse(timeStr, FORMAT);
    }

    public HTTPTime(ZonedDateTime dateTime) {
        this.time = dateTime;
    }

    public static HTTPTime getCurrentTime() {
        return new HTTPTime(ZonedDateTime.now(ZoneId.of("GMT")));
    }

    @Override
    public String toString() {
        return this.time.format(FORMAT);
    }
}
