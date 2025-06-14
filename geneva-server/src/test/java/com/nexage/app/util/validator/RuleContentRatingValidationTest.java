package com.nexage.app.util.validator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nexage.admin.core.enums.RuleTargetType;
import com.nexage.admin.core.model.ContentRating;
import com.nexage.admin.core.repository.ContentRatingRepository;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RuleContentRatingValidationTest {
  @Mock ContentRatingRepository contentRatingRepository;

  @InjectMocks private RuleContentRatingValidation ruleContentRatingValidation;
  @Spy private ObjectMapper objectMapper;

  @Test
  void shouldValidateEmptyContentRatingData() {
    assertFalse(ruleContentRatingValidation.isValid(""));
  }

  @Test
  void shouldValidateInvalidContentRatingData() {
    assertFalse(ruleContentRatingValidation.isValid("invalid"));
  }

  @Test
  void shouldValidateContentRatingWithoutRating() {
    assertFalse(ruleContentRatingValidation.isValid("{\"excludeTrafficWithoutRating\" : 1 }"));
  }

  @Test
  void shouldValidateContentRatingWithoutExcludeTrafficWithoutRating() {
    assertFalse(ruleContentRatingValidation.isValid("{\"ratings\": [\"PG-13\" ,\"R\" ]}"));
  }

  @Test
  void shouldValidateContentRatingNonArray() {
    assertFalse(
        ruleContentRatingValidation.isValid(
            "{\"ratings\": \"PG-13\", \"excludeTrafficWithoutRating\" : 1 }"));
  }

  @Test
  void shouldValidateContentRatingHasUnknownField() {
    assertFalse(
        ruleContentRatingValidation.isValid(
            "{\"ratings\": [\"PG-13\" ], \"excludeTrafficWithoutRating\" : 1, \"dummy\" : 1 }"));
  }

  @Test
  void shouldValidateContentRatingHasInvalidExcludeTrafficWithoutRating() {
    assertFalse(
        ruleContentRatingValidation.isValid(
            "{\"ratings\": [\"PG-13\" ], \"excludeTrafficWithoutRating\" : 2}"));
  }

  @Test
  void shouldValidateContentRatingHasEmptyRatingWithTrafficFlag() {
    assertTrue(
        ruleContentRatingValidation.isValid(
            "{\"ratings\": [], \"excludeTrafficWithoutRating\" : 1}"));
  }

  @Test
  void shouldValidateContentRatingHasEmptyRatingWithOutTrafficFlag() {
    assertFalse(
        ruleContentRatingValidation.isValid(
            "{\"ratings\": [], \"excludeTrafficWithoutRating\" : 0}"));
  }

  @Test
  void shouldValidateContentRatingIsValid() {
    ContentRating contentRating = new ContentRating();
    contentRating.setRating("PG-13");
    Mockito.when(contentRatingRepository.findByRatingIn(any())).thenReturn(List.of(contentRating));
    assertTrue(
        ruleContentRatingValidation.isValid(
            "{\"ratings\": [\"PG-13\" ], \"excludeTrafficWithoutRating\" : 1 }"));
  }

  @Test
  void shouldValidateContentRatingIsInvalidValid() {
    Mockito.when(contentRatingRepository.findByRatingIn(any())).thenReturn(Collections.emptyList());
    assertFalse(
        ruleContentRatingValidation.isValid(
            "{\"ratings\": [\"PG-13\" ], \"excludeTrafficWithoutRating\" : 1 }"));
  }

  @Test
  void shouldValidateContentRatingIsValidWithTrafficFlagFalse() {
    ContentRating contentRating = new ContentRating();
    contentRating.setRating("PG-13");
    Mockito.when(contentRatingRepository.findByRatingIn(any())).thenReturn(List.of(contentRating));
    assertTrue(
        ruleContentRatingValidation.isValid(
            "{\"ratings\": [\"PG-13\" ], \"excludeTrafficWithoutRating\" : 0 }"));
  }

  @Test
  void shouldTestRuleTarget() {
    assertEquals(RuleTargetType.CONTENT_RATING, ruleContentRatingValidation.getRuleTarget());
  }
}
