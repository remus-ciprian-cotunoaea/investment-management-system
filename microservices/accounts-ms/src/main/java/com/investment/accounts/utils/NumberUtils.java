package com.investment.accounts.utils;

import lombok.experimental.UtilityClass;

import java.math.BigDecimal;

@UtilityClass
public class NumberUtils {

    public boolean isZeroOrNull(BigDecimal v) {
        return v == null || v.compareTo(BigDecimal.ZERO) == 0;
    }

    public String trimOrNull(String s) {
        if (s == null) return null;
        String t = s.trim();
        return t.isEmpty() ? null : t;
    }
}