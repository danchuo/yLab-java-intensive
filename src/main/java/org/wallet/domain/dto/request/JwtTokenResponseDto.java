package org.wallet.domain.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

/**
 * The {@code JwtTokenResponseDto} class represents a response DTO (Data Transfer Object) for a JWT
 * (JSON Web Token) token. It includes a field to hold the JWT token and an {@code isValid} method
 * for validation.
 */
@Data
public class JwtTokenResponseDto implements Validator {
  private String jwtToken;

  /** Default constructor for JwtTokenResponseDto. */
  public JwtTokenResponseDto() {}

  /**
   * Constructs a JwtTokenResponseDto with the provided JWT token.
   *
   * @param jwtToken The JWT token to set in the response DTO.
   */
  public JwtTokenResponseDto(String jwtToken) {
    this.jwtToken = jwtToken;
  }

  /**
   * Checks the validity of the JwtTokenResponseDto. It is considered valid if the JWT token is not
   * null.
   *
   * @return {@code true} if the JWT token is not null, indicating validity; otherwise, {@code
   *     false}.
   */
  @Override
  @JsonIgnore
  public boolean isValid() {
    return jwtToken != null;
  }
}
