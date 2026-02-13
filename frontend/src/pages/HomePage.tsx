import React from 'react';
import Box from '@mui/material/Box';
import Typography from '@mui/material/Typography';
import Button from '@mui/material/Button';
import Stack from '@mui/material/Stack';
import { useNavigate } from 'react-router-dom';

const HomePage: React.FC = () => {
  const navigate = useNavigate();

  return (
    <Box sx={{ textAlign: 'center', py: 8 }}>
      <Typography variant="h2" component="h1" gutterBottom>
        Welcome to E-Library System
      </Typography>
      <Typography variant="h5" color="text.secondary" paragraph>
        A modern digital library management system
      </Typography>
      <Stack direction="row" spacing={2} justifyContent="center" sx={{ mt: 4 }}>
        <Button variant="contained" size="large" onClick={() => navigate('/books')}>
          Browse Books
        </Button>
        <Button variant="outlined" size="large" onClick={() => navigate('/login')}>
          Login
        </Button>
        <Button variant="outlined" size="large" onClick={() => navigate('/register')}>
          Register
        </Button>
      </Stack>
    </Box>
  );
};

export default HomePage;
