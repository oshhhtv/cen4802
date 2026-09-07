package com.oshhhtv.cen4802;

import org.springframework.stereotype.Service;

@Service
public class ReadinessService {

    private static final int RELEASE_THRESHOLD = 95;

    public ReadinessResult evaluate(
            int passedTests,
            int totalTests,
            int criticalDefects) {

        if (totalTests <= 0) {
            throw new IllegalArgumentException(
                    "Total tests must be greater than zero."
            );
        }

        if (passedTests < 0 || passedTests > totalTests) {
            throw new IllegalArgumentException(
                    "Passed tests must be between zero and the total number of tests."
            );
        }

        if (criticalDefects < 0) {
            throw new IllegalArgumentException(
                    "Critical defects cannot be negative."
            );
        }

        double passRate = (passedTests * 100.0) / totalTests;
        passRate = Math.round(passRate * 10.0) / 10.0;

        if (passRate < RELEASE_THRESHOLD) {
            return new ReadinessResult(
                    passRate,
                    criticalDefects,
                    "NOT READY",
                    "The test pass rate must reach at least "
                            + RELEASE_THRESHOLD
                            + "% before release."
            );
        }

        if (criticalDefects > 0) {
            return new ReadinessResult(
                    passRate,
                    criticalDefects,
                    "NOT READY",
                    "All critical defects must be resolved before release."
            );
        }

        return new ReadinessResult(
                passRate,
                criticalDefects,
                "READY",
                "The test pass rate meets the "
                        + RELEASE_THRESHOLD
                        + "% threshold and no critical defects remain."
        );
    }
}