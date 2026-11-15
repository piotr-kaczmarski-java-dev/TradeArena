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
