package com.investment.orders.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;

public final class NumberUtils {

    private NumberUtils() {}

    /**
     * Redondea un BigDecimal a la escala especificada
     */
    public static BigDecimal round(BigDecimal value, int scale) {
        return value == null ? null : value.setScale(scale, RoundingMode.HALF_UP);
    }

    /**
     * Verifica si un BigDecimal es positivo (> 0)
     */
    public static boolean isPositive(BigDecimal value) {
        return value != null && value.compareTo(BigDecimal.ZERO) > 0;
    }

    /**
     * Verifica si un BigDecimal es mayor o igual a cero
     */
    public static boolean isZeroOrPositive(BigDecimal value) {
        return value != null && value.compareTo(BigDecimal.ZERO) >= 0;
    }
}