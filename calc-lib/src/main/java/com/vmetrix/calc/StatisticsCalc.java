package com.vmetrix.calc;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.List;

/**
 * Descriptive statistics utilities.
 */
public final class StatisticsCalc {

    private static final int SCALE = 4;
    private static final RoundingMode RM = RoundingMode.HALF_UP;

    private StatisticsCalc() {}

    private static double[] toArray(List<Double> values) {
        if (values == null || values.isEmpty())
            throw new IllegalArgumentException("Values list must not be null or empty");
        return values.stream().mapToDouble(Double::doubleValue).toArray();
    }

    /**
     * Arithmetic mean of a list of values.
     */
    public static BigDecimal mean(List<Double> values) {
        double[] arr = toArray(values);
        double sum = Arrays.stream(arr).sum();
        return BigDecimal.valueOf(sum / arr.length).setScale(SCALE, RM);
    }

    /**
     * Median value of a list (average of two middle values for even-length lists).
     */
    public static BigDecimal median(List<Double> values) {
        double[] arr = toArray(values);
        double[] sorted = Arrays.stream(arr).sorted().toArray();
        int n = sorted.length;
        if (n % 2 == 1) return BigDecimal.valueOf(sorted[n / 2]).setScale(SCALE, RM);
        return BigDecimal.valueOf((sorted[n / 2 - 1] + sorted[n / 2]) / 2).setScale(SCALE, RM);
    }

    /**
     * Population standard deviation.
     */
    public static BigDecimal standardDeviation(List<Double> values) {
        double[] arr = toArray(values);
        double avg = Arrays.stream(arr).average().orElse(0);
        double variance = Arrays.stream(arr).map(v -> Math.pow(v - avg, 2)).average().orElse(0);
        return BigDecimal.valueOf(Math.sqrt(variance)).setScale(SCALE, RM);
    }

    /**
     * Minimum value.
     */
    public static BigDecimal min(List<Double> values) {
        return BigDecimal.valueOf(toArray(values)[0] == toArray(values)[0]
                ? Arrays.stream(toArray(values)).min().orElseThrow()
                : 0).setScale(SCALE, RM);
    }

    /**
     * Maximum value.
     */
    public static BigDecimal max(List<Double> values) {
        return BigDecimal.valueOf(Arrays.stream(toArray(values)).max().orElseThrow()).setScale(SCALE, RM);
    }

    /**
     * Sum of all values.
     */
    public static BigDecimal sum(List<Double> values) {
        return BigDecimal.valueOf(Arrays.stream(toArray(values)).sum()).setScale(SCALE, RM);
    }
}
