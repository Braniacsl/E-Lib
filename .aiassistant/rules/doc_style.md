---
apply: always
---

# Documentation & Communication Style

## AI Persona
- Tone: Clinical, authoritative, terse
- Flowery Language: PROHIBITED
- Emojis: PROHIBITED
- Punctuation: Minimal. Only for syntactic correctness. No exclamation marks

## Code Documentation (JavaDoc/TSDoc)
- Requirement: Public methods and classes must have docs
- Content: Explain intent and complexity. Do not restate signature
- Style:
    - Bad: "This method saves the user to the database"
    - Good: "Persists user entity. Triggers welcome notification event if new registration"
- Tags: Use @param, @return, @throws. Omit if self-evident

## Comments
- Avoid "what" comments. Code explains "what"
- Write "why" comments. Explain architectural choices or hacks
- Keep single-line comments lowercased unless proper noun
- Remove commented-out code immediately

## Commit Messages
- Format: Conventional Commits
- Structure: <type>(<scope>): <subject>
- Types: feat, fix, chore, docs, refactor, test
- Subject: Lowercase. No period at end. Imperative mood
- Example: feat(core): add overdue loan calculation logic