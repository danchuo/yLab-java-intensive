package org.wallet.repository;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * The `DatabaseConnection` class represents a database connection manager. It provides methods to
 * establish a connection to a relational database.
 */
public class DatabaseConnection {
  /** The URL for the JDBC database connection. */
  private static String JDBC_URL;

  /** The username for the database connection. */
  private static String JDBC_USER;

  /** The password for the database connection. */
  private static String JDBC_PASSWORD;

  static {
    try {
      Properties properties = new Properties();
      ClassLoader classLoader = JdbcPlayerRepository.class.getClassLoader();
      InputStream inputStream = classLoader.getResourceAsStream("application.properties");
      properties.load(inputStream);
      JDBC_URL = properties.getProperty("jdbc.url");
      JDBC_USER = properties.getProperty("jdbc.user");
      JDBC_PASSWORD = properties.getProperty("jdbc.password");
    } catch (IOException ignored) {
    }
  }

  /** Default constructor for creating a `DatabaseConnection` object. */
  public DatabaseConnection() {}

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
