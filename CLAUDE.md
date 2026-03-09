# Resilience4j-BaseCommands

## Purpose
Base command classes for HTTP operations wrapped in Resilience4j circuit-breaker patterns. The modern replacement for Hystrix-BaseCommands, providing resilient HTTP abstractions using the actively maintained Resilience4j library and Java 11+ HttpClient.

## Tech Stack
- Language: Java 11+
- Framework: Resilience4j
- Build: Maven
- Key dependencies: Resilience4j, Java HttpClient, SLF4J

## Architecture
Library providing base command classes (`BaseHttpGetResilience4jCommand`, `BaseHttpPostResilience4jCommand`, `BaseHttpPutResilience4jCommand`, `BaseHttpDeleteResilience4jCommand`) that wrap HTTP operations in Resilience4j circuit-breakers. Uses Java 11 built-in HttpClient instead of Apache HttpClient. Commands are executed through a `CommandProxy` that manages circuit-breaker state.

## Key Entry Points
- `BaseResilience4jCommand` - Abstract base for all commands
- `CommandProxy` - Execution proxy managing circuit-breaker state
- `pom.xml` - Maven coordinates: `no.cantara.base:Resilience4j-BaseCommands`

## Development
```bash
# Build
mvn clean install

# Test
mvn test
```

## Domain Context
Resilience infrastructure library. Modern successor to Hystrix-BaseCommands, providing circuit-breaker-wrapped HTTP commands for fault-tolerant service communication in the Cantara ecosystem.
