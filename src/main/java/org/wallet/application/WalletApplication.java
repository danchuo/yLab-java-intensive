package org.wallet.application;

import java.math.BigDecimal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.wallet.aop.annotations.Loggable;
import org.wallet.domain.model.Log;
import org.wallet.domain.model.LogAction;
import org.wallet.domain.model.Player;
import org.wallet.domain.model.Transaction;
import org.wallet.domain.service.AuditService;
import org.wallet.domain.service.PlayerService;
import org.wallet.domain.service.TransactionService;
import org.wallet.exception.PlayerNotFoundException;
import org.wallet.exception.UnauthorizedAccessException;

/**
 * The {@code WalletApplication} class represents an application for managing player wallets and
 * transactions. It provides functionality for user registration, login, logout, checking player and
 * transaction existence, registering transactions, and viewing transaction history.
 */
@Service
@RequiredArgsConstructor
public class WalletApplication {

  private final TransactionService transactionService;
  private final PlayerService playerService;

  private final AuditService auditService;

  /**
   * Registers a new player with the specified login and password.
   *
   * @param login The player's login.
   * @param password The player's password.
   */
  @Loggable(LogAction.REGISTRATION)
  public void registerPlayer(String login, String password) {
    var currentPlayer = playerService.registerPlayer(login, password);
  }

  /**
   * Retrieves the balance of a player with the specified login.
   *
   * @param login The player's login.
   * @return The balance of the player.
   */
  public BigDecimal getBalanceOfPlayer(String login) {
    return getPlayerByLogin(login).getBalance();
  }

  /**
   * Logs in a player with the specified login and password.
   *
   * @param login The player's login.
   * @param password The player's password.
   */
  @Loggable(LogAction.AUTHORIZATION)
  public void login(String login, String password) {
    playerService.login(login, password).orElseThrow(PlayerNotFoundException::new);
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
  @Loggable(LogAction.TRANSACTION)
  public void registerTransaction(Transaction transaction) {
    var currentPlayer = getPlayerByLogin(transaction.playerLogin());
    transactionService.registerTransaction(currentPlayer, transaction);
    playerService.updatePlayer(currentPlayer);
  }

  /**
   * Returns a list of transactions for the currently authenticated player. If no player is
   * authenticated, an {@code UnauthorizedAccessException} is thrown.
   *
   * @param login The login of the authenticated player.
   * @return A list of transactions for the authenticated player.
   * @throws UnauthorizedAccessException If no player is currently authenticated.
   */
  public List<Transaction> getTransactionsOfPlayer(String login) {
    return transactionService.getTransactionsByPlayer(getPlayerByLogin(login));
  }

  /**
   * Get the list of log messages.
   *
   * @return List of log messages.
   */
  public List<Log> getLogMessages() {
    return auditService.getLogMessages();
  }

  private Player getPlayerByLogin(String login) {
    return playerService.getPlayerByLogin(login).orElseThrow(PlayerNotFoundException::new);
  }
}
