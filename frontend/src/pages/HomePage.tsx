import React from 'react';
import Box from '@mui/material/Box';
import Button from '@mui/material/Button';
import Card from '@mui/material/Card';
import CardContent from '@mui/material/CardContent';
import Container from '@mui/material/Container';
import Grid from '@mui/material/Grid';
import Stack from '@mui/material/Stack';
import Typography from '@mui/material/Typography';
import { useNavigate } from 'react-router-dom';
import MenuBookIcon from '@mui/icons-material/MenuBook';
import SearchIcon from '@mui/icons-material/Search';
import HistoryIcon from '@mui/icons-material/History';
import ulLogo from '../assets/ul-logo.svg';
import { useAuth } from '../hooks/useAuth';

const features = [
  {
    icon: <SearchIcon sx={{ fontSize: 40 }} />,
    title: 'Browse',
    desc: 'Explore the full catalogue of books available at the UL Library',
  },
  {
    icon: <MenuBookIcon sx={{ fontSize: 40 }} />,
    title: 'Borrow',
    desc: 'Reserve and borrow books with a single click',
  },
  {
    icon: <HistoryIcon sx={{ fontSize: 40 }} />,
    title: 'Track',
    desc: 'View your loan history and manage returns',
  },
];

const HomePage: React.FC = () => {
  const navigate = useNavigate();
  const { isAuthenticated, getCurrentUser } = useAuth();
  const authed = isAuthenticated();
  const user = getCurrentUser();

  return (
    <Box>
      {/* Hero */}
      <Box
        sx={{
          background: 'linear-gradient(135deg, #005335 0%, #007a4d 50%, #00b140 100%)',
          color: 'white',
          py: { xs: 6, md: 10 },
          px: 2,
          textAlign: 'center',
        }}
      >
        <Container maxWidth="md">
          <Box
            component="img"
            src={ulLogo}
            alt="University of Limerick"
            sx={{ height: { xs: 40, md: 56 }, mb: 3, filter: 'brightness(0) invert(1)' }}
          />
          <Typography variant="h3" fontWeight={700} gutterBottom>
            E-Library System
          </Typography>
          <Typography variant="h6" sx={{ opacity: 0.9, mb: 4, maxWidth: 500, mx: 'auto' }}>
            Your gateway to the University of Limerick digital catalogue
          </Typography>
          <Stack direction="row" spacing={2} justifyContent="center">
            <Button
              variant="contained"
              size="large"
              onClick={() => navigate('/books')}
              sx={{ bgcolor: 'white', color: '#005335', '&:hover': { bgcolor: 'grey.100' } }}
            >
              Browse Books
            </Button>
            {!authed && (
              <Button
                variant="outlined"
                size="large"
                onClick={() => navigate('/register')}
                sx={{
                  color: 'white',
                  borderColor: 'white',
                  '&:hover': { borderColor: 'white', bgcolor: 'rgba(255,255,255,0.1)' },
                }}
              >
                Register
              </Button>
            )}
          </Stack>
        </Container>
      </Box>

      {/* Welcome back */}
      {authed && (
        <Container maxWidth="md" sx={{ mt: -3, mb: 4 }}>
          <Card elevation={4} sx={{ borderRadius: 2 }}>
            <CardContent sx={{ textAlign: 'center', py: 3 }}>
              <Typography variant="h5" gutterBottom>
                Welcome back, {user?.firstName}
              </Typography>
              <Stack direction="row" spacing={2} justifyContent="center">
                <Button variant="outlined" onClick={() => navigate('/loans')}>
                  My Loans
                </Button>
                <Button variant="outlined" onClick={() => navigate('/profile')}>
                  Profile
                </Button>
              </Stack>
            </CardContent>
          </Card>
        </Container>
      )}

      {/* Feature cards */}
      <Container maxWidth="md" sx={{ py: 6 }}>
        <Typography variant="h4" align="center" gutterBottom sx={{ mb: 4 }}>
          How it works
        </Typography>
        <Grid container spacing={3}>
          {features.map(f => (
            <Grid size={{ xs: 12, md: 4 }} key={f.title}>
              <Card
                elevation={2}
                sx={{ height: '100%', textAlign: 'center', py: 3, borderRadius: 2 }}
              >
                <Box sx={{ color: 'primary.main', mb: 1 }}>{f.icon}</Box>
                <Typography variant="h6" gutterBottom>
                  {f.title}
                </Typography>
                <Typography variant="body2" color="text.secondary" px={2}>
                  {f.desc}
                </Typography>
              </Card>
            </Grid>
          ))}
        </Grid>
      </Container>
    </Box>
  );
};

export default HomePage;
