package org.wallet.aop.aspects;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.io.IOException;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.wallet.aop.annotations.Authorized;
import org.wallet.exception.InvalidRequestException;
import org.wallet.exception.UnauthorizedAccessException;
import org.wallet.utils.JwtTokenUtility;

/**
 * This aspect provides authorization checks for methods or classes annotated with {@link
 * Authorized}. It verifies the JWT token in the incoming request to ensure the user is authorized.
 */
@Aspect
public class AuthAspect {

  /**
   * Pointcut for methods annotated with {@link Authorized} and accepting a {@link
   * HttpServletRequest}.
   *
   * @param request The HttpServletRequest argument.
   */
  @Pointcut("@annotation(org.wallet.aop.annotations.Authorized) && args(request, ..)")
  public void annotatedByAuthorized(HttpServletRequest request) {}

  /**
   * Before advice to perform authorized check for methods annotated with {@link Authorized}.
   *
   * @param request The HttpServletRequest argument.
   */
  @Before(value = "annotatedByAuthorized(request)", argNames = "request")
  public void authorizedCheck(HttpServletRequest request) {
    var objectMapper = new ObjectMapper();
    objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
    try {
      var rootNode = objectMapper.readTree(request.getInputStream());
      var jwtToken = rootNode.get("jwtToken").asText();
      if (JwtTokenUtility.isValid(jwtToken)) {
        request.setAttribute("login", JwtTokenUtility.getLogin(jwtToken));
      } else {
        throw new UnauthorizedAccessException();
      }

    } catch (IOException | InvalidRequestException ex) {
      throw new UnauthorizedAccessException();
    }
  }
}
