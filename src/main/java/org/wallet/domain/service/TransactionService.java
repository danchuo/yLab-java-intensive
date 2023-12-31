package org.wallet.domain.service;

import java.math.BigDecimal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.wallet.domain.model.Player;
import org.wallet.domain.model.Transaction;
import org.wallet.domain.model.TransactionType;
import org.wallet.domain.repository.transaction.TransactionRepository;
import org.wallet.exception.InsufficientMoneyException;
import org.wallet.exception.TransactionAlreadyExistException;

/**
 * The `TransactionService` class provides functionality to interact with transactions and perform
 * various operations related to transactions.
 */
@Service
@RequiredArgsConstructor
public class TransactionService {

  /** The repository for managing transactions. */
  private final TransactionRepository transactionRepository;

  /**
   * Checks if a transaction with the given transaction ID exists.
   *
   * @param transactionId The ID of the transaction to check.
   * @return `true` if the transaction exists, `false` otherwise.
   */
  public boolean isTransactionExist(String transactionId) {
    return transactionRepository.isTransactionExist(transactionId);
  }

  /**
   * Processes a credit transaction by adding the specified amount to the player's balance.
   *
   * @param player The player associated with the transaction.
   * @param transaction The credit transaction to process.
   */
  private void processCreditTransaction(Player player, Transaction transaction) {
    BigDecimal amount = transaction.amount();
    player.credit(amount);
  }

  /**
   * Processes a debit transaction by deducting the specified amount from the player's balance. If
   * the player does not have sufficient funds, an `InsufficientMoneyException` is thrown.
   *
   * @param player The player associated with the transaction.
   * @param transaction The debit transaction to process.
   * @throws InsufficientMoneyException If the player does not have sufficient funds for the debit
   *     transaction.
   */
  private void processDebitTransaction(Player player, Transaction transaction) {
    BigDecimal amount = transaction.amount();
    if (player.canDebit(amount)) {
      player.debit(amount);
    } else {
      throw new InsufficientMoneyException();
    }
  }

  /**
   * Registers a transaction for the player. If a transaction with the same ID already exists, a
   * `TransactionAlreadyExistException` is thrown. The type of transaction (credit or debit)
   * determines whether the player's balance is updated accordingly.
   *
   * @param player The player associated with the transaction.
   * @param transaction The transaction to register.
   * @throws TransactionAlreadyExistException If a transaction with the same ID already exists.
   */
  public void registerTransaction(Player player, Transaction transaction) {
    if (isTransactionExist(transaction.transactionId())) {
      throw new TransactionAlreadyExistException();
    } else {
      if (transaction.type() == TransactionType.CREDIT) {
        processCreditTransaction(player, transaction);
      } else if (transaction.type() == TransactionType.DEBIT) {
        processDebitTransaction(player, transaction);
      }
      transactionRepository.addTransaction(transaction);
    }
  }

  /**
   * Retrieves a list of transactions associated with a player based on their login.
   *
   * @param player The player for whom to retrieve transactions.
   * @return A list of transactions associated with the player.
   */
  public List<Transaction> getTransactionsByPlayer(Player player) {
    return transactionRepository.getTransactionsByPlayerLogin(player.getLogin());
  }
}
