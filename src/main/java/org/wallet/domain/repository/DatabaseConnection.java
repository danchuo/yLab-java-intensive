package org.wallet.domain.repository;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.wallet.domain.repository.player.JdbcPlayerRepository;

/**
 * The `DatabaseConnection` class represents a database connection manager. It provides methods to
 * establish a connection to a relational database.
 */
@Component
@RequiredArgsConstructor
public class DatabaseConnection {

  /** The username for the database connection. */
  private static String JDBC_USER;

  /** The URL for the JDBC database connection. */
  private static String JDBC_URL;

  /** The password for the database connection. */
  private static String JDBC_PASSWORD;

  static {
    try {
      Class.forName("org.postgresql.Driver");
      Properties properties = new Properties();
      ClassLoader classLoader = JdbcPlayerRepository.class.getClassLoader();
      InputStream inputStream = classLoader.getResourceAsStream("application.yml");
      properties.load(inputStream);
      JDBC_URL = properties.getProperty("url");
      JDBC_USER = properties.getProperty("username");
      JDBC_PASSWORD = properties.getProperty("password");
    } catch (Exception ignored) {
    }
  }

  /**
   * Constructor for creating a `DatabaseConnection` object with a custom JDBC URL.
   *
   * @param url The custom JDBC URL for the database connection.
   */
  public DatabaseConnection(String url) {
    JDBC_URL = url;
  }

  /**
   * Establishes a database connection using the stored `JDBC_URL`, `JDBC_USER`, and `JDBC_PASSWORD`
   * fields.
   *
   * @return A `Connection` object representing the database connection.
   * @throws SQLException if a database access error occurs.
   */
  public Connection getConnection() throws SQLException {
    return DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);
  }
}
