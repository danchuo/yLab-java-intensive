package org.wallet.model;

import java.math.BigDecimal;
import lombok.Data;
import org.wallet.exception.InsufficientMoneyException;

/**
 * The {@code Player} class represents a player in a financial application. It includes information
 * such as the player's login, password, and balance.
 */
@Data
public class Player {

  private final String password;
  private final String login;
  private BigDecimal balance;

  /**
   * Constructs a new player with the specified login and password.
   *
   * @param login The login of the player.
   * @param password The password of the player.
   */
  public Player(String login, String password) {
    this.password = password;
    this.login = login;
    balance = BigDecimal.valueOf(0);
  }

  /**
   * Checks if the player can debit the specified amount from their balance.
   *
   * @param amount The amount to be debited.
   * @return {@code true} if the player can debit the specified amount, {@code false} otherwise.
   */
  public boolean canDebit(BigDecimal amount) {
    return balance.compareTo(amount) >= 0;
  }

  /**
   * Debits the specified amount from the player's balance.
   *
   * @param amount The amount to be debited.
   * @throws InsufficientMoneyException If the player's balance is insufficient for the debit.
   */
  public void debit(BigDecimal amount) {
    if (canDebit(amount)) {
      balance = balance.subtract(amount);
    } else {
      throw new InsufficientMoneyException();
    }
  }

  /**
   * Credits the specified amount to the player's balance.
   *
   * @param amount The amount to be credited.
   */
  public void credit(BigDecimal amount) {
    balance = balance.add(amount);
  }
}
