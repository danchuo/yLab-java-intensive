package org.wallet.in.config;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.wallet.domain.dto.response.ErrorResponce;
import org.wallet.exception.PlayerAlreadyExistException;
import org.wallet.exception.PlayerNotFoundException;
import org.wallet.exception.TransactionAlreadyExistException;
import org.wallet.exception.UnauthorizedAccessException;

/**
 * The `RestResponseEntityExceptionHandler` class is a controller advice class that handles
 * exceptions and returns appropriate ResponseEntity objects. It extends the Spring
 * ResponseEntityExceptionHandler to provide custom exception handling for the RESTful API.
 */
@ControllerAdvice
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {
  /**
   * Handle exceptions of type Exception. This method determines the HTTP status code based on the
   * specific exception type and creates an error response to return to the client.
   *
   * @param ex The exception that was thrown.
   * @param request The current web request.
   * @return A ResponseEntity containing an error response and the appropriate HTTP status.
   */
  @ExceptionHandler(Exception.class)
  protected ResponseEntity<Object> handleConflict(RuntimeException ex, WebRequest request) {
    var status = HttpStatus.BAD_REQUEST;

    if (ex instanceof UnauthorizedAccessException) {
      status = HttpStatus.UNAUTHORIZED;
    } else if (ex instanceof PlayerNotFoundException) {
      status = HttpStatus.NOT_FOUND;
    } else if (ex instanceof PlayerAlreadyExistException
        || ex instanceof TransactionAlreadyExistException) {
      status = HttpStatus.FORBIDDEN;
    }

    var bodyOfResponse = new ErrorResponce(status, ex.getMessage());
    return handleExceptionInternal(ex, bodyOfResponse, new HttpHeaders(), status, request);
  }
}
