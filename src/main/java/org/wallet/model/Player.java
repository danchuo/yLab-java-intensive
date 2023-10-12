package org.wallet.model;

import org.wallet.exception.InsufficientMoneyException;

/**
 * The {@code Player} class represents a player in a financial application. It includes information
 * such as the player's login, password, and balance.
 */
public class Player {

  private final String password;
  private final String login;
  private long balance;

  /**
   * Constructs a new player with the specified login and password.
   *
   * @param login The login of the player.
   * @param password The password of the player.
   */
  public Player(String login, String password) {
    this.password = password;
    this.login = login;
    balance = 0;
  }

  /**
   * Gets the password of the player.
   *
   * @return The password of the player.
   */
  public String getPassword() {
    return password;
  }

  /**
   * Gets the login of the player.
   *
   * @return The login of the player.
   */
  public String getLogin() {
    return login;
  }

  /**
   * Gets the balance of the player.
   *
   * @return The balance of the player.
   */
  public long getBalance() {
    return balance;
  }

  /**
   * Checks if the player can debit the specified amount from their balance.
   *
   * @param amount The amount to be debited.
   * @return {@code true} if the player can debit the specified amount, {@code false} otherwise.
   */
  public boolean canDebit(long amount) {
    return balance >= amount;
  }

  /**
   * Debits the specified amount from the player's balance.
   *
   * @param amount The amount to be debited.
   * @throws InsufficientMoneyException If the player's balance is insufficient for the debit.
   */
  public void debit(long amount) {
    if (canDebit(amount)) {
      balance -= amount;
    } else {
      throw new InsufficientMoneyException();
    }
  }

  /**
   * Credits the specified amount to the player's balance.
   *
   * @param amount The amount to be credited.
   */
  public void credit(long amount) {
    balance += amount;
  }
}
