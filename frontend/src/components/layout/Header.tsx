import React from 'react';
import AppBar from '@mui/material/AppBar';
import Toolbar from '@mui/material/Toolbar';
import Typography from '@mui/material/Typography';
import Box from '@mui/material/Box';

const Header: React.FC = () => {
  return (
    <AppBar position="static">
      <Toolbar>
        <Typography variant="h6" component="div" sx={{ flexGrow: 1 }}>
          E-Library System
        </Typography>
        <Box sx={{ display: 'flex', gap: 2 }}>{/* Navigation links will be added here */}</Box>
      </Toolbar>
    </AppBar>
  );
};

export default Header;
