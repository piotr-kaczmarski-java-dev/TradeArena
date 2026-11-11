# 0001 - Learning-first workflow with an AI assistant

- **Status:** Accepted
- **Date:** 2026-11-11

## Context
TradeArena exists so I regain hands-on, interview-ready skills after a career break. AI coding assistants can build most of this app quickly - which would produce a working app and very little learning.

## Decision
Split the code into **core areas** that I write myself (matching engine, Kafka code, outbox, JPA/transactions, integration tests, Dockerfiles, Compose, Kubernetes manifests, CI) and **support areas** the assistant may build (UI, gateway wiring, Helm templating, Terraform modules, dashboards, DTOs). In core areas the assistant teaches, hints and reviews; it writes code only when I explicitly ask. The rules live in `CLAUDE.md`. Every session ends with a work-log entry in my own words.

## Alternatives considered
- **Write everything by hand** - maximum learning, but weeks of boilerplate and UI work that teach me little and delay a showable repo.
- **Let the assistant write everything, then read it** - fastest, but reading code is not the same skill as writing and debugging it; interviews probe the latter.

## Consequences
- Slower than "AI writes everything", faster than "all by hand".
- The work log doubles as interview revision notes and as the honest answer to "how do you use AI tools?".
- I must resist asking for full solutions in core areas when tired - `/hint` exists for that.
