package com.nexage.app.services.validation.sellingrule;

import com.nexage.admin.core.enums.MatchType;
import com.nexage.admin.core.enums.RuleTargetType;
import com.nexage.admin.core.enums.Status;
import com.nexage.app.dto.sellingrule.RuleTargetDTO;
import com.nexage.app.error.ServerErrorCodes;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

class GenderTargetValidatorTest {

  public static Object[][] data() {
    return new Object[][] {
      {"F", true, null},
      {"M", true, null},
      {null, false, ServerErrorCodes.SERVER_RULE_TARGET_DATA_IS_BLANK},
      {"", false, ServerErrorCodes.SERVER_RULE_TARGET_DATA_IS_BLANK},
      {"  F", false, ServerErrorCodes.SERVER_GENDER_TARGET_UNSUPPORTED_GENDER},
      {"M  ", false, ServerErrorCodes.SERVER_GENDER_TARGET_UNSUPPORTED_GENDER},
      {"f", false, ServerErrorCodes.SERVER_GENDER_TARGET_UNSUPPORTED_GENDER},
      {"m", false, ServerErrorCodes.SERVER_GENDER_TARGET_UNSUPPORTED_GENDER},
      {"F,M", false, ServerErrorCodes.SERVER_GENDER_TARGET_UNSUPPORTED_GENDER},
      {"L", false, ServerErrorCodes.SERVER_GENDER_TARGET_UNSUPPORTED_GENDER},
    };
  }

  @BeforeAll
  public static void beforeClass() {
    GenderTargetValidator validator = new GenderTargetValidator();
    validator.init();
  }

  @ParameterizedTest
  @MethodSource("data")
  void testValidation(
      String targetData, Boolean expectedValid, ServerErrorCodes expectedErrorIfNotValid) {
    RuleTargetDTO target = generateGenderTarget(targetData);
    if (expectedValid) {
      RuleTargetValidatorRegistry.validate(target);
    } else {
      RuleTargetValidatorHelper.expectValidationFailure(target, expectedErrorIfNotValid);
    }
  }

  private RuleTargetDTO generateGenderTarget(String data) {
    return RuleTargetDTO.builder()
        .pid(1L)
        .version(1)
        .status(Status.ACTIVE)
        .targetType(RuleTargetType.GENDER)
        .matchType(MatchType.INCLUDE_LIST)
        .data(data)
        .build();
  }
}
