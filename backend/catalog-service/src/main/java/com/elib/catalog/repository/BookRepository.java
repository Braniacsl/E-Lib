package com.elib.catalog.repository;

import com.elib.catalog.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    Optional<Book> findByIsbn(String isbn);

    Optional<Book> findByIdAndIsActiveTrue(Long id);

    Page<Book> findByIsActiveTrue(Pageable pageable);

    Page<Book> findByTitleContainingIgnoreCaseAndIsActiveTrue(String title, Pageable pageable);

    Page<Book> findByAuthorContainingIgnoreCaseAndIsActiveTrue(String author, Pageable pageable);

    Page<Book> findByCategoryIgnoreCaseAndIsActiveTrue(String category, Pageable pageable);

    List<Book> findByAvailableCopiesGreaterThanAndIsActiveTrue(Integer copies);

    @Query("""
           SELECT b FROM Book b
           WHERE b.isActive = true AND (
               LOWER(b.title) LIKE LOWER(CONCAT('%', :query, '%')) OR
               LOWER(b.author) LIKE LOWER(CONCAT('%', :query, '%')) OR
               LOWER(b.isbn) LIKE LOWER(CONCAT('%', :query, '%'))
           )
           """)
    Page<Book> searchBooks(@Param("query") String query, Pageable pageable);

    @Query("SELECT DISTINCT b.category FROM Book b WHERE b.isActive = true ORDER BY b.category ASC")
    List<String> findAllDistinctCategories();
}