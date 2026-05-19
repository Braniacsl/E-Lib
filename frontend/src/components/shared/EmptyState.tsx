import React from 'react';
import Paper from '@mui/material/Paper';
import Typography from '@mui/material/Typography';
import type { SvgIconProps } from '@mui/material/SvgIcon';

interface EmptyStateProps {
  icon: React.ElementType<SvgIconProps>;
  title: string;
  description?: string;
  action?: React.ReactNode;
}

const EmptyState: React.FC<EmptyStateProps> = ({ icon: Icon, title, description, action }) => (
  <Paper elevation={2} sx={{ p: 6, textAlign: 'center', borderRadius: 2 }}>
    <Icon sx={{ fontSize: 64, color: 'text.disabled', mb: 2 }} />
    <Typography variant="h6" color="text.secondary" gutterBottom>
      {title}
    </Typography>
    {description && (
      <Typography color="text.secondary" sx={{ mb: action ? 2 : 0 }}>
        {description}
      </Typography>
    )}
    {action}
  </Paper>
);

export default EmptyState;
