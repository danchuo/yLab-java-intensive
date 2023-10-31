package org.wallet.in.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.io.IOException;
import java.io.Serial;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.wallet.domain.dto.response.ErrorResponce;
import org.wallet.exception.UnauthorizedAccessException;

/**
 * The `ExceptionHandler` class is responsible for handling exceptions and sending appropriate JSON
 * responses. It provides methods for sending JSON responses and handling errors. It also handles
 * GET requests for error responses.
 */
@WebServlet("/ExceptionHandler")
public class ExceptionHandler extends HttpServlet {

  @Serial private static final long serialVersionUID = 6862128650993941134L;

  private static ObjectMapper objectMapper = new ObjectMapper();

  /**
   * Sends a JSON response with the specified code and object to the provided `HttpServletResponse`.
   *
   * @param response The `HttpServletResponse` to send the JSON response.
   * @param code The HTTP status code for the response.
   * @param object The object to be serialized and sent as JSON.
   * @throws IOException If an I/O error occurs during response handling.
   */
  public static void sendJsonResponse(HttpServletResponse response, int code, Object object)
      throws IOException {
    response.setStatus(code);
    response.setContentType("application/json");
    response.getOutputStream().write(objectMapper.writeValueAsBytes(object));
  }

  /**
   * Sends an error response with the specified status code and error message.
   *
   * @param response The `HttpServletResponse` to send the error response.
   * @param statusCode The HTTP status code for the error response.
   * @param errorMessage The error message to be included in the response.
   * @throws IOException If an I/O error occurs during response handling.
   */
  public static void sendErrorResponse(
      HttpServletResponse response, int statusCode, String errorMessage) throws IOException {
    ErrorResponce errorResponse = new ErrorResponce(statusCode, errorMessage);
    sendJsonResponse(response, statusCode, errorResponse);
  }

  @Override
  public void init() throws ServletException {
    super.init();
    objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
  }

  /**
   * Handles GET requests for error responses. It retrieves the exception, status code, and servlet
   * name from the request attributes and sends an appropriate error response.
   *
   * @param request The `HttpServletRequest` for incoming GET request.
   * @param response The `HttpServletResponse` for sending the error response.
   * @throws IOException If an I/O error occurs during request handling.
   */
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    Throwable throwable = (Throwable) request.getAttribute("javax.servlet.error.exception");
    Integer code = (Integer) request.getAttribute("javax.servlet.error.status_code");
    String servletName = (String) request.getAttribute("javax.servlet.error.servlet_name");

    if (throwable instanceof UnauthorizedAccessException unauthorizedAccessException) {
      sendErrorResponse(
          response, HttpServletResponse.SC_UNAUTHORIZED, unauthorizedAccessException.getMessage());
    } else {
      sendErrorResponse(response, code, "Unknown Error");
    }
  }

  /**
   * Handles POST requests by forwarding them to the `doGet` method to handle error responses.
   *
   * @param request The `HttpServletRequest` for the incoming POST request.
   * @param response The `HttpServletResponse` for sending the response.
   * @throws ServletException If an error occurs during request forwarding.
   * @throws IOException If an I/O error occurs during response handling.
   */
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {

    doGet(request, response);
  }
}
