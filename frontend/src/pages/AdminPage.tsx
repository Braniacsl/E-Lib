import React, { useState } from 'react';
import Box from '@mui/material/Box';
import Button from '@mui/material/Button';
import Chip from '@mui/material/Chip';
import Container from '@mui/material/Container';
import Dialog from '@mui/material/Dialog';
import DialogTitle from '@mui/material/DialogTitle';
import DialogContent from '@mui/material/DialogContent';
import DialogActions from '@mui/material/DialogActions';
import IconButton from '@mui/material/IconButton';
import Paper from '@mui/material/Paper';
import Stack from '@mui/material/Stack';
import Tab from '@mui/material/Tab';
import Tabs from '@mui/material/Tabs';
import Table from '@mui/material/Table';
import TableBody from '@mui/material/TableBody';
import TableCell from '@mui/material/TableCell';
import TableContainer from '@mui/material/TableContainer';
import TableHead from '@mui/material/TableHead';
import TableRow from '@mui/material/TableRow';
import TextField from '@mui/material/TextField';
import Typography from '@mui/material/Typography';
import AddIcon from '@mui/icons-material/Add';
import EditIcon from '@mui/icons-material/Edit';
import DeleteIcon from '@mui/icons-material/Delete';
import AdminPanelSettingsIcon from '@mui/icons-material/AdminPanelSettings';
import PersonRemoveIcon from '@mui/icons-material/PersonRemove';
import LoadingSpinner from '../components/shared/LoadingSpinner';
import { useAdminUsers, useAdminLoans, useAdminBooks } from '../hooks/useAdmin';
import {
  useCreateBook,
  useUpdateBook,
  useDeleteBook,
  useAddUserRole,
  useRemoveUserRole,
  useBanUser,
  useUnbanUser,
} from '../hooks/useAdminMutations';
import { useAuth } from '../hooks/useAuth';
import type { Book } from '../types/types';

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

const emptyBookForm = {
  title: '',
  author: '',
  isbn: '',
  description: '',
  publicationYear: new Date().getFullYear(),
  publisher: '',
  category: '',
  language: 'English',
  pageCount: 100,
  coverImageUrl: 'https://',
  totalCopies: 1,
};

