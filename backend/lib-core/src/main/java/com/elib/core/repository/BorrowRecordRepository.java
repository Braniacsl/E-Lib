package com.elib.core.repository;

import com.elib.core.entity.BorrowRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BorrowRecordRepository extends JpaRepository<BorrowRecord, Long> {

    List<BorrowRecord> findByUserId(Long userId);

    List<BorrowRecord> findByBookId(Long bookId);

    Optional<BorrowRecord> findByUserIdAndBookIdAndStatus(Long userId, Long bookId, String status);

    List<BorrowRecord> findByUserIdAndStatus(Long userId, String status);

    List<BorrowRecord> findByStatus(String status);
}
