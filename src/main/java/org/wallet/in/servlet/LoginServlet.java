package org.wallet.in.servlet;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import java.io.IOException;
import java.io.Serial;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.wallet.aop.annotations.Timed;
import org.wallet.application.WalletApplication;
import org.wallet.domain.dto.request.JwtTokenResponseDto;
import org.wallet.domain.dto.request.PlayerRequestDto;
import org.wallet.exception.InvalidRequestException;
import org.wallet.exception.PlayerNotFoundException;
import org.wallet.utils.JwtTokenUtility;

/**
 * The `LoginServlet` class is a servlet responsible for handling user login requests. It processes
 * POST requests, reads user input, validates it, and attempts to log in the user. It generates a
 * JWT token upon successful login and sends it as a response.
 */
@Timed
public class LoginServlet extends HttpServlet {

  @Serial private static final long serialVersionUID = -8920545215316551700L;
  private final ObjectMapper objectMapper;
  private final WalletApplication walletApplication;

  /**
   * Constructs a `LoginServlet` with the provided `WalletApplication` and `ObjectMapper`.
   *
   * @param walletApplication The application responsible for handling wallet-related operations.
   * @param objectMapper The object mapper for JSON serialization and deserialization.
   */
  public LoginServlet(WalletApplication walletApplication, ObjectMapper objectMapper) {
    this.walletApplication = walletApplication;
    this.objectMapper = objectMapper;
  }

  /**
   * Handles POST requests for user login. It reads user input, validates it, and attempts to log in
   * the user. If the login is successful, it generates a JWT token and sends it as a response.
   *
   * @param request The `HttpServletRequest` containing the user login data.
   * @param response The `HttpServletResponse` to send the response.
   * @throws IOException If an I/O error occurs during request or response handling.
   */
  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response)
      throws IOException {
    try {
      var requestDto = objectMapper.readValue(request.getInputStream(), PlayerRequestDto.class);

      if (!requestDto.isValid()) {
        throw new InvalidRequestException();
      }

      walletApplication.login(requestDto.getLogin(), requestDto.getPassword());

      var player = walletApplication.getCurrentPlayer();

      var jwtToken = JwtTokenUtility.createJwtToken(player.getLogin());

      ExceptionHandler.sendJsonResponse(
          response, HttpServletResponse.SC_OK, new JwtTokenResponseDto(jwtToken));
    } catch (InvalidRequestException | UnrecognizedPropertyException | JsonParseException ex) {
      ExceptionHandler.sendErrorResponse(
          response, HttpServletResponse.SC_BAD_REQUEST, "Data validation error.");
    } catch (PlayerNotFoundException ex) {
      ExceptionHandler.sendErrorResponse(
          response, HttpServletResponse.SC_FORBIDDEN, "Wrong login or password.");
    }
  }
}
