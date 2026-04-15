package com.elib.borrowing.repository;

import com.elib.borrowing.entity.Loan;
import com.elib.borrowing.entity.LoanStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface LoanRepository extends JpaRepository<Loan, UUID> {

    List<Loan> findByUserId(UUID userId);

    List<Loan> findByStatus(LoanStatus status);

    List<Loan> findByStatusAndDueDateBefore(LoanStatus status, LocalDateTime date);

    int countByUserIdAndStatusIn(UUID userId, List<LoanStatus> statuses);

    List<Loan> findByUserIdAndStatus(UUID userId, LoanStatus status);
}
