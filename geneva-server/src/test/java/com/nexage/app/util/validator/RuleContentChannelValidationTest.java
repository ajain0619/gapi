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
class RuleContentChannelValidationTest {

  @InjectMocks private RuleContentChannelValidation ruleContentChannelValidation;
  @Spy private ObjectMapper objectMapper;

  @Test
  void shouldValidateEmptyDataAndReturnFalse() {
    assertFalse(ruleContentChannelValidation.isValid(""));
  }

  @Test
  void shouldValidateInvalidContentChannelDataAndReturnFalse() {
    assertFalse(ruleContentChannelValidation.isValid("Invalid Json"));
  }

  @Test
  void shouldValidateContentChannelDataWithoutChannelAndReturnFalse() {
    assertFalse(
        ruleContentChannelValidation.isValid(
            "{\"series\": [\"HBO\"], \"excludeTrafficWithoutChannel\" : 1 }"));
  }

  @Test
  void shouldValidateContentChannelDataWithEmptyChannelArrayWithTrafficFlagAndReturnTrue() {
    assertTrue(
        ruleContentChannelValidation.isValid(
            "{\"channels\": [], \"excludeTrafficWithoutChannel\" : 1 }"));
  }

  @Test
  void shouldValidateContentChannelDataWithEmptyChannelArrayWithoutTrafficFlagAndReturnFalse() {
    assertFalse(
        ruleContentChannelValidation.isValid(
            "{\"channels\": [], \"excludeTrafficWithoutChannel\" : 0 }"));
  }

  @Test
  void shouldValidateContentChannelDataWithLengthyChannelAndReturnFalse() {
    assertFalse(
        ruleContentChannelValidation.isValid(
            "{\"channels\": [\"ThisIsAnInvalidChannelNameWithMoreThanThirtyTwoCharacters\" ,\"HBO\" ,\"Discovery\" ], \"excludeTrafficWithoutChannel\" : 1 }"));
  }

  @Test
  void shouldValidateContentChannelDataWithCommaAndReturnFalse() {
    assertFalse(
        ruleContentChannelValidation.isValid(
            "{\"channels\": [\"InvalidChannel,Withcomma\" ,\"HBO\" ,\"Discovery\" ], \"excludeTrafficWithoutChannel\" : 1 }"));
  }

  @Test
  void shouldValidateContentChannelDataNotAsArrayAndReturnFalse() {
    assertFalse(
        ruleContentChannelValidation.isValid(
            "{\"channels\": \"Discovery\", \"excludeTrafficWithoutChannel\" : 1 }"));
  }

  @Test
  void shouldValidateContentChannelDataNotAsArrayWithoutFlagAndReturnFalse() {
    assertFalse(ruleContentChannelValidation.isValid("{\"channels\": \"Discovery\"}"));
  }

  @Test
  void shouldValidateContentChannelDataWithoutTrafficFlagAndReturnFalse() {
    assertFalse(ruleContentChannelValidation.isValid("{\"channels\": [\"HBO\" ,\"Discovery\" ]"));
  }

  @Test
  void shouldValidateContentChannelDataWithInvalidTrafficFlagAndReturnFalse() {
    assertFalse(
        ruleContentChannelValidation.isValid(
            "{\"channels\": [\"HBO\" ,\"Discovery\" ], \"excludeTrafficWithoutChannel\" : 2 }"));
  }

  @Test
  void shouldValidateContentChannelDataWithoutChannelsAndReturnFalse() {
    assertFalse(ruleContentChannelValidation.isValid("{\"excludeTrafficWithoutChannel\" : 1 }"));
  }

  @Test
  void shouldValidateContentChannelDataWithInvalidElementAndReturnFalse() {
    assertFalse(
        ruleContentChannelValidation.isValid(
            "{\"channels\": [\"HBO\" ,\"Discovery\" ], \"excludeTrafficWithoutChannel\" : 1 , \"random\": 1234 }"));
  }

  @Test
  void shouldValidateContentChannelDataAndReturnTrue() {
    assertTrue(
        ruleContentChannelValidation.isValid(
            "{\"channels\": [\"CNN\" ,\"HBO\" ,\"Discovery\"], \"excludeTrafficWithoutChannel\" :1 }"));
  }

  @Test
  void shouldTestContentChannelRuleTargetAndReturnTrue() {
    assertEquals(RuleTargetType.CONTENT_CHANNEL, ruleContentChannelValidation.getRuleTarget());
  }
}
