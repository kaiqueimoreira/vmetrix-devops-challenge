package com.vmetrix.calc;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;

class FinancialCalcTest {

    @Test
    void compoundInterest_basic() {
        BigDecimal result = FinancialCalc.compoundInterest(1000, 0.05, 12, 1);
        assertTrue(result.compareTo(BigDecimal.valueOf(1051)) > 0);
        assertTrue(result.compareTo(BigDecimal.valueOf(1052)) < 0);
    }

    @Test
    void simpleInterest_basic() {
        BigDecimal result = FinancialCalc.simpleInterest(1000, 0.10, 2);
        assertEquals(new BigDecimal("200.00"), result);
    }

    @Test
    void pmt_noInterest() {
        BigDecimal result = FinancialCalc.pmt(1200, 0, 12);
        assertEquals(new BigDecimal("100.00"), result);
    }

    @Test
    void pmt_withInterest() {
        BigDecimal result = FinancialCalc.pmt(10000, 0.01, 12);
        assertTrue(result.compareTo(BigDecimal.valueOf(888)) > 0);
        assertTrue(result.compareTo(BigDecimal.valueOf(890)) < 0);
    }

    @Test
    void applyDiscount_10pct() {
        assertEquals(new BigDecimal("90.00"), FinancialCalc.applyDiscount(100, 10));
    }

    @Test
    void applyDiscount_invalid() {
        assertThrows(IllegalArgumentException.class,
                () -> FinancialCalc.applyDiscount(100, -5));
    }

    @Test
    void percentageChange_increase() {
        BigDecimal result = FinancialCalc.percentageChange(100, 150);
        assertEquals(new BigDecimal("50.0000"), result);
    }

    @Test
    void percentageChange_decrease() {
        BigDecimal result = FinancialCalc.percentageChange(200, 100);
        assertEquals(new BigDecimal("-50.0000"), result);
    }

    @Test
    void percentageChange_fromZero() {
        assertThrows(ArithmeticException.class,
                () -> FinancialCalc.percentageChange(0, 100));
    }
}
