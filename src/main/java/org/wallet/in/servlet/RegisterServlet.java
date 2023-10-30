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
import org.wallet.domain.dto.request.PlayerRequestDto;
import org.wallet.exception.InvalidRequestException;
import org.wallet.exception.PlayerNotFoundException;

/**
 * The `RegisterServlet` class is a servlet responsible for handling user registration requests. It
 * processes POST requests, reads user input, validates it, and attempts to register a new player.
 */
@Timed
public class RegisterServlet extends HttpServlet {

  @Serial private static final long serialVersionUID = 6391597442277732897L;
  private final ObjectMapper objectMapper;
  private final WalletApplication walletApplication;

  /**
   * Constructs a `RegisterServlet` with the provided `WalletApplication` and `ObjectMapper`.
   *
   * @param walletApplication The application responsible for handling wallet-related operations.
   * @param objectMapper The object mapper for JSON serialization and deserialization.
   */
  public RegisterServlet(WalletApplication walletApplication, ObjectMapper objectMapper) {
    this.walletApplication = walletApplication;
    this.objectMapper = objectMapper;
  }

  /**
   * Handles POST requests for user registration. It reads user input, validates it, and attempts to
   * register a new player. If the registration is successful, it responds with HTTP status 201
   * (Created).
   *
   * @param request The `HttpServletRequest` containing the user registration data.
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

      walletApplication.registerPlayer(requestDto.getLogin(), requestDto.getPassword());
      response.setStatus(HttpServletResponse.SC_CREATED);
    } catch (InvalidRequestException | UnrecognizedPropertyException | JsonParseException ex) {
      ExceptionHandler.sendErrorResponse(
          response, HttpServletResponse.SC_BAD_REQUEST, "Data validation error.");
    } catch (PlayerNotFoundException ex) {
      ExceptionHandler.sendErrorResponse(
          response, HttpServletResponse.SC_FORBIDDEN, "Wrong login or password.");
    }
  }
}
