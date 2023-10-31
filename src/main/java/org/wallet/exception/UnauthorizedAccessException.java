package org.wallet.exception;

import java.io.Serial;

/** An exception indicating that the user is not authorized to perform the requested operation. */
public class UnauthorizedAccessException extends IllegalArgumentException {

  @Serial private static final long serialVersionUID = -6452914812304209181L;

  /**
   * Constructs a new `UnauthorizedAccessException` with the default message "User is not
   * authorized."
   */
  public UnauthorizedAccessException() {
    super("An authorization error occurred.");
  }
}
