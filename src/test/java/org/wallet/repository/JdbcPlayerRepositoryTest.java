package org.wallet.repository;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import java.io.File;
import java.math.BigDecimal;
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
import org.wallet.domain.repository.DatabaseConnection;
import org.wallet.domain.repository.player.JdbcPlayerRepository;
import org.wallet.domain.repository.LiquibaseManager;
import org.wallet.domain.repository.player.PlayerRepository;
import org.wallet.exception.PlayerAlreadyExistException;
import org.wallet.domain.model.Player;

@Testcontainers
public class JdbcPlayerRepositoryTest {

  private static final int POSTGRES_PORT = 5432;

  @Container
  private static final DockerComposeContainer DOCKER_COMPOSE_CONTAINER =
      new DockerComposeContainer(new File("src/test/java/resources/docker-compose-test.yml"))
          .withExposedService("postgres", POSTGRES_PORT)
          .withLocalCompose(true)
          .withOptions("--compatibility");

  private static PlayerRepository playerRepository;
  private static DatabaseConnection connection;

  @BeforeAll
  public static void setUp() {
    String original =
        DOCKER_COMPOSE_CONTAINER.getServiceHost("postgres", POSTGRES_PORT)
            + ":"
            + DOCKER_COMPOSE_CONTAINER.getServicePort("postgres", POSTGRES_PORT);

    String result = "jdbc:postgresql://" + original + "/wallet";
    connection = new DatabaseConnection(result);
    playerRepository = new JdbcPlayerRepository(connection);
    var liquibase = new LiquibaseManager(connection);
    liquibase.migrate();
  }

  @BeforeEach
  public void setUpBeforeEach() {
    try (Connection local = connection.getConnection()) {
      Statement statement = local.createStatement();

      statement.executeUpdate("DELETE FROM wallet.players");

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Test
  @DisplayName("Empty repository should return an empty list of players")
  public void emptyRepository_getPlayers_returnsEmptyList() {
    List<Player> players = playerRepository.getPlayers();
    assertThat(players).isEmpty();
  }

  @Test
  @DisplayName("Adding a player and getting players should return the added player")
  public void addPlayerAndGetPlayers_playerAdded() {
    Player player = new Player("testLogin", "testPassword");
    playerRepository.addPlayer(player);

    List<Player> players = playerRepository.getPlayers();
    assertThat(players).hasSize(1).contains(player);
  }

  @Test
  @DisplayName("Getting a non-existent player by login should return an empty Optional")
  public void getPlayerByLogin_notFound_returnsEmptyOptional() {
    var player = playerRepository.getPlayerByLogin("nonExistentLogin");
    assertThat(player).isEmpty();
  }

  @Test
  @DisplayName("Getting an existent player by login should return an Optional with the player")
  public void getPlayerByLogin_found_returnsOptionalWithPlayer() {
    Player player = new Player("testLogin", "testPassword");
    playerRepository.addPlayer(player);

    var foundPlayer = playerRepository.getPlayerByLogin("testLogin");
    assertThat(foundPlayer).isPresent().contains(player);
  }

  @Test
  @DisplayName(
      "Adding a player with an already existing login should throw PlayerAlreadyExistException")
  public void addPlayerAlreadyExists_throwsPlayerAlreadyExistException() {
    Player player = new Player("testLogin", "testPassword");
    playerRepository.addPlayer(player);

    assertThatThrownBy(() -> playerRepository.addPlayer(player))
        .isInstanceOf(PlayerAlreadyExistException.class);
  }

  @Test
  @DisplayName("Player should exist if added")
  public void isPlayerExist_playerAdded_returnsTrue() {
    Player player = new Player("testLogin", "testPassword");
    playerRepository.addPlayer(player);

    assertThat(playerRepository.isPlayerExist("testLogin")).isTrue();
  }

  @Test
  @DisplayName("Player should not exist if not added")
  public void isPlayerExist_playerNotAdded_returnsFalse() {
    assertThat(playerRepository.isPlayerExist("nonExistentLogin")).isFalse();
  }

  @Test
  @DisplayName("Player balance should be updated")
  public void updatePlayerBalance_playerExists_balanceUpdated() {
    Player player = new Player("testLogin", "testPassword");
    playerRepository.addPlayer(player);

    player.credit(BigDecimal.TEN);

    playerRepository.updatePlayerBalance(player);

    Player updatedPlayer = playerRepository.getPlayerByLogin("testLogin").orElse(null);

    assertDoesNotThrow(
        () -> {
          assertThat(updatedPlayer)
              .isNotNull()
              .extracting(Player::getBalance)
              .isEqualTo(BigDecimal.TEN);
        });
  }
}
