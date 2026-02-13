import React from 'react';
import Alert from '@mui/material/Alert';
import AlertTitle from '@mui/material/AlertTitle';
import Box from '@mui/material/Box';

interface ErrorAlertProps {
  error: string;
  title?: string;
  severity?: 'error' | 'warning' | 'info';
  onClose?: () => void;
}

const ErrorAlert: React.FC<ErrorAlertProps> = ({
  error,
  title = 'Error',
  severity = 'error',
  onClose,
}) => {
  return (
    <Box sx={{ width: '100%', mb: 2 }}>
      <Alert severity={severity} onClose={onClose}>
        <AlertTitle>{title}</AlertTitle>
        {error}
      </Alert>
    </Box>
  );
};

export default ErrorAlert;
