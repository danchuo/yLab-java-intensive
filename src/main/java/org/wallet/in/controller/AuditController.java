package org.wallet.in.controller;

import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.wallet.aop.annotations.Timed;
import org.wallet.application.WalletApplication;
import org.wallet.domain.dto.mapper.LogMapper;
import org.wallet.domain.dto.response.LogResponseDto;
import org.wallet.domain.model.Log;

/**
 * The `AuditController` class is a Spring MVC controller responsible for handling HTTP requests related
 * to audit logs. It exposes an endpoint for retrieving audit logs in JSON format.
 */
@RestController
@RequiredArgsConstructor
@Timed
public class AuditController {
  private final WalletApplication walletApplication;

  /**
   * Retrieves a list of audit logs and maps them to LogResponseDto objects.
   *
   * @return A list of LogResponseDto objects containing audit log information.
   */
  @GetMapping(value = "/logs", produces = MediaType.APPLICATION_JSON_VALUE)
  public List<LogResponseDto> getLogs() {
    List<Log> logs = walletApplication.getLogMessages();

    return logs.stream().map(LogMapper.INSTANCE::logToLogDto).collect(Collectors.toList());
  }
}