package com.nexage.app.util.validator;

import com.nexage.app.error.FieldError;
import java.lang.annotation.Annotation;
import java.util.List;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public abstract class BaseValidator<A extends Annotation, T> implements ConstraintValidator<A, T> {

  private A annotation;

  @Override
  public void initialize(A annotation) {
    this.annotation = annotation;
  }

  public A getAnnotation() {
    return annotation;
  }

  public boolean addConstraintMessage(
      ConstraintValidatorContext context, String field, String message) {
    ValidationUtils.addConstraintMessage(context, field, message);
    return false;
  }

  public boolean addConstraintMessage(
      ConstraintValidatorContext context, List<FieldError> fieldErrors) {
    ValidationUtils.addConstraintMessage(context, fieldErrors);
    return false;
  }

  protected void buildConstraintViolationWithTemplate(
      ConstraintValidatorContext context, String template) {
    context.disableDefaultConstraintViolation();
    context.buildConstraintViolationWithTemplate(template).addConstraintViolation();
  }
}
