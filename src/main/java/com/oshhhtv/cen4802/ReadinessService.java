package com.oshhhtv.cen4802;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ReadinessService {

    private static final Logger logger =
            LoggerFactory.getLogger(ReadinessService.class);

    private static final int RELEASE_THRESHOLD = 95;

    public ReadinessResult evaluate(
            int passedTests,
            int totalTests,
            int criticalDefects) {

        logger.debug(
                "Evaluating readiness: passedTests={}, totalTests={}, criticalDefects={}, releaseThreshold={}",
                passedTests,
                totalTests,
                criticalDefects,
                RELEASE_THRESHOLD
        );

        if (totalTests <= 0) {
            logger.warn(
                    "Rejected readiness evaluation: totalTests={} must be greater than zero; passedTests={}, criticalDefects={}",
                    totalTests,
                    passedTests,
                    criticalDefects
            );

            throw new IllegalArgumentException(
                    "Total tests must be greater than zero."
            );
        }

        if (passedTests < 0 || passedTests > totalTests) {
            logger.warn(
                    "Rejected readiness evaluation: passedTests={} is outside valid range 0..{}; criticalDefects={}",
                    passedTests,
                    totalTests,
                    criticalDefects
            );

            throw new IllegalArgumentException(
                    "Passed tests must be between zero and the total number of tests."
            );
        }

        if (criticalDefects < 0) {
            logger.warn(
                    "Rejected readiness evaluation: criticalDefects={} cannot be negative; passedTests={}, totalTests={}",
                    criticalDefects,
                    passedTests,
                    totalTests
            );

            throw new IllegalArgumentException(
                    "Critical defects cannot be negative."
            );
        }

        double passRate = (passedTests * 100.0) / totalTests;
        passRate = Math.round(passRate * 10.0) / 10.0;

        logger.debug(
                "Readiness calculation completed: passRate={}%, releaseThreshold={}%, criticalDefects={}",
                passRate,
                RELEASE_THRESHOLD,
                criticalDefects
        );

        List<String> blockers = new ArrayList<>();

        if (passRate < RELEASE_THRESHOLD) {
            blockers.add(
                    "The test pass rate must reach at least "
                            + RELEASE_THRESHOLD
                            + "% before release."
            );
        }

        if (criticalDefects > 0) {
            blockers.add(
                    "All critical defects must be resolved before release."
            );
        }

        if (!blockers.isEmpty()) {
            logger.warn(
                    "Release not ready: passRate={}%, criticalDefects={}, blockers={}",
                    passRate,
                    criticalDefects,
                    blockers
            );

            return new ReadinessResult(
                    passRate,
                    criticalDefects,
                    "NOT READY",
                    blockers.get(0),
                    List.copyOf(blockers)
            );
        }

        logger.info(
                "Release ready: passRate={}%, criticalDefects={}",
                passRate,
                criticalDefects
        );

        return new ReadinessResult(
                passRate,
                criticalDefects,
                "READY",
                "The test pass rate meets the "
                        + RELEASE_THRESHOLD
                        + "% threshold and no critical defects remain.",
                List.of()
        );
    }
}