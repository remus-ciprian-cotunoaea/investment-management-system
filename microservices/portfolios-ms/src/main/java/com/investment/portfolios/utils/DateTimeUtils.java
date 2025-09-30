package com.investment.portfolios.utils;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

public final class DateTimeUtils {

    private DateTimeUtils() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static OffsetDateTime toOffsetDateTimeUTC(Instant instant) {
        return instant == null ? null : OffsetDateTime.ofInstant(instant, ZoneOffset.UTC);
    }
}
