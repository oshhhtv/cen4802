package com.oshhhtv.cen4802;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/readiness")
public class ReadinessController {

    private static final Logger logger =
            LoggerFactory.getLogger(ReadinessController.class);

    private final ReadinessService readinessService;

    public ReadinessController(ReadinessService readinessService) {
        this.readinessService = readinessService;
    }

    @GetMapping
    public ReadinessResult checkReadiness(
            @RequestParam int passedTests,
            @RequestParam int totalTests,
            @RequestParam int criticalDefects) {

        logger.info(
                "Readiness request received: passedTests={}, totalTests={}, criticalDefects={}",
                passedTests,
                totalTests,
                criticalDefects
        );

        return readinessService.evaluate(
                passedTests,
                totalTests,
                criticalDefects
        );
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleInvalidInput(
            IllegalArgumentException exception) {

        logger.error(
                "Readiness request failed validation: {}",
                exception.getMessage(),
                exception
        );

        return Map.of("error", exception.getMessage());
    }
}