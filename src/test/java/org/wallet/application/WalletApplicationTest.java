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
import org.wallet.exception.PlayerNotFoundException;
import org.wallet.exception.UnauthorizedAccessException;
import org.wallet.domain.model.LogAction;
import org.wallet.domain.model.Player;
import org.wallet.domain.model.Transaction;
import org.wallet.domain.model.TransactionType;
import org.wallet.domain.service.AuditService;
import org.wallet.domain.service.PlayerService;
import org.wallet.domain.service.TransactionService;
import org.wallet.utils.BigDecimalUtils;

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
  public void registerPlayer_shouldSucceed() {
    Player registeredPlayer = new Player(TEST_USER, TEST_PASSWORD);
    when(playerService.registerPlayer(TEST_USER, TEST_PASSWORD)).thenReturn(registeredPlayer);

    walletApplication.registerPlayer(TEST_USER, TEST_PASSWORD);

    assertThat(walletApplication.getCurrentPlayer()).isEqualTo(registeredPlayer);
    verify(auditService).log(eq(LogAction.AUTHORIZATION), eq(TEST_USER), eq("User registered."));
  }

  @Test
  @DisplayName("Login should succeed")
  public void login_shouldSucceed() {
    Player loggedInPlayer = new Player(TEST_USER, TEST_PASSWORD);
    when(playerService.login(TEST_USER, TEST_PASSWORD)).thenReturn(Optional.of(loggedInPlayer));

    walletApplication.login(TEST_USER, TEST_PASSWORD);

    assertThat(walletApplication.getCurrentPlayer()).isEqualTo(loggedInPlayer);
    verify(auditService).log(eq(LogAction.AUTHORIZATION), eq(TEST_USER), eq("User logged in."));
  }

  @Test
  @DisplayName("Login should fail for nonexistent user")
  public void login_shouldFailForNonexistentUser() {
    when(playerService.login(TEST_USER, TEST_PASSWORD)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> walletApplication.login(TEST_USER, TEST_PASSWORD))
        .isInstanceOf(PlayerNotFoundException.class);
    verify(auditService)
        .log(eq(LogAction.AUTHORIZATION), eq(TEST_USER), startsWith("Login failed:"));
  }

  @Test
  @DisplayName("Logging out should set the current player to null and log the exit action")
  public void testLogout() {
    Player player = new Player(TEST_USER, TEST_PASSWORD);
    when(playerService.registerPlayer(TEST_USER, TEST_PASSWORD)).thenReturn(player);
    walletApplication.registerPlayer(player.getLogin(), player.getPassword());

    walletApplication.logout();

    assertThat(walletApplication.getCurrentPlayer()).isNull();
    verify(auditService).log(eq(LogAction.EXIT), eq(player.getLogin()), eq("User logged out."));
  }

  @Test
  @DisplayName("Logout when currentPlayer is null should not log any action")
  public void logout_whenCurrentPlayerIsNull_shouldNotLogAnyAction() {
    walletApplication.logout();

    verify(auditService, never()).log(any(), any(), any());
  }

  @Test
  @DisplayName("Registering a transaction with currentPlayer null should not log any action")
  public void registerTransaction_withCurrentPlayerNull_shouldNotLogAnyAction() {
    Transaction testTransaction =
        new Transaction(
            "testPlayer",
            "testTransactionId",
            TransactionType.CREDIT,
            BigDecimalUtils.fromLong(100));

    walletApplication.registerTransaction(testTransaction);

    verify(auditService, never()).log(any(), any(), any());
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
  @DisplayName("Getting transactions with no authenticated player should throw an UnauthorizedAccessException")
  public void getTransactionOfCurrentPlayer_withNoAuthenticatedPlayer_shouldThrowUnauthorizedAccessException() {
    assertThatThrownBy(() -> walletApplication.getTransactionOfCurrentPlayer())
            .isInstanceOf(UnauthorizedAccessException.class);
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
