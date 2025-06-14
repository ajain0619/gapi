package com.nexage.app.util.validator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.nexage.admin.core.enums.RuleTargetType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RuleAdFormatTypeValidationTest {

  @InjectMocks private RuleAdFormatTypeValidation ruleAdFormatTypeValidation;

  @Test
  void shouldValidateEmptyListAndReturnFalse() {
    assertFalse(ruleAdFormatTypeValidation.isValid(""));
  }

  @Test
  void shouldValidateNullAndReturnTrue() {
    assertFalse(ruleAdFormatTypeValidation.isValid(null));
  }

  @Test
  void shouldValidateValidValueAndReturnTrue() {
    assertTrue(ruleAdFormatTypeValidation.isValid("VIDEO"));
  }

  @Test
  void shouldValidateDifferentValidValueAndReturnTrue() {
    assertTrue(ruleAdFormatTypeValidation.isValid("BANNER"));
  }

  @Test
  void shouldValidateValidValueWithDifferentCaseAndReturnFalse() {
    assertFalse(ruleAdFormatTypeValidation.isValid("Video"));
  }

  @Test
  void shouldValidateMultipleValidValuesAndReturnFalse() {
    assertFalse(ruleAdFormatTypeValidation.isValid("VIDEO,BANNER"));
  }

  @Test
  void shouldValidateInvalidValuesAndReturnFalse() {
    assertFalse(ruleAdFormatTypeValidation.isValid("INVALID_VALUE"));
  }

  @Test
  void shouldValidateCombinationOfValidAndInvalidValuesAndReturnFalse() {
    assertFalse(ruleAdFormatTypeValidation.isValid("VIDEO,Banner"));
  }

  @Test
  void shouldTestAdFormatTypeRuleTargetAndReturnTrue() {
    assertEquals(RuleTargetType.AD_FORMAT_TYPE, ruleAdFormatTypeValidation.getRuleTarget());
  }
}
