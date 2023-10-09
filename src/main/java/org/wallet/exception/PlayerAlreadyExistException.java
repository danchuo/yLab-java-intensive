package org.wallet.exception;

import java.io.Serial;

/**
 * The {@code PlayerAlreadyExistException} is an exception that is thrown when attempting to create
 * a player with a login that already exists.
 */
public class PlayerAlreadyExistException extends IllegalArgumentException {
  @Serial private static final long serialVersionUID = -6706477305081508627L;

  /**
   * Creates a new instance of the {@code PlayerAlreadyExistException} class with a default error
   * message.
   */
  public PlayerAlreadyExistException() {
    super("A player with this login already exists.");
  }
}
