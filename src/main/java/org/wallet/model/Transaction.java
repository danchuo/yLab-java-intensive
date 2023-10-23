package org.wallet.model;

import java.math.BigDecimal;

/**
 * The {@code Transaction} class represents a financial transaction associated with a player. It
 * includes information such as the player's login, transaction ID, transaction type, and amount.
 *
 * @param playerLogin The login of the player associated with the transaction.
 * @param transactionId The unique identifier for the transaction.
 * @param type The type of the transaction (DEBIT or CREDIT).
 * @param amount The amount involved in the transaction.
 */
public record Transaction(
    String playerLogin, String transactionId, TransactionType type, BigDecimal amount) {}
