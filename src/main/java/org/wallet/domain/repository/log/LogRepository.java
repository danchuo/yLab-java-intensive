package org.wallet.domain.repository.log;

import java.util.List;
import org.wallet.domain.model.Log;

/**
 * The `LogRepository` interface defines methods for managing logs in a data store. Implementing
 * classes should provide functionality to retrieve logs and add new log entries.
 */
public interface LogRepository {
  /**
   * Retrieves a list of log entries from the data store.
   *
   * @return A list of log entries.
   */
  List<Log> getLogs();

  /**
   * Adds a new log entry to the data store.
   *
   * @param log The log entry to be added.
   */
  void addLog(Log log);
}
