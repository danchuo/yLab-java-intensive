package org.wallet.domain.repository.log;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.wallet.domain.repository.DatabaseConnection;
import org.wallet.domain.model.LogAction;
import org.wallet.domain.model.Log;

/**
 * The `JdbcLogRepository` class is an implementation of the `LogRepository` interface. It provides
 * methods for retrieving logs and adding new logs to a relational database.
 */
@RequiredArgsConstructor
@Repository
public class JdbcLogRepository implements LogRepository {
  /** SQL query to select all logs from the database. */
  private static final String SELECT_ALL_LOGS_SQL = "SELECT * FROM wallet.logs";

  /** SQL query to insert a new log entry into the database. */
  private static final String INSERT_LOG_SQL =
      "INSERT INTO wallet.logs (timestamp, action, username, details) VALUES (?, ?, ?, ?)";

  /** The `DatabaseConnection` used to establish a connection to the database. */
  private final DatabaseConnection databaseConnection;

  /**
   * Retrieves a list of log entries from the database.
   *
   * @return A list of log entries.
   */
  @Override
  public List<Log> getLogs() {
    List<Log> logs = new ArrayList<>();
    try (Connection connection = databaseConnection.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(SELECT_ALL_LOGS_SQL);
        ResultSet resultSet = preparedStatement.executeQuery()) {
      while (resultSet.next()) {
        LocalDateTime timestamp = resultSet.getTimestamp("timestamp").toLocalDateTime();
        String action = resultSet.getString("action");
        LogAction actionType = LogAction.valueOf(action);
        String username = resultSet.getString("username");
        String details = resultSet.getString("details");

        Log log = new Log(timestamp, actionType, username, details);
        logs.add(log);
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return logs;
  }

  /**
   * Adds a new log entry to the database.
   *
   * @param log The log entry to be added.
   */
  @Override
  public void addLog(Log log) {
    try (Connection connection = databaseConnection.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(INSERT_LOG_SQL)) {
      preparedStatement.setTimestamp(1, Timestamp.valueOf(log.getTimestamp()));
      preparedStatement.setString(2, log.getAction().toString());
      preparedStatement.setString(3, log.getUsername());
      preparedStatement.setString(4, log.getDetails());

      preparedStatement.executeUpdate();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }
}
