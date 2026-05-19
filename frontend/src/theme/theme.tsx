import { createTheme } from '@mui/material/styles';

export const getTheme = (mode: 'light' | 'dark') =>
  createTheme({
    palette: {
      mode,
      ...(mode === 'light'
        ? {
            primary: { main: '#1976d2', light: '#42a5f5', dark: '#1565c0' },
            secondary: { main: '#dc004e', light: '#ff4081', dark: '#9a0036' },
            background: { default: '#f5f5f5', paper: '#ffffff' },
            text: { primary: '#212121', secondary: '#757575' },
          }
        : {
            primary: { main: '#90caf9', light: '#e3f2fd', dark: '#42a5f5' },
            secondary: { main: '#f48fb1', light: '#fce4ec', dark: '#f06292' },
            background: { default: '#121212', paper: '#1e1e1e' },
            text: { primary: '#e0e0e0', secondary: '#a0a0a0' },
          }),
    },
    typography: {
      fontFamily: '"Roboto", "Helvetica", "Arial", sans-serif',
      h1: { fontSize: '2.5rem', fontWeight: 500 },
      h2: { fontSize: '2rem', fontWeight: 500 },
      h3: { fontSize: '1.75rem', fontWeight: 500 },
      h4: { fontSize: '1.5rem', fontWeight: 500 },
      h5: { fontSize: '1.25rem', fontWeight: 500 },
      h6: { fontSize: '1rem', fontWeight: 500 },
    },
    shape: { borderRadius: 8 },
    components: {
      MuiButton: { styleOverrides: { root: { textTransform: 'none' } } },
      MuiAppBar: {
        styleOverrides: {
          root: {
            boxShadow: '0 2px 4px rgba(0,0,0,0.1)',
          },
        },
      },
    },
  });
