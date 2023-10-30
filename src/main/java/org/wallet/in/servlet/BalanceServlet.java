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
import org.wallet.domain.dto.response.BalanceResponseDto;
import org.wallet.exception.InvalidRequestException;
import org.wallet.exception.PlayerNotFoundException;

/**
 * The `BalanceServlet` class is responsible for handling GET requests to retrieve the balance of
 * the currently authorized player. It retrieves the player's balance from the `WalletApplication`
 * and sends it as a JSON response. The servlet requires authorization to access the balance
 * information.
 */
@Timed
public class BalanceServlet extends HttpServlet {

  @Serial private static final long serialVersionUID = -6284436669872590530L;
  private final ObjectMapper objectMapper;
  private final WalletApplication walletApplication;

  /**
   * Constructs a `BalanceServlet` with the provided `WalletApplication` and `ObjectMapper`.
   *
   * @param walletApplication The `WalletApplication` instance for retrieving player balances.
   * @param objectMapper The `ObjectMapper` for JSON serialization and deserialization.
   */
  public BalanceServlet(WalletApplication walletApplication, ObjectMapper objectMapper) {
    this.walletApplication = walletApplication;
    this.objectMapper = objectMapper;
  }

  /**
   * Handles GET requests to retrieve the balance of the currently authorized player. It retrieves
   * the player's balance from the `WalletApplication` and sends it as a JSON response. The servlet
   * requires authorization to access the balance information.
   *
   * @param request The `HttpServletRequest` for incoming GET request.
   * @param response The `HttpServletResponse` for sending the response.
   * @throws IOException If an I/O error occurs during request handling.
   */
  @Authorized
  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
      throws IOException {
    try {
      var login = (String) request.getAttribute("login");

      walletApplication.setCurrentPlayer(login);

      var balance = walletApplication.getCurrentPlayer().getBalance();

      ExceptionHandler.sendJsonResponse(
          response, HttpServletResponse.SC_OK, new BalanceResponseDto(balance));
    } catch (InvalidRequestException | UnrecognizedPropertyException | JsonParseException ex) {
      ExceptionHandler.sendErrorResponse(
          response, HttpServletResponse.SC_BAD_REQUEST, "Data validation error.");
    } catch (PlayerNotFoundException ex) {
      ExceptionHandler.sendErrorResponse(
          response, HttpServletResponse.SC_FORBIDDEN, "Wrong login or password.");
    }
  }
}
