# Interview questions

Collected while building. Mark ✅ when you can answer out loud in under two minutes without notes.

## Kafka
- [ ] How do partitions and keys affect ordering? Why key by instrument?
- [ ] What happens if a consumer crashes after processing but before committing the offset?
- [ ] At-most-once, at-least-once, exactly-once - which do you get by default, and what does "exactly-once" really cover?
- [ ] What is the dual-write problem, and how does the outbox pattern solve it?
- [ ] How do you make a consumer idempotent?
- [ ] What's a dead-letter topic and when do you use one?

## Matching engine / core Java
- [ ] Which data structures for an order book, and what's the complexity of insert, cancel and match?
- [ ] Explain price-time priority.
- [ ] Why a single writer per instrument instead of locks? What are the trade-offs?
- [ ] Virtual threads: what problem do they solve, and when do they not help?
- [ ] How do you benchmark Java code without fooling yourself? (JIT warm-up, dead-code elimination - JMH)

## Persistence
- [ ] Optimistic vs pessimistic locking - which for a cash balance, and why?
- [ ] What's the N+1 problem? How do you detect and fix it?
- [ ] Where do transaction boundaries belong, and what does `@Transactional` propagation do?
- [ ] Why Flyway instead of `ddl-auto`?

## Testing
- [ ] Unit vs integration tests - what goes where in this project?
- [ ] Why Testcontainers rather than H2 or an embedded Kafka?
- [ ] How do you test concurrent code?

## Docker / Kubernetes / CI
- [ ] Readiness vs liveness probes?
- [ ] How does a rolling update avoid downtime?
- [ ] ConfigMap vs Secret - and why a Kubernetes Secret is not really secret?
- [ ] Describe your CI pipeline stage by stage.

## System design
- [ ] Walk me through TradeArena's architecture in 5 minutes.
- [ ] What happens when the portfolio service is down for 10 minutes?
- [ ] How would you scale the matching engine to 1000 instruments?
- [ ] How do you prevent a player from spending the same cash twice?

## AI tools
- [ ] How do you use AI coding tools, and how do you keep quality under control?
- [ ] Give an example of an AI suggestion you rejected, and why.
