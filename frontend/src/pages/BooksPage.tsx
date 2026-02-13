import React from 'react';
import Box from '@mui/material/Box';
import Typography from '@mui/material/Typography';
import Paper from '@mui/material/Paper';
import { useBooks } from '../hooks/useBooks';

const BooksPage: React.FC = () => {
  const { data: booksData, isLoading, error } = useBooks();

  return (
    <Box>
      <Typography variant="h4" component="h1" gutterBottom>
        Books
      </Typography>
      <Paper elevation={2} sx={{ p: 3 }}>
        {isLoading && <Typography>Loading books...</Typography>}
        {error && <Typography color="error">Error loading books</Typography>}
        {booksData && (
          <Box>
            <Typography variant="h6" gutterBottom>
              Total Books: {booksData.totalElements}
            </Typography>
            <Typography color="text.secondary">
              Books page will be implemented with search, filtering, and pagination
            </Typography>
          </Box>
        )}
      </Paper>
    </Box>
  );
};

export default BooksPage;
