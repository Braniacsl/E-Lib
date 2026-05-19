import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { apiClient } from '../axios';
import type { Loan } from '../types/types';

export const useLoans = (userId: string) => {
  return useQuery({
    queryKey: ['loans', userId],
    queryFn: async () => {
      const response = await apiClient.get<Loan[]>(`/api/v1/loans/user/${userId}`);
      return response.data ?? [];
    },
    enabled: !!userId,
  });
};

export const useBorrowBook = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: async ({ bookId, userEmail }: { bookId: string; userEmail?: string }) => {
      const response = await apiClient.post<Loan>(
        `/api/v1/loans/borrow`,
        { bookId },
        {
          headers: userEmail ? { 'X-User-Email': userEmail } : {},
        }
      );
      return response.data;
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['loans'] });
      queryClient.invalidateQueries({ queryKey: ['books'] });
      queryClient.invalidateQueries({ queryKey: ['book'] });
    },
  });
};

export const useReturnBook = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: async ({ loanId, userEmail }: { loanId: string; userEmail?: string }) => {
      const response = await apiClient.post<Loan>(`/api/v1/loans/${loanId}/return`, null, {
        headers: userEmail ? { 'X-User-Email': userEmail } : {},
      });
      return response.data;
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['loans'] });
      queryClient.invalidateQueries({ queryKey: ['books'] });
    },
  });
};

export const useBalance = (userId: string) => {
  return useQuery({
    queryKey: ['balance', userId],
    queryFn: async () => {
      const response = await apiClient.get<{ userId: string; totalFineAmount: number }>(
        `/api/v1/loans/user/${userId}/balance`
      );
      return response.data;
    },
    enabled: !!userId,
  });
};
