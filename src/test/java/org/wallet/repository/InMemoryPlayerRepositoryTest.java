package org.wallet.repository;

import static org.assertj.core.api.Assertions.*;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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
    @DisplayName("Empty repository should return an empty list of players")
    public void emptyRepository_getPlayers_returnsEmptyList() {
        List<Player> players = playerRepository.getPlayers();
        assertThat(players).isEmpty();
    }

    @Test
    @DisplayName("Adding a player to the repository should store the player")
    public void addPlayerAndGetPlayers_playerAdded() {
        Player player = new Player("testLogin", "testPassword");
        playerRepository.addPlayer(player);

        List<Player> players = playerRepository.getPlayers();
        assertThat(players).hasSize(1);
        assertThat(players).contains(player);
    }

    @Test
    @DisplayName("Getting a player by login not found should return an empty Optional")
    public void getPlayerByLogin_notFound_returnsEmptyOptional() {
        Optional<Player> player = playerRepository.getPlayerByLogin("nonExistentLogin");
        assertThat(player).isEmpty();
    }

    @Test
    @DisplayName("Getting a player by login found should return an Optional with the player")
    public void getPlayerByLogin_found_returnsOptionalWithPlayer() {
        Player player = new Player("testLogin", "testPassword");
        playerRepository.addPlayer(player);

        Optional<Player> foundPlayer = playerRepository.getPlayerByLogin("testLogin");
        assertThat(foundPlayer).isPresent();
        assertThat(foundPlayer).hasValue(player);
    }

    @Test
    @DisplayName("Adding a player that already exists should throw PlayerAlreadyExistException")
    public void addPlayerAlreadyExists_throwsPlayerAlreadyExistException() {
        Player player = new Player("testLogin", "testPassword");
        playerRepository.addPlayer(player);

        assertThatThrownBy(() -> playerRepository.addPlayer(player))
                .isInstanceOf(PlayerAlreadyExistException.class);
    }
}
