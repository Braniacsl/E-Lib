# Frontend Documentation

## Overview

React TypeScript frontend for E-Library System. Built with Material UI, Redux Toolkit, React Query. Follows strict architectural patterns.

## Tech Stack

- React 19.2.0
- TypeScript 5.9.3
- Material UI 7.0.1
- Redux Toolkit 2.6.0
- React Query 5.66.0
- React Router 7.3.0
- Vite 8.0.0-beta.13
- Axios 1.7.9

## Architecture

### Component Structure

```
frontend/src/
├── components/        # Reusable UI components
│   ├── layout/       # Layout components (Header, Layout)
│   └── shared/       # Shared components (LoadingSpinner, ErrorAlert)
├── hooks/            # Custom React hooks
├── pages/            # Page components
├── providers/        # Context providers
├── store/            # Redux store configuration
├── types/            # TypeScript interfaces
├── utils/            # Utility functions
└── theme/            # MUI theme configuration
```

### State Management

- **Redux Toolkit**: Global client state (auth, UI preferences)
- **React Query**: Server state (books, users, loans)
- **Local Storage**: Auth tokens, user session

### API Layer

- Axios client with request/response interceptors
- Automatic token refresh on 401 responses
- Base URL configurable via environment variables
- Standardized error handling

## Development Setup

### Prerequisites

- Node.js 18+
- npm 9+

### Installation

```bash
cd frontend
npm install
```

### Environment Configuration

Create `.env` file:

```
VITE_API_BASE_URL=http://localhost:8081
```

### Development Server

```bash
npm run dev
```

Runs on http://localhost:3000 with proxy to backend API

### Build Commands

```bash
npm run build      # Production build
npm run lint       # ESLint check
npm run lint:fix   # ESLint auto-fix
npm run format     # Prettier format
npm run preview    # Preview production build
```

## Naming Conventions

### Files

- Components: `PascalCase.tsx` (BookCard.tsx, Layout.tsx)
- Hooks: `useCamelCase.ts` (useAuth.ts, useBooks.ts)
- Pages: `PascalCase.tsx` (HomePage.tsx, LoginPage.tsx)
- Utils: `camelCase.ts` (dateUtils.ts, validation.ts)
- Types: `types.ts` (non-JSX files use .ts extension)

### Code Style

- Functional components only
- TypeScript interfaces for all props
- MUI `sx` prop for inline styles
- No `any` types allowed
- No console.log in production code

## Material UI Usage

### Theme

Custom theme defined in `theme/theme.ts`. Includes:

- Primary/secondary color palette
- Typography scale
- Component overrides
- Shape borderRadius

### Components

- Use Box, Stack, Grid for layout
- Use Typography component for text
- Avoid raw HTML elements (div, h1, p)
- Use `styled()` API for complex styling

## Authentication Flow

### Login Process

1. User submits credentials via LoginPage
2. `useAuth` hook calls `/api/v1/auth/login`
3. Tokens stored in localStorage
4. Axios interceptor adds Bearer token to requests
5. Token refresh handled automatically on 401

### Protected Routes

- Check authentication via `useAuth().isAuthenticated()`
- Redirect to login if unauthenticated
- User data available via `useAuth().getCurrentUser()`

## API Integration

### React Query Configuration

- Stale time: 5 minutes
- Retry: 1 attempt
- Refetch on window focus: disabled

### Example Query Hook

```typescript
export const useBooks = (page = 0, size = 10) => {
  return useQuery({
    queryKey: ['books', page, size],
    queryFn: async () => {
      const response = await apiClient.get<PaginatedResponse<Book>>(
        `/api/v1/books?page=${page}&size=${size}`
      )
      return response.data
    },
  })
}
```

### Error Handling

- Global error boundary for React errors
- ErrorAlert component for API errors
- Axios interceptor for token refresh failures

## Routing Structure

### Routes

- `/` - HomePage (public)
- `/login` - LoginPage (public)
- `/register` - RegisterPage (public)
- `/books` - BooksPage (protected)
- `/profile` - ProfilePage (protected)
- `*` - NotFoundPage (404)

### Layout

- AppProviders wraps entire application
- Layout component provides header/footer
- Nested routing within Layout

## Development Guidelines

### Component Creation

1. Create TypeScript interface for props
2. Use functional component syntax
3. Extract logic to custom hooks if complex
4. Follow MUI component patterns
5. Add PropTypes or TypeScript validation

### State Management Rules

- Use Redux for global UI state
- Use React Query for server data
- Use local state for component-specific data
- Avoid prop drilling beyond 2 levels

### Testing Strategy

- Unit tests for hooks and utils
- Component tests with React Testing Library
- Integration tests for pages
- Mock API calls with MSW

## Performance Considerations

### Code Splitting

- Route-based code splitting via React.lazy
- Component-level splitting for large features
- Vendor chunk separation

### Bundle Optimization

- Tree shaking enabled
- Minification for production
- Gzip compression via nginx

### Caching Strategy

- React Query cache for API responses
- Local storage for user session
- Service worker for offline support (future)

## Troubleshooting

### Common Issues

- CORS errors: Ensure backend allows frontend origin
- Token refresh failures: Check refresh endpoint availability
- Type errors: Verify TypeScript interfaces match backend DTOs
- Build failures: Check Node.js version compatibility

### Debugging

- Development mode includes React DevTools
- Redux DevTools extension available
- React Query DevTools for cache inspection
- Network tab for API request monitoring

## Future Enhancements

### Planned Features

- Advanced book search with filters
- User dashboard with reading history
- Admin panel for library management
- Real-time notifications
- Mobile-responsive improvements

### Technical Debt

- Add comprehensive test coverage
- Implement end-to-end testing
- Add performance monitoring
- Enhance accessibility compliance
- Add PWA capabilities
