package org.wallet.exception;

import java.io.Serial;

/**
 * The {@code TransactionAlreadyExistException} is an exception that is thrown when attempting to
 * add a transaction with a duplicate transaction ID, indicating that a transaction with the same ID
 * already exists.
 */
public class TransactionAlreadyExistException extends IllegalArgumentException {
  @Serial private static final long serialVersionUID = 7595982816828333760L;

  /**
   * Creates a new instance of the {@code TransactionAlreadyExistException} class with a default
   * error message.
   */
  public TransactionAlreadyExistException() {
    super("Transaction with the same ID already exists.");
  }
}
