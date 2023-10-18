package org.wallet.repository;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.wallet.exception.PlayerAlreadyExistException;
import org.wallet.model.Player;

/**
 * The `JdbcPlayerRepository` class is an implementation of the `PlayerRepository` interface. It
 * provides methods for interacting with player data in a relational database.
 */
public class JdbcPlayerRepository implements PlayerRepository {

  private static final String SELECT_ALL_PLAYERS_SQL = "SELECT * FROM wallet.players";
  private static final String SELECT_PLAYER_BY_LOGIN_SQL =
      "SELECT * FROM wallet.players WHERE login = ? LIMIT 1";
  private static final String INSERT_PLAYER_SQL =
      "INSERT INTO wallet.players (login, balance, password) VALUES (?, ?, ?)";
  private static final String UPDATE_PLAYER_BALANCE_SQL =
      "UPDATE wallet.players SET balance = ? WHERE login = ?";
  private static final String CHECK_PLAYER_BY_LOGIN_SQL =
      "SELECT CASE WHEN EXISTS (SELECT 1 FROM wallet.players WHERE login = ?) THEN true ELSE false END;";

  /** The `DatabaseConnection` used to establish a connection to the database. */
  private final DatabaseConnection databaseConnection;

  /**
   * Constructs a new `JdbcPlayerRepository` with the provided `DatabaseConnection`.
   *
   * @param databaseConnection The database connection to be used for player data access.
   */
  public JdbcPlayerRepository(DatabaseConnection databaseConnection) {
    this.databaseConnection = databaseConnection;
  }

  /**
   * Retrieves a list of all players from the database.
   *
   * @return A list of player entities.
   */
  @Override
  public List<Player> getPlayers() {
    List<Player> players = new ArrayList<>();
    try (Connection connection = databaseConnection.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(SELECT_ALL_PLAYERS_SQL);
        ResultSet resultSet = preparedStatement.executeQuery()) {
      while (resultSet.next()) {
        String login = resultSet.getString("login");
        BigDecimal balance = resultSet.getBigDecimal("balance");
        String password = resultSet.getString("password");
        var player = new Player(login, password);
        player.credit(balance);
        players.add(player);
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return players;
  }

  /**
   * Retrieves a player from the database by their login.
   *
   * @param login The login of the player to retrieve.
   * @return An `Optional` containing the player if found, or empty if not found.
   */
  @Override
  public Optional<Player> getPlayerByLogin(String login) {
    try (Connection connection = databaseConnection.getConnection();
        PreparedStatement preparedStatement =
            connection.prepareStatement(SELECT_PLAYER_BY_LOGIN_SQL)) {
      preparedStatement.setString(1, login);
      try (ResultSet resultSet = preparedStatement.executeQuery()) {
        if (resultSet.next()) {
          BigDecimal balance = resultSet.getBigDecimal("balance");
          String password = resultSet.getString("password");
          var player = new Player(login, password);
          player.credit(balance);
          return Optional.of(player);
        }
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return Optional.empty();
  }

  /**
   * Checks if a player with the given login exists in the database.
   *
   * @param login The login of the player to check.
   * @return `true` if the player exists, `false` otherwise.
   */
  @Override
  public boolean isPlayerExist(String login) {
    try (Connection connection = databaseConnection.getConnection();
        PreparedStatement preparedStatement =
            connection.prepareStatement(CHECK_PLAYER_BY_LOGIN_SQL)) {
      preparedStatement.setString(1, login);
      try (ResultSet resultSet = preparedStatement.executeQuery()) {
        return resultSet.next() && resultSet.getBoolean(1);
      }
    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
  }

  /**
   * Adds a new player to the database.
   *
   * @param player The player entity to be added.
   * @throws PlayerAlreadyExistException if a player with the same login already exists.
   */
  @Override
  public void addPlayer(Player player) {
    if (isPlayerExist(player.getLogin())) {
      throw new PlayerAlreadyExistException();
    }

    try (Connection connection = databaseConnection.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(INSERT_PLAYER_SQL)) {
      preparedStatement.setString(1, player.getLogin());
      preparedStatement.setBigDecimal(2, player.getBalance());
      preparedStatement.setString(3, player.getPassword());
      preparedStatement.executeUpdate();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  /**
   * Updates the balance of a player in the database.
   *
   * @param player The player entity with the updated balance.
   */
  @Override
  public void updatePlayerBalance(Player player) {
    try (Connection connection = databaseConnection.getConnection();
        PreparedStatement preparedStatement =
            connection.prepareStatement(UPDATE_PLAYER_BALANCE_SQL)) {
      preparedStatement.setBigDecimal(1, player.getBalance());
      preparedStatement.setString(2, player.getLogin());
      preparedStatement.executeUpdate();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }
}
