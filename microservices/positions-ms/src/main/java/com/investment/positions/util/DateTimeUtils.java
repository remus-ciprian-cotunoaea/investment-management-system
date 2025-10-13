package com.investment.positions.util;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

/** Utilidades mínimas para manejo de fechas en positions-ms. */
public final class DateTimeUtils {

    private DateTimeUtils() {
        throw new IllegalStateException("Utility class");
    }

    /** Devuelve el timestamp actual en UTC. */
    public static OffsetDateTime nowUtc() {
        return OffsetDateTime.now(ZoneOffset.UTC);
    }
}