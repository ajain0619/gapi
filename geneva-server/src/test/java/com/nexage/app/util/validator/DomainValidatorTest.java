package com.nexage.app.util.validator;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import javax.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class DomainValidatorTest {

  @Mock private ConstraintValidatorContext context;
  @Mock private DomainConstraint domainConstraint;
  private DomainValidator validator = new DomainValidator();

  @BeforeEach
  public void setup() {
    initializeConstraint();
  }

  @Test
  void validDomain() {
    assertTrue(validator.isValid("www.validdomain.com", context));
  }

  @Test
  void invalidDomain() {
    assertFalse(validator.isValid("www.invaliddomain", context));
  }

  private void initializeConstraint() {
    ReflectionTestUtils.setField(validator, "annotation", domainConstraint);
  }
}
