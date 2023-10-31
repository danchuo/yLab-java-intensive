package org.wallet.in.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.Serial;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.wallet.aop.annotations.Authorized;
import org.wallet.aop.annotations.Timed;
import org.wallet.application.WalletApplication;
import org.wallet.domain.dto.mapper.TransactionMapper;
import org.wallet.exception.PlayerNotFoundException;
import org.wallet.exception.TransactionAlreadyExistException;

/**
 * The `TransactionsServlet` class is a servlet responsible for retrieving user transactions. It
 * processes GET requests and returns a list of transactions for the currently logged-in player.
 */
@Timed
public class TransactionsServlet extends HttpServlet {

  @Serial private static final long serialVersionUID = 5906379282805989595L;
  private final ObjectMapper objectMapper;
  private final WalletApplication walletApplication;

  /**
   * Constructs a `TransactionsServlet` with the provided `WalletApplication` and `ObjectMapper`.
   *
   * @param walletApplication The application responsible for handling wallet-related operations.
   * @param objectMapper The object mapper for JSON serialization and deserialization.
   */
  public TransactionsServlet(WalletApplication walletApplication, ObjectMapper objectMapper) {
    this.walletApplication = walletApplication;
    this.objectMapper = objectMapper;
  }

  /**
   * Handles GET requests for retrieving user transactions. It retrieves transactions for the
   * currently logged-in player, serializes them into DTOs, and responds with HTTP status 200 (OK).
   *
   * @param request The `HttpServletRequest` containing the user's session information.
   * @param response The `HttpServletResponse` to send the response.
   * @throws IOException If an I/O error occurs during request or response handling.
   */
  @Authorized
  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
      throws IOException {
    try {
      var login = (String) request.getAttribute("login");

      walletApplication.setCurrentPlayer(login);

      var transactions = walletApplication.getTransactionOfCurrentPlayer();

      var transactionDtos =
          transactions.stream()
              .map(TransactionMapper.INSTANCE::transactionToTransactionResponseDto)
              .toList();

      ExceptionHandler.sendJsonResponse(response, HttpServletResponse.SC_OK, transactionDtos);
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
