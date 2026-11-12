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
