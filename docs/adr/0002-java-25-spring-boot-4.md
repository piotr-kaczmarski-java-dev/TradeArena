# 0002 - Java 25 and Spring Boot 4.1

- **Status:** Proposed (assistant's choice at setup - accept, or switch before Phase 1)
- **Date:** 2026-11-11

## Context
The original plan said "Java 21 / Spring Boot 3". At setup time (Nov 2026): Spring Boot **4.1.2** is the current release and the 3.x line is out of open-source support; **Java 25** is the current LTS and is the JDK installed locally. Job postings mostly mention "Spring Boot" without a version and Java 17/21 features.

## Decision
Use **Java 25** and **Spring Boot 4.1** (Maven, Spring Boot starter parent).

## Alternatives considered
- **Java 21 + Spring Boot 3.5** - closest to what many companies run today, but 3.x is end-of-OSS-support; new code on an unsupported line is a poor signal in a portfolio.
- **Java 21 + Spring Boot 4.1** - also valid; would need JDK 21 installed. Java 25 includes every 17/21 feature interviewers ask about (records, sealed types, pattern matching, virtual threads).

## Consequences
- Interview bonus: I can explain what changed from Boot 3 to 4 (modularised starters such as `spring-boot-starter-webmvc`, Spring Framework 7, Jackson 3, JSpecify null-safety, HTTP API versioning) - many teams are migrating now.
- Some tutorials and Stack Overflow answers target Boot 3; check APIs against the 4.x docs.
- Spring Cloud Gateway (Phase 4) needs a Spring Cloud release train compatible with Boot 4.1 - check before adding it.
