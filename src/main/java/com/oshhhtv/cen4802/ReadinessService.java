package com.oshhhtv.cen4802;

import org.springframework.stereotype.Service;

@Service
public class ReadinessService {

    public ReadinessResult evaluate(int passedTests, int totalTests) {
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

        double passRate = (passedTests * 100.0) / totalTests;
        passRate = Math.round(passRate * 10.0) / 10.0;

        if (passRate >= 90.0) {
            return new ReadinessResult(
                    passRate,
                    "READY",
                    "The test pass rate meets the 90% release threshold."
            );
        }

        return new ReadinessResult(
                passRate,
                "NOT READY",
                "The test pass rate must reach at least 90% before release."
        );
    }
}