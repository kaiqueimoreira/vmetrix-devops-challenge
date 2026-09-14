package com.vmetrix.calc;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

/**
 * Financial calculation utilities.
 */
public final class FinancialCalc {

    private static final int SCALE = 6;
    private static final RoundingMode RM = RoundingMode.HALF_UP;

    private FinancialCalc() {}

    /**
     * Calculates compound interest: A = P * (1 + r/n)^(n*t)
     *
     * @param principal  initial amount
     * @param annualRate annual interest rate (e.g. 0.05 for 5%)
     * @param periods    number of compounding periods per year
     * @param years      duration in years
     * @return final amount
     */
    public static BigDecimal compoundInterest(double principal, double annualRate,
                                               int periods, double years) {
        if (principal < 0) throw new IllegalArgumentException("Principal must be non-negative");
        if (periods <= 0)  throw new IllegalArgumentException("Periods must be positive");
        BigDecimal p = BigDecimal.valueOf(principal);
        BigDecimal r = BigDecimal.valueOf(annualRate / periods).add(BigDecimal.ONE);
        double exponent = periods * years;
        BigDecimal factor = r.pow((int) exponent, new MathContext(SCALE + 4, RM));
        return p.multiply(factor).setScale(2, RM);
    }

    /**
     * Calculates simple interest: I = P * r * t
     *
     * @param principal  initial amount
     * @param annualRate annual interest rate (e.g. 0.05 for 5%)
     * @param years      duration in years
     * @return interest earned (not total)
     */
    public static BigDecimal simpleInterest(double principal, double annualRate, double years) {
        if (principal < 0) throw new IllegalArgumentException("Principal must be non-negative");
        return BigDecimal.valueOf(principal * annualRate * years).setScale(2, RM);
    }

    /**
     * Calculates the monthly instalment for a fixed-rate loan (Price table / PMT formula).
     *
     * @param principal    loan amount
     * @param monthlyRate  monthly interest rate (e.g. 0.01 for 1%)
     * @param months       number of instalments
     * @return monthly payment
     */
    public static BigDecimal pmt(double principal, double monthlyRate, int months) {
        if (principal <= 0) throw new IllegalArgumentException("Principal must be positive");
        if (months <= 0)    throw new IllegalArgumentException("Months must be positive");
        if (monthlyRate == 0) return BigDecimal.valueOf(principal / months).setScale(2, RM);
        double r = monthlyRate;
        double factor = Math.pow(1 + r, months);
        double payment = principal * (r * factor) / (factor - 1);
        return BigDecimal.valueOf(payment).setScale(2, RM);
    }

    /**
     * Applies a percentage discount to a value.
     *
     * @param value      original value
     * @param percentage discount percentage (e.g. 10.0 for 10%)
     * @return discounted value
     */
    public static BigDecimal applyDiscount(double value, double percentage) {
        if (percentage < 0 || percentage > 100)
            throw new IllegalArgumentException("Percentage must be between 0 and 100");
        BigDecimal v = BigDecimal.valueOf(value);
        BigDecimal discount = v.multiply(BigDecimal.valueOf(percentage / 100));
        return v.subtract(discount).setScale(2, RM);
    }

    /**
     * Calculates percentage change between two values.
     *
     * @param from original value
     * @param to   new value
     * @return percentage change (positive = increase, negative = decrease)
     */
    public static BigDecimal percentageChange(double from, double to) {
        if (from == 0) throw new ArithmeticException("Cannot calculate change from zero");
        return BigDecimal.valueOf((to - from) / Math.abs(from) * 100).setScale(4, RM);
    }
}
