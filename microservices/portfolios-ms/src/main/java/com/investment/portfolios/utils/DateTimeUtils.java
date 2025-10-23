package com.investment.portfolios.utils;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

/**
 * Utility class that provides common date-time conversion helpers.
 *
 * <p>This class centralizes conversions between Instant and other date-time
 * types using UTC as the reference zone. All methods are static and the
 * class is not instantiable.</p>
 *
 * @author Remus-Ciprian Cotunoaea
 * @since October 22, 2025
 */
public final class DateTimeUtils {

    /**
     * Private constructor to prevent instantiation of this utility class.
     *
     * <p>If called reflectively, this constructor throws an
     * UnsupportedOperationException.</p>
     */
    private DateTimeUtils() {

        throw new UnsupportedOperationException("Utility class");
    }

    /**
     * Convert an {@link Instant} to an {@link OffsetDateTime} in UTC.
     *
     * @param instant the instant to convert; may be {@code null}
     * @return an {@link OffsetDateTime} representing the same instant in UTC,
     *         or {@code null} if the input instant is {@code null}
     */
    public static OffsetDateTime toOffsetDateTimeUTC(Instant instant) {
        return instant == null ? null : OffsetDateTime.ofInstant(instant, ZoneOffset.UTC);
    }
}
