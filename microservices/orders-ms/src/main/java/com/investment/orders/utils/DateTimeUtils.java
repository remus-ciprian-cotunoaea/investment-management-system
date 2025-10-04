package com.investment.orders.utils;

import java.time.Instant;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

public final class DateTimeUtils {

    private DateTimeUtils() {}

    /**
     * Convierte un Instant a OffsetDateTime en UTC
     */
    public static OffsetDateTime toOffsetDateTime(Instant instant) {
        return instant == null ? null : instant.atOffset(ZoneOffset.UTC);
    }

    /**
     * Convierte OffsetDateTime a Instant
     */
    public static Instant toInstant(OffsetDateTime dateTime) {
        return dateTime == null ? null : dateTime.toInstant();
    }

    /**
     * Convierte un LocalDate a OffsetDateTime en UTC (00:00)
     */
    public static OffsetDateTime toOffsetDateTime(LocalDate date) {
        return date == null ? null : date.atStartOfDay().atOffset(ZoneOffset.UTC);
    }

    /**
     * Convierte un OffsetDateTime a LocalDate
     */
    public static LocalDate toLocalDate(OffsetDateTime dateTime) {
        return dateTime == null ? null : dateTime.toLocalDate();
    }
}