package org.wallet.exception;

import java.io.Serial;

/**
 * An exception indicating that the request data is invalid or does not meet the required criteria.
 */
public class InvalidRequestException extends IllegalArgumentException {
  @Serial private static final long serialVersionUID = 641630253982946318L;

  /**
   * Constructs a new `InvalidRequestException` with the default message "Data validation error."
   */
  public InvalidRequestException() {
    super("Data validation error.");
  }
}
