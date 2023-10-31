package org.wallet.aop.aspects;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.wallet.aop.annotations.Loggable;
import org.wallet.domain.model.LogAction;
import org.wallet.domain.model.Transaction;
import org.wallet.domain.repository.DatabaseConnection;
import org.wallet.domain.repository.log.JdbcLogRepository;
import org.wallet.domain.service.AuditService;

/**
 * The {@code LoggableAspect} aspect provides logging functionality for methods or classes annotated
 * with {@link Loggable}. It logs specific actions, details, and user login information.
 */
@Aspect
@Component
public class LoggableAspect {

  private static final AuditService AUDIT_SERVICE;

  static {
    AUDIT_SERVICE = new AuditService(new JdbcLogRepository(new DatabaseConnection()));
  }

  /** Pointcut for methods annotated with {@link Loggable}. */
  @Pointcut("@annotation(org.wallet.aop.annotations.Loggable)")
  public void annotatedByLoggable() {}

  /**
   * Around advice to log the specified action, details, and user login information for methods
   * annotated with {@link Loggable}.
   *
   * @param proceedingJoinPoint The join point representing the method being logged.
   * @return The result of the method execution.
   * @throws Throwable if an error occurs during the method execution.
   */
  @Around("annotatedByLoggable()")
  public Object log(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
    LogAction action = getLogAction(proceedingJoinPoint);
    String details = action.getDetails();
    Object[] args = proceedingJoinPoint.getArgs();
    String login = extractLoginFromArgs(args);
    try {
      AUDIT_SERVICE.log(action, login, details);
    } catch (Exception e) {
      AUDIT_SERVICE.log(action, login, "Error occurred: " + e.getMessage());
      throw e;
    }

    return proceedingJoinPoint.proceed();
  }

  /**
   * Retrieves the LogAction value from the {@link Loggable} annotation of the annotated method.
   *
   * @param proceedingJoinPoint The join point representing the method being logged.
   * @return The LogAction value.
   */
  private LogAction getLogAction(ProceedingJoinPoint proceedingJoinPoint) {
    var methodSignature = (MethodSignature) proceedingJoinPoint.getSignature();
    var method = methodSignature.getMethod();
    var loggableAnnotation = method.getAnnotation(Loggable.class);
    if (loggableAnnotation != null) {
      return loggableAnnotation.value();
    }
    return LogAction.REGISTRATION;
  }

  /**
   * Extracts the user login information from the method arguments.
   *
   * @param args The arguments of the method.
   * @return The user login information if found, or null if not found.
   */
  private String extractLoginFromArgs(Object... args) {
    for (Object arg : args) {
      if (arg instanceof String) {
        return (String) arg;
      } else if (arg instanceof Transaction transaction) {
        if (transaction.playerLogin() != null) {
          return transaction.playerLogin();
        }
      }
    }
    return null;
  }
}
