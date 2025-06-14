package com.nexage.app.error;

import java.util.HashSet;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.ValidationException;

public class EntityConstraintViolationException extends ValidationException {
  private final Set<ConstraintViolation<?>> constraintViolations;

  public EntityConstraintViolationException(
      String message, Set<? extends ConstraintViolation<?>> constraintViolations) {
    super(message);
    if (constraintViolations == null) {
      this.constraintViolations = null;
    } else {
      this.constraintViolations = new HashSet<>(constraintViolations);
    }
  }

  public EntityConstraintViolationException(
      Set<? extends ConstraintViolation<?>> constraintViolations) {
    this(null, constraintViolations);
  }

  public Set<ConstraintViolation<?>> getConstraintViolations() {
    return this.constraintViolations;
  }
}
