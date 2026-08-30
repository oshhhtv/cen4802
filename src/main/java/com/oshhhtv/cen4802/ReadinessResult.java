package com.oshhhtv.cen4802;

public record ReadinessResult(
        double passRate,
        String status,
        String message
) {
}