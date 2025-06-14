package com.nexage.app.services.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.nexage.admin.core.validator.CreateGroup;
import com.nexage.admin.core.validator.UpdateGroup;
import com.nexage.app.error.EntityConstraintViolationException;
import java.util.List;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;

class BeanValidationServiceImplTest {

  private static final Long ID_OK = 1L;
  private static final String NAME_OK = "Valid name";
  private static final List<String> INTERESTS_OK = List.of("coding");
  private static final int AGE_OK = 38;

  private static final Long ID_NULL = null;
  private static final String NAME_BLANK = "";
  private static final List<String> INTERESTS_EMPTY = List.of();
  private static final int AGE_INVALID = -1;

  private final BeanValidationServiceImpl beanValidationService =
      new BeanValidationServiceImpl(Validation.buildDefaultValidatorFactory().getValidator());

  @Test
  void shouldNotThrowOnCorrectInput() {
    var person = new Person(ID_OK, NAME_OK, INTERESTS_OK, AGE_OK);
    beanValidationService.validate(person);
  }

  @Test
  void shouldThrowOnIncorrectInputAndProvideValidationErrorsForEachFailedConstraint() {
    // when
    var person = new Person(ID_NULL, NAME_BLANK, INTERESTS_EMPTY, AGE_INVALID);
    EntityConstraintViolationException exception =
        assertThrows(
            EntityConstraintViolationException.class, () -> beanValidationService.validate(person));

    // then
    Set<ConstraintViolation<?>> violations = exception.getConstraintViolations();
    assertEquals(4, violations.size());
    assertTrue(matchViolation(violations, "id", "must not be null"));
    assertTrue(matchViolation(violations, "name", "must not be blank"));
    assertTrue(matchViolation(violations, "interests", "must not be empty"));
    assertTrue(matchViolation(violations, "age", "must be greater than or equal to 0"));
  }

  @Test
  void shouldNotThrowOnValidInputUsingGroups() {
    var entity = new Entity(ID_OK);
    beanValidationService.validate(entity, UpdateGroup.class);
  }

  @Test
  void shouldThrowOnInvalidInputUsingGroups() {
    // when
    var entity = new Entity(ID_OK);
    EntityConstraintViolationException exception =
        assertThrows(
            EntityConstraintViolationException.class,
            () -> beanValidationService.validate(entity, CreateGroup.class));

    // then
    Set<ConstraintViolation<?>> violations = exception.getConstraintViolations();
    assertEquals(1, violations.size());
    assertTrue(matchViolation(violations, "id", "Already exists!"));
  }

  private boolean matchViolation(
      Set<ConstraintViolation<?>> violations, String fieldName, String message) {
    return violations.stream()
        .anyMatch(
            violation ->
                violation.getPropertyPath().toString().equals(fieldName)
                    && violation.getMessage().equals(message));
  }

  @RequiredArgsConstructor
  private static class Person {
    @NotNull public final Long id;
    @NotBlank public final String name;
    @NotEmpty public final List<String> interests;

    @Min(0)
    public final int age;
  }

  @RequiredArgsConstructor
  private static class Entity {
    @Null(groups = CreateGroup.class, message = "Already exists!")
    public final Long id;
  }
}
