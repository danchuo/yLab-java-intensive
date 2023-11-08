package org.wallet.application;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.wallet.domain.service.AuditService;
import org.wallet.domain.service.PlayerService;
import org.wallet.domain.service.TransactionService;
import org.wallet.exception.PlayerNotFoundException;

@ExtendWith(MockitoExtension.class)
public class WalletApplicationTest {

  private static final String TEST_USER = "testUser";
  private static final String TEST_PASSWORD = "testPassword";
  @InjectMocks private WalletApplication walletApplication;
  @Mock private TransactionService transactionService;
  @Mock private PlayerService playerService;
  @Mock private AuditService auditService;

  @Test
  @DisplayName("Register Player should succeed")
  public void registerPlayer_shouldSucceed() {}

  @Test
  @DisplayName("Login should succeed")
  public void login_shouldSucceed() {}

  @Test
  @DisplayName("Login should fail for nonexistent user")
  public void login_shouldFailForNonexistentUser() {
    when(playerService.login(TEST_USER, TEST_PASSWORD)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> walletApplication.login(TEST_USER, TEST_PASSWORD))
        .isInstanceOf(PlayerNotFoundException.class);
  }

  @Test
  @DisplayName("Check if Player exists for existing player")
  public void isPlayerExist_shouldReturnTrueForExistingPlayer() {
    when(playerService.isPlayerExist("testUser")).thenReturn(true);
    when(playerService.isPlayerExist("nonExistentUser")).thenReturn(false);

    assertThat(walletApplication.isPlayerExist("testUser")).isTrue();
    assertThat(walletApplication.isPlayerExist("nonExistentUser")).isFalse();

    verify(playerService, times(2)).isPlayerExist(anyString());
  }

  @Test
  @DisplayName("Check if Transaction exists for existing transaction")
  public void isTransactionExist_shouldReturnTrueForExistingTransaction() {
    when(transactionService.isTransactionExist("testTransactionId")).thenReturn(true);
    when(transactionService.isTransactionExist("nonExistentTransactionId")).thenReturn(false);

    assertThat(walletApplication.isTransactionExist("testTransactionId")).isTrue();
    assertThat(walletApplication.isTransactionExist("nonExistentTransactionId")).isFalse();

    verify(transactionService, times(2)).isTransactionExist(anyString());
  }
}
