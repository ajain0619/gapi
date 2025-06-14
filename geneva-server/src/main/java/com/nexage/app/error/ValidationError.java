package com.nexage.app.error;

import static java.util.stream.Collectors.mapping;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Maps;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import com.ssp.geneva.common.error.model.Error;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import javax.validation.ConstraintViolation;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.PropertyValueException;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

/**
 * Bean validation exception which is serialized to JSON. Contains additional info about constraint
 * violations.
 *
 * @author Nick Ilkevich
 * @since 08.09.2014
 */
public class ValidationError extends Error {

  /** Fields constrain violations, where key is a field name and value is an error message */
  private Map<String, String> fieldErrors = Maps.newHashMap();

  public ValidationError(
      HttpStatus httpStatus, int code, String msg, GenevaValidationException exception) {
    super(httpStatus, code, msg, exception, false);
    if (exception.getBindingResult() != null) {
      fillErrors(exception.getBindingResult());
    }
  }

  public ValidationError(
      HttpStatus httpStatus, int code, String msg, EntityConstraintViolationException exception) {
    super(httpStatus, code, msg, exception, false);
    if (exception.getConstraintViolations() != null) {
      fillErrors(exception.getConstraintViolations());
    }
  }

  public ValidationError(
      HttpStatus httpStatus, int code, String msg, PropertyValueException exception) {
    super(httpStatus, code, msg, exception, false);
    if (exception.getPropertyName() != null) {
      fieldErrors.put(exception.getPropertyName(), exception.getMessage());
    }
  }

  private void fillErrors(BindingResult bindingResult) {
    for (FieldError fieldError : bindingResult.getFieldErrors()) {
      fieldErrors.put(fieldError.getField(), fieldError.getDefaultMessage());
    }
  }

  private void fillErrors(Set<ConstraintViolation<?>> constraintViolations) {
    constraintViolations.parallelStream()
        .filter(
            e ->
                (StringUtils.isNotBlank(e.getMessage())
                    && StringUtils.isNotBlank(String.valueOf(e.getPropertyPath()))))
        .collect(
            Collectors.groupingBy(
                e -> String.valueOf(e.getPropertyPath()),
                mapping(ConstraintViolation::getMessage, Collectors.joining(", "))))
        .forEach((key, message) -> fieldErrors.put(key, message));
  }

  @JsonProperty
  @SuppressWarnings("unused")
  public Map<String, String> getFieldErrors() {
    return fieldErrors;
  }
}
