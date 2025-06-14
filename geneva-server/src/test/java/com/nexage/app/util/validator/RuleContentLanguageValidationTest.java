package com.nexage.app.util.validator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nexage.admin.core.enums.RuleTargetType;
import com.nexage.admin.core.model.IsoLanguage;
import com.nexage.admin.core.repository.IsoLanguageRepository;
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
class RuleContentLanguageValidationTest {
  @Mock IsoLanguageRepository isoLanguageRepository;

  @InjectMocks private RuleContentLanguageValidation ruleContentLanguageValidation;
  @Spy private ObjectMapper objectMapper;

  @Test
  void shouldReturnValidWhenEmptyContentLanguageData() {
    assertFalse(ruleContentLanguageValidation.isValid(""));
  }

  @Test
  void shouldReturnInvalidWhenContentLanguageDataIsNotInExpectedJsonFormat() {
    assertFalse(ruleContentLanguageValidation.isValid("invalid{}"));
  }

  @Test
  void shouldReturnInvalidWhenContentLanguageHasNoLanguages() {
    assertFalse(ruleContentLanguageValidation.isValid("{\"excludeTrafficWithoutLanguage\" : 1 }"));
  }

  @Test
  void shouldReturnInvalidWhenContentLanguageHasNoExcludeTrafficWithoutLanguage() {
    assertFalse(ruleContentLanguageValidation.isValid("{\"languages\": [\"en\" ,\"fr\" ]}"));
  }

  @Test
  void shouldReturnInvalidWhenContentLanguageHasNonArray() {
    assertFalse(
        ruleContentLanguageValidation.isValid(
            "{\"languages\": \"en\", \"excludeTrafficWithoutLanguage\" : 1 }"));
  }

  @Test
  void shouldReturnInvalidWhenContentLanguageHasUnknownField() {
    assertFalse(
        ruleContentLanguageValidation.isValid(
            "{\"languages\": [\"en\" ], \"excludeTrafficWithoutLanguage\" : 1, \"dummy\" : 1 }"));
  }

  @Test
  void shouldReturnInvalidWhenContentLanguageHasInvalidExcludeTrafficWithoutLanguage() {
    assertFalse(
        ruleContentLanguageValidation.isValid(
            "{\"languages\": [\"en\" ], \"excludeTrafficWithoutLanguage\" : 2}"));
  }

  @Test
  void shouldReturnValidWhenContentLanguageHasEmptyLanguageWithTrafficFlag() {
    assertTrue(
        ruleContentLanguageValidation.isValid(
            "{\"languages\": [], \"excludeTrafficWithoutLanguage\" : 1}"));
  }

  @Test
  void shouldReturnInvalidWhenContentLanguageHasEmptyLanguageWithOutTrafficFlag() {
    assertFalse(
        ruleContentLanguageValidation.isValid(
            "{\"languages\": [], \"excludeTrafficWithoutLanguage\" : 0}"));
  }

  @Test
  void shouldReturnValidWhenContentLanguageIsValid() {
    IsoLanguage isoLanguage = new IsoLanguage();
    isoLanguage.setLanguageCode("en");
    Mockito.when(isoLanguageRepository.findByLanguageCodeIn(any()))
        .thenReturn(List.of(isoLanguage));
    assertTrue(
        ruleContentLanguageValidation.isValid(
            "{\"languages\": [\"en\" ], \"excludeTrafficWithoutLanguage\" : 1 }"));
  }

  @Test
  void shouldReturnInvalidWhenContentLanguageIsInvalid() {
    Mockito.when(isoLanguageRepository.findByLanguageCodeIn(any()))
        .thenReturn(Collections.emptyList());
    assertFalse(
        ruleContentLanguageValidation.isValid(
            "{\"languages\": [\"enn\" ], \"excludeTrafficWithoutLanguage\" : 1 }"));
  }

  @Test
  void shouldReturnValidWhenContentLanguageIsValidWithTrafficFlagFalse() {
    IsoLanguage isoLanguage = new IsoLanguage();
    isoLanguage.setLanguageCode("en");
    Mockito.when(isoLanguageRepository.findByLanguageCodeIn(any()))
        .thenReturn(List.of(isoLanguage));
    assertTrue(
        ruleContentLanguageValidation.isValid(
            "{\"languages\": [\"en\" ], \"excludeTrafficWithoutLanguage\" : 0 }"));
  }

  @Test
  void shouldReturnInvalidWhenDuplicateLanguagesArePresent() {
    IsoLanguage english = new IsoLanguage();
    english.setLanguageCode("en");
    IsoLanguage french = new IsoLanguage();
    french.setLanguageCode("fr");
    Mockito.when(isoLanguageRepository.findByLanguageCodeIn(any()))
        .thenReturn(List.of(english, french));
    assertFalse(
        ruleContentLanguageValidation.isValid(
            "{\"languages\": [\"en\", \"fr\", \"en\"], \"excludeTrafficWithoutLanguage\" : 0 }"));
  }

  @Test
  void shouldTestRuleTarget() {
    assertEquals(RuleTargetType.CONTENT_LANGUAGE, ruleContentLanguageValidation.getRuleTarget());
  }
}
