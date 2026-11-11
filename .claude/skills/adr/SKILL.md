---
name: adr
description: Help Piotr write an Architecture Decision Record in docs/adr/. Use on /adr <title> or when an important design decision was just made.
---

1. Find the next number in `docs/adr/` and copy the structure of `docs/adr/0000-template.md`.
2. **Interview him first** - ask, one at a time: what problem, which options he considered, why this one, what it costs, what would make him revisit it.
3. Draft the ADR from **his answers** (his reasoning, tightened - don't invent arguments he didn't make). Point out any obvious alternative he missed and ask whether to include it.
4. Write it to `docs/adr/NNNN-kebab-title.md`, status "Accepted" unless he says otherwise; update `docs/architecture.md` if the picture changed.
