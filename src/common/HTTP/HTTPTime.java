package common.HTTP;

import java.time.*;
import java.time.format.DateTimeFormatter;

/**
 * Class that stores a time value and provides methods to easily parse to and from HTTP standard format RFC 1123.
 */
public class HTTPTime {

    // DateTimeFormatter for RFC 1123 format. Use DateTimeFormatter for better thread safety.
    private static final DateTimeFormatter FORMAT = DateTimeFormatter.RFC_1123_DATE_TIME;
    // GMT ZoneID
    private static final ZoneId gmtZone = ZoneId.of("GMT");

    // ZonedDateTime object stores time of current object
    public final ZonedDateTime time;

    /**
     * Constructor for HTTPTime with given time as long value.
     * @param time  Given time in milliseconds since epoch.
     */
    public HTTPTime(Long time) {
        Instant i = Instant.ofEpochMilli(time);
        this.time = ZonedDateTime.ofInstant(i, gmtZone);
    }

    /**
     * Constructor for HTTPTime with given time as String value.
     * @param timeStr   Time string in RFC 1123 format.
     */
    public HTTPTime(String timeStr) {
        this.time = ZonedDateTime.parse(timeStr, FORMAT);
    }

    /**
     * Constructor for HTTPTime with given dateTime as value.
     * @param dateTime  ZonedDateTime from which to construct a HTTPTime object.
     */
    public HTTPTime(ZonedDateTime dateTime) {
        this.time = ZonedDateTime.of(dateTime.toLocalDateTime(), gmtZone);
    }

    /**
     * Static method to return HTTPTime representing time of now.
     * @return  HTTPTime object representing time of now
     */
    public static HTTPTime getCurrentTime() {
        return new HTTPTime(ZonedDateTime.now(gmtZone));
    }

    @Override
    public String toString() {
        return this.time.format(FORMAT);
    }
}
