package com.nexage.app.util.validator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nexage.admin.core.enums.RuleTargetType;
import com.nexage.admin.core.model.ContentGenre;
import com.nexage.admin.core.repository.ContentGenreRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RuleContentGenreValidationTest {
  @Mock ContentGenreRepository contentGenreRepository;

  @InjectMocks private RuleContentGenreValidation ruleContentGenreValidation;
  @Spy private ObjectMapper objectMapper;

  @Test
  void shouldValidateEmptyContentGenreData() {
    assertFalse(ruleContentGenreValidation.isValid(""));
  }

  @Test
  void shouldValidateInvalidContentGenreData() {
    assertFalse(ruleContentGenreValidation.isValid("invalid"));
  }

  @Test
  void shouldValidateContentGenreWithoutGenre() {
    assertFalse(ruleContentGenreValidation.isValid("{\"excludeTrafficWithoutGenre\" : 1 }"));
  }

  @Test
  void shouldValidateContentGenreWithoutExcludeTrafficWithoutGenre() {
    assertFalse(ruleContentGenreValidation.isValid("{\"genres\": [\"Action\" ,\"Adventure\" ]}"));
  }

  @Test
  void shouldValidateContentGenreNonArray() {
    assertFalse(
        ruleContentGenreValidation.isValid(
            "{\"genres\": \"Action\", \"excludeTrafficWithoutGenre\" : 1 }"));
  }

  @Test
  void shouldValidateContentGenreHasUnknownField() {
    assertFalse(
        ruleContentGenreValidation.isValid(
            "{\"genres\": [\"Action\" ], \"excludeTrafficWithoutGenre\" : 1, \"dummy\" : 1 }"));
  }

  @Test
  void shouldValidateContentGenreHasInvalidexcludeTrafficWithoutGenre() {
    assertFalse(
        ruleContentGenreValidation.isValid(
            "{\"genres\": [\"Action\" ], \"excludeTrafficWithoutGenre\" : 2}"));
  }

  @Test
  void shouldValidateContentGenreHasEmptyGenreWithTrafficFlag() {
    Mockito.when(contentGenreRepository.existsByGenre(any())).thenReturn(0);
    assertTrue(
        ruleContentGenreValidation.isValid("{\"genres\": [], \"excludeTrafficWithoutGenre\" : 1}"));
  }

  @Test
  void shouldValidateContentGenreHasEmptyGenreWithOutTrafficFlag() {
    assertFalse(
        ruleContentGenreValidation.isValid("{\"genres\": [], \"excludeTrafficWithoutGenre\" : 0}"));
  }

  @Test
  void shouldValidateContentGenreIsValid() {
    ContentGenre contentGenre = new ContentGenre();
    contentGenre.setGenre("Action");
    Mockito.when(contentGenreRepository.existsByGenre(any())).thenReturn(1);
    assertTrue(
        ruleContentGenreValidation.isValid(
            "{\"genres\": [\"Action\" ], \"excludeTrafficWithoutGenre\" : 1 }"));
  }

  @Test
  void shouldValidateContentGenreIsValidWithTrafficFlagFalse() {
    ContentGenre contentGenre = new ContentGenre();
    contentGenre.setGenre("Action");
    Mockito.when(contentGenreRepository.existsByGenre(any())).thenReturn(1);
    assertTrue(
        ruleContentGenreValidation.isValid(
            "{\"genres\": [\"Action\" ], \"excludeTrafficWithoutGenre\" : 0 }"));
  }

  @Test
  void shouldValidateContentGenreIsInvalid() {
    Mockito.when(contentGenreRepository.existsByGenre(any())).thenReturn(0);
    assertFalse(
        ruleContentGenreValidation.isValid(
            "{\"genres\": [\"Dummy\" ], \"excludeTrafficWithoutGenre\" : 1 }"));
  }

  @Test
  void shouldTestRuleTarget() {
    assertEquals(RuleTargetType.CONTENT_GENRE, ruleContentGenreValidation.getRuleTarget());
  }
}
