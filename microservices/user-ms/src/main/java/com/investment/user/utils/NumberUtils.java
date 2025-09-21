package com.investment.user.utils;

public final class NumberUtils {

    private NumberUtils() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Returns true if the given number is positive (> 0).
     * Useful for validating inputs such as page size or numeric fields.
     */
    public static boolean isPositive(int number) {
        return number > 0;
    }
}