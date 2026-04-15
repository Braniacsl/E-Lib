package com.elib.borrowing.service;

import com.elib.borrowing.client.CatalogClient;
import com.elib.borrowing.dto.BalanceResponse;
import com.elib.borrowing.dto.BorrowRequest;
import com.elib.borrowing.dto.CatalogStockResponse;
import com.elib.borrowing.dto.LoanResponse;
import com.elib.borrowing.entity.Loan;
import com.elib.borrowing.entity.LoanStatus;
import com.elib.borrowing.exception.ResourceNotFoundException;
import com.elib.borrowing.mapper.LoanMapper;
import com.elib.borrowing.repository.LoanRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class BorrowingService {

    private static final int MAX_ACTIVE_LOANS = 5;
    private static final List<LoanStatus> ACTIVE_STATUSES = List.of(LoanStatus.BORROWED, LoanStatus.OVERDUE);

    private final LoanRepository loanRepository;
    private final CatalogClient catalogClient;
    private final LoanMapper loanMapper;
    private final LoanEventPublisher loanEventPublisher;

    @Transactional
    public LoanResponse borrowBook(UUID userId, String userEmail, BorrowRequest request) {
        int activeLoanCount = loanRepository.countByUserIdAndStatusIn(userId, ACTIVE_STATUSES);
        if (activeLoanCount >= MAX_ACTIVE_LOANS) {
            throw new IllegalArgumentException("Maximum active loans (5) reached");
        }

        CatalogStockResponse availability = catalogClient.checkAvailability(request.bookId());
        if (!availability.available()) {
            throw new IllegalArgumentException("No copies available");
        }

        catalogClient.decrementStock(request.bookId());

        LocalDateTime now = LocalDateTime.now();
        Loan loan = Loan.builder()
                .userId(userId)
                .bookId(request.bookId())
                .borrowDate(now)
                .dueDate(now.plusDays(14))
                .status(LoanStatus.BORROWED)
                .fineAmount(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP))
                .build();

        Loan savedLoan = loanRepository.save(loan);
        loanEventPublisher.publishLoanCreated(savedLoan, userEmail);
        return loanMapper.toResponse(savedLoan);
    }

    @Transactional
    public LoanResponse returnBook(UUID loanId, String userEmail) {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new ResourceNotFoundException("Loan not found with id: " + loanId));

        if (loan.getStatus() == LoanStatus.RETURNED) {
            throw new IllegalArgumentException("Loan is already returned");
        }

        LocalDateTime now = LocalDateTime.now();
        loan.setReturnDate(now);
        if (loan.getStatus() == LoanStatus.OVERDUE || now.isAfter(loan.getDueDate())) {
            loan.setFineAmount(calculateFineAmount(loan.getDueDate(), now));
        }
        loan.setStatus(LoanStatus.RETURNED);

        catalogClient.incrementStock(loan.getBookId());

        Loan savedLoan = loanRepository.save(loan);
        loanEventPublisher.publishLoanReturned(savedLoan, userEmail);
        return loanMapper.toResponse(savedLoan);
    }

    @Transactional(readOnly = true)
    public List<LoanResponse> getLoansForUser(UUID userId) {
        return loanMapper.toResponseList(loanRepository.findByUserId(userId));
    }

    @Transactional(readOnly = true)
    public List<LoanResponse> getOverdueLoans() {
        return loanMapper.toResponseList(loanRepository.findByStatus(LoanStatus.OVERDUE));
    }

    @Transactional(readOnly = true)
    public BalanceResponse getUserBalance(UUID userId) {
        BigDecimal overdueFine = loanRepository.findByUserIdAndStatus(userId, LoanStatus.OVERDUE)
                .stream()
                .map(loan -> calculateFineAmount(loan.getDueDate(), LocalDateTime.now()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal returnedLateFine = loanRepository.findByUserIdAndStatus(userId, LoanStatus.RETURNED)
                .stream()
                .map(Loan::getFineAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new BalanceResponse(userId, overdueFine.add(returnedLateFine).setScale(2, RoundingMode.HALF_UP));
    }

    @Transactional
    @Scheduled(cron = "0 0/30 * * * *")
    public void checkAndMarkOverdue() {
        LocalDateTime now = LocalDateTime.now();
        List<Loan> overdueCandidates = loanRepository.findByStatusAndDueDateBefore(LoanStatus.BORROWED, now);

        for (Loan loan : overdueCandidates) {
            loan.setStatus(LoanStatus.OVERDUE);
            loan.setFineAmount(calculateFineAmount(loan.getDueDate(), now));
            Loan saved = loanRepository.save(loan);
            loanEventPublisher.publishLoanOverdue(saved, null);
        }

        if (!overdueCandidates.isEmpty()) {
            log.info("Marked {} loans as overdue", overdueCandidates.size());
        }
    }

    private BigDecimal calculateFineAmount(LocalDateTime dueDate, LocalDateTime effectiveDate) {
        if (!effectiveDate.isAfter(dueDate)) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }

        LocalDate due = dueDate.toLocalDate();
        LocalDate effective = effectiveDate.toLocalDate();
        long daysBetween = ChronoUnit.DAYS.between(due, effective);
        long additionalPerDay = Math.max(daysBetween, 1L);

        return BigDecimal.valueOf(1 + additionalPerDay).setScale(2, RoundingMode.HALF_UP);
    }
}
