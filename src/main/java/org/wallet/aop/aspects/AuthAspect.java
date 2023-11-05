package org.wallet.aop.aspects;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.wallet.aop.annotations.Authorized;
import org.wallet.domain.dto.request.JwtTokenResponseDto;
import org.wallet.exception.UnauthorizedAccessException;
import org.wallet.utils.JwtTokenUtility;

/**
 * The {@code AuthAspect} aspect provides authorization checks for methods or classes annotated with
 * {@link Authorized}. It verifies the JWT token in the incoming request to ensure the user is
 * authorized.
 */
@Aspect
@Component
public class AuthAspect {

  /**
   * Pointcut for methods annotated with {@link Authorized} and accepting a {@link
   * JwtTokenResponseDto}.
   *
   * @param request The HttpServletRequest argument.
   */
  @Pointcut("@annotation(org.wallet.aop.annotations.Authorized) && args(request, ..)")
  public void annotatedByAuthorized(Object request) {}

  /**
   * Before advice to perform authorized check for methods annotated with {@link Authorized}.
   *
   * @param request The HttpServletRequest argument.
   */
  @Before(value = "annotatedByAuthorized(request)", argNames = "request")
  public void authorizedCheck(Object request) {
    try {
      var jwtTokenField = request.getClass().getDeclaredField("jwtToken");
      jwtTokenField.setAccessible(true);
      Object jwtTokenValue = jwtTokenField.get(request);
      if (jwtTokenValue == null || !JwtTokenUtility.isValid(jwtTokenValue.toString())) {
        throw new UnauthorizedAccessException();
      }
    } catch (Exception e) {
      throw new UnauthorizedAccessException();
    }
  }
}
