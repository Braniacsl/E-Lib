import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import AppBar from '@mui/material/AppBar';
import Toolbar from '@mui/material/Toolbar';
import Typography from '@mui/material/Typography';
import Button from '@mui/material/Button';
import IconButton from '@mui/material/IconButton';
import Avatar from '@mui/material/Avatar';
import Menu from '@mui/material/Menu';
import MenuItem from '@mui/material/MenuItem';
import ListItemIcon from '@mui/material/ListItemIcon';
import Divider from '@mui/material/Divider';
import Box from '@mui/material/Box';
import Stack from '@mui/material/Stack';
import MenuBookIcon from '@mui/icons-material/MenuBook';
import PersonIcon from '@mui/icons-material/Person';
import HistoryIcon from '@mui/icons-material/History';
import AdminPanelSettingsIcon from '@mui/icons-material/AdminPanelSettings';
import LogoutIcon from '@mui/icons-material/Logout';
import DarkModeIcon from '@mui/icons-material/DarkMode';
import LightModeIcon from '@mui/icons-material/LightMode';
import { useAuth } from '../../hooks/useAuth';
import { useColorMode } from '../../providers/AppProviders';

const Header: React.FC = () => {
  const navigate = useNavigate();
  const { isAuthenticated, getCurrentUser, logout } = useAuth();
  const { toggleColorMode } = useColorMode();
  const [mode, setMode] = useState<'light' | 'dark'>(
    () => (localStorage.getItem('colorMode') as 'light' | 'dark') || 'light'
  );
  const authed = isAuthenticated();
  const user = getCurrentUser();
  const [anchorEl, setAnchorEl] = useState<null | HTMLElement>(null);

  const handleMenuOpen = (e: React.MouseEvent<HTMLElement>) => setAnchorEl(e.currentTarget);
  const handleMenuClose = () => setAnchorEl(null);

  const initials = user
    ? `${user.firstName?.[0] || ''}${user.lastName?.[0] || ''}`.toUpperCase()
    : '';

  return (
    <AppBar position="static">
      <Toolbar>
        <MenuBookIcon sx={{ mr: 1.5 }} />
        <Typography
          variant="h6"
          component="div"
          sx={{ flexGrow: 0, mr: 4, cursor: 'pointer' }}
          onClick={() => navigate('/')}
        >
          E-Library
        </Typography>

        <Button color="inherit" onClick={() => navigate('/books')} sx={{ mr: 'auto' }}>
          Books
        </Button>

        <IconButton
          color="inherit"
          onClick={() => {
            toggleColorMode();
            setMode(prev => (prev === 'light' ? 'dark' : 'light'));
          }}
          sx={{ mr: 1 }}
          title={mode === 'light' ? 'Switch to dark mode' : 'Switch to light mode'}
        >
          {mode === 'light' ? <DarkModeIcon /> : <LightModeIcon />}
        </IconButton>

        {authed ? (
          <Box>
            <IconButton onClick={handleMenuOpen} size="small" sx={{ ml: 1 }}>
              <Avatar sx={{ width: 34, height: 34, bgcolor: 'secondary.main', fontSize: '0.9rem' }}>
                {initials}
              </Avatar>
            </IconButton>
            <Menu
              anchorEl={anchorEl}
              open={Boolean(anchorEl)}
              onClose={handleMenuClose}
              transformOrigin={{ horizontal: 'right', vertical: 'top' }}
              anchorOrigin={{ horizontal: 'right', vertical: 'bottom' }}
            >
              <MenuItem disabled sx={{ opacity: '1 !important' }}>
                <Typography variant="body2" fontWeight={500}>
                  {user?.firstName} {user?.lastName}
                </Typography>
              </MenuItem>
              <Divider />
              <MenuItem
                onClick={() => {
                  handleMenuClose();
                  navigate('/profile');
                }}
              >
                <ListItemIcon>
                  <PersonIcon fontSize="small" />
                </ListItemIcon>
                Profile
              </MenuItem>
              <MenuItem
                onClick={() => {
                  handleMenuClose();
                  navigate('/loans');
                }}
              >
                <ListItemIcon>
                  <HistoryIcon fontSize="small" />
                </ListItemIcon>
                My Loans
              </MenuItem>
              {user?.roles?.includes('ADMIN') && (
                <MenuItem
                  onClick={() => {
                    handleMenuClose();
                    navigate('/admin');
                  }}
                >
                  <ListItemIcon>
                    <AdminPanelSettingsIcon fontSize="small" />
                  </ListItemIcon>
                  Admin
                </MenuItem>
              )}
              <Divider />
              <MenuItem
                onClick={() => {
                  handleMenuClose();
                  logout();
                }}
              >
                <ListItemIcon>
                  <LogoutIcon fontSize="small" />
                </ListItemIcon>
                Logout
              </MenuItem>
            </Menu>
          </Box>
        ) : (
          <Stack direction="row" spacing={1}>
            <Button color="inherit" onClick={() => navigate('/login')}>
              Login
            </Button>
            <Button
              variant="outlined"
              color="inherit"
              onClick={() => navigate('/register')}
              sx={{ borderColor: 'rgba(255,255,255,0.5)' }}
            >
              Register
            </Button>
          </Stack>
        )}
      </Toolbar>
    </AppBar>
  );
};

export default Header;
