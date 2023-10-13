package org.wallet.in;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.wallet.application.WalletApplication;
import org.wallet.exception.PlayerNotFoundException;
import org.wallet.model.Player;
import org.wallet.model.Transaction;
import org.wallet.model.TransactionType;
import org.wallet.utils.ConsoleParser;

/**
 * The {@code ConsoleInteraction} class represents the console-based user interaction for the Wallet
 * application. It provides methods to start a session, handle user registration and login, display
 * menus, and perform various operations based on user input.
 */
public class ConsoleInteraction {

  /** The WalletApplication instance associated with this console interaction. */
  private final WalletApplication walletApplication;

  /** The BufferedWriter used for writing output to the console. */
  private final BufferedWriter writer;

  /** The ConsoleParser instance used for parsing user input. */
  private final ConsoleParser consoleParser;

  /**
   * Initializes a new instance of the {@code ConsoleInteraction} class.
   *
   * @param walletApplication The WalletApplication instance to interact with.
   */
  public ConsoleInteraction(WalletApplication walletApplication) {
    this.walletApplication = walletApplication;
    writer = new BufferedWriter(new OutputStreamWriter(System.out, StandardCharsets.UTF_8));
    BufferedReader reader =
        new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));
    consoleParser = new ConsoleParser(writer, reader);
  }

  /**
   * Starts a new session for the Wallet application. Displays the main menu and handles user input.
   *
   * @throws IOException If an I/O error occurs.
   */
  public void startSession() throws IOException {
    writer.write("--------------\n");
    writer.write("Welcome to the Wallet application!\n");
    writer.write("Please select an option:\n");
    writer.write("1. Register\n");
    writer.write("2. Login\n");
    writer.write("3. Logs\n");
    writer.write("4. Exit\n");
    writer.flush();

    int option = consoleParser.askNumber("option number", 1, 4);

    switch (option) {
      case 1:
        register();
        break;
      case 2:
        login();
        break;
      case 3:
        logs();
        break;
      case 4:
        System.exit(0);
        break;
      default:
        writer.write("Invalid option\n");
        writer.flush();
        startSession();
    }
  }

  /**
   * Displays the log messages to the console, or a message if there are no logs available.
   *
   * @throws IOException If an I/O error occurs while writing to the console.
   */
  private void logs() throws IOException {
    var logs = walletApplication.getLogMessages();
    if (logs.isEmpty()) {
      writer.write("No logs available.\n");
    } else {
      writer.write("Log Messages:\n");
      for (var logMessage : logs) {
        writer.write(String.valueOf(logMessage));
      }
    }
    writer.flush();
    startSession();
  }

  /**
   * Logs in a user by parsing their credentials, checking if the player exists, and performing the
   * login operation.
   *
   * @throws IOException If an I/O error occurs.
   */
  private void login() throws IOException {
    Player player = consoleParser.parsePlayer();

    try {
      walletApplication.login(player.getLogin(), player.getPassword());
    } catch (PlayerNotFoundException e) {
      writer.write("Player not found\n");
      writer.flush();
      startSession();
      return;
    }

    playerMenu();
  }

  /**
   * Registers a new user by parsing their credentials, checking if the player already exists, and
   * performing the registration operation.
   *
   * @throws IOException If an I/O error occurs.
   */
  private void register() throws IOException {
    Player player = consoleParser.parsePlayer();

    if (walletApplication.isPlayerExist(player.getLogin())) {
      writer.write("Player already exists\n");
      writer.flush();
      startSession();
    }

    walletApplication.registerPlayer(player.getLogin(), player.getPassword());
    playerMenu();
  }

  /**
   * Displays the player menu and handles user input for various player-related operations.
   *
   * @throws IOException If an I/O error occurs.
   */
  void playerMenu() throws IOException {
    writer.write("--------------\n");
    writer.write("Player Menu:\n");
    writer.write("1. Check Balance\n");
    writer.write("2. Create Transaction\n");
    writer.write("3. View Transaction History\n");
    writer.write("4. Logout\n");
    writer.flush();

    int option = consoleParser.askNumber("option number", 1, 4);

    switch (option) {
      case 1:
        checkBalance();
        break;
      case 2:
        createTransaction();
        break;
      case 3:
        viewTransactionHistory();
        break;
      case 4:
        logout();
        return;
      default:
        writer.write("Invalid option\n");
        writer.flush();
        playerMenu();
    }
  }

  /**
   * Checks and displays the balance of the current player.
   *
   * @throws IOException If an I/O error occurs.
   */
  private void checkBalance() throws IOException {
    BigDecimal balance = walletApplication.getCurrentPlayer().getBalance();
    writer.write("Your balance: " + balance + "\n");
    writer.flush();
    playerMenu();
  }

  /**
   * Creates a new transaction based on user input, checks for duplicate transactions, and performs
   * the transaction registration.
   *
   * @throws IOException If an I/O error occurs.
   */
  private void createTransaction() throws IOException {
    var transaction =
        consoleParser.parseTransaction(walletApplication.getCurrentPlayer().getLogin());
    if (walletApplication.isTransactionExist(transaction.transactionId())) {
      writer.write("Transaction with the same ID already exists.\n");
    } else if (transaction.type() == TransactionType.DEBIT
        && !walletApplication.getCurrentPlayer().canDebit(transaction.amount())) {
      writer.write("Insufficient funds for the debit transaction.\n");
    } else {
      writer.write("Balance before: " + walletApplication.getCurrentPlayer().getBalance() + "\n");
      writer.write("Transaction created successfully.\n");
      walletApplication.registerTransaction(transaction);
      writer.write("Balance after: " + walletApplication.getCurrentPlayer().getBalance() + "\n");
    }

    writer.flush();
    playerMenu();
  }

  /**
   * Displays the transaction history of the current player.
   *
   * @throws IOException If an I/O error occurs.
   */
  private void viewTransactionHistory() throws IOException {
    List<Transaction> transactions = walletApplication.getTransactionOfCurrentPlayer();

    if (transactions.isEmpty()) {
      writer.write("You have no transactions.\n");
    } else {
      writer.write("Transaction History:\n");
      for (Transaction transaction : transactions) {
        writer.write("Transaction ID: " + transaction.transactionId() + "\n");
        writer.write("Amount: " + transaction.amount() + "\n");
        writer.write("Transaction Type: " + transaction.type() + "\n");
        writer.write("--------------\n");
      }
    }

    writer.flush();
    playerMenu();
  }

  /**
   * Logs out the current user, writes a logout message, and starts a new session.
   *
   * @throws IOException If an I/O error occurs.
   */
  private void logout() throws IOException {
    walletApplication.logout();
    writer.write("Logged out.\n");
    writer.flush();
    startSession();
  }
}
