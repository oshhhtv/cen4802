package com.oshhhtv.cen4802;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ReadinessServiceTests {

    private final ReadinessService service = new ReadinessService();

    @Test
    void releaseIsReadyAtThresholdWithNoCriticalDefects() {
        ReadinessResult result = service.evaluate(95, 100, 0);

        assertEquals("READY", result.status());
        assertEquals(95.0, result.passRate(), 0.001);
        assertEquals(0, result.criticalDefects());
        assertTrue(result.blockers().isEmpty());
    }

    @Test
    void releaseIsNotReadyBelowPassThreshold() {
        ReadinessResult result = service.evaluate(94, 100, 0);

        assertEquals("NOT READY", result.status());
        assertEquals(94.0, result.passRate(), 0.001);
        assertTrue(result.message().contains("95%"));
        assertEquals(List.of(result.message()), result.blockers());
    }

    @Test
    void criticalDefectsBlockOtherwiseReadyRelease() {
        ReadinessResult result = service.evaluate(100, 100, 2);

        assertEquals("NOT READY", result.status());
        assertEquals(2, result.criticalDefects());
        assertTrue(result.message().contains("critical defects"));
        assertEquals(List.of(result.message()), result.blockers());
    }

    @Test
    void allReleaseBlockersAreReportedTogether() {
        ReadinessResult result = service.evaluate(90, 100, 2);

        assertEquals("NOT READY", result.status());
        assertEquals(90.0, result.passRate(), 0.001);
        assertEquals(2, result.criticalDefects());
        assertEquals(
                List.of(
                        "The test pass rate must reach at least 95% before release.",
                        "All critical defects must be resolved before release."
                ),
                result.blockers()
        );
    }

    @Test
    void passedTestsCannotExceedTotalTests() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> service.evaluate(101, 100, 0)
        );

        assertEquals(
                "Passed tests must be between zero and the total number of tests.",
                exception.getMessage()
        );
    }

    @Test
    void criticalDefectsCannotBeNegative() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> service.evaluate(95, 100, -1)
        );

        assertEquals(
                "Critical defects cannot be negative.",
                exception.getMessage()
        );
    }

    @Test
    void totalTestsMustBeGreaterThanZero() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> service.evaluate(0, 0, 0)
        );

        assertEquals(
                "Total tests must be greater than zero.",
                exception.getMessage()
        );
    }
}
