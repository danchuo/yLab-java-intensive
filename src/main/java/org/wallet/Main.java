package org.wallet;

import java.io.IOException;
import org.wallet.application.WalletApplication;
import org.wallet.in.ConsoleInteraction;
import org.wallet.repository.DatabaseConnection;
import org.wallet.repository.JdbcLogRepository;
import org.wallet.repository.JdbcPlayerRepository;
import org.wallet.repository.JdbcTransactionRepository;
import org.wallet.repository.LiquibaseManager;
import org.wallet.service.AuditService;
import org.wallet.service.PlayerService;
import org.wallet.service.TransactionService;

/** The `Main` class is the entry point for the application. */
public final class Main {

  private Main() {}

  /**
   * The main method for the application.
   *
   * @param args Command-line arguments (not used).
   * @throws IOException If an I/O error occurs.
   */
  public static void main(String... args) throws IOException {
    var connection = new DatabaseConnection();

    var liquibaseManager = new LiquibaseManager(connection);
    liquibaseManager.migrate();

    var transactionRepository = new JdbcTransactionRepository(connection);
    var transactionService = new TransactionService(transactionRepository);

    var playerRepository = new JdbcPlayerRepository(connection);
    var playerService = new PlayerService(playerRepository);

    var logRepository = new JdbcLogRepository(connection);
    var auditService = new AuditService(logRepository);

    var wallet = new WalletApplication(transactionService, playerService, auditService);

    var consoleInteraction = new ConsoleInteraction(wallet);

    consoleInteraction.startSession();
  }
}
