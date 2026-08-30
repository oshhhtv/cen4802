# CEN4802 Release Readiness Checker

**Author:** Destry Jinks

The Release Readiness Checker is a small Java web application that evaluates whether a software release meets a defined testing threshold. Users enter the number of passing tests and total tests, and the application calculates the test pass rate and reports whether the release is ready.

The project uses Java, Spring Boot, and Maven. It is intended to serve as the foundation for later Software Integration and DevOps assignments involving testing, building, deployment, automation, and analysis.

## Running the Application

On Windows, run the application from the project directory with:

```cmd
mvnw.cmd spring-boot:run
```

Then open a web browser and navigate to:

`http://localhost:8080`

## Release Criteria

A release is considered **READY** only when:

- at least 90% of its tests pass, and
- there are zero unresolved critical defects.

If either condition is not satisfied, the application reports that the release
is **NOT READY**.

The application also validates its input to prevent invalid values, such as
reporting more passing tests than total tests or entering a negative number of
critical defects.