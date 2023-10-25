package org.wallet.domain.dto.response;

import lombok.Data;

/**
 * The {@code ErrorResponce} class represents a Data Transfer Object (DTO) for providing error
 * information. It includes an error code and an error message to describe the error.
 */
@Data
public class ErrorResponce {
  /** The error code associated with the error. */
  private int code;

  /** The error message providing details about the error. */
  private String message;

  /**
   * Creates a new instance of the {@code ErrorResponce} class with the specified error code and
   * error message.
   *
   * @param code The error code to set.
   * @param message The error message to set.
   */
  public ErrorResponce(int code, String message) {
    this.code = code;
    this.message = message;
  }
}
