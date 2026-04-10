package com.elib.core.service;

import com.elib.core.dto.BookRequest;
import com.elib.core.dto.BookResponse;
import com.elib.core.entity.Book;
import com.elib.core.exception.ResourceNotFoundException;
import com.elib.core.mapper.BookMapper;
import com.elib.core.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookService {

    private final BookRepository bookRepository;
    private final BookMapper bookMapper;

    @Transactional
    public BookResponse createBook(BookRequest request) {
        log.info("Creating new book ISBN: {}", request.isbn());

        if (bookRepository.findByIsbn(request.isbn()).isPresent()) {
            throw new IllegalArgumentException("Duplicate ISBN: " + request.isbn());
        }

        validateCopies(request.totalCopies(), request.totalCopies());

        Book book = bookMapper.toEntity(request);
        book.setAvailableCopies(request.totalCopies());
        book.setIsActive(true);

        Book saved = bookRepository.save(book);
        return bookMapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    public BookResponse getBookById(Long id) {
        return bookRepository.findByIdAndIsActiveTrue(id)
                .map(bookMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Active book not found: " + id));
    }

    @Transactional(readOnly = true)
    public Page<BookResponse> getAllBooks(Pageable pageable) {
        return bookRepository.findByIsActiveTrue(pageable)
                .map(bookMapper::toResponse);
    }

    @Transactional
    public BookResponse updateBook(Long id, BookRequest request) {
        log.info("Updating book with ID: {}", id);

        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with ID: " + id));

        if (!book.getIsbn().equals(request.isbn())) {
            bookRepository.findByIsbn(request.isbn())
                    .ifPresent(existingBook -> {
                        throw new IllegalArgumentException("Book with ISBN " + request.isbn() + " already exists");
                    });
        }

        bookMapper.updateEntityFromRequest(request, book);

        if (book.getAvailableCopies() > book.getTotalCopies()) {
            book.setAvailableCopies(book.getTotalCopies());
        }

        validateCopies(book.getTotalCopies(), book.getAvailableCopies());

        Book updatedBook = bookRepository.save(book);

        log.info("Book updated successfully with ID: {}", id);
        return bookMapper.toResponse(updatedBook);
    }

    @Transactional
    public void deleteBook(Long id) {
        log.info("Soft deleting book with ID: {}", id);

        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with ID: " + id));

        book.setIsActive(false);
        bookRepository.save(book);

        log.info("Book soft-deleted with ID: {}", id);
    }

    @Transactional(readOnly = true)
    public Page<BookResponse> searchBooks(String query, Pageable pageable) {
        log.debug("Searching books with query: {}", query);
        return bookRepository.searchBooks(query, pageable)
                .map(bookMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public List<String> getAllCategories() {
        log.debug("Fetching all distinct book categories");
        return bookRepository.findAllDistinctCategories();
    }

    @Transactional
    public BookResponse borrowBook(Long bookId) {
        log.info("Borrowing book with ID: {}", bookId);

        Book book = bookRepository.findByIdAndIsActiveTrue(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Active book not found with ID: " + bookId));

        if (book.getAvailableCopies() <= 0) {
            throw new IllegalStateException("No copies available for book ID: " + bookId);
        }

        book.setAvailableCopies(book.getAvailableCopies() - 1);
        Book updatedBook = bookRepository.save(book);

        log.info("Book borrowed successfully with ID: {}, available copies now: {}",
                bookId, updatedBook.getAvailableCopies());

        return bookMapper.toResponse(updatedBook);
    }

    @Transactional
    public BookResponse returnBook(Long bookId) {
        log.info("Returning book with ID: {}", bookId);

        Book book = bookRepository.findByIdAndIsActiveTrue(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Active book not found with ID: " + bookId));

        if (book.getAvailableCopies() >= book.getTotalCopies()) {
            throw new IllegalStateException("All copies are already available for book ID: " + bookId);
        }

        book.setAvailableCopies(book.getAvailableCopies() + 1);
        Book updatedBook = bookRepository.save(book);

        log.info("Book returned successfully with ID: {}, available copies now: {}",
                bookId, updatedBook.getAvailableCopies());

        return bookMapper.toResponse(updatedBook);
    }

    private void validateCopies(Integer totalCopies, Integer availableCopies) {
        if (totalCopies == null || totalCopies < 0) {
            throw new IllegalArgumentException("Total copies must be zero or greater");
        }

        if (availableCopies == null || availableCopies < 0) {
            throw new IllegalArgumentException("Available copies must be zero or greater");
        }

        if (availableCopies > totalCopies) {
            throw new IllegalArgumentException("Available copies cannot exceed total copies");
        }
    }
}