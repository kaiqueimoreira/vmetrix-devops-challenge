package com.vmetrix.svccalc.model;

public record FinancialRequest(
        double principal,
        double rate,
        int periods,
        double years
) {}
