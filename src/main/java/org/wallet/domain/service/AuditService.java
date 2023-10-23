package org.wallet.domain.service;

import java.util.Collections;
import java.util.List;
import org.wallet.domain.model.LogAction;
import org.wallet.domain.model.Log;
import org.wallet.domain.repository.log.LogRepository;

/**
 * The {@code AuditService} class is responsible for logging audit actions. It can log various types
 * of actions, such as authorization, debit, credit, and exit. Log messages are stored in a
 * collection for later retrieval.
 */
public class AuditService {

  /** Collection to store log messages. */
  private final LogRepository logRepository;

  public AuditService(LogRepository logRepository) {
    this.logRepository = logRepository;
  }

  /**
   * Logs an audit action with the specified parameters.
   *
   * @param action The type of audit action (e.g., AUTHORIZATION, DEBIT, CREDIT, EXIT).
   * @param username The username associated with the action.
   * @param details Additional details or information about the action.
   */
  public void log(LogAction action, String username, String details) {
    Log log = new Log(action, username, details);
    logRepository.addLog(log);
  }

  /**
   * Get the list of log messages.
   *
   * @return List of log messages.
   */
  public List<Log> getLogMessages() {
    return Collections.unmodifiableList(logRepository.getLogs());
  }
}
