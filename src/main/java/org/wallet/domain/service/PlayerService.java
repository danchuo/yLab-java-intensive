package org.wallet.domain.service;

import java.util.Optional;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.wallet.domain.model.Player;
import org.wallet.domain.repository.player.PlayerRepository;
import org.wallet.exception.PlayerAlreadyExistException;
import org.wallet.utils.StringHasher;

/**
 * The `PlayerService` class provides functionality to interact with player-related operations such
 * as player registration, login, and checking player existence.
 */
@Service
@RequiredArgsConstructor
public class PlayerService {

  /** The repository for managing players. */
  private final PlayerRepository playerRepository;

  /**
   * Checks if a player with the given login exists.
   *
   * @param login The login of the player to check.
   * @return `true` if the player exists, `false` otherwise.
   */
  public boolean isPlayerExist(String login) {
    return playerRepository.isPlayerExist(login);
  }

  /**
   * Registers a new player with the specified login and password. If a player with the same login
   * already exists, a `PlayerAlreadyExistException` is thrown. The password is hashed before
   * storing it in the player's information.
   *
   * @param login The login of the player to register.
   * @param password The password of the player to register.
   * @return The registered player.
   * @throws PlayerAlreadyExistException If a player with the same login already exists.
   */
  public Player registerPlayer(String login, String password) {
    if (isPlayerExist(login)) {
      throw new PlayerAlreadyExistException();
    }

    String hashedPassword = StringHasher.hashString(password);

    Player player = new Player(login, hashedPassword);

    playerRepository.addPlayer(player);

    return player;
  }

  /**
   * Attempts to log in a player with the specified login and password. If a player with the
   * provided login and password is found, the player is considered logged in, and an optional
   * containing the player is returned. If the login or password is incorrect or the player does not
   * exist, an empty optional is returned.
   *
   * @param login The login of the player to log in.
   * @param password The password of the player to log in.
   * @return An optional containing the logged-in player, or an empty optional if login fails.
   */
  public Optional<Player> login(String login, String password) {
    var player = playerRepository.getPlayerByLogin(login);

    if (player.isPresent()
        && StringHasher.hashString(password).equals(player.get().getPassword())) {
      return player;
    }

    return Optional.empty();
  }

  /**
   * Updates the player's balance in the data store.
   *
   * <p>This method is a convenience method for updating the balance of a player in the data store.
   *
   * @param player The player whose balance needs to be updated.
   */
  public void updatePlayer(Player player) {
    playerRepository.updatePlayerBalance(player);
  }

  /**
   * Retrieves a player from the repository based on the provided login.
   *
   * @param login The login of the player to retrieve.
   * @return An optional containing the player if found, or an empty optional if not found.
   */
  public Optional<Player> getPlayerByLogin(String login) {
    return playerRepository.getPlayerByLogin(login);
  }
}
