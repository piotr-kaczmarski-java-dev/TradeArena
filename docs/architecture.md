# Architecture

> Living document. Update it when an ADR changes the picture.

```
            Angular UI  (live prices + order entry, WebSocket)
                 │
            gateway  (auth via Keycloak, routing, WebSocket relay)
       ┌─────────┼──────────────┐
  market-data   trading         portfolio
  (simulator)   (order book +   (cash, positions,
                 matching)       valuation, leaderboard)
       │         │                    │
       └──── Kafka topics ────────────┘
   prices          orders / trades / trade-settled
                 │                    │
            PostgreSQL           PostgreSQL + Redis
```

| Service | Port | Owns | Talks to |
|---|---|---|---|
| gateway | 8080 | routing, auth, WebSocket relay | all services, Keycloak |
| market-data | 8081 | price simulation | Kafka (`prices`) |
| trading | 8082 | orders, order books, trades | Kafka (`orders`, `trades`), PostgreSQL |
| portfolio | 8083 | cash, positions, leaderboard | Kafka (`prices`, `trades`), PostgreSQL, Redis |

Kafka events are versioned JSON records; evolution rules and golden-file tests are in [ADR 0003](adr/0003-json-for-kafka-events.md).

Ground rules: virtual money and simulated prices only; non-commercial; written from scratch.
