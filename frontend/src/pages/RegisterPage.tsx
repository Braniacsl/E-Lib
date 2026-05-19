import React, { useState } from 'react';
import Box from '@mui/material/Box';
import TextField from '@mui/material/TextField';
import Button from '@mui/material/Button';
import Typography from '@mui/material/Typography';
import Paper from '@mui/material/Paper';
import Alert from '@mui/material/Alert';
import { useNavigate, Link as RouterLink } from 'react-router-dom';
import { apiClient } from '../axios';

const RegisterPage: React.FC = () => {
  const navigate = useNavigate();
  const [formData, setFormData] = useState({
    firstName: '',
    lastName: '',
    email: '',
    username: '',
    phoneNumber: '',
    address: '',
    password: '',
    confirmPassword: '',
  });
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);
  const [fieldErrors, setFieldErrors] = useState<Record<string, string>>({});

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError(null);
    setFieldErrors({});

    if (formData.password !== formData.confirmPassword) {
      setError('Passwords do not match');
      return;
    }

    setLoading(true);
    try {
      await apiClient.post('/api/v1/auth/register', {
        firstName: formData.firstName,
        lastName: formData.lastName,
        email: formData.email,
        username: formData.username,
        phoneNumber: formData.phoneNumber,
        address: formData.address,
        password: formData.password,
      });
      navigate('/login', { state: { registered: true } });
    } catch (err: any) {
      console.error('Register error:', err);
      const data = err.response?.data;
      if (!err.response) {
        setError(
          'Network error — is the API server running on port 8081? Check Docker: docker compose ps'
        );
      } else if (data?.validationErrors) {
        setFieldErrors(data.validationErrors);
        setError(data.message || 'Please fix the errors below');
      } else {
        const msg = data?.message || 'Registration failed';
        setError(msg);
        // Try to map known error messages to fields
        if (msg.includes('email')) setFieldErrors(prev => ({ ...prev, email: msg }));
        else if (msg.includes('Username') || msg.includes('username'))
          setFieldErrors(prev => ({ ...prev, username: msg }));
        else if (msg.includes('phone') || msg.includes('Phone'))
          setFieldErrors(prev => ({ ...prev, phoneNumber: msg }));
      }
    } finally {
      setLoading(false);
    }
  };

  return (
    <Box sx={{ maxWidth: 480, mx: 'auto', py: 8 }}>
      <Paper elevation={3} sx={{ p: 4 }}>
        <Typography variant="h4" component="h1" gutterBottom align="center">
          Register
        </Typography>
        {error && (
          <Alert severity="error" sx={{ mb: 2 }}>
            {error}
          </Alert>
        )}
        <form onSubmit={handleSubmit}>
          <TextField
            fullWidth
            label="First Name"
            value={formData.firstName}
            onChange={e => setFormData({ ...formData, firstName: e.target.value })}
            margin="normal"
            required
            error={!!fieldErrors.firstName}
            helperText={fieldErrors.firstName}
          />
          <TextField
            fullWidth
            label="Last Name"
            value={formData.lastName}
            onChange={e => setFormData({ ...formData, lastName: e.target.value })}
            margin="normal"
            required
            error={!!fieldErrors.lastName}
            helperText={fieldErrors.lastName}
          />
          <TextField
            fullWidth
            label="Email"
            type="email"
            value={formData.email}
            onChange={e => setFormData({ ...formData, email: e.target.value })}
            margin="normal"
            required
            error={!!fieldErrors.email}
            helperText={fieldErrors.email}
          />
          <TextField
            fullWidth
            label="Username"
            value={formData.username}
            onChange={e => setFormData({ ...formData, username: e.target.value })}
            margin="normal"
            required
            error={!!fieldErrors.username}
            helperText={fieldErrors.username}
          />
          <TextField
            fullWidth
            label="Phone Number"
            value={formData.phoneNumber}
            onChange={e => setFormData({ ...formData, phoneNumber: e.target.value })}
            margin="normal"
            required
            error={!!fieldErrors.phoneNumber}
            helperText={fieldErrors.phoneNumber}
          />
          <TextField
            fullWidth
            label="Address"
            value={formData.address}
            onChange={e => setFormData({ ...formData, address: e.target.value })}
            margin="normal"
            required
            error={!!fieldErrors.address}
            helperText={fieldErrors.address}
          />
          <TextField
            fullWidth
            label="Password"
            type="password"
            value={formData.password}
            onChange={e => setFormData({ ...formData, password: e.target.value })}
            margin="normal"
            required
            error={!!fieldErrors.password}
            helperText={fieldErrors.password}
          />
          <TextField
            fullWidth
            label="Confirm Password"
            type="password"
            value={formData.confirmPassword}
            onChange={e => setFormData({ ...formData, confirmPassword: e.target.value })}
            margin="normal"
            required
          />
          <Button
            type="submit"
            fullWidth
            variant="contained"
            size="large"
            disabled={loading}
            sx={{ mt: 3 }}
          >
            {loading ? 'Registering...' : 'Register'}
          </Button>
        </form>
        <Typography align="center" sx={{ mt: 2 }}>
          Already have an account?{' '}
          <RouterLink to="/login" style={{ textDecoration: 'none' }}>
            Login here
          </RouterLink>
        </Typography>
      </Paper>
    </Box>
  );
};

export default RegisterPage;
