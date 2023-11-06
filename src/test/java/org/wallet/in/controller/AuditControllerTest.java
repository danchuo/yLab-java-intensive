package org.wallet.in.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
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
import org.wallet.domain.model.Log;
import org.wallet.domain.model.LogAction;
import org.wallet.in.config.RestResponseEntityExceptionHandler;

public class AuditControllerTest {

  @Mock private WalletApplication walletApplication;

  @InjectMocks private AuditController auditController;

  private MockMvc mockMvc;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.initMocks(this);
    mockMvc =
        MockMvcBuilders.standaloneSetup(auditController)
            .setControllerAdvice(new RestResponseEntityExceptionHandler())
            .build();
  }

  @Test
  @DisplayName("Retrieve a list of audit logs and map them to LogResponseDto objects")
  void getLogs() throws Exception {
    // Prepare test data
    Log log1 = new Log(LocalDateTime.now(), LogAction.AUTHORIZATION, "test1", "test");
    Log log2 = new Log(LocalDateTime.now(), LogAction.AUTHORIZATION, "test2", "test");
    List<Log> logs = Arrays.asList(log1, log2);

    // Mock the service
    when(walletApplication.getLogMessages()).thenReturn(logs);

    mockMvc
        .perform(get("/logs").contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].action").value("AUTHORIZATION"))
        .andExpect(jsonPath("$[0].username").value("test1"))
        .andExpect(jsonPath("$[1].action").value("AUTHORIZATION"))
        .andExpect(jsonPath("$[1].username").value("test2"));
  }
}
