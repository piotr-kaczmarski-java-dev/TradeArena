# TradeArena

[![CI](https://github.com/piotr-kaczmarski-java-dev/TradeArena/actions/workflows/ci.yml/badge.svg)](https://github.com/piotr-kaczmarski-java-dev/TradeArena/actions/workflows/ci.yml)

A multiplayer **stock-market trading game**: simulated prices move in real time, players trade with each other through a real **order book and matching engine**, bots provide liquidity, and a leaderboard ranks portfolio value.

> 🚧 **Work in progress** - currently in Phase 0 (skeleton). See [`docs/plan.md`](docs/plan.md).

## Why this project

I'm a senior Java developer building this to sharpen hands-on skills with the stack most fintech teams use today: **Java 25, Spring Boot 4, Kafka, PostgreSQL, Testcontainers, Docker, Kubernetes, Terraform on AWS**.

It's also an experiment in **learning-first AI-assisted development**: the hard parts (matching engine, Kafka consumers and the outbox, transactions and locking, integration tests, infrastructure) I write myself; Claude Code teaches, reviews and builds the supporting parts (UI, wiring, templates). The rules are in [`CLAUDE.md`](CLAUDE.md), the reasoning in [ADR 0001](docs/adr/0001-learning-first-workflow.md), and every session is recorded in the [work log](docs/work-log.md).

## Architecture

| Service | Responsibility |
|---|---|
| `market-data` | Price simulator → Kafka `prices` |
| `trading` | Order API, order book per instrument, matching engine, trades via the outbox pattern |
| `portfolio` | Cash and positions, settlement, valuation, leaderboard |
| `gateway` | Authentication (Keycloak) and routing for the UI |
| `ui` | Angular - live prices and order entry *(Phase 4)* |

Details: [`docs/architecture.md`](docs/architecture.md) · decisions: [`docs/adr/`](docs/adr/)

## Build and run

Requirements: JDK 25, Docker. Maven comes with the wrapper.

```bash
./mvnw verify                            # build + tests
./mvnw -pl trading spring-boot:run       # run one service → http://localhost:8082/actuator/health
```

Local infrastructure (`compose.yaml`) arrives with Phase 0.

## Ground rules

Virtual money and simulated prices only - no real trading, no live market data. Non-commercial learning project.
