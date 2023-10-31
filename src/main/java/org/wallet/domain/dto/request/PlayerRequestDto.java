package org.wallet.domain.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

/**
 * The {@code PlayerRequestDto} class represents a Data Transfer Object (DTO) for player requests.
 * It includes fields for player login and password and an {@code isValid} method for validation.
 */
@Data
public class PlayerRequestDto implements Validator {
  private String login;
  private String password;

  /**
   * Checks the validity of the PlayerRequestDto. It is considered valid if both the login and
   * password fields are not null.
   *
   * @return {@code true} if both the login and password fields are not null, indicating validity;
   *     otherwise, {@code false}.
   */
  @Override
  @JsonIgnore
  public boolean isValid() {
    return login != null && !login.isEmpty() && password != null && !password.isEmpty();
  }
}
