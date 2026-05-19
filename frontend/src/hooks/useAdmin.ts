import { useQuery } from '@tanstack/react-query';
import { apiClient } from '../axios';
import type { User, Loan, Book, PaginatedResponse } from '../types/types';

export const useAdminUsers = () => {
  return useQuery({
    queryKey: ['admin', 'users'],
    queryFn: async () => {
      const response = await apiClient.get<User[]>('/api/v1/users');
      return { content: response.data, totalElements: response.data.length };
    },
  });
};

export const useAdminLoans = () => {
  return useQuery({
    queryKey: ['admin', 'loans'],
    queryFn: async () => {
      const response = await apiClient.get<Loan[]>('/api/v1/loans/overdue');
      return { content: response.data, totalElements: response.data.length };
    },
  });
};

export const useAdminBooks = (page = 0, size = 20) => {
  return useQuery({
    queryKey: ['admin', 'books', page, size],
    queryFn: async () => {
      const response = await apiClient.get<PaginatedResponse<Book>>(
        `/api/v1/books?page=${page}&size=${size}`
      );
      return response.data;
    },
  });
};
