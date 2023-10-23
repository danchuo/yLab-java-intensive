package org.wallet.repository;

import static org.assertj.core.api.Assertions.*;

import java.io.File;
import java.sql.Connection;
import java.sql.Statement;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.DockerComposeContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.wallet.log.LogAction;
import org.wallet.model.Log;

@Testcontainers
public class JdbcLogRepositoryTest {

  private static final int POSTGRES_PORT = 5432;

  @Container
  private static final DockerComposeContainer DOCKER_COMPOSE_CONTAINER =
      new DockerComposeContainer(new File("src/test/java/resources/docker-compose-test.yml"))
          .withExposedService("postgres", POSTGRES_PORT)
          .withLocalCompose(true)
          .withOptions("--compatibility");

  private static DatabaseConnection connection;
  private static LogRepository logRepository;

  @BeforeAll
  public static void setUp() {
    String original =
        DOCKER_COMPOSE_CONTAINER.getServiceHost("postgres", POSTGRES_PORT)
            + ":"
            + DOCKER_COMPOSE_CONTAINER.getServicePort("postgres", POSTGRES_PORT);

    String result = "jdbc:postgresql://" + original + "/wallet";
    connection = new DatabaseConnection(result);
    logRepository = new JdbcLogRepository(connection);
    var liquibase = new LiquibaseManager(connection);
    liquibase.migrate();
  }

  @BeforeEach
  public void setUpBeforeEach() {
    try (Connection local = connection.getConnection()) {
      Statement statement = local.createStatement();

      statement.executeUpdate("DELETE FROM wallet.logs");

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Test
  @DisplayName("GetLogs for empty repository should return an empty list")
  public void getLogs_emptyRepository_returnsEmptyList() {
    List<Log> logs = logRepository.getLogs();
    assertThat(logs).isEmpty();
  }

  @Test
  @DisplayName("Adding a log and getting logs should return a list with the added log")
  public void addLogAndGetLogs_logAdded_returnsListWithAddedLog() {
    Log log = new Log(LogAction.AUTHORIZATION, "Test User", "Test Details");

    logRepository.addLog(log);

    List<Log> logs = logRepository.getLogs();
    assertThat(logs).hasSize(1);
  }
}
