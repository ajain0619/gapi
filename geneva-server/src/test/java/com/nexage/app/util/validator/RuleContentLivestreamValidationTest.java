package com.nexage.app.util.validator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nexage.admin.core.enums.RuleTargetType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RuleContentLivestreamValidationTest {

  @InjectMocks private RuleContentLivestreamValidation ruleContentLivestreamValidation;
  @Spy private ObjectMapper objectMapper;

  @Test
  void shouldValidateContentLivestreamIsValid() {
    assertTrue(
        ruleContentLivestreamValidation.isValid(
            "{\"livestream\": [\"LIVE\",\"VOD\"], \"excludeTrafficWithoutLivestream\" : 1 }"));
  }

  @Test
  void shouldValidateEmptyContentLivestreamIsValid() {
    assertFalse(ruleContentLivestreamValidation.isValid(""));
  }

  @Test
  void shouldValidateInvalidContentLivestreamIsValid() {
    assertFalse(ruleContentLivestreamValidation.isValid("invalid"));
  }

  @Test
  void shouldValidateContentLivestreamWithoutLivestream() {
    assertFalse(
        ruleContentLivestreamValidation.isValid("{\"excludeTrafficWithoutLivestream\" : 1 }"));
  }

  @Test
  void shouldValidateContentLivestreamWithoutExcludeTrafficWithoutLivestream() {
    assertFalse(ruleContentLivestreamValidation.isValid("{\"livestream\": [\"LIVE\" ,\"VOD\" ]}"));
  }

  @Test
  void shouldValidateContentLivestreamNonArray() {
    assertFalse(
        ruleContentLivestreamValidation.isValid(
            "{\"livestream\": \"LIVE\", \"excludeTrafficWithoutLivestream\" : 1 }"));
  }

  @Test
  void shouldValidateContentLivestreamHasUnknownField() {
    assertFalse(
        ruleContentLivestreamValidation.isValid(
            "{\"livestream\": [\"LIVE\" ], \"excludeTrafficWithoutLivestream\" : 1, \"extra\" : 1 }"));
  }

  @Test
  void shouldValidateContentLivestreamHasInvalidexcludeTrafficWithoutLivestream() {
    assertFalse(
        ruleContentLivestreamValidation.isValid(
            "{\"livestream\": [\"LIVE\" ], \"excludeTrafficWithoutLivestream\" : 2}"));
  }

  @Test
  void shouldValidateContentLivestreamIsInvalid() {
    assertFalse(
        ruleContentLivestreamValidation.isValid(
            "{\"livestream\": [\"NONE\" ], \"excludeTrafficWithoutLivestream\" : 1 }"));
  }

  @Test
  void shouldValidateContentLivestreamHasInCorrectValueIsValid() {
    assertFalse(
        ruleContentLivestreamValidation.isValid(
            "{\"livestream\": [\"LIVE\",\"VOD\",\"ABC\"], \"excludeTrafficWithoutLivestream\" : 1 }"));
  }

  @Test
  void shouldValidateContentLivestreamHasExcludeTrafficWithoutLivestreamValueIsOffIsValid() {
    assertTrue(
        ruleContentLivestreamValidation.isValid(
            "{\"livestream\": [\"LIVE\"], \"excludeTrafficWithoutLivestream\" : 0 }"));
  }

  @Test
  void
      shouldValidateContentLivestreamHasEmptyWithExcludeTrafficWithoutLivestreamValueIsOffIsValid() {
    assertFalse(
        ruleContentLivestreamValidation.isValid(
            "{\"livestream\": [], \"excludeTrafficWithoutLivestream\" : 0 }"));
  }

  @Test
  void shouldValidateContentLivestreamHasCorrectValuesIsValid() {
    assertFalse(
        ruleContentLivestreamValidation.isValid(
            "{\"livestream\": [\"LIVE\",\"VOD\",\"VOD\"], \"excludeTrafficWithoutLivestream\" : 0 }"));
  }

  @Test
  void shouldValidateContentLivestreamHasValueExcludeTrafficWithoutLivestreamIsValid() {
    assertFalse(
        ruleContentLivestreamValidation.isValid(
            "{\"livestream\": [\"LIVE\"], \"excludeTrafficWithoutLivestream\" :  }"));
  }

  @Test
  void shouldValidateContentLivestreamHasNoValueExcludeTrafficWithoutLivestreamIsValid() {
    assertFalse(
        ruleContentLivestreamValidation.isValid(
            "{\"livestream\": [], \"excludeTrafficWithoutLivestream\" :  }"));
  }

  @Test
  void shouldTestRuleTarget() {
    assertEquals(
        RuleTargetType.CONTENT_LIVESTREAM, ruleContentLivestreamValidation.getRuleTarget());
  }
}
