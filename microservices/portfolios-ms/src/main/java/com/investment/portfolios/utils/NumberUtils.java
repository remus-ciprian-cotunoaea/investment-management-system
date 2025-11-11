package com.investment.portfolios.utils;

import java.math.BigDecimal;

/**
 * Utility methods for numeric checks used throughout the portfolios microservice.
 *
 * <p>This final utility class provides null-safe and simple numeric predicates that
 * are commonly used for input validation and guard clauses (for example: checking
 * page sizes, numeric parameters, or values coming from external sources).</p>
 *
 * <p>The class is not instantiable and exposes only static helper methods.</p>
 *
 * @author Remus-Ciprian Cotunoaea
 * @since October 22, 2025
 */
public final class NumberUtils {
    /**
     * Private constructor to prevent instantiation of this utility class.
     *
     * @throws UnsupportedOperationException always thrown to indicate that this
     *         utility class should not be instantiated
     * @author Remus-Ciprian Cotunoaea
     * @since October 22, 2025
     */
    private NumberUtils() {
        throw new UnsupportedOperationException("Utility class");
    }

    /**
     * Checks whether the provided {@link BigDecimal} is null or equal to zero.
     *
     * <p>Null is treated as equivalent to zero for convenience in validation logic where
     * missing values should be considered zero-like.</p>
     *
     * @param value the {@link BigDecimal} to check; may be null
     * @return {@code true} if {@code value} is {@code null} or numerically equal to {@code BigDecimal.ZERO},
     *         {@code false} otherwise
     * @author Remus-Ciprian Cotunoaea
     * @since October 22, 2025
     */
    public static boolean isNullOrZero(BigDecimal value) {
        return value == null || BigDecimal.ZERO.compareTo(value) == 0;
    }

    /**
     * Determines whether an integer is non-positive.
     *
     * <p>Non-positive means less than or equal to zero. This is helpful for validating
     * page numbers, sizes or other integer inputs that must be strictly positive.</p>
     *
     * @param n the integer to evaluate
     * @return {@code true} if {@code n} is less than or equal to zero, {@code false} otherwise
     * @author Remus-Ciprian Cotunoaea
     * @since October 22, 2025
     */
    public static boolean isNonPositive(int n) {
        return n <= 0;
    }
}