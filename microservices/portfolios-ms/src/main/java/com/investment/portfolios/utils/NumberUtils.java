package com.investment.portfolios.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;

public final class NumberUtils {

    private NumberUtils() {
        // Evita instancias
    }

    /**
     * Verifica si un BigDecimal es nulo o cero.
     */
    public static boolean isNullOrZero(BigDecimal value) {
        return value == null || BigDecimal.ZERO.compareTo(value) == 0;
    }

    /**
     * Verifica si un BigDecimal es positivo (mayor que cero).
     */
    public static boolean isPositive(BigDecimal value) {
        return value != null && value.compareTo(BigDecimal.ZERO) > 0;
    }

    /**
     * Redondea un BigDecimal a los decimales especificados.
     */
    public static BigDecimal round(BigDecimal value, int scale) {
        if (value == null) {
            return null;
        }
        return value.setScale(scale, RoundingMode.HALF_UP);
    }

    /**
     * Compara dos BigDecimal de forma segura (maneja nulls).
     */
    public static boolean equals(BigDecimal a, BigDecimal b) {
        if (a == null && b == null) return true;
        if (a == null || b == null) return false;
        return a.compareTo(b) == 0;
    }
}