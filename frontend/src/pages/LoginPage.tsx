import React, { useState } from 'react';
import Box from '@mui/material/Box';
import Button from '@mui/material/Button';
import Divider from '@mui/material/Divider';
import Paper from '@mui/material/Paper';
import TextField from '@mui/material/TextField';
import Typography from '@mui/material/Typography';
import LoginIcon from '@mui/icons-material/Login';
import PersonAddIcon from '@mui/icons-material/PersonAdd';
import { useNavigate, Link as RouterLink } from 'react-router-dom';
import { useAuth } from '../hooks/useAuth';

const LoginPage: React.FC = () => {
  const navigate = useNavigate();
  const { login, loading, error } = useAuth();
  const [credentials, setCredentials] = useState({ emailOrUsername: '', password: '' });

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    const result = await login(credentials);
    if (result.success) {
      navigate('/');
    }
  };

  return (
    <Box sx={{ maxWidth: 420, mx: 'auto', py: 8 }}>
      <Paper elevation={3} sx={{ borderRadius: 2, overflow: 'hidden' }}>
        <Box sx={{ bgcolor: 'primary.main', py: 3, textAlign: 'center', color: 'white' }}>
          <LoginIcon sx={{ fontSize: 40, mb: 0.5 }} />
          <Typography variant="h5" fontWeight={600}>
            Welcome back
          </Typography>
          <Typography variant="body2" sx={{ opacity: 0.8 }}>
            Sign in to your account
          </Typography>
        </Box>
        <Box sx={{ p: 3 }}>
          {error && (
            <Typography color="error" align="center" sx={{ mb: 2 }}>
              {error}
            </Typography>
          )}
          <Box component="form" onSubmit={handleSubmit}>
            <TextField
              fullWidth
              label="Email or Username"
              value={credentials.emailOrUsername}
              onChange={e => setCredentials({ ...credentials, emailOrUsername: e.target.value })}
              margin="normal"
              required
            />
            <TextField
              fullWidth
              label="Password"
              type="password"
              value={credentials.password}
              onChange={e => setCredentials({ ...credentials, password: e.target.value })}
              margin="normal"
              required
            />
            <Button
              type="submit"
              fullWidth
              variant="contained"
              size="large"
              disabled={loading}
              sx={{ mt: 3, py: 1.2 }}
            >
              {loading ? 'Logging in...' : 'Login'}
            </Button>
          </Box>
          <Divider sx={{ my: 2 }} />
          <Typography align="center" variant="body2">
            Don't have an account?{' '}
            <Button
              component={RouterLink}
              to="/register"
              size="small"
              startIcon={<PersonAddIcon />}
            >
              Register here
            </Button>
          </Typography>
        </Box>
      </Paper>
    </Box>
  );
};

export default LoginPage;
