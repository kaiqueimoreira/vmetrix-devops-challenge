package com.vmetrix.calc;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class StatisticsCalcTest {

    private final List<Double> data = List.of(2.0, 4.0, 4.0, 4.0, 5.0, 5.0, 7.0, 9.0);

    @Test void mean()   { assertEquals(new BigDecimal("5.0000"), StatisticsCalc.mean(data)); }
    @Test void median() { assertEquals(new BigDecimal("4.5000"), StatisticsCalc.median(data)); }
    @Test void stdDev() { assertEquals(new BigDecimal("2.0000"), StatisticsCalc.standardDeviation(data)); }
    @Test void sum()    { assertEquals(new BigDecimal("40.0000"), StatisticsCalc.sum(data)); }
    @Test void max()    { assertEquals(new BigDecimal("9.0000"), StatisticsCalc.max(data)); }

    @Test
    void empty_throws() {
        assertThrows(IllegalArgumentException.class,
                () -> StatisticsCalc.mean(List.of()));
    }
}
