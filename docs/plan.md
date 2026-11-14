# Plan

Learning-first: each phase lists what Piotr should be able to **explain, build and debug** by its end. A phase is done when its learning goals are met *and* the build is green - not just when the code exists.

Rough pace: ~10-12 hours a week. **Phases 0-3 carry most of the value; start applying for jobs after Phase 2.**

## Phase 0 - Skeleton (week 1) ← current

- [x] Maven multi-module skeleton, health endpoints, smoke tests (assistant)
- [x] Learning framework: CLAUDE.md, docs, ADRs, session commands (assistant)
- [x] `compose.yaml`: PostgreSQL, Kafka (KRaft), Keycloak - **by Piotr** → `docs/tasks/phase-0.md`
- [x] GitHub Actions CI: build + test on every push - **by Piotr** → `docs/tasks/phase-0.md`
- [x] Repo on GitHub, linked from the profile README

**Can explain:** Maven multi-module and the Spring Boot BOM; Compose services, volumes, healthchecks, networking; what KRaft replaced; a CI pipeline's stages and caching.

## Phase 1 - Prices (week 2)

- [ ] `market-data`: price simulator (random walk with drift and volatility, occasional shocks)
- [ ] Kafka producer: `prices` topic, key = instrument, JSON or Avro serialization (decide in an ADR)
- [ ] A consumer that logs ticks

**Can explain:** topics, partitions, keys and ordering; producer acks and idempotence; consumer groups, offsets, auto vs manual commit.

## Phase 2 - Trading core ⭐ (weeks 3-4)

- [ ] Order REST API (validation, error handling, OpenAPI)
- [ ] Order book per instrument + matching engine (price-time priority, partial fills, cancel) - **by Piotr**
- [ ] Concurrency model: single writer per instrument - **by Piotr**, with an ADR
- [ ] Trades published via the **transactional outbox** - **by Piotr**
- [ ] Unit tests (JUnit 5, Mockito) and a JMH benchmark - **by Piotr**

**Can explain:** order book data structures and their complexity; price-time priority; why a single writer avoids locks; the dual-write problem and the outbox; how to benchmark JVM code honestly.

## Phase 3 - Portfolio (weeks 5-6)

- [ ] Cash and position reservation when an order is placed; release on cancel
- [ ] Trade settlement from the `trades` topic with an **idempotent consumer** - **by Piotr**
- [ ] JPA entities, transactions, **optimistic locking** against double-spending - **by Piotr**
- [ ] Flyway migrations
- [ ] Testcontainers integration tests (PostgreSQL + Kafka) - **by Piotr**

**Can explain:** entity lifecycle, lazy loading and N+1; transaction boundaries and propagation; optimistic vs pessimistic locking; at-least-once delivery and idempotency keys; testing with real dependencies.

## Phase 4 - Playable game (weeks 7-8)

- [ ] Keycloak realm, login; Spring Security resource servers (JWT)
- [ ] Gateway (Spring Cloud Gateway) and WebSocket price relay
- [ ] Angular UI: live chart, order entry, portfolio, leaderboard (assistant-heavy)
- [ ] Bots: market makers and simple strategies (virtual threads)
- [ ] Redis leaderboard (sorted sets)

**Can explain:** OAuth2/OIDC flows and JWT validation; what the gateway does; Redis sorted sets.

## Phase 5 - Kubernetes (week 9)

- [ ] Dockerfiles - **by Piotr**
- [ ] Manifests: deployments, services, config, secrets, probes - **by Piotr**; Helm chart (assistant)
- [ ] Runs on local kind or k3d, including Kafka (Strimzi or Redpanda)

**Can explain:** pods, deployments, services, readiness vs liveness, rolling updates, config vs secrets.

## Phase 6 - AWS + observability (week 10)

- [ ] AWS Budget alert **before** anything else
- [ ] Terraform for EKS (or k3s on EC2): `apply` for a demo, `destroy` right after
- [ ] Micrometer → Prometheus/Grafana dashboard (orders/s, match latency)
- [ ] OpenTelemetry tracing across services

**Can explain:** what Terraform state is; the cost model; metrics vs logs vs traces.

## Phase 7 - Polish (half a week)

- [ ] README: architecture diagram, how to run, design decisions, **how I used AI** (from the work log)
- [ ] Demo video
- [ ] SonarCloud quality gate badge
