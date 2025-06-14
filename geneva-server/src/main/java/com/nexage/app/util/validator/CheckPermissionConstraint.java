package com.nexage.app.util.validator;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import com.nexage.admin.core.model.User.Role;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import javax.validation.Constraint;

@Documented
@Constraint(validatedBy = CheckPermissionValidator.class)
@Target(FIELD)
@Retention(RUNTIME)
public @interface CheckPermissionConstraint {

  String message() default "Insufficient permissions - value should be empty";

  Class<?>[] groups() default {};

  Class[] payload() default {};

  /**
   * Array of {@link Role} to be verified if user contains any
   *
   * @return
   */
  Role[] roles() default {};

  /**
   * If set to true, user will be validated if Nexage User
   *
   * @return
   */
  boolean checkIfNexageUser() default false;
}
