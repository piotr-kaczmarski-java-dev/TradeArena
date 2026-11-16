# Phase 1 - your tasks

Goal of the phase: prices flow **simulator → Kafka `prices` topic → a consumer**, and you can explain every hop. This is your first Kafka code, so it's all yours; the assistant explains, hints and reviews.

Ask for `/hint` when stuck. Keep each task small enough to run and see working before moving on.

## Task 1 - Price simulator (`market-data`, plain Java)

**Goal:** a class that produces the next price for an instrument, with no Spring and no Kafka in it, so it's trivially unit-testable.

- A handful of instruments (e.g. 5 made-up tickers) with a starting price.
- Each step: random walk with **drift** and **volatility** (geometric Brownian motion is the textbook choice, a simpler percentage random walk is fine). Prices never go to zero or negative.
- Occasional **shocks** (rare, larger jumps) so the game has some drama.
- Inject the randomness source (`RandomGenerator`, seedable) so tests are deterministic.
- A `PriceTick` record: instrument, price, timestamp, plus a schema `version` field (events are versioned - see `CLAUDE.md`).

Think about: `double` vs `BigDecimal` vs `long` in minor units for a price. There's no single right answer - be ready to defend yours.

Acceptance:
- [ ] Unit tests: same seed → same sequence; prices stay positive over many steps; a shock actually happens with a forced random source

## Task 2 - ADR 0003: message serialization

**Goal:** decide **JSON vs Avro** (vs Protobuf) for Kafka events and record it in `docs/adr/0003-...md` (`/adr` helps).

Consider: schema evolution, a schema registry as one more piece of infrastructure, debuggability with the console consumer, payload size, what a small learning project actually needs.

- [ ] ADR written and committed

## Task 3 - Kafka producer (`market-data`)

**Goal:** every N ms (configurable), publish one tick per instrument to the `prices` topic.

- Add `spring-boot-starter-kafka` (check the exact Boot 4 starter name).
- **Key = instrument symbol.** You'll explain why in the warm-up.
- The `prices` topic with more than one partition (e.g. 3). Decide how it gets created: a `NewTopic` bean, a Compose init step, or broker auto-create - and why.
- Producer settings you can justify: `acks`, `enable.idempotence`, retries. Know which ones are already the defaults in the Kafka version you're using.
- Don't ignore the result of `send()` - log failures. Know why `send()` returning doesn't mean the message is on the broker.
- Configuration in `application.yml`, bootstrap servers matching your Compose listener for the host.

Acceptance:
- [ ] `docker compose up -d`, `./mvnw -pl market-data spring-boot:run` → ticks visible with `kafka-console-consumer --property print.key=true --property print.partition=true`
- [ ] The same instrument always lands on the same partition (check it, don't assume it)
- [ ] Golden-file test for `PriceTick` (ADR 0003): a sample `price-tick-v1.json` in test resources; every stored sample still deserializes with all its fields and types; an extra unknown field is ignored, a missing required field fails

## Task 4 - A consumer that logs ticks (`portfolio`)

**Goal:** `portfolio` will need prices for valuation in Phase 3, so its first consumer lives there. For now it just logs each tick.

- `@KafkaListener` on `prices` with an explicit `group.id`.
- Decide `auto.offset.reset` (`earliest` or `latest`) and be able to say what each does for a **new** group vs an **existing** one.

Experiments (the real learning - do them, note what you see):
- [ ] Stop the consumer for a minute, start it again. Did it miss ticks, re-read them, or neither? Check with `kafka-consumer-groups --describe` (look at `LAG`).
- [ ] Run **two** portfolio instances (different ports, same group). How are the 3 partitions split? Kill one - what happens, and how long does it take?
- [ ] Run a second instance with a **different** group id. What does it receive?

## Task 5 - One integration test (`*IT.java`)

**Goal:** the convention "every service has at least one Testcontainers integration test" starts here.

- [ ] A `market-data` IT that starts Kafka in Testcontainers, lets the app publish, and asserts a tick for a known instrument arrives with the right key. Runs in `./mvnw verify` and in CI.

Think about: how do you wait for an async message in a test without `Thread.sleep`?

## Questions you should be able to answer afterwards

- Topic, partition, offset, consumer group - explain with this project's `prices` topic.
- What does the key give you? What do you lose with a null key? What happens to per-key ordering if you add partitions later?
- `acks=0/1/all` and `min.insync.replicas` - what can be lost in each case?
- What does producer idempotence protect against, and what does it *not* protect against?
- Auto-commit vs manual commit: in which crash scenario does each one lose or duplicate a message?
- Why is a dropped price tick much less serious than a dropped trade? (Hint: Phase 3.)
