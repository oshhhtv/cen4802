# CEN4802 Release Readiness Checker

**Author:** Destry Jinks

The Release Readiness Checker is a small Java web application that evaluates whether a software release meets defined readiness criteria. Users enter the number of passing tests, total tests, and unresolved critical defects. The application calculates the test pass rate and reports whether the release is ready.

The project uses Java, Spring Boot, and Maven. It is intended to serve as the foundation for later Software Integration and DevOps assignments involving testing, automated builds, deployment, automation, and analysis.

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

## Release Criteria

A release is considered **READY** only when:

- at least 95% of its tests pass, and
- there are zero unresolved critical defects.

If either condition is not satisfied, the application reports that the release is **NOT READY**.

The application also validates its input to prevent invalid values, such as reporting more passing tests than total tests or entering a negative number of critical defects.