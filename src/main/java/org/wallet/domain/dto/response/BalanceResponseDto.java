package org.wallet.domain.dto.response;

import java.math.BigDecimal;
import lombok.Data;

/**
 * The {@code BalanceResponseDto} class represents a Data Transfer Object (DTO) for providing
 * balance information. It includes the balance amount of a player's account.
 */
@Data
public class BalanceResponseDto {
  /** The balance amount of a player's account. */
  private BigDecimal balance;

  /**
   * Creates a new instance of the {@code BalanceResponseDto} class with the specified balance.
   *
   * @param balance The balance amount to set.
   */
  public BalanceResponseDto(BigDecimal balance) {
    this.balance = balance;
  }

  /** Creates a new instance of the {@code BalanceResponseDto} class with a default constructor. */
  public BalanceResponseDto() {}
}
