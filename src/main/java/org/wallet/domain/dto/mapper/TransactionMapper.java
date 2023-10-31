package org.wallet.domain.dto.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import org.wallet.domain.dto.request.TransactionRequestDto;
import org.wallet.domain.dto.response.TransactionResponseDto;
import org.wallet.domain.model.Transaction;

/**
 * Mapper interface for converting between {@link TransactionRequestDto} and {@link
 * TransactionResponseDto} and {@link Transaction} objects.
 */
@Mapper
public interface TransactionMapper {

  /** A shared instance of the {@code TransactionMapper}. */
  TransactionMapper INSTANCE = Mappers.getMapper(TransactionMapper.class);

  /**
   * Converts a {@link TransactionRequestDto} to a {@link Transaction} object.
   *
   * @param transaction The {@link TransactionRequestDto} to convert.
   * @return The corresponding {@link Transaction} object.
   */
  @Mapping(source = "transactionType", target = "type")
  @Mapping(
      target = "playerLogin",
      expression = "java(org.wallet.utils.JwtTokenUtility.getLogin(transaction.getJwtToken()))")
  Transaction transactionRequestDtoToTransaction(TransactionRequestDto transaction);

  /**
   * Converts a {@link Transaction} to a {@link TransactionResponseDto} object.
   *
   * @param transaction The {@link Transaction} to convert.
   * @return The corresponding {@link TransactionResponseDto} object.
   */
  TransactionResponseDto transactionToTransactionResponseDto(Transaction transaction);
}
