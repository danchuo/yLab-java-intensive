package org.wallet.aop.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.wallet.domain.model.LogAction;

/**
 * The {@code Loggable} annotation is used to indicate that a method should be logged for auditing
 * purposes with the specified log action.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Loggable {
  /**
   * Specifies the log action associated with the annotated method.
   *
   * @return the log action for the method
   */
  LogAction value();
}
