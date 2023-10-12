package org.wallet.model;

/**
 * The {@code TransactionType} enumeration represents the type of a financial transaction. It can be
 * either a DEBIT or CREDIT transaction.
 */
public enum TransactionType {
  /** Represents a DEBIT transaction, where money is withdrawn from an account. */
  DEBIT,

  /** Represents a CREDIT transaction, where money is deposited into an account. */
  CREDIT
}
