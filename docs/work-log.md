# Work log

One entry per session, written by Piotr in his own words (the assistant helps with `/wrap-up`).
This log is a deliverable: revision notes before interviews, and the honest answer to "How do you use AI tools?".

<!-- Template
## YYYY-MM-DD - <topic>
- Built:
- Written by me / by the assistant:
- Learned: (one or two sentences, in my own words)
- Interview question this answers:
- Rejected from AI suggestions, and why:
- Next:
-->

## 2026-11-11 - Repository setup
- Built: Maven multi-module skeleton (market-data, trading, portfolio, gateway) with health endpoints and smoke tests; docs, ADRs and session commands.
- Written by me / by the assistant: all by the assistant (setup only - by agreement).
- Learned: -
- Interview question this answers: -
- Rejected from AI suggestions, and why: -
- Next: Phase 0 tasks - `compose.yaml` and CI, by me.

## 2026-11-12 - Docker Compose: PostgreSQL, Kafka (KRaft), Keycloak
- Built: a Docker Compose file configured with Postgres database, Kafka log service with KRaft consensus for metadata and Keycloak identity provider.
- Written by me / by the assistant: I wrote all the files (`compose.yaml`, `.env.example`, Postgres init script). Assistant: explanations, review, model interview answer.
- Learned: Kafka clients use advertised listeners, retrieved after connecting to the bootstrap.
- Interview question this answers: "Your app connects to Kafka from the host but fails to produce/consume (or works on the host but not from a container) - why?"
- Rejected from AI suggestions, and why: nothing rejected. The assistant flagged a likely permission problem on the Kafka data volume; I didn't change anything, it just worked (a named volume copies the image's directory ownership).
- Next: Phase 0 Task 2 - GitHub Actions CI. Then add `:?` fail-fast to the remaining `.env` variables.

## 2026-11-14 - GitHub Actions CI
- Built: a GitHub CI workflow with jobs and steps that builds and verifies the Maven project. Also `:?` fail-fast on the last `.env` variable in `compose.yaml` and the CI badge in the README.
- Written by me / by the assistant: the whole `ci.yml` was done by me (plus the compose fix and the badge). Assistant: explanations (cache keys, jobs vs steps), two review rounds.
- Learned: how the GitHub cache works and how the ephemeral builds differ from the Jenkins approach.
- Interview question this answers: "Walk me through your CI pipeline: what triggers it, how do you keep it fast (cache key design), and how do you debug a failure that only happens on CI?"
- Rejected from AI suggestions, and why: nothing rejected, but I went beyond them: my own concurrency setup (cancel superseded runs only on PRs, one group per commit on `main`) and a `timeout-minutes` that wasn't suggested.
- Next: fix the Mockito self-attach warning (Surefire `argLine` + `dependency:properties`); link the repo from my GitHub profile README; then Phase 1 - price simulator and the first Kafka producer.

## 2026-11-15 - Mockito javaagent, PriceTick event record
- Built: fixed the Mockito self-attach warning, and added the `PriceTick` record and its unit tests.
- Written by me / by the assistant: I wrote all the code. Assistant: the Phase 1 tasks file (`docs/tasks/phase-1.md`), explanations and review rounds.
- Learned: proper data types for common fields like `Instant` and `BigDecimal`, and the considerations behind them - e.g. `BigDecimal.equals` checks the scale (`100.0` != `100.00`), `compareTo` doesn't. Better naming and structure for unit tests, and how to construct a simple record.
- Interview question this answers: "How do you represent money or prices in Java, and what's the catch with `BigDecimal.equals` in a `HashMap` or a record?" Also: "Mockito warns about self-attaching - why, and how do you fix it?"
- Rejected from AI suggestions, and why: nothing.
- Next: remove the leftover `StringUtils` import from `PriceTick`; then Phase 1 Task 1 - the price simulator (injected `RandomGenerator` and `Clock`, rounding to 2 decimals in the simulator).

## 2026-11-15 - GBM price step
- Built: a simulator for Geometric Brownian Motion, with a test suite.
- Written by me / by the assistant: I wrote everything based on the assistant's advice. Assistant: explanations (normal distribution, log returns, square-root-of-time rule, floating-point underflow) and three review rounds.
- Learned: how GBM works and different methods to implement a simulation for it, especially how the choice of time step can influence the implementation.
- Interview question this answers: "Why do price models use log returns, and why does volatility scale with the square root of time?" Also: "What are NaN, overflow and underflow in `double`, and how do you guard numeric code against them?"
- Rejected from AI suggestions, and why: nothing.
- Next: continue Task 1 - a class holding the instruments, their current prices and an injected `RandomGenerator` (test: same seed gives the same sequence); then drift, shocks, and what happens when a tiny price rounds to `0.00` in `PriceTick`.

## 2026-11-17 - ADR 0003 (JSON for Kafka events), InstrumentPrices
- Built: a class Instruments and a class for management of Instrument prices, and tests for them. Also ADR 0003: JSON for Kafka events, with schema-evolution rules.
- Written by me / by the assistant: I wrote the classes, most but not all tests generated by AI assistant. ADR: reasoning, rules and revisit triggers mine, text drafted by the assistant from my answers. From now on: a few important tests by hand (picked by the assistant), the rest generated.
- Learned: schema evolution with JSON, proper validation and initialization of parameters.
- Interview question this answers: "How do you change an event schema without breaking consumers - and without a schema registry?" (add optional fields only, expand-contract, golden-file tests). Also: "Where should validation live, and why fail at construction rather than on first use?"
- Rejected from AI suggestions, and why: nothing rejected this time.
- Next: replace the real tickers in `InstrumentTest`; finish Task 1 - drift, occasional shocks, tests "prices stay positive over many steps" and "a shock happens with a forced random source"; then Task 3 - the Kafka producer (golden-file test for `PriceTick` by hand).

## 2026-11-18 - Drift and per-instrument parameters
- Built: drift term in the price simulator, per-instrument sigma and drift, tests.
- Written by me / by the assistant: I wrote all the code and tests. Assistant: hints and reviews.
- Learned: proper separation of concerns in test cases, and how to prepare expectations so that they can be easily tested.
- Interview question this answers: "How do you unit-test numeric code without re-implementing the formula in the test?" (isolate one term - sigma = 0 for drift; choose inputs with a known answer like ln 2 -> doubling; compare doubles with a tolerance). Also: "Why does a log-normal model subtract sigma^2/2, and what's the difference between mean and median growth?"
- Rejected from AI suggestions, and why: nothing. My own idea of player trades moving the simulated price is deferred to Phase 4 (bots) - the order book will give market impact for free, and a simulator rule would be exploitable.
- Next: fix the drift/shock mix-up in the `nextPrice` comment, rename `CANCEL_DRIFT`, add an Ito-correction test, update Javadoc and the exception message; then shocks (forced-shock test) and "prices stay positive over many steps".
