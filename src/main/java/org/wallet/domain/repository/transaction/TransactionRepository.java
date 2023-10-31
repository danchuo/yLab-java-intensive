package org.wallet.domain.repository.transaction;

import java.util.List;
import java.util.Optional;
import org.wallet.domain.model.Transaction;

/**
 * The {@code TransactionRepository} interface defines methods for interacting with transaction
 * data. Implementations of this interface are responsible for managing transactions, including
 * retrieval, addition, and querying by transaction ID or player login.
 */
public interface TransactionRepository {

  /**
   * Retrieves a list of all transactions stored in the repository.
   *
   * @return A list of transactions.
   */
  List<Transaction> getTransactions();

  /**
   * Adds a new transaction to the repository.
   *
   * @param transaction The transaction to be added.
   */
  void addTransaction(Transaction transaction);

  /**
   * Retrieves a transaction by its unique ID.
   *
   * @param transactionId The ID of the transaction to retrieve.
   * @return An optional containing the retrieved transaction, or an empty optional if not found.
   */
  Optional<Transaction> getTransactionById(String transactionId);

  /**
   * Retrieves a list of transactions associated with a player's login.
   *
   * @param playerLogin The login of the player.
   * @return A list of transactions associated with the specified player.
   */
  List<Transaction> getTransactionsByPlayerLogin(String playerLogin);

  /**
   * Checks whether a player with the given login exists in the data store.
   *
   * @param transactionId Id of the transaction to check for existence.
   * @return `true` if the transaction exists; otherwise, `false`.
   */
  boolean isTransactionExist(String transactionId);
}
