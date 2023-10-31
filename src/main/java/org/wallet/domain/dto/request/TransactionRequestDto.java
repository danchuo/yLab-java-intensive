package org.wallet.domain.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.math.BigDecimal;
import lombok.Data;

/**
 * The {@code TransactionRequestDto} class represents a Data Transfer Object (DTO) for transaction
 * requests. It includes fields for a JWT token, transaction amount, transaction ID, and transaction
 * type. It also provides an {@code isValid} method for validation.
 */
@Data
public class TransactionRequestDto implements Validator {
  private String jwtToken;
  private BigDecimal amount;
  private String transactionId;
  private String transactionType;

  /**
   * Checks the validity of the TransactionRequestDto. It is considered valid if the transaction ID
   * is not null, not blank, the transaction type is not null, not blank, and the transaction amount
   * is not null and greater than zero.
   *
   * @return {@code true} if the conditions for validity are met; otherwise, {@code false}.
   */
  @Override
  @JsonIgnore
  public boolean isValid() {
    return transactionId != null
        && !transactionId.isBlank()
        && transactionType != null
        && !transactionType.isBlank()
        && amount != null
        && amount.compareTo(BigDecimal.ZERO) > 0;
  }
}
