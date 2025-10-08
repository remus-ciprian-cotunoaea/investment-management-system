package com.investment.accounts.utils;

import lombok.experimental.UtilityClass;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@UtilityClass
public class DateTimeUtils {

    public OffsetDateTime now() {
        return OffsetDateTime.now(ZoneOffset.UTC);
    }

    public OffsetDateTime today() {
        return now();
    }
}