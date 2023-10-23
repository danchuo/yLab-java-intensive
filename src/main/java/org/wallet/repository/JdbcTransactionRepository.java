package org.wallet.repository;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.wallet.exception.TransactionAlreadyExistException;
import org.wallet.model.Transaction;
import org.wallet.model.TransactionType;

/**
 * The `JdbcTransactionRepository` class is an implementation of the `TransactionRepository`
 * interface. It provides methods for interacting with transaction data in a relational database.
 */
public class JdbcTransactionRepository implements TransactionRepository {

  private static final String SELECT_ALL_TRANSACTIONS_SQL = "SELECT * FROM wallet.transactions";
  private static final String INSERT_TRANSACTION_SQL =
      "INSERT INTO wallet.transactions (player_login, transaction_id, type, amount) VALUES (?, ?, ?, ?)";
  private static final String SELECT_TRANSACTION_BY_ID_SQL =
      "SELECT * FROM wallet.transactions WHERE transaction_id = ?";
  private static final String SELECT_TRANSACTIONS_BY_PLAYER_SQL =
      "SELECT * FROM wallet.transactions WHERE player_login = ?";

  private static final String CHECK_TRANSACTION_BY_ID_SQL =
      "SELECT CASE WHEN EXISTS (SELECT 1 FROM wallet.transactions WHERE transaction_id = ?) THEN true ELSE false END;";

  /** The `DatabaseConnection` used to establish a connection to the database. */
  private final DatabaseConnection databaseConnection;

  /**
   * Constructs a new `JdbcTransactionRepository` with the provided `DatabaseConnection`.
   *
   * @param databaseConnection The database connection to be used for transaction data access.
   */
  public JdbcTransactionRepository(DatabaseConnection databaseConnection) {
    this.databaseConnection = databaseConnection;
  }

  /**
   * Retrieves a list of all transactions from the database.
   *
   * @return A list of transaction entities.
   */
  @Override
  public List<Transaction> getTransactions() {
    List<Transaction> transactions = new ArrayList();
    try (Connection connection = databaseConnection.getConnection();
        PreparedStatement preparedStatement =
            connection.prepareStatement(SELECT_ALL_TRANSACTIONS_SQL);
        ResultSet resultSet = preparedStatement.executeQuery()) {
      while (resultSet.next()) {
        String playerLogin = resultSet.getString("player_login");
        String transactionId = resultSet.getString("transaction_id");
        TransactionType type = TransactionType.valueOf(resultSet.getString("type"));
        BigDecimal amount = resultSet.getBigDecimal("amount");
        var transaction = new Transaction(playerLogin, transactionId, type, amount);
        transactions.add(transaction);
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return transactions;
  }

  /**
   * Adds a new transaction to the database.
   *
   * @param transaction The transaction entity to be added.
   * @throws TransactionAlreadyExistException if a transaction with the same ID already exists.
   */
  @Override
  public void addTransaction(Transaction transaction) {
    if (isTransactionExist(transaction.transactionId())) {
      throw new TransactionAlreadyExistException();
    }

    try (Connection connection = databaseConnection.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(INSERT_TRANSACTION_SQL)) {
      preparedStatement.setString(1, transaction.playerLogin());
      preparedStatement.setString(2, transaction.transactionId());
      preparedStatement.setString(3, transaction.type().toString());
      preparedStatement.setBigDecimal(4, transaction.amount());
      preparedStatement.executeUpdate();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  /**
   * Retrieves a transaction from the database by its ID.
   *
   * @param transactionId The ID of the transaction to retrieve.
   * @return An `Optional` containing the transaction if found, or empty if not found.
   */
  @Override
  public Optional<Transaction> getTransactionById(String transactionId) {
    try (Connection connection = databaseConnection.getConnection();
        PreparedStatement preparedStatement =
            connection.prepareStatement(SELECT_TRANSACTION_BY_ID_SQL)) {
      preparedStatement.setString(1, transactionId);
      try (ResultSet resultSet = preparedStatement.executeQuery()) {
        if (resultSet.next()) {
          String playerLogin = resultSet.getString("player_login");
          TransactionType type = TransactionType.valueOf(resultSet.getString("type"));
          BigDecimal amount = resultSet.getBigDecimal("amount");
          return Optional.of(new Transaction(playerLogin, transactionId, type, amount));
        }
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return Optional.empty();
  }

  /**
   * Checks if a transaction with the given ID exists in the database.
   *
   * @param transactionId The ID of the transaction to check.
   * @return `true` if the transaction exists, `false` otherwise.
   */
  @Override
  public boolean isTransactionExist(String transactionId) {
    try (Connection connection = databaseConnection.getConnection();
        PreparedStatement preparedStatement =
            connection.prepareStatement(CHECK_TRANSACTION_BY_ID_SQL)) {
      preparedStatement.setString(1, transactionId);
      try (ResultSet resultSet = preparedStatement.executeQuery()) {
        return resultSet.next() && resultSet.getBoolean(1);
      }
    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
  }

  /**
   * Retrieves a list of transactions for a specific player by their login.
   *
   * @param playerLogin The login of the player for whom transactions are to be retrieved.
   * @return A list of transaction entities.
   */
  @Override
  public List<Transaction> getTransactionsByPlayerLogin(String playerLogin) {
    var transactions = new ArrayList<Transaction>();
    try (Connection connection = databaseConnection.getConnection();
        PreparedStatement preparedStatement =
            connection.prepareStatement(SELECT_TRANSACTIONS_BY_PLAYER_SQL)) {
      preparedStatement.setString(1, playerLogin);
      try (ResultSet resultSet = preparedStatement.executeQuery()) {
        while (resultSet.next()) {
          String transactionId = resultSet.getString("transaction_id");
          TransactionType type = TransactionType.valueOf(resultSet.getString("type"));
          BigDecimal amount = resultSet.getBigDecimal("amount");
          transactions.add(new Transaction(playerLogin, transactionId, type, amount));
        }
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return transactions;
  }
}
