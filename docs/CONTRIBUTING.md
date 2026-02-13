# Contributing Guidelines

## 1. The "Golden Rule"

**No code is merged without a Pull Request (PR) and a Review.** Direct pushes to `main` are disabled. You must work on a branch.

## 2. Git Workflow

We use a simplified Feature Branch workflow.

1. **Sync First:** Always pull the latest `main` before starting.

    ```bash
    git checkout main
    git pull origin main
    ```

2. **Create Branch:** Name your branch based on the ticket/task.
    - Syntax: `type/short-description`
    - Examples:
      - `feat/user-login`
      - `fix/book-search-bug`
      - `docs/update-readme`
3. **Commit Often:**
    - Messages must be clear: "Added login form" (Good) vs "fixed stuff" (Bad).
4. **Push & PR:**
    - Push your branch.
    - Open a Pull Request (PR) in GitHub targeting `main`.
    - **Assign the Tech Lead** for review.

## 3. Definition of Done (DoD)

A task is NOT done until:

- [ ] The code runs locally without crashing.
- [ ] There are no linter errors (check your console!).
- [ ] You have removed all `console.log` and unnecessary comments.
- [ ] You have tested the feature manually.

## 4. Frontend Structure (React)

- **Components:** Put reusable UI (buttons, cards) in `src/components`.
- **Pages:** Put full page views in `src/pages`.
- **State:** Use Redux slices in `src/store`. Do not use `useState` for global data.

## 5. Backend Structure (Spring Boot)

- **Controller:** Only handles HTTP requests (GET/POST). No logic here.
- **Service:** All business logic goes here.
- **Repository:** Only database interaction.
- **DTOs:** Never return an Entity directly. Convert it to a DTO first.
