package org.wallet.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.wallet.log.LogAction;
import org.wallet.model.Log;

public class AuditServiceTest {

    private AuditService auditService;

    @BeforeEach
    void setUp() {
        auditService = new AuditService();
    }

    @Test
    @DisplayName("Logging a single log should add it to the list of log messages")
    void log_shouldAddLogToMessages() {
        auditService.log(LogAction.AUTHORIZATION, "testUser", "User logged in.");
        List<Log> logMessages = auditService.getLogMessages();

        assertThat(logMessages).hasSize(1);
        Log log = logMessages.get(0);
        assertThat(log.getAction()).isEqualTo(LogAction.AUTHORIZATION);
        assertThat(log.getUsername()).isEqualTo("testUser");
        assertThat(log.getDetails()).isEqualTo("User logged in.");
    }

    @Test
    @DisplayName("Logging multiple logs should add them to the list of log messages")
    void log_shouldHandleMultipleLogs() {
        auditService.log(LogAction.AUTHORIZATION, "user1", "User 1 logged in.");
        auditService.log(LogAction.CREDIT, "user2", "User 2 performed a credit transaction.");
        auditService.log(LogAction.DEBIT, "user1", "User 1 performed a debit transaction.");

        List<Log> logMessages = auditService.getLogMessages();

        assertThat(logMessages).hasSize(3);
        assertThat(logMessages.get(0).getAction()).isEqualTo(LogAction.AUTHORIZATION);
        assertThat(logMessages.get(1).getAction()).isEqualTo(LogAction.CREDIT);
        assertThat(logMessages.get(2).getAction()).isEqualTo(LogAction.DEBIT);
    }
}
