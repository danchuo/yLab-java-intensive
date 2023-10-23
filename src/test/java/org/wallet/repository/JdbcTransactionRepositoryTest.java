package org.wallet.repository;

import static org.assertj.core.api.Assertions.*;
import static org.wallet.utils.BigDecimalUtils.fromLong;

import java.io.File;
import java.sql.Connection;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.DockerComposeContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.wallet.domain.repository.DatabaseConnection;
import org.wallet.domain.repository.transaction.JdbcTransactionRepository;
import org.wallet.domain.repository.LiquibaseManager;
import org.wallet.domain.repository.transaction.TransactionRepository;
import org.wallet.exception.TransactionAlreadyExistException;
import org.wallet.domain.model.Transaction;
import org.wallet.domain.model.TransactionType;

@Testcontainers
public class JdbcTransactionRepositoryTest {

  private static final int POSTGRES_PORT = 5432;

  @Container
  private static final DockerComposeContainer DOCKER_COMPOSE_CONTAINER =
      new DockerComposeContainer(new File("src/test/java/resources/docker-compose-test.yml"))
          .withExposedService("postgres", POSTGRES_PORT)
          .withLocalCompose(true)
          .withOptions("--compatibility");

  private static DatabaseConnection connection;
  private static TransactionRepository transactionRepository;
  private static Transaction testTransaction;

  @BeforeAll
  public static void setUp() {
    String original =
        DOCKER_COMPOSE_CONTAINER.getServiceHost("postgres", POSTGRES_PORT)
            + ":"
            + DOCKER_COMPOSE_CONTAINER.getServicePort("postgres", POSTGRES_PORT);

    String result = "jdbc:postgresql://" + original + "/wallet";
    connection = new DatabaseConnection(result);
    transactionRepository = new JdbcTransactionRepository(connection);
    var liquibase = new LiquibaseManager(connection);
    liquibase.migrate();
    testTransaction =
        new Transaction("testPlayer", "testTransactionId", TransactionType.CREDIT, fromLong(100));
  }

  @BeforeEach
  public void setUpBeforeEach() {
    try (Connection local = connection.getConnection()) {
      Statement statement = local.createStatement();

      statement.executeUpdate("DELETE FROM wallet.transactions");

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Test
  @DisplayName("Empty repository should return an empty list of transactions")
  public void emptyRepository_getTransactions_returnsEmptyList() {
    List<Transaction> transactions = transactionRepository.getTransactions();
    assertThat(transactions).isEmpty();
  }

  @Test
  @DisplayName("Adding a transaction to the repository should store the transaction")
  public void addTransactionAndGetTransactions_transactionAdded() {
    transactionRepository.addTransaction(testTransaction);
    List<Transaction> transactions = transactionRepository.getTransactions();
    assertThat(transactions).hasSize(1);
    assertThat(transactions).contains(testTransaction);
  }

  @Test
  @DisplayName("Adding a duplicate transaction should throw TransactionAlreadyExistException")
  public void addDuplicateTransaction_throwsTransactionAlreadyExistException() {
    transactionRepository.addTransaction(testTransaction);
    assertThatThrownBy(() -> transactionRepository.addTransaction(testTransaction))
        .isInstanceOf(TransactionAlreadyExistException.class);
  }

  @Test
  @DisplayName(
      "Getting a transaction by ID, when found, should return an Optional with the transaction")
  public void getTransactionById_found_returnsOptionalWithTransaction() {
    transactionRepository.addTransaction(testTransaction);
    Optional<Transaction> retrievedTransaction =
        transactionRepository.getTransactionById("testTransactionId");
    assertThat(retrievedTransaction).isPresent().hasValue(testTransaction);
  }

  @Test
  @DisplayName("Getting a transaction by ID, when not found, should return an empty Optional")
  public void getTransactionById_notFound_returnsEmptyOptional() {
    Optional<Transaction> retrievedTransaction =
        transactionRepository.getTransactionById("nonExistentId");
    assertThat(retrievedTransaction).isEmpty();
  }

  @Test
  @DisplayName(
      "Getting transactions by player login, when found, should return a list of transactions")
  public void getTransactionsByPlayerLogin_transactionsFound_returnsList() {
    Transaction transaction1 =
        new Transaction("testPlayer", "transaction1", TransactionType.CREDIT, fromLong(50));
    Transaction transaction2 =
        new Transaction("testPlayer", "transaction2", TransactionType.DEBIT, fromLong(30));
    Transaction transaction3 =
        new Transaction("otherPlayer", "transaction3", TransactionType.CREDIT, fromLong(70));

    transactionRepository.addTransaction(transaction1);
    transactionRepository.addTransaction(transaction2);
    transactionRepository.addTransaction(transaction3);

    List<Transaction> playerTransactions =
        transactionRepository.getTransactionsByPlayerLogin("testPlayer");

    assertThat(playerTransactions).hasSize(2).contains(transaction1, transaction2);
  }

  @Test
  @DisplayName(
      "Getting transactions by player login, when no transactions found, should return an empty list")
  public void getTransactionsByPlayerLogin_noTransactionsFound_returnsEmptyList() {
    Transaction transaction1 =
        new Transaction("otherPlayer", "transaction1", TransactionType.CREDIT, fromLong(50));
    Transaction transaction2 =
        new Transaction("otherPlayer", "transaction2", TransactionType.DEBIT, fromLong(30));

    transactionRepository.addTransaction(transaction1);
    transactionRepository.addTransaction(transaction2);

    List<Transaction> playerTransactions =
        transactionRepository.getTransactionsByPlayerLogin("testPlayer");

    assertThat(playerTransactions).isEmpty();
  }
}
