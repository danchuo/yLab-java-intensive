package org.wallet.repository;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.util.Properties;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.ClassLoaderResourceAccessor;

/**
 * The `LiquibaseManager` class is responsible for managing database schema changes using Liquibase.
 * It allows you to migrate and update the database schema based on the specified change log file.
 */
public class LiquibaseManager {

  private static String CHANGE_LOG_FILE;
  private static String LIQUIBASE_SCHEMA_NAME;
  private static String DEFAULT_SCHEMA_NAME;

  static {
    try {
      Properties properties = new Properties();
      ClassLoader classLoader = JdbcPlayerRepository.class.getClassLoader();
      InputStream inputStream = classLoader.getResourceAsStream("application.properties");
      properties.load(inputStream);
      CHANGE_LOG_FILE = properties.getProperty("changeLogFile");
      LIQUIBASE_SCHEMA_NAME = properties.getProperty("liquibaseSchemaName");
      DEFAULT_SCHEMA_NAME = properties.getProperty("defaultSchemaName");
    } catch (IOException ignored) {
    }
  }

  /** The `DatabaseConnection` used for establishing a connection to the database. */
  private final DatabaseConnection databaseConnection;

  /**
   * Constructs a new `LiquibaseManager` with the provided `DatabaseConnection`.
   *
   * @param databaseConnection The database connection to be used for Liquibase operations.
   */
  public LiquibaseManager(DatabaseConnection databaseConnection) {
    this.databaseConnection = databaseConnection;
  }

  /**
   * Migrates the database schema using Liquibase, applying changes defined in the change log file.
   */
  public void migrate() {
    try (Connection connection = databaseConnection.getConnection()) {
      Database database =
          DatabaseFactory.getInstance()
              .findCorrectDatabaseImplementation(new JdbcConnection(connection));
      Liquibase liquibase =
          new Liquibase(CHANGE_LOG_FILE, new ClassLoaderResourceAccessor(), database);
      liquibase.getDatabase().setLiquibaseSchemaName(LIQUIBASE_SCHEMA_NAME);
      liquibase.getDatabase().setDefaultSchemaName(DEFAULT_SCHEMA_NAME);
      liquibase.update("");
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
