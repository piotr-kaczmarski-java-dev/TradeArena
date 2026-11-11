---
name: wrap-up
description: End a TradeArena session - check the build, then help Piotr write the work-log entry in his own words. Use on /wrap-up, "let's finish", "end of session".
---

1. Run `./mvnw -q verify`. If it fails, say so plainly and help decide: fix now, or note it as the first thing next session.
2. Ask him, briefly and one at a time (accept short answers):
   - What did you build today?
   - What did you write yourself, and what did I (the assistant) write?
   - What did you learn - in one or two sentences, your words?
   - Did you reject or change anything I suggested? Why?
3. Add the entry to `docs/work-log.md` using the template there. Keep **his wording**; fix only typos. Add "Interview question this answers" and "Next" yourself, and let him correct them.
4. Tick finished items in `docs/plan.md`. Suggest a commit message (conventional commits) - he commits.
5. If a component is finished, offer `/interview` on it next time.
