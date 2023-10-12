package org.wallet.service;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;
import org.wallet.log.LogAction;

/**
 * The `AuditService` class provides logging functionality for auditing actions such as
 * authorization, debit, credit, and logout.
 */
public class AuditService {

  /** The logger for auditing actions. */
  private static final Logger LOGGER = LogManager.getLogger(AuditService.class);

  static {
    try {
      Configurator.initialize(null, "log4j2.xml");
      Runtime.getRuntime().addShutdownHook(new Thread(() -> {}));
    } catch (Exception ignored) {
    }
  }

  /**
   * Logs an audit action with the specified parameters.
   *
   * @param action The type of audit action (e.g., AUTHORIZATION, DEBIT, CREDIT, EXIT).
   * @param username The username associated with the action.
   * @param details Additional details or information about the action.
   */
  public void log(LogAction action, String username, String details) {
    String message =
        switch (action) {
          case AUTHORIZATION -> "User " + username + " logged in.";
          case DEBIT -> "User " + username + " performed a debit transaction.";
          case CREDIT -> "User " + username + " performed a credit transaction.";
          case EXIT -> "User " + username + " logged out.";
        };

    if (message != null) {
      LOGGER.log(Level.INFO, message, new Object[] {username, details});
    }
  }
}
