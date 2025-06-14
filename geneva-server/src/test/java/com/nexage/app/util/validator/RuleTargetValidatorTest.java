package com.nexage.app.util.validator;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import com.nexage.admin.core.enums.DealCategory;
import com.nexage.admin.core.enums.MatchType;
import com.nexage.admin.core.enums.RuleTargetType;
import com.nexage.admin.core.enums.Status;
import com.nexage.app.dto.sellingrule.RuleTargetDTO;
import java.lang.annotation.Annotation;
import java.util.Set;
import javax.validation.ConstraintValidatorContext;
import javax.validation.ConstraintValidatorContext.ConstraintViolationBuilder;
import javax.validation.Payload;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RuleTargetValidatorTest {
  @Mock ConstraintValidatorContext constraintValidatorContext;
  @Mock ConstraintViolationBuilder constraintViolationBuilder;

  private RuleTargetValidator ruleTargetValidator;

  @Test
  void shouldReturnTrueWhenDeviceTypeRuleTargetValidationIsTrue() {
    RuleTargetDTO ruleTargetDTO =
        RuleTargetDTO.builder()
            .data("Phone")
            .status(Status.ACTIVE)
            .matchType(MatchType.INCLUDE_LIST)
            .targetType(RuleTargetType.DEVICE_TYPE)
            .build();

    ruleTargetValidator =
        new RuleTargetValidator(
            Set.of(createRuleTargetValidation(true, RuleTargetType.DEVICE_TYPE)));
    ruleTargetValidator.initialize(
        createAnnotation(new RuleTargetType[] {RuleTargetType.DEVICE_TYPE}));
    assertTrue(ruleTargetValidator.isValid(ruleTargetDTO, constraintValidatorContext));
  }

  @Test
  void shouldReturnFalseWhenDeviceTypeRuleTargetValidationIsFalse() {
    when(constraintValidatorContext.buildConstraintViolationWithTemplate(anyString()))
        .thenReturn(constraintViolationBuilder);
    RuleTargetDTO ruleTargetDTO =
        RuleTargetDTO.builder()
            .data("Phone")
            .status(Status.ACTIVE)
            .matchType(MatchType.INCLUDE_LIST)
            .targetType(RuleTargetType.DEVICE_TYPE)
            .build();

    ruleTargetValidator =
        new RuleTargetValidator(
            Set.of(createRuleTargetValidation(false, RuleTargetType.DEVICE_TYPE)));
    ruleTargetValidator.initialize(
        createAnnotation(new RuleTargetType[] {RuleTargetType.DEVICE_TYPE}));
    assertFalse(ruleTargetValidator.isValid(ruleTargetDTO, constraintValidatorContext));
  }

  @Test
  void shouldReturnFalseWhenNonDeviceTypeRuleTargetValidation() {
    RuleTargetDTO ruleTargetDTO =
        RuleTargetDTO.builder()
            .data("Phone")
            .status(Status.ACTIVE)
            .matchType(MatchType.INCLUDE_LIST)
            .targetType(RuleTargetType.COUNTRY)
            .build();

    ruleTargetValidator =
        new RuleTargetValidator(
            Set.of(createRuleTargetValidation(false, RuleTargetType.DEVICE_TYPE)));
    ruleTargetValidator.initialize(
        createAnnotation(new RuleTargetType[] {RuleTargetType.DEVICE_TYPE}));
    assertTrue(ruleTargetValidator.isValid(ruleTargetDTO, constraintValidatorContext));
  }

  @Test
  void shouldReturnFalseWhenRuleTargetValidationIsNull() {
    RuleTargetDTO ruleTargetDTO =
        RuleTargetDTO.builder()
            .data("Phone")
            .status(Status.ACTIVE)
            .matchType(MatchType.INCLUDE_LIST)
            .targetType(RuleTargetType.DEVICE_TYPE)
            .build();

    ruleTargetValidator = new RuleTargetValidator(null);
    ruleTargetValidator.initialize(
        createAnnotation(new RuleTargetType[] {RuleTargetType.DEVICE_TYPE}));
    assertFalse(ruleTargetValidator.isValid(ruleTargetDTO, constraintValidatorContext));
  }

  @Test
  void shouldReturnTrueWhenContentChannelRuleTargetValidationIsTrue() {
    RuleTargetDTO ruleTargetDTO =
        RuleTargetDTO.builder()
            .data(
                "{\"channels\": [\"CNN\" ,\"HBO\" ,\"Discovery\"], \"excludeTrafficWithoutChannel\" :1 }")
            .status(Status.ACTIVE)
            .matchType(MatchType.INCLUDE_LIST)
            .targetType(RuleTargetType.CONTENT_CHANNEL)
            .build();

    ruleTargetValidator =
        new RuleTargetValidator(
            Set.of(createRuleTargetValidation(true, RuleTargetType.CONTENT_CHANNEL)));
    ruleTargetValidator.initialize(
        createAnnotation(new RuleTargetType[] {RuleTargetType.CONTENT_CHANNEL}));
    assertTrue(ruleTargetValidator.isValid(ruleTargetDTO, constraintValidatorContext));
  }

  @Test
  void shouldReturnFalseWhenContentChannelRuleTargetValidationIsFalse() {
    when(constraintValidatorContext.buildConstraintViolationWithTemplate(anyString()))
        .thenReturn(constraintViolationBuilder);
    RuleTargetDTO ruleTargetDTO =
        RuleTargetDTO.builder()
            .data("Invalid JSON")
            .status(Status.ACTIVE)
            .matchType(MatchType.INCLUDE_LIST)
            .targetType(RuleTargetType.CONTENT_CHANNEL)
            .build();

    ruleTargetValidator =
        new RuleTargetValidator(
            Set.of(createRuleTargetValidation(false, RuleTargetType.CONTENT_CHANNEL)));
    ruleTargetValidator.initialize(
        createAnnotation(new RuleTargetType[] {RuleTargetType.CONTENT_CHANNEL}));
    assertFalse(ruleTargetValidator.isValid(ruleTargetDTO, constraintValidatorContext));
  }

  @Test
  void shouldReturnFalseWhenContentRatingRuleTargetValidationIsFalse() {
    when(constraintValidatorContext.buildConstraintViolationWithTemplate(anyString()))
        .thenReturn(constraintViolationBuilder);
    RuleTargetDTO ruleTargetDTO =
        RuleTargetDTO.builder()
            .data("invalid")
            .status(Status.ACTIVE)
            .matchType(MatchType.INCLUDE_LIST)
            .targetType(RuleTargetType.CONTENT_RATING)
            .build();

    ruleTargetValidator =
        new RuleTargetValidator(
            Set.of(createRuleTargetValidation(false, RuleTargetType.CONTENT_RATING)));
    ruleTargetValidator.initialize(
        createAnnotation(new RuleTargetType[] {RuleTargetType.CONTENT_RATING}));
    assertFalse(ruleTargetValidator.isValid(ruleTargetDTO, constraintValidatorContext));
  }

  @Test
  void shouldReturnTrueWhenContentRatingRuleTargetValidationIsTrue() {
    RuleTargetDTO ruleTargetDTO =
        RuleTargetDTO.builder()
            .data("{\"ratings\": [\"PG-13\" ], \"excludeTrafficWithoutRating\" : 1 }")
            .status(Status.ACTIVE)
            .matchType(MatchType.INCLUDE_LIST)
            .targetType(RuleTargetType.CONTENT_RATING)
            .build();

    ruleTargetValidator =
        new RuleTargetValidator(
            Set.of(createRuleTargetValidation(true, RuleTargetType.CONTENT_RATING)));
    ruleTargetValidator.initialize(
        createAnnotation(new RuleTargetType[] {RuleTargetType.CONTENT_RATING}));
    assertTrue(ruleTargetValidator.isValid(ruleTargetDTO, constraintValidatorContext));
  }

  @Test
  void shouldReturnFalseWhenContentGenreRuleTargetValidationIsFalse() {
    when(constraintValidatorContext.buildConstraintViolationWithTemplate(anyString()))
        .thenReturn(constraintViolationBuilder);
    RuleTargetDTO ruleTargetDTO =
        RuleTargetDTO.builder()
            .data("invalid")
            .status(Status.ACTIVE)
            .matchType(MatchType.INCLUDE_LIST)
            .targetType(RuleTargetType.CONTENT_GENRE)
            .build();

    ruleTargetValidator =
        new RuleTargetValidator(
            Set.of(createRuleTargetValidation(false, RuleTargetType.CONTENT_GENRE)));
    ruleTargetValidator.initialize(
        createAnnotation(new RuleTargetType[] {RuleTargetType.CONTENT_GENRE}));
    assertFalse(ruleTargetValidator.isValid(ruleTargetDTO, constraintValidatorContext));
  }

  @Test
  void shouldReturnTrueWhenContentGenreRuleTargetValidationIsTrue() {
    RuleTargetDTO ruleTargetDTO =
        RuleTargetDTO.builder()
            .data("{\"genres\": [\"Action\" ], \"excludeTrafficWithoutGenre\" : 1}")
            .status(Status.ACTIVE)
            .matchType(MatchType.INCLUDE_LIST)
            .targetType(RuleTargetType.CONTENT_GENRE)
            .build();

    ruleTargetValidator =
        new RuleTargetValidator(
            Set.of(createRuleTargetValidation(true, RuleTargetType.CONTENT_GENRE)));
    ruleTargetValidator.initialize(
        createAnnotation(new RuleTargetType[] {RuleTargetType.CONTENT_GENRE}));
    assertTrue(ruleTargetValidator.isValid(ruleTargetDTO, constraintValidatorContext));
  }

  @Test
  void shouldReturnFalseWhenAdFormatTypeRuleTargetValidationIsFalse() {
    when(constraintValidatorContext.buildConstraintViolationWithTemplate(anyString()))
        .thenReturn(constraintViolationBuilder);
    RuleTargetDTO ruleTargetDTO =
        RuleTargetDTO.builder()
            .data("VIDEO,BANNER")
            .status(Status.ACTIVE)
            .matchType(MatchType.INCLUDE_LIST)
            .targetType(RuleTargetType.AD_FORMAT_TYPE)
            .build();

    ruleTargetValidator =
        new RuleTargetValidator(
            Set.of(createRuleTargetValidation(false, RuleTargetType.AD_FORMAT_TYPE)));
    ruleTargetValidator.initialize(
        createAnnotation(new RuleTargetType[] {RuleTargetType.AD_FORMAT_TYPE}));
    assertFalse(ruleTargetValidator.isValid(ruleTargetDTO, constraintValidatorContext));
  }

  @Test
  void shouldReturnTrueWhenAdFormatTypeRuleTargetValidationIsTrue() {
    RuleTargetDTO ruleTargetDTO =
        RuleTargetDTO.builder()
            .data("BANNER")
            .status(Status.ACTIVE)
            .matchType(MatchType.INCLUDE_LIST)
            .targetType(RuleTargetType.AD_FORMAT_TYPE)
            .build();

    ruleTargetValidator =
        new RuleTargetValidator(
            Set.of(createRuleTargetValidation(true, RuleTargetType.AD_FORMAT_TYPE)));
    ruleTargetValidator.initialize(
        createAnnotation(new RuleTargetType[] {RuleTargetType.AD_FORMAT_TYPE}));
    assertTrue(ruleTargetValidator.isValid(ruleTargetDTO, constraintValidatorContext));
  }

  @Test
  void shouldReturnTrueWhenDealCategoryTypeRuleTargetValidationIsTrue() {
    RuleTargetDTO ruleTargetDTO =
        RuleTargetDTO.builder()
            .data(DealCategory.SSP.asInt() + "," + DealCategory.SSP.asInt())
            .status(Status.ACTIVE)
            .matchType(MatchType.INCLUDE_LIST)
            .targetType(RuleTargetType.DEAL_CATEGORY)
            .build();

    ruleTargetValidator =
        new RuleTargetValidator(
            Set.of(createRuleTargetValidation(true, RuleTargetType.DEAL_CATEGORY)));
    ruleTargetValidator.initialize(
        createAnnotation(new RuleTargetType[] {RuleTargetType.DEAL_CATEGORY}));
    assertTrue(ruleTargetValidator.isValid(ruleTargetDTO, constraintValidatorContext));
  }

  @Test
  void shouldReturnFalseWhenDealCategoryTypeRuleTargetValidationIsFalse() {
    when(constraintValidatorContext.buildConstraintViolationWithTemplate(anyString()))
        .thenReturn(constraintViolationBuilder);
    RuleTargetDTO ruleTargetDTO =
        RuleTargetDTO.builder()
            .data(String.valueOf(DealCategory.SSP.asInt()))
            .status(Status.ACTIVE)
            .matchType(MatchType.INCLUDE_LIST)
            .targetType(RuleTargetType.DEAL_CATEGORY)
            .build();

    ruleTargetValidator =
        new RuleTargetValidator(
            Set.of(createRuleTargetValidation(false, RuleTargetType.DEAL_CATEGORY)));
    ruleTargetValidator.initialize(
        createAnnotation(new RuleTargetType[] {RuleTargetType.DEAL_CATEGORY}));
    assertFalse(ruleTargetValidator.isValid(ruleTargetDTO, constraintValidatorContext));
  }

  @Test
  void shouldReturnFalseWhenContentLivestreamRuleTargetValidationIsFalse() {
    when(constraintValidatorContext.buildConstraintViolationWithTemplate(anyString()))
        .thenReturn(constraintViolationBuilder);
    RuleTargetDTO ruleTargetDTO =
        RuleTargetDTO.builder()
            .data("invalid")
            .status(Status.ACTIVE)
            .matchType(MatchType.INCLUDE_LIST)
            .targetType(RuleTargetType.CONTENT_LIVESTREAM)
            .build();

    ruleTargetValidator =
        new RuleTargetValidator(
            Set.of(createRuleTargetValidation(false, RuleTargetType.CONTENT_LIVESTREAM)));
    ruleTargetValidator.initialize(
        createAnnotation(new RuleTargetType[] {RuleTargetType.CONTENT_LIVESTREAM}));
    assertFalse(ruleTargetValidator.isValid(ruleTargetDTO, constraintValidatorContext));
  }

  @Test
  void shouldReturnTrueWhenContentLivestreamRuleTargetValidationIsTrue() {
    RuleTargetDTO ruleTargetDTO =
        RuleTargetDTO.builder()
            .data("{\"Livestreams\": [\"LIVE\" ], \"excludeTrafficWithoutLivestream\" : 1}")
            .status(Status.ACTIVE)
            .matchType(MatchType.INCLUDE_LIST)
            .targetType(RuleTargetType.CONTENT_LIVESTREAM)
            .build();

    ruleTargetValidator =
        new RuleTargetValidator(
            Set.of(createRuleTargetValidation(true, RuleTargetType.CONTENT_LIVESTREAM)));
    ruleTargetValidator.initialize(
        createAnnotation(new RuleTargetType[] {RuleTargetType.CONTENT_LIVESTREAM}));
    assertTrue(ruleTargetValidator.isValid(ruleTargetDTO, constraintValidatorContext));
  }

  @Test
  void shouldReturnFalseWhenExcludeTrafficWithoutLanguageNotPresentAndInvalidLanguage() {
    when(constraintValidatorContext.buildConstraintViolationWithTemplate(anyString()))
        .thenReturn(constraintViolationBuilder);
    RuleTargetDTO ruleTargetDTO =
        RuleTargetDTO.builder()
            .data("invalid")
            .status(Status.ACTIVE)
            .matchType(MatchType.INCLUDE_LIST)
            .targetType(RuleTargetType.CONTENT_LANGUAGE)
            .build();

    ruleTargetValidator =
        new RuleTargetValidator(
            Set.of(createRuleTargetValidation(false, RuleTargetType.CONTENT_LANGUAGE)));
    ruleTargetValidator.initialize(
        createAnnotation(new RuleTargetType[] {RuleTargetType.CONTENT_LANGUAGE}));
    assertFalse(ruleTargetValidator.isValid(ruleTargetDTO, constraintValidatorContext));
  }

  @Test
  void shouldReturnTrueWhenExcludeTrafficWithoutLanguageIsOneAndLanguageIsValid() {
    RuleTargetDTO ruleTargetDTO =
        RuleTargetDTO.builder()
            .data("{\"languages\": [\"fr\" ], \"excludeTrafficWithoutLanguage\" : 1 }")
            .status(Status.ACTIVE)
            .matchType(MatchType.INCLUDE_LIST)
            .targetType(RuleTargetType.CONTENT_LANGUAGE)
            .build();

    ruleTargetValidator =
        new RuleTargetValidator(
            Set.of(createRuleTargetValidation(true, RuleTargetType.CONTENT_LANGUAGE)));
    ruleTargetValidator.initialize(
        createAnnotation(new RuleTargetType[] {RuleTargetType.CONTENT_LANGUAGE}));
    assertTrue(ruleTargetValidator.isValid(ruleTargetDTO, constraintValidatorContext));
  }

  private RuleTargetValidation createRuleTargetValidation(
      boolean validValue, RuleTargetType ruleTargetType) {
    return new RuleTargetValidation() {
      @Override
      public boolean isValid(String data) {
        return validValue;
      }

      @Override
      public RuleTargetType getRuleTarget() {
        return ruleTargetType;
      }
    };
  }

  private RuleTargetConstraint createAnnotation(RuleTargetType[] ruleTargetTypes) {
    return new RuleTargetConstraint() {
      @Override
      public String message() {
        return null;
      }

      @Override
      public Class<?>[] groups() {
        return new Class[0];
      }

      @Override
      public Class<? extends Payload>[] payload() {
        return new Class[0];
      }

      @Override
      public RuleTargetType[] allowedTargets() {
        return ruleTargetTypes;
      }

      @Override
      public Class<? extends Annotation> annotationType() {
        return null;
      }
    };
  }
}
