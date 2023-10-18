package org.wallet.application;

import java.util.List;
import lombok.Getter;
import org.wallet.exception.PlayerNotFoundException;
import org.wallet.exception.UnauthorizedAccessException;
import org.wallet.log.LogAction;
import org.wallet.model.Log;
import org.wallet.model.Player;
import org.wallet.model.Transaction;
import org.wallet.model.TransactionType;
import org.wallet.service.AuditService;
import org.wallet.service.PlayerService;
import org.wallet.service.TransactionService;

/**
 * The {@code WalletApplication} class represents an application for managing player wallets and
 * transactions. It provides functionality for user registration, login, logout, checking player and
 * transaction existence, registering transactions, and viewing transaction history.
 */
public class WalletApplication {

  private final TransactionService transactionService;
  private final PlayerService playerService;

  private final AuditService auditService;

  @Getter private Player currentPlayer;

  /**
   * Creates a new instance of the {@code WalletApplication} class with the specified services.
   *
   * @param transactionService The service for managing transactions.
   * @param playerService The service for managing player accounts.
   * @param auditService The service for logging audit actions.
   */
  public WalletApplication(
      TransactionService transactionService,
      PlayerService playerService,
      AuditService auditService) {
    this.transactionService = transactionService;
    this.playerService = playerService;
    this.auditService = auditService;
  }

  /**
   * Registers a new player with the specified login and password.
   *
   * @param login The player's login.
   * @param password The player's password.
   */
  public void registerPlayer(String login, String password) {
    currentPlayer = playerService.registerPlayer(login, password);
    auditService.log(LogAction.AUTHORIZATION, login, "User registered.");
  }

  /**
   * Logs in a player with the specified login and password.
   *
   * @param login The player's login.
   * @param password The player's password.
   */
  public void login(String login, String password) {
    try {
      currentPlayer =
          playerService.login(login, password).orElseThrow(PlayerNotFoundException::new);
      auditService.log(LogAction.AUTHORIZATION, login, "User logged in.");
    } catch (PlayerNotFoundException e) {
      auditService.log(LogAction.AUTHORIZATION, login, "Login failed: " + e.getMessage());
      throw e;
    }
  }

  /**
   * Logs out the currently logged-in player. If there is a player logged in, this method logs the
   * player out and sets the currentPlayer to null. If no player is logged in, it does nothing.
   */
  public void logout() {
    if (currentPlayer != null) {
      auditService.log(LogAction.EXIT, currentPlayer.getLogin(), "User logged out.");
      currentPlayer = null;
    }
  }

  /**
   * Checks if a player with the specified login exists.
   *
   * @param login The player's login to check.
   * @return {@code true} if the player exists, {@code false} otherwise.
   */
  public boolean isPlayerExist(String login) {
    return playerService.isPlayerExist(login);
  }

  /**
   * Checks if a transaction with the specified ID exists.
   *
   * @param transactionId The ID of the transaction to check.
   * @return {@code true} if the transaction exists, {@code false} otherwise.
   */
  public boolean isTransactionExist(String transactionId) {
    return transactionService.isTransactionExist(transactionId);
  }

  /**
   * Registers a new transaction for the currently logged-in player. If no player is logged in, it
   * does nothing.
   *
   * @param transaction The transaction to register.
   */
  public void registerTransaction(Transaction transaction) {
    if (currentPlayer != null) {
      try {
        transactionService.registerTransaction(currentPlayer, transaction);
        playerService.updatePlayer(currentPlayer);
        auditService.log(
            transaction.type() == TransactionType.DEBIT ? LogAction.DEBIT : LogAction.CREDIT,
            currentPlayer.getLogin(),
            "Transaction registered: " + transaction.transactionId());
      } catch (IllegalArgumentException e) {
        auditService.log(
            transaction.type() == TransactionType.DEBIT ? LogAction.DEBIT : LogAction.CREDIT,
            currentPlayer.getLogin(),
            "Transaction failed: " + e.getMessage());
      }
    }
  }

  /**
   * Returns a list of transactions for the currently authenticated player. If no player is
   * authenticated, an {@code UnauthorizedAccessException} is thrown.
   *
   * @return A list of transactions for the authenticated player.
   * @throws UnauthorizedAccessException If no player is currently authenticated.
   */
  public List<Transaction> getTransactionOfCurrentPlayer() {
    if (currentPlayer == null) {
      throw new UnauthorizedAccessException();
    }
    return transactionService.getTransactionByPlayer(currentPlayer);
  }

  /**
   * Get the list of log messages.
   *
   * @return List of log messages.
   */
  public List<Log> getLogMessages() {
    return auditService.getLogMessages();
  }
}
