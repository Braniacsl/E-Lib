import React from 'react';
import Box from '@mui/material/Box';
import Typography from '@mui/material/Typography';
import Paper from '@mui/material/Paper';
import Button from '@mui/material/Button';
import { useAuth } from '../hooks/useAuth';

const ProfilePage: React.FC = () => {
  const { getCurrentUser, logout } = useAuth();
  const user = getCurrentUser();

  if (!user) {
    return (
      <Box sx={{ textAlign: 'center', py: 8 }}>
        <Typography variant="h5">Please login to view your profile</Typography>
      </Box>
    );
  }

  return (
    <Box>
      <Typography variant="h4" component="h1" gutterBottom>
        My Profile
      </Typography>
      <Paper elevation={2} sx={{ p: 3 }}>
        <Typography variant="h6" gutterBottom>
          {user.firstName} {user.lastName}
        </Typography>
        <Typography color="text.secondary" gutterBottom>
          Email: {user.email}
        </Typography>
        <Typography color="text.secondary" gutterBottom>
          Role: {user.role}
        </Typography>
        <Button variant="outlined" color="error" onClick={logout} sx={{ mt: 2 }}>
          Logout
        </Button>
      </Paper>
    </Box>
  );
};

export default ProfilePage;
