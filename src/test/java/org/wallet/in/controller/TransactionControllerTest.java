package org.wallet.in.controller;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.util.Arrays;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.wallet.application.WalletApplication;
import org.wallet.domain.dto.mapper.TransactionMapper;
import org.wallet.domain.dto.request.JwtTokenResponseDto;
import org.wallet.domain.dto.request.TransactionRequestDto;
import org.wallet.domain.model.Transaction;
import org.wallet.domain.model.TransactionType;
import org.wallet.in.config.RestResponseEntityExceptionHandler;
import org.wallet.utils.JwtTokenUtility;

class TransactionControllerTest {

  @Mock private WalletApplication walletApplication;

  @InjectMocks private TransactionController transactionController;

  private MockMvc mockMvc;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.initMocks(this);
    mockMvc =
        MockMvcBuilders.standaloneSetup(transactionController)
            .setControllerAdvice(new RestResponseEntityExceptionHandler())
            .build();
  }

  @Test
  @DisplayName("Create a new transaction")
  void createTransaction() throws Exception {
    TransactionRequestDto requestDto = new TransactionRequestDto();
    requestDto.setTransactionId("1L");
    requestDto.setTransactionType("CREDIT");
    requestDto.setAmount(BigDecimal.TEN);
    var jwt = JwtTokenUtility.createJwtToken("test");
    requestDto.setJwtToken(jwt);

    Transaction transaction =
        TransactionMapper.INSTANCE.transactionRequestDtoToTransaction(requestDto);

    doNothing().when(walletApplication).registerTransaction(transaction);

    mockMvc
        .perform(
            post("/transaction")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsBytes(requestDto)))
        .andExpect(status().isCreated());
  }

  @Test
  @DisplayName("Retrieve all transactions of the authenticated player")
  void getAllTransactions() throws Exception {
    var login = "test";
    var jwt = JwtTokenUtility.createJwtToken(login);
    JwtTokenResponseDto request = new JwtTokenResponseDto();
    request.setJwtToken(jwt);
    var transactions =
        Arrays.asList(
            new Transaction(login, "id1", TransactionType.CREDIT, new BigDecimal("100.00")),
            new Transaction(login, "id2", TransactionType.DEBIT, new BigDecimal("50.00")));

    when(walletApplication.getTransactionsOfPlayer(login)).thenReturn(transactions);

    var answer =
        transactions.stream()
            .map(TransactionMapper.INSTANCE::transactionToTransactionResponseDto)
            .toList();

    mockMvc
        .perform(
            get("/transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsBytes(request)))
        .andExpect(status().isOk())
        .andExpect(content().json(new ObjectMapper().writeValueAsString(answer)));
  }
}
