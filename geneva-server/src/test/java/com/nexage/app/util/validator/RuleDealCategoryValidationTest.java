package com.nexage.app.util.validator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.nexage.admin.core.enums.DealCategory;
import com.nexage.admin.core.enums.RuleTargetType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RuleDealCategoryValidationTest {

  @InjectMocks private RuleDealCategoryValidation ruleDealCategoryValidation;

  @Test
  void shouldFailValidationWhenDataIsEmpty() {
    assertFalse(ruleDealCategoryValidation.isValid(""));
  }

  @Test
  void shouldValidateWhenDataIsValidValue() {
    assertTrue(ruleDealCategoryValidation.isValid(String.valueOf(DealCategory.SSP.asInt())));
  }

  @Test
  void shouldValidateWhenDataIsComposedOfMultipleValidValues() {
    assertTrue(ruleDealCategoryValidation.isValid(DealCategory.SSP.asInt() + ","));
  }

  @Test
  void shouldFailValidationWhenDataContainsNotAllowedValue() {
    assertFalse(
        ruleDealCategoryValidation.isValid(
            DealCategory.SSP.asInt() + "," + DealCategory.S2S_PLACEMENT_DEAL.asInt()));
  }

  @Test
  void shouldGetRuleTargetReturnRuleTargetTypeDealCategory() {
    assertEquals(RuleTargetType.DEAL_CATEGORY, ruleDealCategoryValidation.getRuleTarget());
  }
}
