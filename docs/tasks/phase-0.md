# Phase 0 - your tasks

The assistant built the Maven skeleton. These two are yours: they're on the "refresh" list (Docker 36% and CI/CD 43% of senior Java postings) and small enough for one or two evenings.

Ask the assistant for `/hint` when stuck - it gives the next hint, not the answer.

## Task 1 - `compose.yaml` (repo root)

**Goal:** `docker compose up -d` starts everything the services will need locally.

Services:
1. **PostgreSQL** - one instance; a separate database (or schema) for `trading` and for `portfolio`. A named volume so data survives restarts. A healthcheck.
2. **Kafka in KRaft mode** - single broker, no ZooKeeper. Reachable from your host (for `./mvnw spring-boot:run`) *and* from other containers later.
3. **Keycloak** - dev mode is fine for now; you'll configure the realm in Phase 4.

Acceptance:
- [ ] `docker compose up -d` → all containers healthy (`docker compose ps`)
- [ ] You can connect to PostgreSQL from your host (`psql` or IntelliJ)
- [ ] You can create a topic and produce/consume a message with the Kafka CLI tools inside the container
- [ ] Keycloak admin console opens in the browser
- [ ] No passwords committed as real secrets - use a `.env` file (already in `.gitignore`) with a committed `.env.example`

Questions you should be able to answer afterwards (the assistant will ask):
- Why does Kafka need two listeners (or advertised listeners) to work from both the host and other containers?
- What did KRaft replace, and why does it matter operationally?
- `depends_on` vs a healthcheck condition - what's the difference?
- Bind mount vs named volume?

## Task 2 - GitHub Actions CI (`.github/workflows/ci.yml`)

**Goal:** every push and pull request builds the project and runs the tests.

Requirements:
- [ ] Triggers on push and pull_request
- [ ] Sets up JDK 25 (Temurin)
- [ ] Caches the Maven repository
- [ ] Runs `./mvnw -B verify`
- [ ] Green badge in the README

Stretch (later phases will need it): Testcontainers works on GitHub's Ubuntu runners out of the box - think about why.

Questions afterwards:
- What's the difference between a workflow, a job and a step?
- How does the cache key decide when the cache is reused?
- How would you do the same in Jenkins or GitLab CI? (Your refresh cards cover this.)

## Optional - a warning worth understanding

`./mvnw verify` prints *"Mockito is currently self-attaching to enable the inline-mock-maker. This will no longer work in future releases of the JDK."*

- Find out why Mockito attaches a Java agent at runtime and why newer JDKs restrict dynamic agent loading.
- Fix it the documented way (configure Mockito as a `-javaagent` for the Surefire plugin in the parent POM) so the warning disappears.

Small, but it's exactly the kind of "what changed in recent JDKs" detail that comes up in interviews.
