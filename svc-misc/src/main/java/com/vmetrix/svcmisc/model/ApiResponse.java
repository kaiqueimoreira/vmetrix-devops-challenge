package com.vmetrix.svcmisc.model;

import java.time.Instant;

public record ApiResponse<T>(String status, T data, Instant timestamp) {
    public static <T> ApiResponse<T> ok(T data) {
        return new ApiResponse<>("ok", data, Instant.now());
    }
    public static <T> ApiResponse<T> error(T data) {
        return new ApiResponse<>("error", data, Instant.now());
    }
}
