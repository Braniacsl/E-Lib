export interface User {
  id: string;
  email: string;
  firstName: string;
  lastName: string;
  username: string;
  phoneNumber: string;
  address: string;
  roles: string[];
  isActive: boolean;
  createdAt: string;
  updatedAt: string;
}

export interface Book {
  id: string;
  title: string;
  author: string;
  isbn: string;
  description?: string;
  publicationYear: number;
  publisher: string;
  category?: string;
  language?: string;
  pageCount?: number;
  coverImageUrl?: string;
  availableCopies: number;
  totalCopies: number;
  isActive?: boolean;
  createdAt: string;
  updatedAt: string;
}

export interface Loan {
  id: string;
  userId: string;
  bookId: string;
  borrowDate: string;
  dueDate: string;
  returnDate: string | null;
  status: 'ACTIVE' | 'RETURNED' | 'OVERDUE';
}

export interface ApiResponse<T> {
  data: T;
  message: string;
  timestamp: string;
}

export interface PaginatedResponse<T> {
  content: T[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
}
