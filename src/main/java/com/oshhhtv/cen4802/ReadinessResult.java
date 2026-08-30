package com.oshhhtv.cen4802;

public record ReadinessResult(
        double passRate,
        int criticalDefects,
        String status,
        String message
) {
}