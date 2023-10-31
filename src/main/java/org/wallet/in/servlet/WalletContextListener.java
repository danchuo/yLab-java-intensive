package org.wallet.in.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import org.wallet.application.WalletApplication;
import org.wallet.domain.repository.DatabaseConnection;
import org.wallet.domain.repository.LiquibaseManager;
import org.wallet.domain.repository.log.JdbcLogRepository;
import org.wallet.domain.repository.player.JdbcPlayerRepository;
import org.wallet.domain.repository.transaction.JdbcTransactionRepository;
import org.wallet.domain.service.AuditService;
import org.wallet.domain.service.PlayerService;
import org.wallet.domain.service.TransactionService;

/**
 * The `WalletContextListener` class is a Servlet Context Listener responsible for initializing the
 * Wallet application when the web application starts.
 */
public class WalletContextListener implements ServletContextListener {

  /**
   * Returns a new `WalletApplication` with the necessary services and repositories configured.
   *
   * @return A new `WalletApplication` instance.
   */
  private static WalletApplication getWalletApplication() {
    var connection = new DatabaseConnection();

    var liquibaseManager = new LiquibaseManager(connection);
    liquibaseManager.migrate();

    var transactionRepository = new JdbcTransactionRepository(connection);
    var transactionService = new TransactionService(transactionRepository);

    var playerRepository = new JdbcPlayerRepository(connection);
    var playerService = new PlayerService(playerRepository);

    var logRepository = new JdbcLogRepository(connection);
    var auditService = new AuditService(logRepository);

    return new WalletApplication(transactionService, playerService, auditService);
  }

  /**
   * Initializes the Wallet application and servlets when the Servlet Context is created.
   *
   * @param sce The `ServletContextEvent` fired during web application initialization.
   */
  @Override
  public void contextInitialized(ServletContextEvent sce) {
    var wallet = getWalletApplication();

    var objectMapper = new ObjectMapper();
    objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);

    var context = sce.getServletContext();
    context
        .addServlet("RegisterServlet", new RegisterServlet(wallet, objectMapper))
        .addMapping("/register");
    context.addServlet("LoginServlet", new LoginServlet(wallet, objectMapper)).addMapping("/login");
    context
        .addServlet("BalanceServlet", new BalanceServlet(wallet, objectMapper))
        .addMapping("/balance");
    context
        .addServlet("TransactionServlet", new TransactionServlet(wallet, objectMapper))
        .addMapping("/transaction");
    context
        .addServlet("TransactionsServlet", new TransactionsServlet(wallet, objectMapper))
        .addMapping("/transactions");
    context.addServlet("AuditServlet", new AuditServlet(wallet, objectMapper)).addMapping("/audit");

    System.out.println("Web application is starting...");
  }
}
