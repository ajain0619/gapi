package com.nexage.app.util.validator;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import javax.validation.Constraint;

@Documented
@Constraint(validatedBy = {InventoryMdmIdQueryParameterValidator.class})
@Target({TYPE})
@Retention(RUNTIME)
public @interface InventoryMdmIdQueryFieldParameterConstraint {

  String message() default "Query Field parameter is invalid";

  Class<?>[] groups() default {};

  Class[] payload() default {};
}
