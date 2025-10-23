package com.investment.portfolios.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Utility class with common BigDecimal helper methods.
 *
 * <p>This final utility class provides null-safe checks and common operations
 * for {@link java.math.BigDecimal} values such as zero checks, positivity check,
 * scale rounding and value equality using numeric comparison.</p>
 *
 * <p>All methods are static and the class is not instantiable.</p>
 *
 * @author Remus-Ciprian Cotunoaea
 * @since October 22, 2025
 */
public final class NumberUtils {

    /**
     * Private constructor to prevent instantiation of this utility class.
     *
     * @author Remus-Ciprian Cotunoaea
     * @since October 22, 2025
     */
    private NumberUtils() {

    }

    /**
     * Return true if the provided BigDecimal is null or equals zero.
     *
     * <p>The comparison uses {@link java.math.BigDecimal#compareTo} against
     * {@link java.math.BigDecimal#ZERO} to correctly handle scale differences
     * (e.g. 0.0 equals 0).</p>
     *
     * @param value the BigDecimal to check; may be {@code null}
     * @return {@code true} if {@code value} is {@code null} or numerically zero, otherwise {@code false}
     *
     * @author Remus-Ciprian Cotunoaea
     * @since October 22, 2025
     */
    public static boolean isNullOrZero(BigDecimal value) {
        return value == null || BigDecimal.ZERO.compareTo(value) == 0;
    }

    /**
     * Return true if the provided BigDecimal is strictly greater than zero.
     *
     * <p>Null values are treated as non-positive and will return {@code false}.</p>
     *
     * @param value the BigDecimal to check; may be {@code null}
     * @return {@code true} if {@code value} is non-null and greater than zero, otherwise {@code false}
     *
     * @author Remus-Ciprian Cotunoaea
     * @since October 22, 2025
     */
    public static boolean isPositive(BigDecimal value) {
        return value != null && value.compareTo(BigDecimal.ZERO) > 0;
    }

    /**
     * Round the provided BigDecimal to the given scale using HALF_UP rounding mode.
     *
     * <p>If the input is {@code null}, this method returns {@code null}. The method
     * delegates to {@link BigDecimal#setScale(int, java.math.RoundingMode)} with
     * {@link RoundingMode#HALF_UP}.</p>
     *
     * @param value the BigDecimal to round; may be {@code null}
     * @param scale the scale (number of decimal places) to round to; must be >= 0
     * @return the rounded BigDecimal, or {@code null} if the input was {@code null}
     * @throws ArithmeticException if the scale is negative (delegated from BigDecimal)
     *
     * @author Remus-Ciprian Cotunoaea
     * @since October 22, 2025
     */
    public static BigDecimal round(BigDecimal value, int scale) {
        if (value == null) {
            return null;
        }
        return value.setScale(scale, RoundingMode.HALF_UP);
    }

    /**
     * Null-safe numeric equality check for BigDecimal values.
     *
     * <p>Returns {@code true} if both values are {@code null}. If exactly one value
     * is {@code null} returns {@code false}. Otherwise compares numerically using
     * {@link BigDecimal#compareTo} so that values like 2.0 and 2.00 are considered equal.</p>
     *
     * @param a the first BigDecimal, may be {@code null}
     * @param b the second BigDecimal, may be {@code null}
     * @return {@code true} if both are null or numerically equal; {@code false} otherwise
     *
     * @author Remus-Ciprian Cotunoaea
     * @since October 22, 2025
     */
    public static boolean equals(BigDecimal a, BigDecimal b) {
        if (a == null && b == null) return true;
        if (a == null || b == null) return false;
        return a.compareTo(b) == 0;
    }
}