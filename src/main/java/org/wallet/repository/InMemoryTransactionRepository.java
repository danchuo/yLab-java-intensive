package org.wallet.repository;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import org.wallet.exception.TransactionAlreadyExistException;
import org.wallet.model.Transaction;

/**
 * The {@code InMemoryTransactionRepository} class is an in-memory implementation of the {@link
 * TransactionRepository} interface. It stores transaction data in a HashMap.
 */
public class InMemoryTransactionRepository implements TransactionRepository {

  private final HashMap<String, Transaction> transactions;

  /** Constructs a new InMemoryTransactionRepository with an empty transaction storage. */
  public InMemoryTransactionRepository() {
    transactions = new HashMap<>();
  }

  /**
   * Retrieves a list of all transactions stored in the repository.
   *
   * @return A list of transactions.
   */
  @Override
  public List<Transaction> getTransactions() {
    return List.copyOf(transactions.values());
  }

  /**
   * Adds a new transaction to the repository.
   *
   * @param transaction The transaction to be added.
   * @throws TransactionAlreadyExistException If a transaction with the same ID already exists.
   */
  @Override
  public void addTransaction(Transaction transaction) {
    if (transactions.containsKey(transaction.transactionId())) {
      throw new TransactionAlreadyExistException();
    }
    transactions.put(transaction.transactionId(), transaction);
  }

  /**
   * Retrieves a transaction by its ID.
   *
   * @param transactionId The ID of the transaction to retrieve.
   * @return An optional containing the retrieved transaction, or an empty optional if not found.
   */
  @Override
  public Optional<Transaction> getTransactionById(String transactionId) {
    return Optional.ofNullable(transactions.get(transactionId));
  }

  /**
   * Retrieves a list of transactions by the player's login.
   *
   * @param playerLogin The login of the player to filter transactions.
   * @return A list of transactions associated with the specified player login.
   */
  @Override
  public List<Transaction> getTransactionsByPlayerLogin(String playerLogin) {
    return transactions.values().stream()
        .filter(transaction -> transaction.playerLogin().equals(playerLogin))
        .toList();
  }
}
