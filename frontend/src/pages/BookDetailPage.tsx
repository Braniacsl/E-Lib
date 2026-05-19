import React from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import Box from '@mui/material/Box';
import Button from '@mui/material/Button';
import Chip from '@mui/material/Chip';
import Container from '@mui/material/Container';
import Divider from '@mui/material/Divider';
import Paper from '@mui/material/Paper';
import Stack from '@mui/material/Stack';
import Typography from '@mui/material/Typography';
import ArrowBackIcon from '@mui/icons-material/ArrowBack';
import BookIcon from '@mui/icons-material/MenuBook';
import LoadingSpinner from '../components/shared/LoadingSpinner';
import ErrorAlert from '../components/shared/ErrorAlert';
import { useBook } from '../hooks/useBooks';
import { useBorrowBook } from '../hooks/useLoans';
import { useAuth } from '../hooks/useAuth';

const BookDetailPage: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const { data: book, isLoading, error } = useBook(id || '');
  const borrowMutation = useBorrowBook();
  const { isAuthenticated, getCurrentUser } = useAuth();
  const authed = isAuthenticated();
  const user = getCurrentUser();

  if (isLoading) return <LoadingSpinner />;
  if (error || !book) return <ErrorAlert error="Book not found" />;

  const available = book.availableCopies > 0;
  const availabilityLabel = available
    ? `${book.availableCopies} of ${book.totalCopies} available`
    : 'All copies borrowed';

  return (
    <Container maxWidth="md">
      <Button startIcon={<ArrowBackIcon />} onClick={() => navigate('/books')} sx={{ mb: 3 }}>
        Back to Books
      </Button>

      <Paper elevation={2} sx={{ p: 4 }}>
        <Stack direction={{ xs: 'column', md: 'row' }} spacing={4}>
          <Box
            sx={{
              width: { xs: '100%', md: 200 },
              height: 280,
              bgcolor: 'grey.100',
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'center',
              borderRadius: 2,
              flexShrink: 0,
              overflow: 'hidden',
            }}
          >
            {book.coverImageUrl ? (
              <Box
                component="img"
                src={book.coverImageUrl}
                alt={book.title}
                sx={{ width: '100%', height: '100%', objectFit: 'contain', p: 1 }}
              />
            ) : (
              <BookIcon sx={{ fontSize: 80, color: 'primary.light' }} />
            )}
          </Box>

          <Box sx={{ flex: 1 }}>
            <Typography variant="h4" gutterBottom>
              {book.title}
            </Typography>
            <Typography variant="h6" color="text.secondary" gutterBottom>
              {book.author}
            </Typography>

            <Stack direction="row" spacing={1} sx={{ mb: 3 }}>
              <Chip
                label={availabilityLabel}
                color={available ? 'success' : 'error'}
                size="small"
              />
              <Chip label={book.isbn} variant="outlined" size="small" />
            </Stack>

            <Divider sx={{ mb: 3 }} />

            <Stack spacing={1.5}>
              <DetailRow label="Publisher" value={book.publisher} />
              <DetailRow label="Year" value={String(book.publicationYear)} />
              <DetailRow label="ISBN" value={book.isbn} />
              <DetailRow label="Total Copies" value={String(book.totalCopies)} />
            </Stack>

            {authed && (
              <Button
                variant="contained"
                size="large"
                disabled={!available || borrowMutation.isPending}
                onClick={() => borrowMutation.mutate({ bookId: book.id, userEmail: user?.email })}
                sx={{ mt: 3 }}
              >
                {borrowMutation.isPending
                  ? 'Borrowing...'
                  : available
                    ? 'Borrow This Book'
                    : 'Currently Unavailable'}
              </Button>
            )}

            {borrowMutation.isSuccess && (
              <Typography color="success.main" sx={{ mt: 1 }}>
                Book borrowed. Due in 14 days.
              </Typography>
            )}

            {borrowMutation.isError && (
              <Typography color="error" sx={{ mt: 1 }}>
                {(borrowMutation.error as any)?.response?.data?.message || 'Failed to borrow book'}
              </Typography>
            )}
          </Box>
        </Stack>
      </Paper>
    </Container>
  );
};

const DetailRow: React.FC<{ label: string; value: string }> = ({ label, value }) => (
  <Stack direction="row" spacing={2}>
    <Typography variant="body2" color="text.secondary" sx={{ minWidth: 120 }}>
      {label}
    </Typography>
    <Typography variant="body2">{value}</Typography>
  </Stack>
);

export default BookDetailPage;
