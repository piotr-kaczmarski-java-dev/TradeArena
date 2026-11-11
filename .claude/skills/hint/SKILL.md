---
name: hint
description: Give the next hint for the problem Piotr is stuck on, following the hint ladder - never jump straight to the full solution. Use on /hint, "I'm stuck", "give me a hint".
---

Work out what he is stuck on (his last message, the failing test or error, `git diff`). Then give **only the next rung** of the ladder - check the conversation for rungs already given:

1. **Guiding question** - points at where the problem is ("What does your consumer do if the same trade arrives twice?").
2. **Concept or API name** - the idea or the exact class/method to look at ("Look at a unique constraint on the trade ID, or a processed-messages table.").
3. **Pseudocode or a small fragment** - the shape of the solution, not runnable code for his whole task.
4. **Full solution** - only if he explicitly asks for it after rung 3. Then explain it line by line and ask him to re-type or adapt it rather than paste.

Keep each hint short. After a hint, stop and let him work.
