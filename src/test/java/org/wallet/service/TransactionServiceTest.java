package org.wallet.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.wallet.exception.InsufficientMoneyException;
import org.wallet.exception.TransactionAlreadyExistException;
import org.wallet.model.Player;
import org.wallet.model.Transaction;
import org.wallet.model.TransactionType;
import org.wallet.repository.TransactionRepository;

class TransactionServiceTest {

  private TransactionService transactionService;

  @Mock private TransactionRepository transactionRepository;

  @Mock private Player player;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    transactionService = new TransactionService(transactionRepository);
  }

  @Test
  void givenTransactionIdExists_whenIsTransactionExist_thenTrue() {
    when(transactionRepository.getTransactionById("testTransactionId"))
        .thenReturn(
            Optional.of(
                new Transaction("testPlayer", "testTransactionId", TransactionType.CREDIT, 100)));

    boolean result = transactionService.isTransactionExist("testTransactionId");

    assertTrue(result);
  }

  @Test
  void givenTransactionIdDoesNotExist_whenIsTransactionExist_thenFalse() {
    when(transactionRepository.getTransactionById("nonExistentId")).thenReturn(Optional.empty());

    boolean result = transactionService.isTransactionExist("nonExistentId");

    assertFalse(result);
  }

  @Test
  void givenCreditTransaction_whenRegisterTransaction_thenPlayerCredited() {
    Transaction creditTransaction =
        new Transaction("testPlayer", "testTransactionId", TransactionType.CREDIT, 100);

    when(transactionRepository.getTransactionById("testTransactionId"))
        .thenReturn(Optional.empty());
    when(player.canDebit(100)).thenReturn(true);

    transactionService.registerTransaction(player, creditTransaction);

    verify(player, times(1)).credit(100);
    verify(transactionRepository, times(1)).addTransaction(creditTransaction);
  }

  @Test
  void givenDebitTransactionWithSufficientBalance_whenRegisterTransaction_thenPlayerDebited() {
    Transaction debitTransaction =
        new Transaction("testPlayer", "testTransactionId", TransactionType.DEBIT, 50);

    when(transactionRepository.getTransactionById("testTransactionId"))
        .thenReturn(Optional.empty());
    when(player.canDebit(50)).thenReturn(true);

    transactionService.registerTransaction(player, debitTransaction);

    verify(player, times(1)).debit(50);
    verify(transactionRepository, times(1)).addTransaction(debitTransaction);
  }

  @Test
  void
      givenDebitTransactionWithInsufficientBalance_whenRegisterTransaction_thenInsufficientMoneyExceptionThrown() {
    Transaction debitTransaction =
        new Transaction("testPlayer", "testTransactionId", TransactionType.DEBIT, 100);

    when(transactionRepository.getTransactionById("testTransactionId"))
        .thenReturn(Optional.empty());
    when(player.canDebit(100)).thenReturn(false);

    assertThrows(
        InsufficientMoneyException.class,
        () -> transactionService.registerTransaction(player, debitTransaction));
    verify(player, never()).debit(anyLong());
    verify(transactionRepository, never()).addTransaction(debitTransaction);
  }

  @Test
  void
      givenTransactionAlreadyExists_whenRegisterTransaction_thenTransactionAlreadyExistExceptionThrown() {
    Transaction existingTransaction =
        new Transaction("testPlayer", "testTransactionId", TransactionType.CREDIT, 100);

    when(transactionRepository.getTransactionById("testTransactionId"))
        .thenReturn(Optional.of(existingTransaction));

    assertThrows(
        TransactionAlreadyExistException.class,
        () -> transactionService.registerTransaction(player, existingTransaction));
    verify(player, never()).credit(anyLong());
    verify(player, never()).debit(anyLong());
    verify(transactionRepository, never()).addTransaction(existingTransaction);
  }

  @Test
  void givenPlayer_whenGetTransactionByPlayer_thenListOfTransactionsReturned() {
    player = new Player("testPlayer", "testPassword");
    when(transactionRepository.getTransactionsByPlayerLogin("testPlayer"))
        .thenReturn(
            List.of(
                new Transaction("testPlayer", "transaction1", TransactionType.CREDIT, 50),
                new Transaction("testPlayer", "transaction2", TransactionType.DEBIT, 30)));

    List<Transaction> transactions = transactionService.getTransactionByPlayer(player);

    assertEquals(2, transactions.size());
  }

  @Test
  void givenPlayerWithNoTransactions_whenGetTransactionByPlayer_thenEmptyListReturned() {
    when(transactionRepository.getTransactionsByPlayerLogin("testPlayer"))
        .thenReturn(new ArrayList<>());

    List<Transaction> transactions = transactionService.getTransactionByPlayer(player);

    assertTrue(transactions.isEmpty());
  }
}
