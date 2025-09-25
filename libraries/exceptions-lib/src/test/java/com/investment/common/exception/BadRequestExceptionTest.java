package com.investment.common.exception;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class BadRequestExceptionTest {

    @Test
    void messageIsSetCorrectly() {
        BadRequestException ex = new BadRequestException("BAD_REQUEST");
        assertEquals("BAD_REQUEST", ex.getMessage());
    }
}