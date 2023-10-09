package org.wallet.application;

import java.util.List;
import org.wallet.exception.PlayerNotFoundException;
import org.wallet.log.LogAction;
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
  private Player currentPlayer;

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
   * Gets the currently logged-in player.
   *
   * @return The currently logged-in player, or {@code null} if no player is logged in.
   */
  public Player getCurrentPlayer() {
    return currentPlayer;
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

  /** Logs out the currently logged-in player. */
  public void logout() {
    auditService.log(LogAction.EXIT, currentPlayer.getLogin(), "User logged out.");
    currentPlayer = null;
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
   * Registers a new transaction for the currently logged-in player.
   *
   * @param transaction The transaction to register.
   */
  public void registerTransaction(Transaction transaction) {
    try {
      transactionService.registerTransaction(currentPlayer, transaction);
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

  /**
   * Gets the transaction history of the currently logged-in player.
   *
   * @return A list of transactions for the current player, or an empty list if no player is logged
   *     in.
   */
  public List<Transaction> getTransactionOfCurrentPlayer() {
    if (currentPlayer == null) {
      return List.of();
    }
    return transactionService.getTransactionByPlayer(currentPlayer);
  }
}
