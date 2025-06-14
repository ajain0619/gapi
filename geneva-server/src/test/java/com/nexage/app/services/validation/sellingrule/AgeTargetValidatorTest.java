package com.nexage.app.services.validation.sellingrule;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.nexage.admin.core.enums.MatchType;
import com.nexage.admin.core.enums.RuleTargetType;
import com.nexage.admin.core.enums.Status;
import com.nexage.app.dto.sellingrule.RuleTargetDTO;
import com.nexage.app.error.ServerErrorCodes;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class AgeTargetValidatorTest {

  private AgeTargetValidator createValidator() {
    final AgeTargetValidator validator = new AgeTargetValidator();
    return validator;
  }

  @ParameterizedTest
  @ValueSource(
      strings = {
        "18-20,25-34,35-44,45-54,55-64,65-,UNKNOWN_AND_UNDER_18",
        "18-20,65-",
        "UNKNOWN_AND_UNDER_18",
        "65-",
        "35-44"
      })
  void shouldNotThrowExceptionWhenAgeTargetHasValidTargetedAgeGrps(String data) {
    AgeTargetValidator validator = createValidator();
    assertDoesNotThrow(() -> validator.accept(generateAgeTarget(data)));
  }

  @ParameterizedTest
  @ValueSource(strings = {" 25-34,45-54", "25-34, 45-54"})
  void shouldThrowBadRequestWhenAgeTargetHasAgeGrpTargetDataWithSpace(String data) {
    AgeTargetValidator validator = createValidator();
    expectValidationFailure(validator, data, ServerErrorCodes.SERVER_TARGET_INVALID_AGEGRP);
  }

  @Test
  void shouldThrowBadRequestWhenAgeTargetHasInvalidTargetedAgeGrps() {
    final String data = "37-47";
    AgeTargetValidator validator = createValidator();
    expectValidationFailure(validator, data, ServerErrorCodes.SERVER_TARGET_INVALID_AGEGRP);
  }

  @Test
  void shouldThrowBadRequestWhenDataIsNull() {
    final String data = null;
    AgeTargetValidator validator = createValidator();
    expectValidationFailure(validator, data, ServerErrorCodes.SERVER_RULE_TARGET_DATA_IS_BLANK);
  }

  @Test
  void shouldThrowBadRequestWhenDataIsBlank() {
    final String data = "   ";
    AgeTargetValidator validator = createValidator();
    expectValidationFailure(validator, data, ServerErrorCodes.SERVER_RULE_TARGET_DATA_IS_BLANK);
  }

  private void expectValidationFailure(
      AgeTargetValidator validator, String data, ServerErrorCodes expectedError) {
    RuleTargetDTO ruleTargetDTO = generateAgeTarget(data);
    GenevaValidationException GenevaValidationException =
        assertThrows(GenevaValidationException.class, () -> validator.accept(ruleTargetDTO));
    assertEquals(expectedError, GenevaValidationException.getErrorCode());
  }

  private RuleTargetDTO generateAgeTarget(String data) {
    return RuleTargetDTO.builder()
        .pid(1L)
        .version(1)
        .status(Status.ACTIVE)
        .targetType(RuleTargetType.AGE)
        .matchType(MatchType.INCLUDE_LIST)
        .data(data)
        .build();
  }
}
