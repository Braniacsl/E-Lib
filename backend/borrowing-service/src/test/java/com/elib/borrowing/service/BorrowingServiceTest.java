package com.elib.borrowing.service;

import com.elib.borrowing.client.CatalogClient;
import com.elib.borrowing.dto.BorrowRequest;
import com.elib.borrowing.dto.CatalogStockResponse;
import com.elib.borrowing.dto.LoanResponse;
import com.elib.borrowing.entity.Loan;
import com.elib.borrowing.entity.LoanStatus;
import com.elib.borrowing.exception.ResourceNotFoundException;
import com.elib.borrowing.mapper.LoanMapper;
import com.elib.borrowing.repository.LoanRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BorrowingServiceTest {

    @Mock
    private LoanRepository loanRepository;
    @Mock
    private CatalogClient catalogClient;
    @Mock
    private LoanMapper loanMapper;
    @Mock
    private LoanEventPublisher loanEventPublisher;

    @InjectMocks
    private BorrowingService borrowingService;

    @Test
    void borrowBook_HappyPath_ShouldCreateLoanAndPublishEvent() {
        UUID userId = UUID.randomUUID();
        UUID bookId = UUID.randomUUID();
        BorrowRequest request = new BorrowRequest(bookId);
        Loan saved = Loan.builder()
                .id(UUID.randomUUID())
                .userId(userId)
                .bookId(bookId)
                .borrowDate(LocalDateTime.now())
                .dueDate(LocalDateTime.now().plusDays(14))
                .status(LoanStatus.BORROWED)
                .fineAmount(BigDecimal.ZERO)
                .build();

        when(loanRepository.countByUserIdAndStatusIn(eq(userId), anyList())).thenReturn(1);
        when(catalogClient.checkAvailability(bookId)).thenReturn(new CatalogStockResponse(bookId, true, 3));
        when(catalogClient.decrementStock(bookId)).thenReturn(new CatalogStockResponse(bookId, true, 2));
        when(loanRepository.save(any(Loan.class))).thenReturn(saved);
        when(loanMapper.toResponse(saved)).thenReturn(new LoanResponse(saved.getId(), userId, bookId,
                saved.getBorrowDate(), saved.getDueDate(), null, LoanStatus.BORROWED, BigDecimal.ZERO, null, null));

        LoanResponse response = borrowingService.borrowBook(userId, "user@example.com", request);

        assertThat(response).isNotNull();
        verify(catalogClient).checkAvailability(bookId);
        verify(catalogClient).decrementStock(bookId);
        verify(loanEventPublisher).publishLoanCreated(saved, "user@example.com");
    }

    @Test
    void returnBook_HappyPath_ShouldReturnAndPublishEvent() {
        UUID loanId = UUID.randomUUID();
        UUID bookId = UUID.randomUUID();
        Loan existing = Loan.builder()
                .id(loanId)
                .bookId(bookId)
                .userId(UUID.randomUUID())
                .borrowDate(LocalDateTime.now().minusDays(5))
                .dueDate(LocalDateTime.now().plusDays(9))
                .status(LoanStatus.BORROWED)
                .fineAmount(BigDecimal.ZERO)
                .build();

        when(loanRepository.findById(loanId)).thenReturn(Optional.of(existing));
        when(catalogClient.incrementStock(bookId)).thenReturn(new CatalogStockResponse(bookId, true, 1));
        when(loanRepository.save(any(Loan.class))).thenAnswer(inv -> inv.getArgument(0));
        when(loanMapper.toResponse(any(Loan.class))).thenAnswer(inv -> {
            Loan loan = inv.getArgument(0);
            return new LoanResponse(loan.getId(), loan.getUserId(), loan.getBookId(), loan.getBorrowDate(),
                    loan.getDueDate(), loan.getReturnDate(), loan.getStatus(), loan.getFineAmount(), null, null);
        });

        LoanResponse response = borrowingService.returnBook(loanId, "user@example.com");

        assertThat(response.status()).isEqualTo(LoanStatus.RETURNED);
        verify(catalogClient).incrementStock(bookId);
        verify(loanEventPublisher).publishLoanReturned(existing, "user@example.com");
    }

    @Test
    void borrowBook_MaxLoansReached_ShouldReject() {
        UUID userId = UUID.randomUUID();
        BorrowRequest request = new BorrowRequest(UUID.randomUUID());

        when(loanRepository.countByUserIdAndStatusIn(eq(userId), anyList())).thenReturn(5);

        assertThatThrownBy(() -> borrowingService.borrowBook(userId, "user@example.com", request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Maximum active loans (5) reached");
    }

    @Test
    void returnBook_Overdue_ShouldCalculateFine() {
        UUID loanId = UUID.randomUUID();
        UUID bookId = UUID.randomUUID();
        Loan overdueLoan = Loan.builder()
                .id(loanId)
                .bookId(bookId)
                .userId(UUID.randomUUID())
                .borrowDate(LocalDateTime.now().minusDays(30))
                .dueDate(LocalDateTime.now().minusDays(2))
                .status(LoanStatus.OVERDUE)
                .fineAmount(BigDecimal.ZERO)
                .build();

        when(loanRepository.findById(loanId)).thenReturn(Optional.of(overdueLoan));
        when(catalogClient.incrementStock(bookId)).thenReturn(new CatalogStockResponse(bookId, true, 2));
        when(loanRepository.save(any(Loan.class))).thenAnswer(inv -> inv.getArgument(0));
        when(loanMapper.toResponse(any(Loan.class))).thenAnswer(inv -> {
            Loan loan = inv.getArgument(0);
            return new LoanResponse(loan.getId(), loan.getUserId(), loan.getBookId(), loan.getBorrowDate(),
                    loan.getDueDate(), loan.getReturnDate(), loan.getStatus(), loan.getFineAmount(), null, null);
        });

        LoanResponse response = borrowingService.returnBook(loanId, "user@example.com");

        assertThat(response.fineAmount()).isGreaterThan(BigDecimal.ZERO);
        verify(loanEventPublisher).publishLoanReturned(overdueLoan, "user@example.com");
    }

    @Test
    void returnBook_WhenMissing_ShouldThrowNotFound() {
        UUID loanId = UUID.randomUUID();
        when(loanRepository.findById(loanId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> borrowingService.returnBook(loanId, "user@example.com"))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
