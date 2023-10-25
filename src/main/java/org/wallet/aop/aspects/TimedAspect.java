package org.wallet.aop.aspects;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.wallet.aop.annotations.Timed;

/**
 * This aspect provides method execution time measurement for methods or classes annotated with {@link Timed}.
 * It logs the execution time of the annotated methods.
 */
@Aspect
public class TimedAspect {

  /**
   * Pointcut for methods within classes annotated with {@link Timed} and any execution.
   */
  @Pointcut("within(@org.wallet.aop.annotations.Timed *) && execution(* *(..))")
  public void annotatedByTimed() {}

  /**
   * Around advice to measure the execution time of annotated methods.
   *
   * @param proceedingJoinPoint The proceeding join point representing the method call.
   * @return The result of the method call.
   * @throws Throwable If an error occurs during method execution.
   */
  @Around("annotatedByTimed()")
  public Object timeMetering(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
    System.out.println("Calling method " + proceedingJoinPoint.getSignature());
    var startTime = System.currentTimeMillis();
    var result = proceedingJoinPoint.proceed();
    var endTime = System.currentTimeMillis();
    System.out.println(
            "Execution of method "
                    + proceedingJoinPoint.getSignature()
                    + " finished. Execution time is "
                    + (endTime - startTime)
                    + " ms");
    return result;
  }
}
