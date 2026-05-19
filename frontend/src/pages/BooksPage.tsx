import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import Box from '@mui/material/Box';
import Button from '@mui/material/Button';
import Card from '@mui/material/Card';
import CardMedia from '@mui/material/CardMedia';
import CardContent from '@mui/material/CardContent';
import CardActionArea from '@mui/material/CardActionArea';
import Chip from '@mui/material/Chip';
import Container from '@mui/material/Container';
import Grid from '@mui/material/Grid';
import Pagination from '@mui/material/Pagination';
import Stack from '@mui/material/Stack';
import TextField from '@mui/material/TextField';
import Typography from '@mui/material/Typography';
import { useQuery } from '@tanstack/react-query';
import SearchOffIcon from '@mui/icons-material/SearchOff';
import EmptyState from '../components/shared/EmptyState';
import LoadingSpinner from '../components/shared/LoadingSpinner';
import ErrorAlert from '../components/shared/ErrorAlert';
import { useBooks } from '../hooks/useBooks';
import { apiClient } from '../axios';

const useCategories = () =>
  useQuery({
    queryKey: ['categories'],
    queryFn: async () => {
      const response = await apiClient.get<string[]>('/api/v1/books/categories');
      return response.data;
    },
  });

const BooksPage: React.FC = () => {
  const navigate = useNavigate();
  const [page, setPage] = useState(0);
  const [search, setSearch] = useState('');
  const [searchInput, setSearchInput] = useState('');
  const [category, setCategory] = useState('');
  const { data: categories } = useCategories();
  const { data: booksData, isLoading, error } = useBooks(page, 10, search, category);

  const handleSearch = (e: React.FormEvent) => {
    e.preventDefault();
    setPage(0);
    setSearch(searchInput);
  };

  return (
    <Container maxWidth="lg">
      <Typography variant="h4" component="h1" gutterBottom>
        Books
      </Typography>

      <Box component="form" onSubmit={handleSearch} sx={{ mb: 2, display: 'flex', gap: 1 }}>
        <TextField
          size="small"
          placeholder="Search by title or author..."
          value={searchInput}
          onChange={e => setSearchInput(e.target.value)}
          sx={{ flexGrow: 1, maxWidth: 400 }}
        />
        <Button type="submit" variant="contained" size="medium">
          Search
        </Button>
      </Box>

      {categories && categories.length > 0 && (
        <Stack direction="row" spacing={0.5} sx={{ mb: 3, flexWrap: 'wrap', gap: 0.5 }}>
          <Chip
            label="All"
            size="small"
            variant={category === '' ? 'filled' : 'outlined'}
            color={category === '' ? 'primary' : 'default'}
            onClick={() => {
              setCategory('');
              setPage(0);
            }}
          />
          {categories.map(c => (
            <Chip
              key={c}
              label={c}
              size="small"
              variant={category === c ? 'filled' : 'outlined'}
              color={category === c ? 'primary' : 'default'}
              onClick={() => {
                setCategory(c);
                setPage(0);
              }}
            />
          ))}
        </Stack>
      )}

      {isLoading && <LoadingSpinner />}
      {error && <ErrorAlert error="Failed to load books" />}

      {booksData && booksData.content.length === 0 && (
        <EmptyState
          icon={SearchOffIcon}
          title={search ? `No results for "${search}"` : 'No books found'}
          description={
            search ? 'Try a different search term' : 'Check back later for new additions'
          }
        />
      )}

      {booksData && booksData.content.length > 0 && (
        <>
          <Typography color="text.secondary" sx={{ mb: 2 }}>
            {booksData.totalElements} book{booksData.totalElements !== 1 ? 's' : ''} found
          </Typography>

          <Grid container spacing={2}>
            {booksData.content.map(book => (
              <Grid size={{ xs: 12, sm: 6, md: 4 }} key={book.id}>
                <Card
                  variant="outlined"
                  sx={{
                    height: '100%',
                    display: 'flex',
                    flexDirection: 'column',
                    transition: 'transform 0.15s, box-shadow 0.15s',
                    '&:hover': { transform: 'translateY(-3px)', boxShadow: 4 },
                  }}
                >
                  <CardActionArea
                    onClick={() => navigate(`/books/${book.id}`)}
                    sx={{
                      flex: 1,
                      display: 'flex',
                      flexDirection: 'column',
                      alignItems: 'stretch',
                    }}
                  >
                    {book.coverImageUrl ? (
                      <CardMedia
                        component="img"
                        height="160"
                        image={book.coverImageUrl}
                        alt={book.title}
                        sx={{ objectFit: 'contain', bgcolor: 'grey.100', p: 1 }}
                        onError={(e: React.SyntheticEvent<HTMLImageElement>) => {
                          (e.target as HTMLImageElement).style.display = 'none';
                        }}
                      />
                    ) : null}
                    <CardContent sx={{ flex: 1 }}>
                      <Typography variant="h6" noWrap>
                        {book.title}
                      </Typography>
                      <Typography variant="body2" color="text.secondary">
                        {book.author}
                      </Typography>
                      <Box sx={{ mt: 1, display: 'flex', gap: 0.5, flexWrap: 'wrap' }}>
                        <Chip
                          label={`${book.availableCopies} available`}
                          size="small"
                          color={book.availableCopies > 0 ? 'success' : 'default'}
                        />
                        <Chip label={book.isbn} size="small" variant="outlined" />
                      </Box>
                    </CardContent>
                  </CardActionArea>
                </Card>
              </Grid>
            ))}
          </Grid>

          {booksData.totalPages > 1 && (
            <Box sx={{ mt: 3, display: 'flex', justifyContent: 'center' }}>
              <Pagination
                count={booksData.totalPages}
                page={page + 1}
                onChange={(_, value) => setPage(value - 1)}
              />
            </Box>
          )}
        </>
      )}
    </Container>
  );
};

export default BooksPage;
