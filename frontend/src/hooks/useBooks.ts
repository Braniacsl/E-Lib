import { useQuery } from '@tanstack/react-query';
import { apiClient } from '../axios.ts';
import { Book, PaginatedResponse } from '../types/types.ts';

export const useBooks = (page = 0, size = 10) => {
  return useQuery({
    queryKey: ['books', page, size],
    queryFn: async () => {
      const response = await apiClient.get<PaginatedResponse<Book>>(
        `/api/v1/books?page=${page}&size=${size}`
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
