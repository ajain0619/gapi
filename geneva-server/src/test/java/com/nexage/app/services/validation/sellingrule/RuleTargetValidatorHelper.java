package com.nexage.app.services.validation.sellingrule;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

import com.nexage.admin.core.enums.RuleTargetType;
import com.nexage.app.dto.sellingrule.RuleTargetDTO;
import com.nexage.app.error.ServerErrorCodes;
import com.ssp.geneva.common.error.exception.GenevaValidationException;

public class RuleTargetValidatorHelper {
  public static void clearValidators() {
    RuleTargetValidatorRegistry.clearValidators();
  }

  public static RuleTargetValidatorRegistry.Validator mockValidator(RuleTargetType ruleTargetType) {
    RuleTargetValidatorRegistry.Validator v = mock(RuleTargetValidatorRegistry.Validator.class);
    RuleTargetValidatorRegistry.registerValidator(ruleTargetType, v);
    return v;
  }

  public static void expectValidationFailure(RuleTargetDTO target, ServerErrorCodes expectedError) {
    GenevaValidationException gve =
        assertThrows(
            GenevaValidationException.class,
            () -> RuleTargetValidatorRegistry.validate(target),
            "GenevaValidationException was expected but not thrown");
    assertEquals(expectedError, gve.getErrorCode());
  }
}
