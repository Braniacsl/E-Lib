package com.elib.core.service;

import com.elib.core.dto.BookResponse;
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
public class CatalogSearchService {

    private final BookRepository bookRepository;
    private final BookMapper bookMapper;

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
}