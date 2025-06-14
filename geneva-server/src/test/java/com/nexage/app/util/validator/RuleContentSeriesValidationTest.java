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
class RuleContentSeriesValidationTest {

  @InjectMocks private RuleContentSeriesValidation ruleContentSeriesValidation;
  @Spy private ObjectMapper objectMapper;

  @Test
  void shouldValidateEmptyDataAndReturnFalse() {
    assertFalse(ruleContentSeriesValidation.isValid(""));
  }

  @Test
  void shouldValidateContentSeriesDataAndReturnFalse() {
    assertFalse(ruleContentSeriesValidation.isValid("Invalid Json"));
  }

  @Test
  void shouldValidateContentSeriesDataWithoutSeriesAndReturnFalse() {
    assertFalse(
        ruleContentSeriesValidation.isValid(
            "{\"channel\": \"HBO\", \"excludeTrafficWithoutSeries\" : 1 }"));
  }

  @Test
  void shouldValidateContentSeriesDataWithLengthySeriesAndReturnFalse() {
    assertFalse(
        ruleContentSeriesValidation.isValid(
            "{\"series\": [\"ThisIsAnInvalidSeriesNameWithMoreThanThirtyTwoCharacters\" ,\"HBO\" ,\"Discovery\" ], \"excludeTrafficWithoutSeries\" : 1 }"));
  }

  @Test
  void shouldValidateContentSeriesDataWithCommaAndReturnFalse() {
    assertFalse(
        ruleContentSeriesValidation.isValid(
            "{\"series\": [\"InvalidSeries,Withcomma\" ,\"HBO\" ,\"Discovery\" ], \"excludeTrafficWithoutSeries\" : 1 }"));
  }

  @Test
  void shouldValidateContentSeriesDataWithEmptySeriesArrayWithTrafficFlagAndReturnTrue() {
    assertTrue(
        ruleContentSeriesValidation.isValid(
            "{\"series\": [], \"excludeTrafficWithoutSeries\" : 1 }"));
  }

  @Test
  void shouldValidateContentSeriesDataWithEmptySeriesArrayWithoutTrafficFlagAndReturnFalse() {
    assertFalse(
        ruleContentSeriesValidation.isValid(
            "{\"series\": [], \"excludeTrafficWithoutSeries\" : 0 }"));
  }

  @Test
  void shouldValidateContentSeriesDataNotAsArrayAndReturnFalse() {
    assertFalse(
        ruleContentSeriesValidation.isValid(
            "{\"series\": \"Discovery\", \"excludeTrafficWithoutSeries\" : 1 }"));
  }

  @Test
  void shouldValidateContentSeriesDataNotAsArrayWithoutFlagAndReturnFalse() {
    assertFalse(ruleContentSeriesValidation.isValid("{\"series\": \"Discovery\"}"));
  }

  @Test
  void shouldValidateContentSeriesDataWithoutTrafficFlagAndReturnFalse() {
    assertFalse(ruleContentSeriesValidation.isValid("{\"series\": [\"HBO\" ,\"Discovery\" ]"));
  }

  @Test
  void shouldValidateContentSeriesDataWithInvalidTrafficFlagAndReturnFalse() {
    assertFalse(
        ruleContentSeriesValidation.isValid(
            "{\"series\": [\"HBO\" ,\"Discovery\" ], \"excludeTrafficWithoutSeries\" : 2 }"));
  }

  @Test
  void shouldValidateContentSeriesDataWithInvalidElementAndReturnFalse() {
    assertFalse(
        ruleContentSeriesValidation.isValid(
            "{\"series\": [\"HBO\" ,\"Discovery\" ], \"excludeTrafficWithoutSeries\" : 1 , \"random\": 1234 }"));
  }

  @Test
  void shouldValidateContentSeriesDataAndReturnTrue() {
    assertTrue(
        ruleContentSeriesValidation.isValid(
            "{\"series\": [\"CNN\" ,\"HBO\" ,\"Discovery\"], \"excludeTrafficWithoutSeries\" :0 }"));
  }

  @Test
  void shouldTestContentSeriesTargetAndReturnTrue() {
    assertEquals(RuleTargetType.CONTENT_SERIES, ruleContentSeriesValidation.getRuleTarget());
  }
}
