package org.wallet.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.wallet.utils.BigDecimalUtils.fromLong;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.wallet.exception.InsufficientMoneyException;
import org.wallet.exception.TransactionAlreadyExistException;
import org.wallet.model.Player;
import org.wallet.model.Transaction;
import org.wallet.model.TransactionType;
import org.wallet.repository.TransactionRepository;

public class TransactionServiceTest {

  private TransactionService transactionService;

  @Mock private TransactionRepository transactionRepository;

  @Mock private Player player;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    transactionService = new TransactionService(transactionRepository);
  }

  @Test
  @DisplayName("When a transaction with a given ID exists, isTransactionExist should return true")
  void givenTransactionIdExists_whenIsTransactionExist_thenTrue() {
    when(transactionRepository.isTransactionExist("testTransactionId")).thenReturn(true);

    boolean result = transactionService.isTransactionExist("testTransactionId");

    assertThat(result).isTrue();
  }

  @Test
  @DisplayName(
      "When a transaction with a given ID does not exist, isTransactionExist should return false")
  void givenTransactionIdDoesNotExist_whenIsTransactionExist_thenFalse() {
    when(transactionRepository.getTransactionById("nonExistentId")).thenReturn(Optional.empty());

    boolean result = transactionService.isTransactionExist("nonExistentId");

    assertThat(result).isFalse();
  }

  @Test
  @DisplayName("When registering a credit transaction, the player should be credited")
  void givenCreditTransaction_whenRegisterTransaction_thenPlayerCredited() {
    Transaction creditTransaction =
        new Transaction("testPlayer", "testTransactionId", TransactionType.CREDIT, fromLong(100));

    when(transactionRepository.getTransactionById("testTransactionId"))
        .thenReturn(Optional.empty());
    when(player.canDebit(fromLong(100))).thenReturn(true);

    transactionService.registerTransaction(player, creditTransaction);

    verify(player, times(1)).credit(fromLong(100));
    verify(transactionRepository, times(1)).addTransaction(creditTransaction);
  }

  @Test
  @DisplayName(
      "When registering a debit transaction with sufficient balance, the player should be debited")
  void givenDebitTransactionWithSufficientBalance_whenRegisterTransaction_thenPlayerDebited() {
    Transaction debitTransaction =
        new Transaction("testPlayer", "testTransactionId", TransactionType.DEBIT, fromLong(50));

    when(transactionRepository.getTransactionById("testTransactionId"))
        .thenReturn(Optional.empty());
    when(player.canDebit(fromLong(50))).thenReturn(true);

    transactionService.registerTransaction(player, debitTransaction);

    verify(player, times(1)).debit(fromLong(50));
    verify(transactionRepository, times(1)).addTransaction(debitTransaction);
  }

  @Test
  @DisplayName(
      "When registering a debit transaction with insufficient balance, an InsufficientMoneyException should be thrown")
  void
      givenDebitTransactionWithInsufficientBalance_whenRegisterTransaction_thenInsufficientMoneyExceptionThrown() {
    Transaction debitTransaction =
        new Transaction("testPlayer", "testTransactionId", TransactionType.DEBIT, fromLong(100));

    when(transactionRepository.getTransactionById("testTransactionId"))
        .thenReturn(Optional.empty());
    when(player.canDebit(fromLong(100))).thenReturn(false);

    assertThatThrownBy(() -> transactionService.registerTransaction(player, debitTransaction))
        .isInstanceOf(InsufficientMoneyException.class);

    verify(player, never()).debit(fromLong(100));
    verify(transactionRepository, never()).addTransaction(debitTransaction);
  }

  @Test
  @DisplayName(
      "When registering an already existing transaction, a TransactionAlreadyExistException should be thrown")
  void
      givenTransactionAlreadyExists_whenRegisterTransaction_thenTransactionAlreadyExistExceptionThrown() {
    Transaction existingTransaction =
        new Transaction("testPlayer", "testTransactionId", TransactionType.CREDIT, fromLong(100));

    when(transactionRepository.isTransactionExist("testTransactionId")).thenReturn(true);

    assertThatThrownBy(() -> transactionService.registerTransaction(player, existingTransaction))
        .isInstanceOf(TransactionAlreadyExistException.class);

    verify(player, never()).credit(fromLong(anyLong()));
    verify(player, never()).debit(fromLong(anyLong()));
    verify(transactionRepository, never()).addTransaction(existingTransaction);
  }

  @Test
  @DisplayName(
      "When retrieving transactions for a player with transactions, a list of transactions should be returned")
  void givenPlayer_whenGetTransactionByPlayer_thenListOfTransactionsReturned() {
    player = new Player("testPlayer", "testPassword");
    when(transactionRepository.getTransactionsByPlayerLogin("testPlayer"))
        .thenReturn(
            List.of(
                new Transaction("testPlayer", "transaction1", TransactionType.CREDIT, fromLong(50)),
                new Transaction(
                    "testPlayer", "transaction2", TransactionType.DEBIT, fromLong(30))));

    List<Transaction> transactions = transactionService.getTransactionByPlayer(player);

    assertThat(transactions).isNotEmpty().hasSize(2);
  }

  @Test
  @DisplayName(
      "When retrieving transactions for a player with no transactions, an empty list should be returned")
  void givenPlayerWithNoTransactions_whenGetTransactionByPlayer_thenEmptyListReturned() {
    when(transactionRepository.getTransactionsByPlayerLogin("testPlayer"))
        .thenReturn(new ArrayList<>());

    List<Transaction> transactions = transactionService.getTransactionByPlayer(player);

    assertThat(transactions).isEmpty();
  }
}
