package org.wallet;

import java.io.IOException;
import org.wallet.application.WalletApplication;
import org.wallet.in.ConsoleInteraction;
import org.wallet.repository.InMemoryPlayerRepository;
import org.wallet.repository.InMemoryTransactionRepository;
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
    var transactionRepository = new InMemoryTransactionRepository();
    var transactionService = new TransactionService(transactionRepository);

    var playerRepository = new InMemoryPlayerRepository();
    var playerService = new PlayerService(playerRepository);

    var auditService = new AuditService();
    var wallet = new WalletApplication(transactionService, playerService, auditService);

    var consoleInteraction = new ConsoleInteraction(wallet);

    consoleInteraction.startSession();
  }
}
