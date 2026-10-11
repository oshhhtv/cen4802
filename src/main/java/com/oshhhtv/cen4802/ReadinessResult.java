package com.oshhhtv.cen4802;

import java.util.List;

public record ReadinessResult(
        double passRate,
        int criticalDefects,
        String status,
        String message,
        List<String> blockers
) {
}
