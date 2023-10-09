package org.wallet.exception;

import java.io.Serial;

/**
 * The {@code InsufficientMoneyException} is an exception that is thrown when there are
 * insufficient funds for a debit transaction.
 */
public class InsufficientMoneyException extends IllegalArgumentException {
  @Serial private static final long serialVersionUID = -2503366394743427892L;

  /**
   * Creates a new instance of the {@code InsufficientMoneyException} class with a default error
   * message.
   */
  public InsufficientMoneyException() {
    super("Insufficient funds for a debit transaction.");
  }
}
