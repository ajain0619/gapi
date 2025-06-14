package com.nexage.app.util.validator;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.common.collect.ImmutableList;
import java.util.List;
import javax.validation.ConstraintValidatorContext;
import javax.validation.ConstraintValidatorContext.ConstraintViolationBuilder;
import javax.validation.ConstraintValidatorContext.ConstraintViolationBuilder.NodeBuilderCustomizableContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ValidationUtilsTest {

  @Mock private ConstraintValidatorContext context;
  @Mock private ConstraintViolationBuilder builder;
  @Mock private NodeBuilderCustomizableContext nodeBuilder;

  @Test
  void shouldAddSingleFieldToContext() {
    String message = "error message";
    when(context.buildConstraintViolationWithTemplate(message)).thenReturn(builder);
    when(builder.addPropertyNode("site")).thenReturn(nodeBuilder);
    when(nodeBuilder.addConstraintViolation()).thenReturn(context);

    ConstraintValidatorContext constraintValidatorContext =
        ValidationUtils.addConstraintMessage(context, ImmutableList.of("site"), message);
    assertNotNull(constraintValidatorContext);
    verify(builder).addPropertyNode("site");
    verify(nodeBuilder, never()).addPropertyNode(anyString());
  }

  @Test
  void shouldAddMultipleFieldsToContext() {
    String message = "error message";
    List<String> fields = ImmutableList.of("site", "type");
    when(context.buildConstraintViolationWithTemplate(message)).thenReturn(builder);
    when(builder.addPropertyNode("site")).thenReturn(nodeBuilder);
    when(nodeBuilder.addPropertyNode("type")).thenReturn(nodeBuilder);
    when(nodeBuilder.addConstraintViolation()).thenReturn(context);

    ConstraintValidatorContext constraintValidatorContext =
        ValidationUtils.addConstraintMessage(context, fields, message);
    assertNotNull(constraintValidatorContext);
    verify(nodeBuilder, never()).addPropertyNode("site");
    verify(nodeBuilder).addPropertyNode("type");
  }

  @Test
  void checkHasNoDuplicates() {
    int[] case1 = null;
    int[] case2 = {};
    int[] case3 = {1, 2, 3};
    int[] case4 = {1, 2, 2};
    assertTrue(ValidationUtils.hasNoDuplicates(case1));
    assertTrue(ValidationUtils.hasNoDuplicates(case2));
    assertTrue(ValidationUtils.hasNoDuplicates(case3));
    assertFalse(ValidationUtils.hasNoDuplicates(case4));
  }

  private void verifyValidationMessage(String expectedMessage) {
    verify(context).buildConstraintViolationWithTemplate(expectedMessage);
  }

  @Test
  void checkValidateAllObjectsNotNull() {
    String message = "one or more value(s) are null";
    String field = "validation check";

    int obj1 = 2;
    String obj2 = "test_string";
    Boolean obj3 = false;
    String obj4 = null;
    when(context.buildConstraintViolationWithTemplate(message)).thenReturn(builder);
    when(builder.addPropertyNode(field)).thenReturn(nodeBuilder);
    when(nodeBuilder.addConstraintViolation()).thenReturn(context);

    assertTrue(
        ValidationUtils.validateAllObjectsNotNull(context, field, message, obj1, obj2, obj3));

    assertFalse(
        ValidationUtils.validateAllObjectsNotNull(context, field, message, obj1, obj2, obj4));

    verifyValidationMessage(message);
  }
}
