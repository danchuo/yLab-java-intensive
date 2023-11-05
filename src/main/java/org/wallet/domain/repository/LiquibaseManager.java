package org.wallet.domain.repository;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.util.Properties;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.ClassLoaderResourceAccessor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.wallet.domain.repository.player.JdbcPlayerRepository;

/**
 * The `LiquibaseManager` class is responsible for managing database schema changes using Liquibase.
 * It allows you to migrate and update the database schema based on the specified change log file.
 */
@Component
@RequiredArgsConstructor
public class LiquibaseManager {

  private static String CHANGE_LOG_FILE;
  private static String LIQUIBASE_SCHEMA_NAME;
  private static String DEFAULT_SCHEMA_NAME;

  static {
    try {
      Properties properties = new Properties();
      ClassLoader classLoader = JdbcPlayerRepository.class.getClassLoader();
      InputStream inputStream = classLoader.getResourceAsStream("application.yml");
      properties.load(inputStream);
      CHANGE_LOG_FILE = properties.getProperty("changelog-file");
      LIQUIBASE_SCHEMA_NAME = properties.getProperty("liquibase-schema");
      DEFAULT_SCHEMA_NAME = properties.getProperty("default-schema");
    } catch (IOException ignored) {
    }
  }

  /** The `DatabaseConnection` used for establishing a connection to the database. */
  private final DatabaseConnection databaseConnection;

  /**
   * Migrates the database schema using Liquibase, applying changes defined in the change log file.
   */
  @PostConstruct
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
