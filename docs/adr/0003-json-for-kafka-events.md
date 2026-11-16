# 0003 - JSON for Kafka events

- **Status:** Accepted
- **Date:** 2026-11-16

## Context
Four event types go through Kafka: `prices`, `orders`, `trades` and `trade-settled`. Producers and consumers live in different services and are deployed separately (during a rolling deploy, old and new versions run side by side), so the message format must survive schema changes in both directions:
- **backward compatible** - a new consumer reads old messages (still in the topic, or replayed);
- **forward compatible** - an old consumer reads new messages (producer upgraded first).

How each format handles an added field:
- **JSON** - depends on the deserializer: it either fails on the unknown field or ignores it.
- **Avro** - the reader resolves the writer's schema against its own and skips fields it doesn't know. The binary carries no field names, so the reader *must* have the writer's schema, which in practice means a schema registry.
- **Protobuf** - unknown fields are kept in a separate section and ignored by the consumer; they are even written back unchanged if the message is re-serialized (Avro drops them).

TradeArena is one developer, four services in one repo deployed together, limited hours, and debugging with the console consumer. The Kafka learning goals are partitioning, offsets, retries, idempotency, the outbox - not schema registries.

## Decision
Use **JSON** (Jackson) for all Kafka events, with schema evolution handled by explicit rules and enforced by tests:
1. Adding an **optional (nullable)** field needs no version bump.
2. Never remove, rename or change the type of a field directly: use **expand-contract** (add the new field next to the old one, migrate consumers, then remove the old one) and bump `version` at the contract step.
3. Consumers **ignore unknown fields**; a missing **required** field fails fast, a missing optional field becomes `null`.
4. A **golden-file test** per event version (stored sample JSON) checks in CI that every old sample still deserializes and keeps all its fields with the same types.

## Alternatives considered
- **Avro + schema registry** - compatibility is checked by a tool instead of by me, and messages are smaller. But it adds a registry to Compose, k8s and CI, code generation, and generated classes instead of records, for a project with one developer and one repo. Time is better spent on Phases 2-3.
- **Protobuf** - same benefits (field numbers make renames safe, unknown fields preserved), same cost of schemas and code generation.
- **JSON + JSON Schema in a registry** - keeps readable JSON and adds tool-checked compatibility, but still needs a registry. Not needed now; the natural fallback if maintaining golden files becomes a chore.

## Consequences
- Easier: simple setup, events stay plain Java records, messages readable in the console consumer.
- Harder: compatibility depends on my discipline - I can forget a rule or a version bump. The golden-file tests reduce this risk; they cannot catch a change of **meaning** with the same shape (e.g. `price` switching from dollars to cents), only naming and documentation help there.
- Changing later is affordable here: all producers and consumers are in one repo, `prices` is throwaway data, and the database (not Kafka) is the source of truth for trades. Migrating from JSON to Avro is planned as a learning exercise once the game is playable.
- Revisit (for a real product) when:
  - the team grows - more people, more risk of human error;
  - message volume makes payload size a problem;
  - more services consume Kafka;
  - the number of event types makes maintaining golden files and tests a chore.
