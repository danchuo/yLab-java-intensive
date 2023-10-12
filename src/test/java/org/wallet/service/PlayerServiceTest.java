package org.wallet.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.wallet.exception.PlayerAlreadyExistException;
import org.wallet.model.Player;
import org.wallet.repository.PlayerRepository;
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
  public void isPlayerExist_existingPlayer_shouldReturnTrue() {
    when(playerRepository.getPlayerByLogin(TEST_USER))
        .thenReturn(Optional.of(new Player(TEST_USER, "hashedPassword")));

    boolean result = playerService.isPlayerExist(TEST_USER);

    assertTrue(result);
  }

  @Test
  public void isPlayerExist_nonExistingPlayer_shouldReturnFalse() {
    when(playerRepository.getPlayerByLogin(TEST_USER)).thenReturn(Optional.empty());

    boolean result = playerService.isPlayerExist(TEST_USER);

    assertFalse(result);
  }

  @Test
  public void registerPlayer_validInput_shouldReturnRegisteredPlayer() {
    when(playerRepository.getPlayerByLogin(TEST_USER)).thenReturn(Optional.empty());

    Player registeredPlayer = playerService.registerPlayer(TEST_USER, TEST_PASSWORD);

    assertNotNull(registeredPlayer);
    assertEquals(TEST_USER, registeredPlayer.getLogin());
    assertNotEquals(TEST_PASSWORD, registeredPlayer.getPassword());
  }

  @Test
  public void registerPlayer_existingPlayer_shouldThrowPlayerAlreadyExistException() {
    when(playerRepository.getPlayerByLogin(TEST_USER))
        .thenReturn(Optional.of(new Player(TEST_USER, StringHasher.hashString(TEST_PASSWORD))));

    assertThrows(
        PlayerAlreadyExistException.class,
        () -> playerService.registerPlayer(TEST_USER, StringHasher.hashString(TEST_PASSWORD)));
  }

  @Test
  public void login_validCredentials_shouldReturnLoggedInPlayer() {
    String hashedPassword = StringHasher.hashString(TEST_PASSWORD);
    when(playerRepository.getPlayerByLogin(TEST_USER))
        .thenReturn(Optional.of(new Player(TEST_USER, hashedPassword)));

    Optional<Player> loggedInPlayer = playerService.login(TEST_USER, TEST_PASSWORD);

    assertTrue(loggedInPlayer.isPresent());
    assertEquals(TEST_USER, loggedInPlayer.get().getLogin());
  }

  @Test
  public void login_invalidPassword_shouldReturnEmptyOptional() {
    String hashedPassword = StringHasher.hashString("differentPassword");
    when(playerRepository.getPlayerByLogin(TEST_USER))
        .thenReturn(Optional.of(new Player(TEST_USER, hashedPassword)));

    Optional<Player> loggedInPlayer = playerService.login(TEST_USER, TEST_PASSWORD);

    assertTrue(loggedInPlayer.isEmpty());
  }

  @Test
  public void login_nonExistingPlayer_shouldReturnEmptyOptional() {
    when(playerRepository.getPlayerByLogin(TEST_USER)).thenReturn(Optional.empty());

    Optional<Player> loggedInPlayer = playerService.login(TEST_USER, TEST_PASSWORD);

    assertTrue(loggedInPlayer.isEmpty());
  }
}
