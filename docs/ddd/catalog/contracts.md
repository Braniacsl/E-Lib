# Catalog Context: Inter-Context Contracts

## Public API (via Gateway, consumed by Frontend)

**GET** `/api/v1/books`
```json
// Response 200 OK
[
  {
    "id": "...",
    "title": "Clean Code",
    "author": "Robert C. Martin",
    "isbn": "978-0132350884",
    "description": "...",
    "publicationYear": 2008,
    "publisher": "Prentice Hall",
    "totalCopies": 5,
    "availableCopies": 3,
    "category": "Software Engineering",
    "language": "English",
    "pageCount": 464,
    "coverImageUrl": "...",
    "isActive": true
  }
]
```

**GET** `/api/v1/books/{id}`
Returns a single book by UUID.

**POST** `/api/v1/books` (Admin/Librarian only)
```json
// Request
{
  "title": "Clean Code",
  "author": "Robert C. Martin",
  "isbn": "978-0132350884",
  "description": "A Handbook of Agile Software Craftsmanship",
  "publicationYear": 2008,
  "publisher": "Prentice Hall",
  "totalCopies": 5,
  "category": "Software Engineering",
  "language": "English",
  "pageCount": 464,
  "coverImageUrl": "https://..."
}
// Response 201 Created (same shape as GET response)
```

**PUT** `/api/v1/books/{id}` (Admin/Librarian only)
**DELETE** `/api/v1/books/{id}` (Admin/Librarian only, soft delete via `isActive = false`)

**GET** `/api/v1/books/search?query=clean+code`
Search by title or author.

**GET** `/api/v1/books/categories`
List distinct categories.

## Internal API (consumed by Borrowing Service)

These endpoints are called by the Borrowing service via Feign client. They are not exposed through the Gateway to end users.

**GET** `/api/v1/books/{id}/availability`
```json
// Response 200 OK
{ "bookId": "...", "available": true, "availableCopies": 3 }
```

**PUT** `/api/v1/books/{id}/decrement-stock`
```json
// Response 200 OK
{ "bookId": "...", "availableCopies": 2 }
// Response 400 Bad Request (if availableCopies == 0)
{ "error": "No copies available for this book" }
```

**PUT** `/api/v1/books/{id}/increment-stock`
```json
// Response 200 OK
{ "bookId": "...", "availableCopies": 3 }
// Response 400 Bad Request (if availableCopies == totalCopies)
{ "error": "Stock already at maximum" }
```

## Anti-Corruption Layer

The Borrowing service defines a local `CatalogStockResponse` DTO to receive responses from the Catalog's stock endpoints. It never imports Catalog's `Book` entity or internal DTOs. The Feign client interface lives entirely within the Borrowing service codebase.

## Context Responsibilities

The Catalog context is the sole authority on:
- Book metadata (title, author, ISBN, etc.)
- Stock levels (totalCopies, availableCopies)
- Book lifecycle (active/inactive)

Other contexts must request stock information through the published API; they may not directly access the `elib_catalog_db` database.
