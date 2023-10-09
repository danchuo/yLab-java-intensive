package org.wallet.repository;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import org.wallet.exception.PlayerAlreadyExistException;
import org.wallet.model.Player;

/**
 * The {@code InMemoryPlayerRepository} class is an in-memory implementation of the {@link
 * PlayerRepository} interface. It stores player data in a HashMap.
 */
public class InMemoryPlayerRepository implements PlayerRepository {

  private final HashMap<String, Player> players;

  /** Constructs a new InMemoryPlayerRepository with an empty player storage. */
  public InMemoryPlayerRepository() {
    players = new HashMap<>();
  }

  /**
   * Retrieves a list of all players stored in the repository.
   *
   * @return A list of players.
   */
  @Override
  public List<Player> getPlayers() {
    return List.copyOf(players.values());
  }

  /**
   * Retrieves a player by their login.
   *
   * @param login The login of the player to retrieve.
   * @return An optional containing the retrieved player, or an empty optional if not found.
   */
  @Override
  public Optional<Player> getPlayerByLogin(String login) {
    return Optional.ofNullable(players.get(login));
  }

  /**
   * Adds a new player to the repository.
   *
   * @param player The player to be added.
   * @throws PlayerAlreadyExistException If a player with the same login already exists.
   */
  @Override
  public void addPlayer(Player player) {
    if (players.containsKey(player.getLogin())) {
      throw new PlayerAlreadyExistException();
    }
    players.put(player.getLogin(), player);
  }
}
