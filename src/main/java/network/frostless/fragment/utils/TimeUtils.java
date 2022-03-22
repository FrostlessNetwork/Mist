package network.frostless.fragment.utils;

import java.time.OffsetDateTime;

public enum TimeUtils {
    SHORT_TIME("<t:%d:t>"),
    LONG_TIME("<t:%d:T>"),
    SHORT_DATE("<t:%d:d>"),
    LONG_DATE("<t:%d:D>"),
    SHORT_DT("<t:%d:f>"),
    LONG_DT("<t:%d:F>"),
    RELATIVE("<t:%d:R>");

    private final String format;

    TimeUtils(String format) {
        this.format = format;
    }

    public String parse(long timestamp) {
        return String.format(format, timestamp);
    }

    public String parse(OffsetDateTime offsetDateTime) {
        return String.format(format, offsetDateTime.toEpochSecond());
    }
}
