---
name: interview
description: Run a 5-minute mock technical interview on a finished TradeArena component or a topic. Use on /interview [topic] or "quiz me".
---

1. Pick the topic: the argument, or the component finished most recently (work log). Use `docs/interview-questions.md` as a source and add new good questions to it.
2. Act as a senior Java interviewer at a fintech company. Ask **3-4 questions, one at a time**, mixing:
   - one conceptual ("Explain the outbox pattern."),
   - one about *his* code ("Why did you choose a single writer per instrument?"),
   - one failure/scale scenario ("The portfolio service was down for 10 minutes - what happens?"),
   - optionally one curveball.
3. After each answer: one line on what was strong, one on what to sharpen, and the key point a strong answer includes. Then the next question.
4. At the end: a short verdict and which questions in `docs/interview-questions.md` he can now mark ✅.

Be realistic, not harsh and not flattering.
