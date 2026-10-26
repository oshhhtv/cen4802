# CEN4802 Release Readiness Checker

**Author:** Destry Jinks

The Release Readiness Checker is a small Java web application that evaluates whether a software release meets defined readiness criteria. Users enter the number of passing tests, total tests, and unresolved critical defects.

The application calculates the test pass rate, displays the number of failed tests, and reports whether the release is ready.

The project uses Java, Spring Boot, Maven, JUnit, and GitHub Actions. It serves as the foundation for Software Integration and DevOps assignments involving testing, automated builds, continuous integration, deployment, and analysis.

## Building the Application

The project uses Maven as its automated build tool.

From the project directory, run:

```cmd
mvn clean package
```

Maven automatically:

- removes previous build output,
- compiles the Java source code,
- processes application resources,
- compiles and runs the automated tests, and
- packages the application as an executable Spring Boot JAR.

The generated executable artifact is:

`target/cen4802-0.0.1-SNAPSHOT.jar`

## Automated Testing

The project uses JUnit through the Spring Boot testing dependencies to provide automated unit testing.

Run the complete test suite with:

```cmd
mvn test
```

The unit tests for `ReadinessService` verify multiple release-readiness and input-validation scenarios, including:

- a release meeting the 95% threshold with no critical defects,
- a release falling below the required test-pass threshold,
- critical defects blocking an otherwise ready release,
- passing tests exceeding the total number of tests,
- negative critical-defect values, and
- a total test count of zero.

An additional application test verifies that the Spring Boot application context loads successfully.

The tests are also executed automatically during the normal Maven build:

```cmd
mvn clean package
```

A build will fail if an automated test fails, preventing the Maven packaging phase from completing successfully.

## Continuous Integration

The project uses GitHub Actions to automate compilation, testing, and packaging whenever new changes are introduced.

The workflow configuration is located at:

`.github/workflows/ci.yml`

The workflow is triggered by:

- pushes to the `main` branch,
- pull requests targeting `main`, and
- manual execution through the GitHub Actions interface.

### CI Build Process

GitHub Actions executes the following steps:

1. Checks out the repository revision associated with the workflow execution.
2. Configures a GitHub-hosted Ubuntu runner with Eclipse Temurin JDK 21.
3. Configures Maven dependency caching.
4. Executes the existing Maven build and test process.
5. Uploads the resulting executable JAR as a downloadable artifact.

The CI system executes:

```cmd
mvn --batch-mode clean package
```

Maven's existing build lifecycle handles compilation, resource processing, unit testing, and packaging. GitHub Actions does not require separate commands for each operation.

If the Maven build or a unit test fails, the workflow reports a failed execution.

### CI Build Artifacts

Successful CI executions generate:

`target/cen4802-0.0.1-SNAPSHOT.jar`

GitHub Actions uploads this file under the artifact name:

`release-readiness-checker`

The artifact can be retrieved from the Artifacts section of the corresponding workflow run on GitHub.

The workflow can be viewed at:

https://github.com/oshhhtv/cen4802/actions

## Running the Packaged Application

After building the project, run the generated JAR with:

```cmd
java -jar target\cen4802-0.0.1-SNAPSHOT.jar
```

Then open a web browser and navigate to:

`http://localhost:8080`

For development purposes, the application can also be launched through the Maven Wrapper with:

```cmd
mvnw.cmd spring-boot:run
```

## Containerization

The application can be packaged and executed using Docker Desktop with the WSL 2 backend.

The existing Maven build process remains responsible for compiling, testing, and packaging the application:

```bash
mvn package
```

This produces the executable Spring Boot JAR:

```text
target/cen4802-0.0.1-SNAPSHOT.jar
```

The Dockerfile does not rebuild the Java project. Instead, it uses the previously packaged Maven artifact and places it into a Java 21 runtime image.

Build the container image with:

```bash
docker build -t cen4802-readiness:v2 .
```

Run the container with:

```bash
docker run -d --name cen4802-readiness-v2 -p 8080:8080 cen4802-readiness:v2
```

The application is then available at:

```text
http://localhost:8080
```

The `.dockerignore` file excludes unnecessary repository and generated build content from the Docker build context while retaining the packaged application JAR required by the Dockerfile.

## Kubernetes Deployment

