package xyz.asitanokibou.common.lang.utils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class DateUtils {
    public static long toTimestamp(LocalDateTime localDateTime) {
        return toInstant(localDateTime).toEpochMilli();
    }
    public static long toEpochSecond(LocalDateTime localDateTime) {
        return toInstant(localDateTime).getEpochSecond();
    }

    public static LocalDateTime fromTimestamp(long timestamp) {
        return fromInstant(Instant.ofEpochMilli(timestamp));
    }

    public static Instant toInstant(LocalDateTime localDateTime) {
        return localDateTime.atZone(ZoneId.systemDefault()).toInstant();
    }

    public static LocalDateTime fromInstant(Instant instant) {
        return instant.atZone(ZoneId.systemDefault()).toLocalDateTime();
    }
}
