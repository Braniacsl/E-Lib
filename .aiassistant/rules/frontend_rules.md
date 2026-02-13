---
apply: always
---

# Frontend Rules (React/MUI)

## Tech Standards
- Framework: React 18 + TypeScript + Vite
- UI Library: Material UI (MUI) ONLY. No Tailwind. No Bootstrap
- State: Redux Toolkit for global data. React Query for server state

## Component Structure
- Functional Components only
- Typed Props: Interface definitions required for all components
- file naming: PascalCase for components (BookCard.tsx), camelCase for hooks (useAuth.ts)

## Material UI Usage
- Use 'sx' prop for one-off styles. Do not use inline 'style' tag
- Use 'styled()' API for reusable complex components
- Layout: Use Box, Stack, Grid v2. Avoid raw HTML divs
- Typography: Use Typography component. Do not use h1, p tags directly

## Code Quality
- No 'any' types in TypeScript. Define interfaces/types
- Extract complex logic to custom hooks
- Avoid large component files. Break down into sub-components
- Console logs: FORBIDDEN in production code