package com.elib.borrowing.integration;

import com.elib.borrowing.dto.BorrowRequest;
import com.elib.borrowing.entity.Loan;
import com.elib.borrowing.entity.LoanStatus;
import com.elib.borrowing.repository.LoanRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.http.Fault;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.put;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class BorrowingServiceIntegrationTest {

    private static final WireMockServer WIRE_MOCK = new WireMockServer(options().dynamicPort());

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private LoanRepository loanRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private RabbitTemplate rabbitTemplate;

    @AfterAll
    static void tearDownServer() {
        WIRE_MOCK.stop();
    }

    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {
        if (!WIRE_MOCK.isRunning()) {
            WIRE_MOCK.start();
        }
        registry.add("spring.cloud.discovery.client.simple.instances.catalog-service[0].uri",
                WIRE_MOCK::baseUrl);
        registry.add("spring.cloud.discovery.client.simple.instances.catalog-service[0].secure", () -> false);
    }

    @BeforeEach
    void setup() {
        loanRepository.deleteAll();
        WIRE_MOCK.resetAll();
    }

    @Test
    void borrowFlow_HappyPath_NoStock_MaxLoans_DoubleReturn() throws Exception {
        UUID userId = UUID.randomUUID();
        UUID bookId = UUID.randomUUID();

        stubCatalogAvailable(bookId, 2);

        mockMvc.perform(post("/api/v1/loans/borrow")
                        .header("X-User-Id", userId.toString())
                        .header("X-User-Email", "reader@example.com")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new BorrowRequest(bookId))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("BORROWED"));

        verify(rabbitTemplate, atLeastOnce()).convertAndSend(anyString(), anyString(), any(Object.class));

        UUID noStockBookId = UUID.randomUUID();
        stubCatalogUnavailable(noStockBookId);

        mockMvc.perform(post("/api/v1/loans/borrow")
                        .header("X-User-Id", userId.toString())
                        .header("X-User-Email", "reader@example.com")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new BorrowRequest(noStockBookId))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("No copies available"));

        UUID maxUserId = UUID.randomUUID();
        for (int i = 0; i < 5; i++) {
            loanRepository.save(Loan.builder()
                    .userId(maxUserId)
                    .bookId(UUID.randomUUID())
                    .borrowDate(LocalDateTime.now().minusDays(1))
                    .dueDate(LocalDateTime.now().plusDays(13))
                    .status(LoanStatus.BORROWED)
                    .fineAmount(BigDecimal.ZERO)
                    .build());
        }

        mockMvc.perform(post("/api/v1/loans/borrow")
                        .header("X-User-Id", maxUserId.toString())
                        .header("X-User-Email", "reader@example.com")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new BorrowRequest(UUID.randomUUID()))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Maximum active loans (5) reached"));

        Loan existing = loanRepository.findAll().stream()
                .filter(loan -> loan.getUserId().equals(userId))
                .findFirst()
                .orElseThrow();

        stubCatalogIncrement(existing.getBookId(), 3);

        mockMvc.perform(post("/api/v1/loans/{id}/return", existing.getId())
                        .header("X-User-Email", "reader@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("RETURNED"));

        mockMvc.perform(post("/api/v1/loans/{id}/return", existing.getId())
                        .header("X-User-Email", "reader@example.com"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Loan is already returned"));
    }

    @Test
    void circuitBreaker_WhenCatalogDown_ShouldReturn503_ThenRecover() throws Exception {
        UUID userId = UUID.randomUUID();
        UUID firstBook = UUID.randomUUID();

        stubCatalogAvailable(firstBook, 2);

        mockMvc.perform(post("/api/v1/loans/borrow")
                        .header("X-User-Id", userId.toString())
                        .header("X-User-Email", "reader@example.com")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new BorrowRequest(firstBook))))
                .andExpect(status().isOk());

        stubCatalogDown();

        UUID secondBook = UUID.randomUUID();
        mockMvc.perform(post("/api/v1/loans/borrow")
                        .header("X-User-Id", userId.toString())
                        .header("X-User-Email", "reader@example.com")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new BorrowRequest(secondBook))))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.message")
                        .value("Catalog service is currently unavailable. Please try again later."));

        stubCatalogAvailable(secondBook, 2);

        Thread.sleep(1300);

        mockMvc.perform(post("/api/v1/loans/borrow")
                        .header("X-User-Id", userId.toString())
                        .header("X-User-Email", "reader@example.com")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new BorrowRequest(secondBook))))
                .andExpect(status().isOk());
    }

    private void stubCatalogAvailable(UUID bookId, int copiesAfterBorrow) {
        WIRE_MOCK.stubFor(get("/api/v1/books/" + bookId + "/availability")
                .willReturn(aResponse().withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"bookId\":\"" + bookId + "\",\"available\":true,\"availableCopies\":"
                                + (copiesAfterBorrow + 1) + "}")));

        WIRE_MOCK.stubFor(put("/api/v1/books/" + bookId + "/decrement-stock")
                .willReturn(aResponse().withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"bookId\":\"" + bookId + "\",\"available\":"
                                + (copiesAfterBorrow > 0) + ",\"availableCopies\":" + copiesAfterBorrow + "}")));
    }

    private void stubCatalogUnavailable(UUID bookId) {
        WIRE_MOCK.stubFor(get("/api/v1/books/" + bookId + "/availability")
                .willReturn(aResponse().withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"bookId\":\"" + bookId + "\",\"available\":false,\"availableCopies\":0}")));
    }

    private void stubCatalogIncrement(UUID bookId, int copiesAfterReturn) {
        WIRE_MOCK.stubFor(put("/api/v1/books/" + bookId + "/increment-stock")
                .willReturn(aResponse().withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"bookId\":\"" + bookId + "\",\"available\":true,\"availableCopies\":"
                                + copiesAfterReturn + "}")));
    }

    private void stubCatalogDown() {
        WIRE_MOCK.stubFor(get(urlPathMatching("/api/v1/books/.*/availability"))
                .willReturn(aResponse().withFault(Fault.CONNECTION_RESET_BY_PEER)));
    }
}
