package com.investment.users.utils;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

public final class DateTimeUtils {
    private DateTimeUtils() {
        throw new IllegalStateException("Utility class");
    }

    /** UTC now, ideal para createdAt/updatedAt (Instant ya es UTC). */
    public static Instant nowUtc() {
        return Instant.now();
    }

    /** UTC now truncado a milisegundos (útil si tu DB/JPA recorta precisión). */
    public static Instant nowUtcMillis() {
        return Instant.now().truncatedTo(ChronoUnit.MILLIS);
    }
}