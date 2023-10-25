package org.wallet.domain.dto.response;

import lombok.Data;

/**
 * The {@code LogResponseDto} class represents a Data Transfer Object (DTO) for providing log
 * information. It includes fields to describe a log entry, such as the log action, timestamp,
 * username, and details.
 */
@Data
public class LogResponseDto {
  /** The action associated with the log entry. */
  private String action;

  /** The timestamp when the log entry was recorded. */
  private String timestamp;

  /** The username or user associated with the log entry. */
  private String username;

  /** Additional details or information related to the log entry. */
  private String details;
}
