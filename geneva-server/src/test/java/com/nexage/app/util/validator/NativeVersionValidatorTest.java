package com.nexage.app.util.validator;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import javax.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class NativeVersionValidatorTest {

  @Mock private ConstraintValidatorContext context;
  private NativeVersionValidator nativeVersionValidator;

  @BeforeEach
  void setup() {
    nativeVersionValidator = new NativeVersionValidator();
  }

  @Test
  void valid() {
    assertTrue(nativeVersionValidator.isValid(null, context));
    assertTrue(nativeVersionValidator.isValid("1.0", context));
    assertTrue(nativeVersionValidator.isValid("1.1", context));
    assertTrue(nativeVersionValidator.isValid("1.2", context));
  }

  @Test
  void invalid() {
    assertFalse(nativeVersionValidator.isValid("1.0.1", context));
  }
}
