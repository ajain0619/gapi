package com.ssp.geneva.common.base.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Annotation for marking functionality not production fully ready.
 *
 * <p>Specifically, {@code @Incubating} is an <em>informative annotation</em> that acts as a guide
 * to understand and warn the status of a given functionality.
 */
@Retention(RetentionPolicy.SOURCE)
@Documented
public @interface Incubating {
  /**
   * Allows for more concise annotation declarations.
   *
   * @return message with more explanatory context.
   */
  String name() default "";
}
