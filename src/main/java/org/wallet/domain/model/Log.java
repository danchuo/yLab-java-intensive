package org.wallet.domain.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import lombok.Data;

/** The `Log` class represents information about system logs. */
@Data
public class Log {
  /** Date and time of the log. */
  private final LocalDateTime timestamp;

  /** Action associated with the log (e.g., "login" or "logout"). */
  private final LogAction action;

  /** User's name associated with the log. */
  private final String username;

  /** Additional details or description of the log. */
  private final String details;

  /**
   * Constructor for creating a `Log` object with specified field values.
   *
   * @param timestamp Date and time of the log.
   * @param action Action associated with the log.
   * @param username User's name associated with the log.
   * @param details Additional log details.
   */
  public Log(LocalDateTime timestamp, LogAction action, String username, String details) {
    this.timestamp = timestamp;
    this.action = action;
    this.username = username;
    this.details = details;
  }

  /**
   * Constructor for creating a `Log` object with the current date and time.
   *
   * @param action Action associated with the log.
   * @param username User's name associated with the log.
   * @param details Additional log details.
   */
  public Log(LogAction action, String username, String details) {
    this(LocalDateTime.now(), action, username, details);
  }

  /**
   * Returns the textual representation of the log.
   *
   * @return Textual representation of the log in the format "yyyy-MM-dd HH:mm:ss Action: User
   *     Username Details".
   */
  public String toString() {
    return timestamp.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
        + " "
        + action
        + ": "
        + "User "
        + username
        + " "
        + details
        + "\n";
  }
}
