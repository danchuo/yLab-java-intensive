package org.wallet.exception;

import java.io.Serial;

/**
 * The {@code PlayerNotFoundException} is an exception that is thrown when attempting to perform an
 * operation on a player that does not exist.
 */
public class PlayerNotFoundException extends IllegalArgumentException {
  @Serial private static final long serialVersionUID = 1090383338661679872L;

  /**
   * Creates a new instance of the {@code PlayerNotFoundException} class with a default error
   * message.
   */
  public PlayerNotFoundException() {
    super("Player not found.");
  }
}
