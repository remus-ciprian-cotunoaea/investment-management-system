package com.investment.common.exception;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class BusinessExceptionTest {

    @Test
    void messageIsSetCorrectly() {
        BusinessException ex = new BusinessException("business rule violated");
        assertEquals("business rule violated", ex.getMessage());
    }
}