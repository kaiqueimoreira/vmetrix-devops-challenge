package com.vmetrix.svccalc.model;

import java.time.Instant;

public record ApiResponse<T>(String status, T data, Instant timestamp) {
    public static <T> ApiResponse<T> ok(T data) {
        return new ApiResponse<>("ok", data, Instant.now());
    }
}
