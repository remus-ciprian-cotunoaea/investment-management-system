package com.investment.accounts.utils;

import lombok.experimental.UtilityClass;

import java.math.BigDecimal;
import java.math.RoundingMode;

@UtilityClass
public class FxUtils {

    public BigDecimal applyRate(BigDecimal amount, BigDecimal rate) {
        if (amount == null || rate == null) return BigDecimal.ZERO;
        return amount.multiply(rate).setScale(2, RoundingMode.HALF_UP);
    }
}