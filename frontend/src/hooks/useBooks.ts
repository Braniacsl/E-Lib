import { useQuery } from '@tanstack/react-query';
import { apiClient } from '../axios';
import type { Book, PaginatedResponse } from '../types/types';

export const useBooks = (page = 0, size = 10, search = '', category = '') => {
  return useQuery({
    queryKey: ['books', page, size, search, category],
    queryFn: async () => {
      if (search) {
        const response = await apiClient.get<PaginatedResponse<Book>>(
          `/api/v1/books/search?query=${encodeURIComponent(search)}&page=${page}&size=${size}`
        );
        return response.data;
      }
      const params = new URLSearchParams({ page: String(page), size: String(size) });
      if (category) params.set('category', category);
      const response = await apiClient.get<PaginatedResponse<Book>>(
        `/api/v1/books?${params.toString()}`
      );
      return response.data;
    },
  });
};

export const useBook = (id: string) => {
  return useQuery({
    queryKey: ['book', id],
    queryFn: async () => {
      const response = await apiClient.get<Book>(`/api/v1/books/${id}`);
      return response.data;
    },
    enabled: !!id,
  });
};
