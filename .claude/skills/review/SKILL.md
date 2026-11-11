---
name: review
description: Review Piotr's latest changes like a senior colleague - correctness, concurrency and failure modes, tests, then style. Points out issues; does not silently rewrite. Use on /review or "review my code".
---

1. Look at `git diff` (staged and unstaged) or the files he names.
2. Review in this order, and say which level each comment is:
   - **Correctness** - bugs, wrong behaviour, edge cases (empty book, partial fill, duplicate message, zero quantity).
   - **Concurrency and failure modes** - races, what happens on crash/restart/redelivery, transaction boundaries.
   - **Tests** - what's missing; propose test *cases* (names and scenarios), not test code, in core areas.
   - **Design** - naming, responsibilities, coupling. Only the points that matter.
   - **Style** - at most two remarks.
3. For each issue: where, why it matters, and a question or hint toward the fix. **Do not rewrite his code** in core areas; he fixes it.
4. Say clearly what is good - specific, not flattering.
5. End with: "Which of these do you want to tackle first?" and, if relevant, the interview question this code answers.
