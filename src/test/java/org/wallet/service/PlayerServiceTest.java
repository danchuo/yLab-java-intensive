package org.wallet.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.wallet.domain.service.PlayerService;
import org.wallet.exception.PlayerAlreadyExistException;
import org.wallet.domain.model.Player;
import org.wallet.domain.repository.player.PlayerRepository;
import org.wallet.utils.StringHasher;

public class PlayerServiceTest {

  private static final String TEST_USER = "testUser";
  private static final String TEST_PASSWORD = "testPassword";
  private PlayerService playerService;
  private PlayerRepository playerRepository;

  @BeforeEach
  public void setUp() {
    playerRepository = mock(PlayerRepository.class);
    playerService = new PlayerService(playerRepository);
  }

  @Test
  @DisplayName("Checking if an existing player exists should return true")
  public void isPlayerExist_existingPlayer_shouldReturnTrue() {
    when(playerRepository.isPlayerExist(TEST_USER)).thenReturn(true);

    boolean result = playerService.isPlayerExist(TEST_USER);

    assertThat(result).isTrue();
  }

  @Test
  @DisplayName("Checking if a non-existing player exists should return false")
  public void isPlayerExist_nonExistingPlayer_shouldReturnFalse() {
    when(playerRepository.isPlayerExist(TEST_USER)).thenReturn(false);

    boolean result = playerService.isPlayerExist(TEST_USER);

    assertThat(result).isFalse();
  }

  @Test
  @DisplayName("Registering a player with valid input should return the registered player")
  public void registerPlayer_validInput_shouldReturnRegisteredPlayer() {
    when(playerRepository.getPlayerByLogin(TEST_USER)).thenReturn(Optional.empty());

    Player registeredPlayer = playerService.registerPlayer(TEST_USER, TEST_PASSWORD);

    assertThat(registeredPlayer)
        .isNotNull()
        .extracting(Player::getLogin, Player::getPassword)
        .containsExactly(TEST_USER, StringHasher.hashString(TEST_PASSWORD));
  }

  @Test
  @DisplayName("Registering an existing player should throw PlayerAlreadyExistException")
  public void registerPlayer_existingPlayer_shouldThrowPlayerAlreadyExistException() {
    when(playerRepository.isPlayerExist(TEST_USER)).thenReturn(true);

    assertThatThrownBy(
            () -> playerService.registerPlayer(TEST_USER, StringHasher.hashString(TEST_PASSWORD)))
        .isInstanceOf(PlayerAlreadyExistException.class);
  }

  @Test
  @DisplayName("Logging in with valid credentials should return the logged-in player")
  public void login_validCredentials_shouldReturnLoggedInPlayer() {
    String hashedPassword = StringHasher.hashString(TEST_PASSWORD);
    when(playerRepository.getPlayerByLogin(TEST_USER))
        .thenReturn(Optional.of(new Player(TEST_USER, hashedPassword)));

    Optional<Player> loggedInPlayer = playerService.login(TEST_USER, TEST_PASSWORD);

    assertThat(loggedInPlayer).isPresent().map(Player::getLogin).isEqualTo(Optional.of(TEST_USER));
  }

  @Test
  @DisplayName("Logging in with an invalid password should return an empty Optional")
  public void login_invalidPassword_shouldReturnEmptyOptional() {
    String hashedPassword = StringHasher.hashString("differentPassword");
    when(playerRepository.getPlayerByLogin(TEST_USER))
        .thenReturn(Optional.of(new Player(TEST_USER, hashedPassword)));

    Optional<Player> loggedInPlayer = playerService.login(TEST_USER, TEST_PASSWORD);

    assertThat(loggedInPlayer).isEmpty();
  }

  @Test
  @DisplayName("Logging in with a non-existing player should return an empty Optional")
  public void login_nonExistingPlayer_shouldReturnEmptyOptional() {
    when(playerRepository.getPlayerByLogin(TEST_USER)).thenReturn(Optional.empty());

    Optional<Player> loggedInPlayer = playerService.login(TEST_USER, TEST_PASSWORD);

    assertThat(loggedInPlayer).isEmpty();
  }

  @Test
  @DisplayName("Update player balance")
  void testUpdatePlayer() {
    Player player = new Player(TEST_USER, TEST_PASSWORD);
    when(playerRepository.getPlayerByLogin(TEST_USER)).thenReturn(Optional.of(player));

    player.credit(BigDecimal.TEN);
    playerService.updatePlayer(player);

    assertThat(player.getBalance()).isEqualTo(BigDecimal.TEN);
    verify(playerRepository, times(1)).updatePlayerBalance(player);
  }
}
