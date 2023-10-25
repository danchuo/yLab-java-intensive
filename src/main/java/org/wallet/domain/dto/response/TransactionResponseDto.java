package org.wallet.domain.dto.response;

import java.math.BigDecimal;
import lombok.Data;
import org.wallet.domain.model.TransactionType;

/**
 * The {@code TransactionResponseDto} class represents a Data Transfer Object (DTO) for providing
 * information about a transaction. It includes fields to describe the transaction amount,
 * transaction ID, and transaction type.
 */
@Data
public class TransactionResponseDto {
  /** The amount associated with the transaction. */
  private BigDecimal amount;

  /** The unique identifier for the transaction. */
  private String transactionId;

  /** The type of transaction (e.g., debit or credit). */
  private TransactionType type;
}
