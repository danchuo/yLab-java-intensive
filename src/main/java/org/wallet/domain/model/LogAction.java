package org.wallet.domain.model;

import lombok.Getter;

/** The `LogAction` enum represents different actions that can be logged in the system. */
@Getter
public enum LogAction {
  /** Represents the action of user registration. */
  REGISTRATION("User registered."),

  /** Represents the action of user authorization (login). */
  AUTHORIZATION("User logged in."),

  /** Represents the action of registering a transaction. */
  TRANSACTION("Transaction registered");

  private final String details;

  /**
   * Creates a new `LogAction` with the given details.
   *
   * @param details Details of the log action.
   */
  LogAction(String details) {
    this.details = details;
  }
}
