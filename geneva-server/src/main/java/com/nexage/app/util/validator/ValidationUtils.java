package com.nexage.app.util.validator;

import com.nexage.app.error.FieldError;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import javax.validation.ConstraintValidatorContext;
import javax.validation.ConstraintValidatorContext.ConstraintViolationBuilder;
import javax.validation.ConstraintValidatorContext.ConstraintViolationBuilder.NodeBuilderCustomizableContext;
import javax.validation.ConstraintViolation;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/** Created by e.kripinevich on 8/10/17. */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ValidationUtils {

  public static boolean validateIntEquals(
      ConstraintValidatorContext context,
      Integer value,
      int expectedValue,
      String field,
      String message) {
    boolean result = true;
    if (expectedValue != value) {
      addConstraintMessage(context, field, message);
      result = false;
    }
    return result;
  }

  public static boolean validateObjectNotNull(
      ConstraintValidatorContext context, Object value, String field, String message) {
    boolean result = true;
    if (value == null) {
      addConstraintMessage(context, field, message);
      result = false;
    }
    return result;
  }

  public static boolean validateObjectNull(
      ConstraintValidatorContext context, Object value, String field, String message) {
    boolean result = true;
    if (value != null) {
      addConstraintMessage(context, field, message);
      result = false;
    }
    return result;
  }

  public static boolean validateAllObjectsNotNull(
      ConstraintValidatorContext context, String field, String message, Object... values) {
    boolean result = true;
    for (int i = 0; i < values.length; i++) {
      if (values[i] == null) {
        addConstraintMessage(context, field, message);
        result = false;
        break;
      }
    }
    return result;
  }

  public static void addConstraintMessage(
      ConstraintValidatorContext context, String field, String message) {
    context
        .buildConstraintViolationWithTemplate(message)
        .addPropertyNode(field)
        .addConstraintViolation();
  }

  public static void addConstraintMessage(
      ConstraintValidatorContext context, List<FieldError> fieldErrors) {
    for (FieldError fieldError : fieldErrors) {
      context
          .buildConstraintViolationWithTemplate(
              fieldError.getErrorMessage()
                  + (fieldError.getFieldValue() != null
                      ? " [" + fieldError.getFieldValue() + "]"
                      : ""))
          .addPropertyNode(fieldError.getFieldName())
          .addConstraintViolation();
    }
  }

  /**
   * Adds {@link ConstraintViolation} with list of fields for a bean node. To be used when
   * validation occurs on nested fields
   *
   * @param context {@link ConstraintValidatorContext}
   * @param fields {@link List<String>} list of fields
   * @param message {@link String} error message
   * @return {@link ConstraintValidatorContext}
   */
  public static ConstraintValidatorContext addConstraintMessage(
      ConstraintValidatorContext context, List<String> fields, String message) {
    ConstraintViolationBuilder builder = context.buildConstraintViolationWithTemplate(message);
    NodeBuilderCustomizableContext nodeBuilder = builder.addPropertyNode(fields.get(0));
    for (int iterator = 1; iterator < fields.size(); iterator++) {
      nodeBuilder = nodeBuilder.addPropertyNode(fields.get(iterator));
    }
    return nodeBuilder.addConstraintViolation();
  }

  /**
   * Finds if the array of {@link int} has any duplicate elements or not
   *
   * @param fields {@link int[]}
   * @return boolean
   */
  public static boolean hasNoDuplicates(int[] fields) {
    return Objects.isNull(fields) || (Arrays.stream(fields).distinct().count() == fields.length);
  }
}
