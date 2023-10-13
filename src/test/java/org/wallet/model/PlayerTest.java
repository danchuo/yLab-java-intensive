package org.wallet.model;

import static org.assertj.core.api.Assertions.*;
import static org.wallet.utils.BigDecimalUtils.fromLong;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.wallet.exception.InsufficientMoneyException;

public class PlayerTest {

  private Player player;

  @BeforeEach
  public void setUp() {
    player = new Player("testUser", "testPassword");
  }

  @Test
  @DisplayName("Get Password should return password")
  public void getPassword_shouldReturnPassword() {
    assertThat(player.getPassword()).isEqualTo("testPassword");
  }

  @Test
  @DisplayName("Get Login should return login")
  public void getLogin_shouldReturnLogin() {
    assertThat(player.getLogin()).isEqualTo("testUser");
  }

  @Test
  @DisplayName("Get Balance should return initial balance of zero")
  public void getBalance_initialBalanceShouldBeZero() {
    assertThat(player.getBalance()).isZero();
  }

  @Test
  @DisplayName("Can Debit with sufficient balance should return true")
  public void canDebit_withSufficientBalance_shouldReturnTrue() {
    player.credit(fromLong(100));
    assertThat(player.canDebit(fromLong(50))).isTrue();
  }

  @Test
  @DisplayName("Can Debit with insufficient balance should return false")
  public void canDebit_withInsufficientBalance_shouldReturnFalse() {
    assertThat(player.canDebit(fromLong(50))).isFalse();
  }

  @Test
  @DisplayName("Debit with sufficient balance should update balance")
  public void debit_withSufficientBalance_shouldUpdateBalance() {
    player.credit(fromLong(100));
    player.debit(fromLong(50));
    assertThat(player.getBalance()).isEqualTo(fromLong(50));
  }

  @Test
  @DisplayName("Debit with insufficient balance should throw InsufficientMoneyException")
  public void debit_withInsufficientBalance_shouldThrowInsufficientMoneyException() {
    assertThatThrownBy(() -> player.debit(fromLong(50)))
        .isInstanceOf(InsufficientMoneyException.class);
  }

  @Test
  @DisplayName("Credit should update balance")
  public void credit_shouldUpdateBalance() {
    player.credit(fromLong(100));
    assertThat(player.getBalance()).isEqualTo(fromLong(100));
  }
}
