import React from 'react';
import Box from '@mui/material/Box';
import Container from '@mui/material/Container';
import Stack from '@mui/material/Stack';
import Typography from '@mui/material/Typography';
import Header from './Header';
import ulLogo from '../../assets/ul-logo.svg';

interface LayoutProps {
  children: React.ReactNode;
}

const Layout: React.FC<LayoutProps> = ({ children }) => {
  return (
    <Box sx={{ display: 'flex', flexDirection: 'column', minHeight: '100vh' }}>
      <Header />
      <Box component="main" sx={{ flex: 1, py: 4 }}>
        {children}
      </Box>
      <Box
        component="footer"
        sx={{
          py: 3,
          px: 2,
          mt: 'auto',
          backgroundColor: 'background.paper',
          borderTop: 1,
          borderColor: 'divider',
        }}
      >
        <Container maxWidth="lg">
          <Stack direction="row" spacing={2} alignItems="center" justifyContent="center">
            <Box component="img" src={ulLogo} alt="UL" sx={{ height: 24, opacity: 0.6 }} />
            <Typography variant="body2" color="text.secondary">
              © {new Date().getFullYear()} E-Library System
            </Typography>
          </Stack>
        </Container>
      </Box>
    </Box>
  );
};

export default Layout;
