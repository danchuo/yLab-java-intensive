package org.wallet.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.wallet.exception.TransactionAlreadyExistException;
import org.wallet.model.Transaction;
import org.wallet.model.TransactionType;

public class InMemoryTransactionRepositoryTest {

  private TransactionRepository transactionRepository;
  private Transaction testTransaction;

  @BeforeEach
  public void setUp() {
    transactionRepository = new InMemoryTransactionRepository();
    testTransaction =
        new Transaction("testPlayer", "testTransactionId", TransactionType.CREDIT, 100);
  }

  @Test
  public void emptyRepository_getTransactions_returnsEmptyList() {
    List<Transaction> transactions = transactionRepository.getTransactions();
    assertTrue(transactions.isEmpty());
  }

  @Test
  public void addTransactionAndGetTransactions_transactionAdded() {
    transactionRepository.addTransaction(testTransaction);
    List<Transaction> transactions = transactionRepository.getTransactions();
    assertEquals(1, transactions.size());
    assertEquals(testTransaction, transactions.get(0));
  }

  @Test
  public void addDuplicateTransaction_throwsTransactionAlreadyExistException() {
    transactionRepository.addTransaction(testTransaction);
    assertThrows(
        TransactionAlreadyExistException.class,
        () -> transactionRepository.addTransaction(testTransaction));
  }

  @Test
  public void getTransactionById_found_returnsOptionalWithTransaction() {
    transactionRepository.addTransaction(testTransaction);
    Optional<Transaction> retrievedTransaction =
        transactionRepository.getTransactionById("testTransactionId");
    assertTrue(retrievedTransaction.isPresent());
    assertEquals(testTransaction, retrievedTransaction.get());
  }

  @Test
  public void getTransactionById_notFound_returnsEmptyOptional() {
    Optional<Transaction> retrievedTransaction =
        transactionRepository.getTransactionById("nonExistentId");
    assertTrue(retrievedTransaction.isEmpty());
  }

  @Test
  public void getTransactionsByPlayerLogin_transactionsFound_returnsList() {
    Transaction transaction1 =
        new Transaction("testPlayer", "transaction1", TransactionType.CREDIT, 50);
    Transaction transaction2 =
        new Transaction("testPlayer", "transaction2", TransactionType.DEBIT, 30);
    Transaction transaction3 =
        new Transaction("otherPlayer", "transaction3", TransactionType.CREDIT, 70);

    transactionRepository.addTransaction(transaction1);
    transactionRepository.addTransaction(transaction2);
    transactionRepository.addTransaction(transaction3);

    List<Transaction> playerTransactions =
        transactionRepository.getTransactionsByPlayerLogin("testPlayer");

    assertEquals(2, playerTransactions.size());
    assertTrue(playerTransactions.contains(transaction1));
    assertTrue(playerTransactions.contains(transaction2));
  }

  @Test
  public void getTransactionsByPlayerLogin_noTransactionsFound_returnsEmptyList() {
    Transaction transaction1 =
        new Transaction("otherPlayer", "transaction1", TransactionType.CREDIT, 50);
    Transaction transaction2 =
        new Transaction("otherPlayer", "transaction2", TransactionType.DEBIT, 30);

    transactionRepository.addTransaction(transaction1);
    transactionRepository.addTransaction(transaction2);

    List<Transaction> playerTransactions =
        transactionRepository.getTransactionsByPlayerLogin("testPlayer");

    assertTrue(playerTransactions.isEmpty());
  }
}
