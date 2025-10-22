package com.investment.users.utils;

import java.time.Instant;

/**
 * Utility class providing date and time helper methods.
 *
 * <p>This class centralizes approaches to retrieve current timestamps so the
 * rest of the application can rely on a single point for obtaining the current
 * time. The class is final and has a private constructor to prevent
 * instantiation.</p>
 *
 * <p>Use the {@link #nowUtc()} method to get the current UTC instant. In the
 * future this class can be extended to provide test hooks or to use a
 * configurable clock.</p>
 *
 * @author Remus-Ciprian Cotunoaea
 * @since October 20, 2025
 */
public final class DateTimeUtils {

    /**
     * Private constructor to prevent instantiation of this utility class.
     * Calling this constructor will always result in an IllegalStateException.
     *
     * @author Remus-Ciprian Cotunoaea
     * @since October 20, 2025
     */
    private DateTimeUtils() {
        throw new IllegalStateException(Constants.MESSAGE_ERROR_UTILITY);
    }

    /**
     * Returns the current instant in UTC.
     *
     * <p>This method delegates to {@link Instant#now()} which obtains the
     * current instant from the system clock. Callers should use this method
     * rather than calling {@code Instant.now()} directly to centralize time
     * access and make future testing or clock substitution easier.</p>
     *
     * @return the current {@link Instant} representing now in UTC
     * @author Remus-Ciprian Cotunoaea
     * @since October 20, 2025
     */
    public static Instant nowUtc() {
        return Instant.now();
    }
}