const AdminPage: React.FC = () => {
  const { getCurrentUser } = useAuth();
  const user = getCurrentUser();
  const [tab, setTab] = useState(0);

  const { data: users, isLoading: usersLoading } = useAdminUsers();
  const { data: loans, isLoading: loansLoading } = useAdminLoans();
  const { data: books, isLoading: booksLoading } = useAdminBooks();

  const createBook = useCreateBook();
  const updateBook = useUpdateBook();
  const deleteBook = useDeleteBook();
  const addRole = useAddUserRole();
  const removeRole = useRemoveUserRole();
  const banUser = useBanUser();
  const unbanUser = useUnbanUser();

  const [bookDialog, setBookDialog] = useState(false);
  const [editingBook, setEditingBook] = useState<Book | null>(null);
  const [bookForm, setBookForm] = useState(emptyBookForm);

  const openAddBook = () => {
    setEditingBook(null);
    setBookForm(emptyBookForm);
    setBookDialog(true);
  };

  const openEditBook = (book: Book) => {
    setEditingBook(book);
    setBookForm({
      title: book.title,
      author: book.author,
      isbn: book.isbn,
      description: book.description || '',
      publicationYear: book.publicationYear,
      publisher: book.publisher,
      category: book.category || '',
      language: book.language || 'English',
      pageCount: book.pageCount || 100,
      coverImageUrl: book.coverImageUrl || 'https://',
      totalCopies: book.totalCopies,
    });
    setBookDialog(true);
  };

  const handleBookSubmit = () => {
    if (editingBook) {
      updateBook.mutate({ id: editingBook.id, data: bookForm });
    } else {
      createBook.mutate(bookForm);
    }
    setBookDialog(false);
  };

  const updateField = (field: string, value: string | number) =>
    setBookForm(prev => ({ ...prev, [field]: value }));

  if (!user || !user.roles?.includes('ADMIN')) {
    return (
      <Box sx={{ textAlign: 'center', py: 8 }}>
        <Typography variant="h5">Access denied</Typography>
        <Typography color="text.secondary">Admin privileges required</Typography>
      </Box>
    );
  }

  return (
    <Container maxWidth="lg">
      <Typography variant="h4" component="h1" gutterBottom>
        Admin Dashboard
      </Typography>

      <Tabs value={tab} onChange={(_, v) => setTab(v)} sx={{ mb: 3 }}>
        <Tab label={`Users${users ? ` (${users.totalElements})` : ''}`} />
        <Tab label={`Books${books ? ` (${books.totalElements})` : ''}`} />
        <Tab label={`Overdue Loans${loans ? ` (${loans.totalElements})` : ''}`} />
      </Tabs>

      {/* USERS TAB */}
      {tab === 0 && (
        <>
          {usersLoading && <LoadingSpinner />}
          {users && (
            <TableContainer component={Paper} elevation={2}>
              <Table size="small">
                <TableHead>
                  <TableRow>
                    <TableCell>Name</TableCell>
                    <TableCell>Email</TableCell>
                    <TableCell>Roles</TableCell>
                    <TableCell>Status</TableCell>
                    <TableCell>Joined</TableCell>
                    <TableCell align="right">Actions</TableCell>
                  </TableRow>
                </TableHead>
                <TableBody>
                  {users.content.map(u => (
                    <TableRow key={u.id} hover sx={{ opacity: u.isActive === false ? 0.5 : 1 }}>
                      <TableCell>
                        {u.firstName} {u.lastName}
                      </TableCell>
                      <TableCell>{u.email}</TableCell>
                      <TableCell>
                        <Stack direction="row" spacing={0.5} alignItems="center">
                          {u.roles?.map(r => (
                            <Chip
                              key={r}
                              label={r}
                              size="small"
                              color={r === 'ADMIN' ? 'secondary' : 'default'}
                            />
                          ))}
                          {u.roles?.includes('ADMIN') ? (
                            <IconButton
                              size="small"
                              title="Remove admin"
                              onClick={() => removeRole.mutate({ userId: u.id, role: 'ADMIN' })}
                            >
                              <PersonRemoveIcon fontSize="small" />
                            </IconButton>
                          ) : (
                            <IconButton
                              size="small"
                              title="Make admin"
                              onClick={() => addRole.mutate({ userId: u.id, role: 'ADMIN' })}
                            >
                              <AdminPanelSettingsIcon fontSize="small" />
                            </IconButton>
                          )}
                        </Stack>
                      </TableCell>
                      <TableCell>
                        <Chip
                          label={u.isActive === false ? 'Banned' : 'Active'}
                          size="small"
                          color={u.isActive === false ? 'error' : 'success'}
                        />
                      </TableCell>
                      <TableCell>{formatDate(u.createdAt)}</TableCell>
                      <TableCell align="right">
                        {u.isActive === false ? (
                          <Button size="small" onClick={() => unbanUser.mutate(u.id)}>
                            Unban
                          </Button>
                        ) : (
                          <Button
                            size="small"
                            color="error"
                            onClick={() => {
                              if (confirm(`Ban ${u.firstName} ${u.lastName}?`))
                                banUser.mutate(u.id);
                            }}
                          >
                            Ban
                          </Button>
                        )}
                      </TableCell>
                    </TableRow>
                  ))}
                </TableBody>
              </Table>
            </TableContainer>
          )}
        </>
      )}

      {/* BOOKS TAB */}
      {tab === 1 && (
        <>
          <Stack direction="row" justifyContent="flex-end" sx={{ mb: 2 }}>
            <Button variant="contained" startIcon={<AddIcon />} onClick={openAddBook}>
              Add Book
            </Button>
          </Stack>
          {booksLoading && <LoadingSpinner />}
          {books && (
            <TableContainer component={Paper} elevation={2}>
              <Table size="small">
                <TableHead>
                  <TableRow>
                    <TableCell>Title</TableCell>
                    <TableCell>Author</TableCell>
                    <TableCell>ISBN</TableCell>
                    <TableCell>Available</TableCell>
                    <TableCell>Total</TableCell>
                    <TableCell align="right">Actions</TableCell>
                  </TableRow>
                </TableHead>
                <TableBody>
                  {books.content.map(b => (
                    <TableRow key={b.id} hover>
                      <TableCell>{b.title}</TableCell>
                      <TableCell>{b.author}</TableCell>
                      <TableCell sx={{ fontFamily: 'monospace', fontSize: '0.85rem' }}>
                        {b.isbn}
                      </TableCell>
                      <TableCell>
                        <Chip
                          label={b.availableCopies}
                          size="small"
                          color={b.availableCopies > 0 ? 'success' : 'error'}
                        />
                      </TableCell>
                      <TableCell>{b.totalCopies}</TableCell>
                      <TableCell align="right">
                        <IconButton size="small" onClick={() => openEditBook(b)} title="Edit">
                          <EditIcon fontSize="small" />
                        </IconButton>
                        <IconButton
                          size="small"
                          onClick={() => {
                            if (confirm(`Delete "${b.title}"?`)) deleteBook.mutate(b.id);
                          }}
                          title="Delete"
                        >
                          <DeleteIcon fontSize="small" />
                        </IconButton>
                      </TableCell>
                    </TableRow>
                  ))}
                </TableBody>
              </Table>
            </TableContainer>
          )}
        </>
      )}

      {/* LOANS TAB */}
      {tab === 2 && (
        <>
          {loansLoading && <LoadingSpinner />}
          {loans && (
            <TableContainer component={Paper} elevation={2}>
              <Table size="small">
                <TableHead>
                  <TableRow>
                    <TableCell>Loan ID</TableCell>
                    <TableCell>User</TableCell>
                    <TableCell>Book</TableCell>
                    <TableCell>Borrowed</TableCell>
                    <TableCell>Due</TableCell>
                    <TableCell>Status</TableCell>
                  </TableRow>
                </TableHead>
                <TableBody>
                  {loans.content.map(l => (
                    <TableRow key={l.id} hover>
                      <TableCell sx={{ fontFamily: 'monospace', fontSize: '0.85rem' }}>
                        {l.id.slice(0, 8)}...
                      </TableCell>
                      <TableCell sx={{ fontFamily: 'monospace', fontSize: '0.85rem' }}>
                        {l.userId.slice(0, 8)}...
                      </TableCell>
                      <TableCell sx={{ fontFamily: 'monospace', fontSize: '0.85rem' }}>
                        {l.bookId.slice(0, 8)}...
                      </TableCell>
                      <TableCell>{formatDate(l.borrowDate)}</TableCell>
                      <TableCell>{formatDate(l.dueDate)}</TableCell>
                      <TableCell>
                        <Chip
                          label={l.status}
                          size="small"
                          color={statusColor[l.status] || 'default'}
                        />
                      </TableCell>
                    </TableRow>
                  ))}
                </TableBody>
              </Table>
            </TableContainer>
          )}
        </>
      )}

      {/* BOOK DIALOG */}
      <Dialog open={bookDialog} onClose={() => setBookDialog(false)} maxWidth="sm" fullWidth>
        <DialogTitle>{editingBook ? 'Edit Book' : 'Add Book'}</DialogTitle>
        <DialogContent>
          <Stack spacing={2} sx={{ mt: 1 }}>
            <TextField
              label="Title"
              value={bookForm.title}
              onChange={e => updateField('title', e.target.value)}
              required
            />
            <TextField
              label="Author"
              value={bookForm.author}
              onChange={e => updateField('author', e.target.value)}
              required
            />
            <TextField
              label="ISBN"
              value={bookForm.isbn}
              onChange={e => updateField('isbn', e.target.value)}
              required
            />
            <TextField
              label="Description"
              value={bookForm.description}
              multiline
              rows={2}
              onChange={e => updateField('description', e.target.value)}
            />
            <Stack direction="row" spacing={2}>
              <TextField
                label="Year"
                type="number"
                value={bookForm.publicationYear}
                onChange={e => updateField('publicationYear', Number(e.target.value))}
                sx={{ flex: 1 }}
              />
              <TextField
                label="Publisher"
                value={bookForm.publisher}
                onChange={e => updateField('publisher', e.target.value)}
                sx={{ flex: 2 }}
              />
            </Stack>
            <Stack direction="row" spacing={2}>
              <TextField
                label="Category"
                value={bookForm.category}
                onChange={e => updateField('category', e.target.value)}
                sx={{ flex: 1 }}
              />
              <TextField
                label="Language"
                value={bookForm.language}
                onChange={e => updateField('language', e.target.value)}
                sx={{ flex: 1 }}
              />
            </Stack>
            <Stack direction="row" spacing={2}>
              <TextField
                label="Pages"
                type="number"
                value={bookForm.pageCount}
                onChange={e => updateField('pageCount', Number(e.target.value))}
                sx={{ flex: 1 }}
              />
              <TextField
                label="Total Copies"
                type="number"
                value={bookForm.totalCopies}
                onChange={e => updateField('totalCopies', Number(e.target.value))}
                sx={{ flex: 1 }}
              />
            </Stack>
            <TextField
              label="Cover Image URL"
              value={bookForm.coverImageUrl}
              onChange={e => updateField('coverImageUrl', e.target.value)}
            />
          </Stack>
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setBookDialog(false)}>Cancel</Button>
          <Button
            variant="contained"
            onClick={handleBookSubmit}
            disabled={createBook.isPending || updateBook.isPending}
          >
            {editingBook ? 'Update' : 'Create'}
          </Button>
        </DialogActions>
      </Dialog>
    </Container>
  );
};

export default AdminPage;
