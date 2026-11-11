---
name: start
description: Start a TradeArena learning session - catch up from the work log and plan, and pick today's goal. Use when Piotr says /start, "let's start", or begins a new session.
---

1. Read `docs/work-log.md` (last 2-3 entries), `docs/plan.md` (find the current phase: first unchecked items) and the matching `docs/tasks/phase-N.md` if it exists.
2. Run `git status` and `git log --oneline -5` to see uncommitted work.
3. Tell Piotr, in at most four lines: where he left off, what's next in the plan, and anything unfinished (failing build, uncommitted changes, an open question from the last session).
4. Ask what he wants to do today, and offer **two concrete options sized for the time he has** - one core-area task he writes himself, one smaller alternative.
5. Before he starts on a core-area task, ask one warm-up question about its concept (e.g. "Before we write the consumer: what happens to the offset if processing throws?"). Don't lecture - let him answer first.

Remember the rules in `CLAUDE.md`: in core areas you teach and review, you don't write the solution.
