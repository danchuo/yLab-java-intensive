package org.wallet.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.wallet.exception.InsufficientMoneyException;

public class PlayerTest {

  private Player player;

  @BeforeEach
  public void setUp() {
    player = new Player("testUser", "testPassword");
  }

  @Test
  public void getPassword_shouldReturnPassword() {
    assertEquals("testPassword", player.getPassword());
  }

  @Test
  public void getLogin_shouldReturnLogin() {
    assertEquals("testUser", player.getLogin());
  }

  @Test
  public void getBalance_initialBalanceShouldBeZero() {
    assertEquals(0, player.getBalance());
  }

  @Test
  public void canDebit_withSufficientBalance_shouldReturnTrue() {
    player.credit(100);
    assertTrue(player.canDebit(50));
  }

  @Test
  public void canDebit_withInsufficientBalance_shouldReturnFalse() {
    assertFalse(player.canDebit(50));
  }

  @Test
  public void debit_withSufficientBalance_shouldUpdateBalance() {
    player.credit(100);
    player.debit(50);
    assertEquals(50, player.getBalance());
  }

  @Test
  public void debit_withInsufficientBalance_shouldThrowInsufficientMoneyException() {
    assertThrows(InsufficientMoneyException.class, () -> player.debit(50));
  }

  @Test
  public void credit_shouldUpdateBalance() {
    player.credit(100);
    assertEquals(100, player.getBalance());
  }
}
