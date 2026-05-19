import React from 'react';
import Box from '@mui/material/Box';
import Button from '@mui/material/Button';
import Card from '@mui/material/Card';
import CardContent from '@mui/material/CardContent';
import Container from '@mui/material/Container';
import Grid from '@mui/material/Grid';
import Paper from '@mui/material/Paper';
import Typography from '@mui/material/Typography';
import Avatar from '@mui/material/Avatar';
import Stack from '@mui/material/Stack';
import Divider from '@mui/material/Divider';
import EmailIcon from '@mui/icons-material/Email';
import BadgeIcon from '@mui/icons-material/Badge';
import EuroIcon from '@mui/icons-material/Euro';
import MenuBookIcon from '@mui/icons-material/MenuBook';
import { useAuth } from '../hooks/useAuth';
import { useBalance } from '../hooks/useLoans';
import { useLoans } from '../hooks/useLoans';

const ProfilePage: React.FC = () => {
  const { getCurrentUser, logout } = useAuth();
  const user = getCurrentUser();
  const { data: balance } = useBalance(user?.id || '');
  const { data: loans } = useLoans(user?.id || '');
  const activeLoans = loans?.filter(l => l.status === 'ACTIVE').length || 0;

  if (!user) {
    return (
      <Container maxWidth="sm" sx={{ textAlign: 'center', py: 8 }}>
        <Typography variant="h5">Please login to view your profile</Typography>
      </Container>
    );
  }

  const initials = `${user.firstName?.[0] || ''}${user.lastName?.[0] || ''}`.toUpperCase();

  return (
    <Container maxWidth="md">
      <Typography variant="h4" component="h1" gutterBottom>
        My Profile
      </Typography>

      {/* Stats row */}
      <Grid container spacing={2} sx={{ mb: 3 }}>
        <Grid size={{ xs: 6, md: 3 }}>
          <Card elevation={2} sx={{ textAlign: 'center', py: 2, borderRadius: 2 }}>
            <MenuBookIcon color="primary" sx={{ fontSize: 28, mb: 0.5 }} />
            <Typography variant="h5" fontWeight={600}>
              {activeLoans}
            </Typography>
            <Typography variant="body2" color="text.secondary">
              Active Loans
            </Typography>
          </Card>
        </Grid>
        <Grid size={{ xs: 6, md: 3 }}>
          <Card elevation={2} sx={{ textAlign: 'center', py: 2, borderRadius: 2 }}>
            <EuroIcon
              color={balance && Number(balance.totalFineAmount) > 0 ? 'error' : 'primary'}
              sx={{ fontSize: 28, mb: 0.5 }}
            />
            <Typography variant="h5" fontWeight={600}>
              €{balance ? Number(balance.totalFineAmount).toFixed(2) : '0.00'}
            </Typography>
            <Typography variant="body2" color="text.secondary">
              Fines
            </Typography>
          </Card>
        </Grid>
      </Grid>

      {/* Profile card */}
      <Paper elevation={2} sx={{ borderRadius: 2, overflow: 'hidden' }}>
        <Box sx={{ bgcolor: 'primary.main', py: 4, textAlign: 'center', color: 'white' }}>
          <Avatar
            sx={{
              width: 80,
              height: 80,
              mx: 'auto',
              mb: 2,
              bgcolor: 'secondary.main',
              fontSize: 32,
              fontWeight: 700,
            }}
          >
            {initials}
          </Avatar>
          <Typography variant="h5" fontWeight={600}>
            {user.firstName} {user.lastName}
          </Typography>
          <Typography variant="body2" sx={{ opacity: 0.8 }}>
            {user.roles?.join(', ') || 'USER'}
          </Typography>
        </Box>
        <CardContent sx={{ p: 3 }}>
          <Stack spacing={2}>
            <Stack direction="row" spacing={1.5} alignItems="center">
              <EmailIcon color="action" />
              <Box>
                <Typography variant="caption" color="text.secondary">
                  Email
                </Typography>
                <Typography>{user.email}</Typography>
              </Box>
            </Stack>
            <Divider />
            <Stack direction="row" spacing={1.5} alignItems="center">
              <BadgeIcon color="action" />
              <Box>
                <Typography variant="caption" color="text.secondary">
                  Username
                </Typography>
                <Typography>{user.username}</Typography>
              </Box>
            </Stack>
          </Stack>
          <Button variant="outlined" color="error" onClick={logout} sx={{ mt: 3 }}>
            Logout
          </Button>
        </CardContent>
      </Paper>
    </Container>
  );
};

export default ProfilePage;
