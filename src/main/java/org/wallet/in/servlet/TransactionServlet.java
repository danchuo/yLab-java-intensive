package org.wallet.in.servlet;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import java.io.IOException;
import java.io.Serial;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.wallet.aop.annotations.Authorized;
import org.wallet.aop.annotations.Timed;
import org.wallet.application.WalletApplication;
import org.wallet.domain.dto.mapper.TransactionMapper;
import org.wallet.domain.dto.request.TransactionRequestDto;
import org.wallet.domain.model.Transaction;
import org.wallet.exception.InvalidRequestException;
import org.wallet.exception.PlayerNotFoundException;
import org.wallet.exception.TransactionAlreadyExistException;

/**
 * The `TransactionServlet` class is a servlet responsible for handling user transactions. It
 * processes POST requests, reads user input, validates it, and attempts to register a new
 * transaction for the currently logged-in player.
 */
@Timed
public class TransactionServlet extends HttpServlet {

  @Serial private static final long serialVersionUID = -693737914085736917L;
  private final ObjectMapper objectMapper;
  private final WalletApplication walletApplication;

  /**
   * Constructs a `TransactionServlet` with the provided `WalletApplication` and `ObjectMapper`.
   *
   * @param walletApplication The application responsible for handling wallet-related operations.
   * @param objectMapper The object mapper for JSON serialization and deserialization.
   */
  public TransactionServlet(WalletApplication walletApplication, ObjectMapper objectMapper) {
    this.walletApplication = walletApplication;
    this.objectMapper = objectMapper;
  }

  /**
   * Handles POST requests for user transactions. It reads user input, validates it, and attempts to
   * register a new transaction for the currently logged-in player. If the registration is
   * successful, it responds with HTTP status 201 (Created).
   *
   * @param request The `HttpServletRequest` containing the transaction data.
   * @param response The `HttpServletResponse` to send the response.
   * @throws IOException If an I/O error occurs during request or response handling.
   */
  @Authorized
  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response)
      throws IOException {
    try {
      var login = (String) request.getAttribute("login");

      var requestDto =
          objectMapper.readValue(request.getInputStream(), TransactionRequestDto.class);

      if (!requestDto.isValid()) {
        throw new InvalidRequestException();
      }

      Transaction transaction =
          TransactionMapper.INSTANCE.transactionRequestDtoToTransaction(requestDto);

      walletApplication.setCurrentPlayer(login);

      walletApplication.registerTransaction(transaction);
      response.setStatus(HttpServletResponse.SC_CREATED);
    } catch (TransactionAlreadyExistException ex) {
      ExceptionHandler.sendJsonResponse(
          response, HttpServletResponse.SC_FORBIDDEN, ex.getMessage());
    } catch (PlayerNotFoundException ex) {
      ExceptionHandler.sendErrorResponse(
          response, HttpServletResponse.SC_FORBIDDEN, "Wrong login or password.");
    } catch (IllegalArgumentException | UnrecognizedPropertyException | JsonParseException ex) {
      ExceptionHandler.sendErrorResponse(
          response, HttpServletResponse.SC_BAD_REQUEST, "Data validation error.");
    }
  }
}
