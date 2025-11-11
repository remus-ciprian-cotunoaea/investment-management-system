package com.investment.orders.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;

public final class NumberUtils {

    private NumberUtils() {}

    public static BigDecimal round(BigDecimal value, int scale) {
        return value == null ? null : value.setScale(scale, RoundingMode.HALF_UP);
    }

    public static boolean isPositive(BigDecimal value) {
        return value != null && value.compareTo(BigDecimal.ZERO) > Constants.INT_ZERO;
    }

    public static boolean isZeroOrPositive(BigDecimal value) {
        return value != null && value.compareTo(BigDecimal.ZERO) >= Constants.INT_ZERO;
    }
}