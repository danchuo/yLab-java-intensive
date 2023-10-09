package org.wallet.application;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.wallet.exception.PlayerNotFoundException;
import org.wallet.log.LogAction;
import org.wallet.model.Player;
import org.wallet.service.AuditService;
import org.wallet.service.PlayerService;
import org.wallet.service.TransactionService;

public class WalletApplicationTest {

  private static final String TEST_USER = "testUser";
  private static final String TEST_PASSWORD = "testPassword";
  private WalletApplication walletApplication;
  private TransactionService transactionService;
  private PlayerService playerService;
  private AuditService auditService;

  @BeforeEach
  public void setUp() {
    transactionService = mock(TransactionService.class);
    playerService = mock(PlayerService.class);
    auditService = mock(AuditService.class);
    walletApplication = new WalletApplication(transactionService, playerService, auditService);
  }

  @Test
  public void testRegisterPlayer() {
    Player registeredPlayer = new Player(TEST_USER, TEST_PASSWORD);
    when(playerService.registerPlayer(TEST_USER, TEST_PASSWORD)).thenReturn(registeredPlayer);

    walletApplication.registerPlayer(TEST_USER, TEST_PASSWORD);

    assertEquals(registeredPlayer, walletApplication.getCurrentPlayer());
    verify(auditService).log(eq(LogAction.AUTHORIZATION), eq(TEST_USER), eq("User registered."));
  }

  @Test
  public void testLoginSuccessful() {
    Player loggedInPlayer = new Player(TEST_USER, TEST_PASSWORD);
    when(playerService.login(TEST_USER, TEST_PASSWORD))
        .thenReturn(java.util.Optional.of(loggedInPlayer));

    walletApplication.login(TEST_USER, TEST_PASSWORD);

    assertEquals(loggedInPlayer, walletApplication.getCurrentPlayer());
    verify(auditService).log(eq(LogAction.AUTHORIZATION), eq(TEST_USER), eq("User logged in."));
  }

  @Test
  public void testLoginFailed() {
    when(playerService.login(TEST_USER, TEST_PASSWORD)).thenReturn(java.util.Optional.empty());

    assertThrows(
        PlayerNotFoundException.class, () -> walletApplication.login(TEST_USER, TEST_PASSWORD));
    verify(auditService)
        .log(eq(LogAction.AUTHORIZATION), eq(TEST_USER), startsWith("Login failed:"));
  }

  @Test
  public void testLogout() {
    Player player = new Player(TEST_USER, TEST_PASSWORD);
    when(playerService.registerPlayer(TEST_USER, TEST_PASSWORD)).thenReturn(player);
    walletApplication.registerPlayer(player.getLogin(), player.getPassword());

    walletApplication.logout();

    assertNull(walletApplication.getCurrentPlayer());
    verify(auditService).log(eq(LogAction.EXIT), eq(player.getLogin()), eq("User logged out."));
  }

  @Test
  public void testIsPlayerExist() {
    when(playerService.isPlayerExist("testUser")).thenReturn(true);
    when(playerService.isPlayerExist("nonExistentUser")).thenReturn(false);

    assertTrue(walletApplication.isPlayerExist("testUser"));
    assertFalse(walletApplication.isPlayerExist("nonExistentUser"));

    verify(playerService, times(2)).isPlayerExist(anyString());
  }

  @Test
  public void testIsTransactionExist() {
    when(transactionService.isTransactionExist("testTransactionId")).thenReturn(true);
    when(transactionService.isTransactionExist("nonExistentTransactionId")).thenReturn(false);

    assertTrue(walletApplication.isTransactionExist("testTransactionId"));
    assertFalse(walletApplication.isTransactionExist("nonExistentTransactionId"));

    verify(transactionService, times(2)).isTransactionExist(anyString());
  }
}
