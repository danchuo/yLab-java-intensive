package com.danchuo.starterannotations.aop.aspects;

import com.danchuo.starterannotations.aop.annotations.Timed;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

/**
 * This aspect provides method execution time measurement for methods or classes annotated with
 * {@link Timed}. It logs the execution time of the annotated methods.
 */
@Aspect
@Component
public class CustomTimedAspect {

  /** Pointcut for methods within classes annotated with {@link Timed} and any execution. */
  @Pointcut("within(@com.danchuo.starterannotations.aop.annotations.Timed *) && execution(* *(..))")
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
    long startTime = System.currentTimeMillis();
    Object result = proceedingJoinPoint.proceed();
    long endTime = System.currentTimeMillis();
    System.out.println(
        "Execution of method "
            + proceedingJoinPoint.getSignature()
            + " finished. Execution time is "
            + (endTime - startTime)
            + " ms");
    return result;
  }
}
