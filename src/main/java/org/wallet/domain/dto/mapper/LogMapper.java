package org.wallet.domain.dto.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.wallet.domain.dto.response.LogResponseDto;
import org.wallet.domain.model.Log;

/**
 * Mapper interface for converting {@link Log} objects to {@link LogResponseDto} objects.
 */
@Mapper
public interface LogMapper {

  /**
   * A shared instance of the {@code LogMapper}.
   */
  LogMapper INSTANCE = Mappers.getMapper(LogMapper.class);

  /**
   * Converts a {@link Log} object to a {@link LogResponseDto}.
   *
   * @param log The {@link Log} object to convert.
   * @return The corresponding {@link LogResponseDto} object.
   */
  LogResponseDto logToLogDto(Log log);
}
