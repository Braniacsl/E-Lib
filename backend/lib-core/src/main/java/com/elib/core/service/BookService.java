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

    /**
     * Persists a new book.
     * @throws IllegalArgumentException if ISBN exists.
     */
    @Transactional
    public BookResponse createBook(BookRequest request) {
        log.info("Creating new book ISBN: {}", request.isbn()); // Accessor is .isbn() for records

        if (bookRepository.findByIsbn(request.isbn()).isPresent()) {
             throw new IllegalArgumentException("Duplicate ISBN: " + request.isbn());
        }

        Book book = bookMapper.toEntity(request);
        book.setAvailableCopies(request.totalCopies());
        Book saved = bookRepository.save(book);
        return bookMapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    public BookResponse getBookById(Long id) {
        return bookRepository.findById(id)
            .map(bookMapper::toResponse)
            .orElseThrow(() -> new ResourceNotFoundException("Book not found: " + id));
    }

    @Transactional(readOnly = true)
    public Page<BookResponse> getAllBooks(Pageable pageable) {
        return bookRepository.findAll(pageable)
            .map(bookMapper::toResponse);
    }

    @Transactional
    public BookResponse updateBook(Long id, BookRequest request) {
        log.info("Updating book with ID: {}", id);

        Book book = bookRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Book not found with ID: " + id));

        // Check if ISBN is being changed and if new ISBN already exists
        if (!book.getIsbn().equals(request.isbn())) {
            bookRepository.findByIsbn(request.isbn())
                .ifPresent(existingBook -> {
                    throw new IllegalArgumentException("Book with ISBN " + request.isbn() + " already exists");
                });
        }

        bookMapper.updateEntityFromRequest(request, book);
        Book updatedBook = bookRepository.save(book);

        log.info("Book updated successfully with ID: {}", id);
        return bookMapper.toResponse(updatedBook);
    }

    @Transactional
    public void deleteBook(Long id) {
        log.info("Deleting book with ID: {}", id);

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

        return null;
    }

    @Transactional
    public BookResponse returnBook(Long bookId) {

        return null;
    }
}