The containerized application can be deployed locally using Kubernetes provided by Docker Desktop. The deployment reuses the existing `cen4802-readiness:v2` container image produced through the previously established Maven and Docker workflow.

Kubernetes configuration files are stored in:

```text
k8s/deployment.yaml
k8s/service.yaml
```

The Deployment initially requests two application replicas and runs the existing container image on port 8080. The Service uses the `app: cen4802-readiness` label to provide a stable network endpoint for the application Pods.

Deploy the application with:

```bash
kubectl apply -f k8s/deployment.yaml
kubectl apply -f k8s/service.yaml
```

Verify the deployment with:

```bash
kubectl get deployment cen4802-readiness
kubectl get pods -l app=cen4802-readiness
kubectl get service cen4802-readiness
```

For local browser access, forward host port 8081 to the Kubernetes Service:

```bash
kubectl port-forward service/cen4802-readiness 8081:8080
```

The deployed application is then available at:

```text
http://localhost:8081
```

The Deployment can be managed independently of the application build process. For example, the application was scaled from two replicas to three with:

```bash
kubectl scale deployment cen4802-readiness --replicas=3
```

Kubernetes created an additional Pod and reconciled the Deployment to a healthy `3/3` state.

The deployed resources can be removed using:

```bash
kubectl delete -f k8s/service.yaml -f k8s/deployment.yaml
```

After deletion, the Deployment and Service return `NotFound`, and no Pods with the application label remain.

## Application Logging

The Release Readiness Checker uses Spring Boot's default SLF4J and Logback logging stack. No additional logging dependency is required beyond the existing Spring Boot application dependencies.

Application logging is configured in:

```text
src/main/resources/application.properties
```

The default configuration records application messages at the `INFO` level and above:

```properties
logging.level.root=INFO
logging.level.com.oshhhtv.cen4802=INFO
```

Logs are written to both the console and:

```text
logs/release-readiness.log
```

The configured format includes the timestamp, severity level, thread, logger name, and descriptive message. The generated `logs/` directory is ignored by Git because log files are runtime output rather than project source.

Application events use several logging levels:

- `DEBUG` records detailed readiness calculations and diagnostic values.
- `INFO` records routine readiness requests and successful release-ready evaluations.
- `WARN` records release-blocking conditions and rejected invalid input.
- `ERROR` records validation failures handled by the controller, including exception details useful for troubleshooting.

For example, a normal readiness request records the supplied test and defect counts, while invalid values such as more passed tests than total tests produce both a warning and a handled error record.

The standard application configuration suppresses `DEBUG` output to avoid unnecessary operational noise. Diagnostic logging can be enabled temporarily at runtime without changing the packaged application:

```bash
java -jar target/cen4802-0.0.1-SNAPSHOT.jar --server.port=8083 --logging.level.com.oshhhtv.cen4802=DEBUG --logging.file.name=logs/release-readiness-debug.log
```

This runtime override adds calculation-level diagnostic entries while leaving the application's behaviour unchanged.

Logs can be examined and filtered using standard command-line tools. For example:

```cmd
findstr /c:" WARN " /c:" ERROR " logs\release-readiness.log
```

or a specific request can be isolated using contextual values:

```cmd
findstr /c:"passedTests=101" logs\release-readiness.log
```

During logging verification, repeated application requests produced more than 100 meaningful log entries. Filtering the larger log set made it possible to isolate release-blocking warnings and validation errors without inspecting every routine entry individually.

## Tests Needed for Release Threshold

The web interface also displays the number of additional passing tests required to reach the 95% release-readiness threshold. This feature was added before rebuilding the Maven package and Docker image to verify that the rebuilt container was running the updated application.

## Release Criteria

A release is considered **READY** only when:

- at least 95% of its tests pass, and
- there are zero unresolved critical defects.

If either condition is not satisfied, the application reports that the release is **NOT READY**.

The application also validates its input to prevent invalid values, such as reporting more passing tests than total tests or entering a negative number of critical defects.

## Failed-Test Counter

The web interface displays the number of failed tests alongside the release-readiness result.

The calculation is:

`Failed tests = Total tests - Passed tests`

For example, 95 passing tests out of 100 produce five failed tests.

This enhancement is implemented in the browser interface and does not modify the existing release-readiness rules or backend API.

The existing JUnit tests verify the Java application logic. The failed-test counter is a JavaScript feature and was additionally verified through manual browser testing.