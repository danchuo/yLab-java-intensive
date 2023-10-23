package org.wallet.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.math.BigDecimal;

import org.wallet.domain.model.Player;
import org.wallet.domain.model.Transaction;
import org.wallet.domain.model.TransactionType;

public class ConsoleParser {

  public static final int MAX_STRING_LENGTH = 64;
  public static final int MAX_TRANSACTION_AMOUNT = 1000;

  private final BufferedWriter writer;
  private final BufferedReader reader;

  public ConsoleParser(BufferedWriter writer, BufferedReader reader) {
    this.writer = writer;
    this.reader = reader;
  }

  public Player parsePlayer() throws IOException {
    String login = askString("your login", 3, MAX_STRING_LENGTH);
    String password = askString("your password", 3, MAX_STRING_LENGTH);

    return new Player(login, password);
  }

  public Transaction parseTransaction(String playerLogin) throws IOException {
    String transactionId = askString("transaction ID", 1, MAX_STRING_LENGTH);
    int transactionTypeChoice = askNumber("transaction type (1 for CREDIT, 2 for DEBIT)", 1, 2);
    TransactionType type =
        transactionTypeChoice == 1 ? TransactionType.CREDIT : TransactionType.DEBIT;
    BigDecimal amount = BigDecimal.valueOf(askNumber("transaction amount", 1, 1000));

    return new Transaction(playerLogin, transactionId, type, amount);
  }

  public int askNumber(String phrase, int min, int max) throws IOException {
    Integer result;
    do {
      writer.write(
          "Please enter the "
              + phrase
              + " that belongs to the range ["
              + min
              + ";"
              + max
              + "]...\n");
      writer.flush();
      result = parseIntOrNull(reader.readLine());
    } while (result == null || result < min || result > max);

    return result;
  }

  private String askString(String phrase, int min, int max) throws IOException {
    String result;
    do {
      writer.write(
          "Please enter "
              + phrase
              + " (length should be between "
              + min
              + " and "
              + max
              + " characters inclusively)...\n");
      writer.flush();
      result = reader.readLine();
    } while (result == null || result.length() < min || result.length() > max);

    return result;
  }

  private Integer parseIntOrNull(String value) {
    try {
      return Integer.parseInt(value);
    } catch (NumberFormatException e) {
      return null;
    }
  }
}
