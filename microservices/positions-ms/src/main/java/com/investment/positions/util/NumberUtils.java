package com.investment.positions.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

/** Utilidades mínimas numéricas para positions-ms. */
public final class NumberUtils {

    private NumberUtils() {
        throw new IllegalStateException("Utility class");
    }

    /** Redondea un BigDecimal al número de decimales indicado, usando HALF_UP. */
    public static BigDecimal scale(BigDecimal value, int scale) {
        return value == null ? null : value.setScale(scale, RoundingMode.HALF_UP);
    }

    /** Devuelve true si el valor es nulo o igual a cero. */
    public static boolean isNullOrZero(BigDecimal value) {
        return value == null || value.compareTo(BigDecimal.ZERO) == 0;
    }
}