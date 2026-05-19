import React from 'react';
import { useNavigate } from 'react-router-dom';
import Button from '@mui/material/Button';
import Card from '@mui/material/Card';
import Chip from '@mui/material/Chip';
import Container from '@mui/material/Container';
import Grid from '@mui/material/Grid';
import Paper from '@mui/material/Paper';
import Table from '@mui/material/Table';
import TableBody from '@mui/material/TableBody';
import TableCell from '@mui/material/TableCell';
import TableContainer from '@mui/material/TableContainer';
import TableHead from '@mui/material/TableHead';
import TableRow from '@mui/material/TableRow';
import Typography from '@mui/material/Typography';
import AssignmentReturnIcon from '@mui/icons-material/AssignmentReturn';
import WarningIcon from '@mui/icons-material/Warning';
import MenuBookIcon from '@mui/icons-material/MenuBook';
import LoadingSpinner from '../components/shared/LoadingSpinner';
import ErrorAlert from '../components/shared/ErrorAlert';
import EmptyState from '../components/shared/EmptyState';
import HistoryIcon from '@mui/icons-material/History';
import { useLoans, useReturnBook } from '../hooks/useLoans';
import { useBooks } from '../hooks/useBooks';
import { useAuth } from '../hooks/useAuth';

const statusColor: Record<string, 'success' | 'error' | 'default'> = {
  ACTIVE: 'success',
  OVERDUE: 'error',
  RETURNED: 'default',
};

const formatDate = (dateStr: string) =>
  new Date(dateStr).toLocaleDateString('en-GB', {
    day: 'numeric',
    month: 'short',
    year: 'numeric',
  });

const LoansPage: React.FC = () => {
  const navigate = useNavigate();
  const { getCurrentUser } = useAuth();
  const user = getCurrentUser();
  const { data: loans, isLoading, error } = useLoans(user?.id || '');
  const { data: booksData } = useBooks(0, 100);
  const returnMutation = useReturnBook();

  const bookTitleMap = new Map(booksData?.content.map(b => [b.id, b.title]) || []);
  const activeCount = loans?.filter(l => l.status === 'ACTIVE').length || 0;
  const overdueCount = loans?.filter(l => l.status === 'OVERDUE').length || 0;

  if (!user) {
    return (
      <Container maxWidth="sm" sx={{ textAlign: 'center', py: 8 }}>
        <Typography variant="h5">Please login to view your loans</Typography>
      </Container>
    );
  }

  if (isLoading)
    return (
      <Container maxWidth="md">
        <LoadingSpinner />
      </Container>
    );

  return (
    <Container maxWidth="md">
      <Typography variant="h4" component="h1" gutterBottom>
        My Loans
      </Typography>

      {error && <ErrorAlert error="Failed to load loans" />}

      {loans && loans.length === 0 ? (
        <EmptyState
          icon={HistoryIcon}
          title="No loans yet"
          description="Borrow a book from the catalog to get started"
        />
      ) : loans ? (
        <>
          <Grid container spacing={2} sx={{ mb: 3 }}>
            <Grid size={{ xs: 6, md: 4 }}>
              <Card elevation={2} sx={{ textAlign: 'center', py: 2, borderRadius: 2 }}>
                <MenuBookIcon color="primary" sx={{ fontSize: 28, mb: 0.5 }} />
                <Typography variant="h5" fontWeight={600}>
                  {loans.length}
                </Typography>
                <Typography variant="body2" color="text.secondary">
                  Total Loans
                </Typography>
              </Card>
            </Grid>
            <Grid size={{ xs: 6, md: 4 }}>
              <Card elevation={2} sx={{ textAlign: 'center', py: 2, borderRadius: 2 }}>
                <AssignmentReturnIcon
                  color={activeCount > 0 ? 'primary' : 'disabled'}
                  sx={{ fontSize: 28, mb: 0.5 }}
                />
                <Typography variant="h5" fontWeight={600}>
                  {activeCount}
                </Typography>
                <Typography variant="body2" color="text.secondary">
                  Active
                </Typography>
              </Card>
            </Grid>
            <Grid size={{ xs: 6, md: 4 }}>
              <Card elevation={2} sx={{ textAlign: 'center', py: 2, borderRadius: 2 }}>
                <WarningIcon
                  color={overdueCount > 0 ? 'error' : 'disabled'}
                  sx={{ fontSize: 28, mb: 0.5 }}
                />
                <Typography
                  variant="h5"
                  fontWeight={600}
                  color={overdueCount > 0 ? 'error' : 'text.primary'}
                >
                  {overdueCount}
                </Typography>
                <Typography variant="body2" color="text.secondary">
                  Overdue
                </Typography>
              </Card>
            </Grid>
          </Grid>

          <TableContainer component={Paper} elevation={2} sx={{ borderRadius: 2 }}>
            <Table>
              <TableHead>
                <TableRow>
                  <TableCell>Book</TableCell>
                  <TableCell>Borrowed</TableCell>
                  <TableCell>Due Date</TableCell>
                  <TableCell>Returned</TableCell>
                  <TableCell>Status</TableCell>
                  <TableCell align="right">Action</TableCell>
                </TableRow>
              </TableHead>
              <TableBody>
                {loans.map(loan => (
                  <TableRow key={loan.id} hover>
                    <TableCell>
                      <Button
                        size="small"
                        sx={{ textTransform: 'none', minWidth: 0, fontWeight: 500 }}
                        onClick={() => navigate(`/books/${loan.bookId}`)}
                      >
                        {bookTitleMap.get(loan.bookId) || loan.bookId.slice(0, 8) + '...'}
                      </Button>
                    </TableCell>
                    <TableCell>{formatDate(loan.borrowDate)}</TableCell>
                    <TableCell>{formatDate(loan.dueDate)}</TableCell>
                    <TableCell>{loan.returnDate ? formatDate(loan.returnDate) : '—'}</TableCell>
                    <TableCell>
                      <Chip
                        label={loan.status}
                        size="small"
                        color={statusColor[loan.status] || 'default'}
                      />
                    </TableCell>
                    <TableCell align="right">
                      {loan.status === 'ACTIVE' && (
                        <Button
                          size="small"
                          variant="outlined"
                          disabled={returnMutation.isPending}
                          onClick={() =>
                            returnMutation.mutate({ loanId: loan.id, userEmail: user.email })
                          }
                        >
                          Return
                        </Button>
                      )}
                    </TableCell>
                  </TableRow>
                ))}
              </TableBody>
            </Table>
          </TableContainer>
        </>
      ) : null}
    </Container>
  );
};

export default LoansPage;
