package org.wallet.domain.repository.player;

import java.util.List;
import java.util.Optional;
import org.wallet.domain.model.Player;

/**
 * The {@code PlayerRepository} interface defines methods for managing player data. Implementations
 * of this interface are responsible for storing, retrieving, and querying player information by
 * login.
 */
public interface PlayerRepository {

  /**
   * Retrieves a list of all players stored in the repository.
   *
   * @return A list of players.
   */
  List<Player> getPlayers();

  /**
   * Retrieves a player by their login.
   *
   * @param login The login of the player to retrieve.
   * @return An optional containing the retrieved player, or an empty optional if not found.
   */
  Optional<Player> getPlayerByLogin(String login);

  /**
   * Adds a new player to the repository.
   *
   * @param player The player to be added.
   */
  void addPlayer(Player player);

  /**
   * Updates the balance of a player in the data store.
   *
   * @param player The player whose balance needs to be updated.
   */
  void updatePlayerBalance(Player player);

  /**
   * Checks whether a player with the given login exists in the data store.
   *
   * @param login The login of the player to check for existence.
   * @return `true` if the player exists; otherwise, `false`.
   */
  boolean isPlayerExist(String login);
}
