package org.wallet.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.wallet.exception.PlayerAlreadyExistException;
import org.wallet.model.Player;

public class InMemoryPlayerRepositoryTest {

    private PlayerRepository playerRepository;

    @BeforeEach
    public void setUp() {
        playerRepository = new InMemoryPlayerRepository();
    }

    @Test
    public void emptyRepository_getPlayers_returnsEmptyList() {
        List<Player> players = playerRepository.getPlayers();
        assertTrue(players.isEmpty());
    }

    @Test
    public void addPlayerAndGetPlayers_playerAdded() {
        Player player = new Player("testLogin", "testPassword");
        playerRepository.addPlayer(player);

        List<Player> players = playerRepository.getPlayers();
        assertEquals(1, players.size());
        assertEquals(player, players.get(0));
    }

    @Test
    public void getPlayerByLogin_notFound_returnsEmptyOptional() {
        Optional<Player> player = playerRepository.getPlayerByLogin("nonExistentLogin");
        assertFalse(player.isPresent());
    }

    @Test
    public void getPlayerByLogin_found_returnsOptionalWithPlayer() {
        Player player = new Player("testLogin", "testPassword");
        playerRepository.addPlayer(player);

        Optional<Player> foundPlayer = playerRepository.getPlayerByLogin("testLogin");
        assertTrue(foundPlayer.isPresent());
        assertEquals(player, foundPlayer.get());
    }

    @Test
    public void addPlayerAlreadyExists_throwsPlayerAlreadyExistException() {
        Player player = new Player("testLogin", "testPassword");
        playerRepository.addPlayer(player);

        assertThrows(PlayerAlreadyExistException.class, () -> playerRepository.addPlayer(player));
    }
}
