import React, { createContext, useContext, useState, useMemo } from 'react';
import { ThemeProvider } from '@mui/material/styles';
import CssBaseline from '@mui/material/CssBaseline';
import { Provider as ReduxProvider } from 'react-redux';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { BrowserRouter } from 'react-router-dom';
import { getTheme } from '../theme/theme.tsx';
import { store } from '../store/store.tsx';

interface AppProvidersProps {
  children: React.ReactNode;
}

const queryClient = new QueryClient({
  defaultOptions: {
    queries: {
      staleTime: 5 * 60 * 1000,
      retry: 1,
      refetchOnWindowFocus: false,
    },
  },
});

const ColorModeContext = createContext({ toggleColorMode: () => {} });

export const useColorMode = () => useContext(ColorModeContext);

const AppProviders: React.FC<AppProvidersProps> = ({ children }) => {
  const [mode, setMode] = useState<'light' | 'dark'>(
    () => (localStorage.getItem('colorMode') as 'light' | 'dark') || 'light'
  );

  const colorMode = useMemo(
    () => ({
      toggleColorMode: () => {
        setMode(prev => {
          const next = prev === 'light' ? 'dark' : 'light';
          localStorage.setItem('colorMode', next);
          return next;
        });
      },
    }),
    []
  );

  const theme = useMemo(() => getTheme(mode), [mode]);

  return (
    <ReduxProvider store={store}>
      <QueryClientProvider client={queryClient}>
        <ColorModeContext.Provider value={colorMode}>
          <ThemeProvider theme={theme}>
            <CssBaseline />
            <BrowserRouter>{children}</BrowserRouter>
          </ThemeProvider>
        </ColorModeContext.Provider>
      </QueryClientProvider>
    </ReduxProvider>
  );
};

export default AppProviders;
