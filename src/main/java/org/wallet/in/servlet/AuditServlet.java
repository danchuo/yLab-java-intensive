package org.wallet.in.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.Serial;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.wallet.aop.annotations.Timed;
import org.wallet.application.WalletApplication;
import org.wallet.domain.dto.mapper.LogMapper;
import org.wallet.exception.PlayerNotFoundException;
import org.wallet.exception.TransactionAlreadyExistException;

/**
 * The `AuditServlet` class is responsible for handling GET requests to retrieve audit logs. It
 * retrieves audit logs from the `WalletApplication` and converts them to DTOs for response. The
 * servlet returns the audit logs as JSON in response.
 */
@Timed
public class AuditServlet extends HttpServlet {

  @Serial private static final long serialVersionUID = 5710233123850407918L;
  private final ObjectMapper objectMapper;
  private final WalletApplication walletApplication;

  /**
   * Constructs an `AuditServlet` with the provided `WalletApplication` and `ObjectMapper`.
   *
   * @param walletApplication The `WalletApplication` instance for retrieving audit logs.
   * @param objectMapper The `ObjectMapper` for JSON serialization and deserialization.
   */
  public AuditServlet(WalletApplication walletApplication, ObjectMapper objectMapper) {
    this.walletApplication = walletApplication;
    this.objectMapper = objectMapper;
  }

  /**
   * Handles GET requests to retrieve audit logs. It retrieves audit logs from the
   * `WalletApplication` and converts them to DTOs for response. The servlet returns the audit logs
   * as JSON in response.
   *
   * @param request The `HttpServletRequest` for incoming GET request.
   * @param response The `HttpServletResponse` for sending the response.
   * @throws IOException If an I/O error occurs during request handling.
   */
  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
      throws IOException {
    try {
      var logs = walletApplication.getLogMessages();
      var logDtos = logs.stream().map(LogMapper.INSTANCE::logToLogDto).toList();
      ExceptionHandler.sendJsonResponse(response, HttpServletResponse.SC_OK, logDtos);
    } catch (TransactionAlreadyExistException ex) {
      ExceptionHandler.sendErrorResponse(
          response, HttpServletResponse.SC_FORBIDDEN, ex.getMessage());
    } catch (PlayerNotFoundException ex) {
      ExceptionHandler.sendErrorResponse(
          response, HttpServletResponse.SC_FORBIDDEN, "Wrong login or password.");
    } catch (IllegalArgumentException ex) {
      ExceptionHandler.sendErrorResponse(
          response, HttpServletResponse.SC_BAD_REQUEST, "Data validation error.");
    }
  }
}
