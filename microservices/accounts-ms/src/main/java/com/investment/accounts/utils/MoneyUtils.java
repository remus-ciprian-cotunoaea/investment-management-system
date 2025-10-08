package com.investment.accounts.utils;

import lombok.experimental.UtilityClass;

import java.math.BigDecimal;
import java.math.RoundingMode;

@UtilityClass
public class MoneyUtils {

    private static final int SCALE = 2;

    public BigDecimal normalize(BigDecimal v) {
        return v == null ? BigDecimal.ZERO.setScale(SCALE, RoundingMode.HALF_UP)
                : v.setScale(SCALE, RoundingMode.HALF_UP);
    }
}