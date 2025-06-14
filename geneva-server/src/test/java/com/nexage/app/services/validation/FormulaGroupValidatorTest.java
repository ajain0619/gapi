package com.nexage.app.services.validation;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

import com.nexage.app.dto.sellingrule.formula.FormulaAttributeDTO;
import com.nexage.app.dto.sellingrule.formula.FormulaGroupDTO;
import com.nexage.app.dto.sellingrule.formula.FormulaOperatorDTO;
import com.nexage.app.dto.sellingrule.formula.FormulaRuleDTO;
import com.nexage.app.util.validator.BaseValidatorTest;
import com.nexage.app.util.validator.FormulaGroupConstraint;
import com.nexage.app.util.validator.FormulaGroupValidator;
import java.util.Arrays;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

class FormulaGroupValidatorTest extends BaseValidatorTest {
  @Mock private FormulaGroupConstraint constraint;
  private static final String MESSAGE = "test message";
  @Mock private FormulaGroupDTO formulaGroup1;

  @Mock private FormulaRuleDTO formulaRule11;
  @Mock private FormulaRuleDTO formulaRule12;

  @InjectMocks private FormulaGroupValidator validator;

  @Test
  void
      shouldReturnTrueForFormulaGroupWithDifferentAttributePidSameOperatorForInventoryAttributeInDifferentFormulaRule() {
    when(formulaGroup1.getFormulaRules()).thenReturn(Arrays.asList(formulaRule11, formulaRule12));

    when(formulaRule11.getAttribute()).thenReturn(FormulaAttributeDTO.INVENTORY_ATTRIBUTE);
    when(formulaRule11.getAttributePid()).thenReturn(11L);
    when(formulaRule11.getOperator()).thenReturn(FormulaOperatorDTO.EQUALS);
    when(formulaRule12.getAttribute()).thenReturn(FormulaAttributeDTO.INVENTORY_ATTRIBUTE);
    when(formulaRule12.getAttributePid()).thenReturn(12L);
    when(formulaRule12.getOperator()).thenReturn(FormulaOperatorDTO.EQUALS);

    assertTrue(validator.isValid(formulaGroup1, ctx));
  }

  @Test
  void shouldReturnTrueForFormulaGroupWithNullAttributePidForNonInventoryAttribute() {

    when(formulaGroup1.getFormulaRules()).thenReturn(Arrays.asList(formulaRule11, formulaRule12));

    when(formulaRule11.getAttribute()).thenReturn(FormulaAttributeDTO.SITE_NAME);
    when(formulaRule11.getOperator()).thenReturn(FormulaOperatorDTO.EQUALS);
    when(formulaRule12.getAttribute()).thenReturn(FormulaAttributeDTO.SITE_NAME);
    when(formulaRule12.getOperator()).thenReturn(FormulaOperatorDTO.NOT_EQUALS);

    assertTrue(validator.isValid(formulaGroup1, ctx));
  }

  @Test
  void
      shouldReturnFalseForFormulaGroupWithSameAttributePidSameOperatorEqualsForInventoryAttributeInDifferentFormulaRule() {
    when(formulaGroup1.getFormulaRules()).thenReturn(Arrays.asList(formulaRule11, formulaRule12));

    when(formulaRule11.getAttribute()).thenReturn(FormulaAttributeDTO.INVENTORY_ATTRIBUTE);
    when(formulaRule11.getAttributePid()).thenReturn(11L);
    when(formulaRule11.getOperator()).thenReturn(FormulaOperatorDTO.EQUALS);
    when(formulaRule12.getAttribute()).thenReturn(FormulaAttributeDTO.INVENTORY_ATTRIBUTE);
    when(formulaRule12.getAttributePid()).thenReturn(11L);
    when(formulaRule12.getOperator()).thenReturn(FormulaOperatorDTO.EQUALS);

    // method under test
    assertFalse(validator.isValid(formulaGroup1, ctx));
  }

  @Test
  void
      shouldReturnFalseForFormulaGroupWithSameAttributePidSameOperatorNotEqualsForInventoryAttributeInDifferentFormulaRule() {
    when(formulaGroup1.getFormulaRules()).thenReturn(Arrays.asList(formulaRule11, formulaRule12));

    when(formulaRule11.getAttribute()).thenReturn(FormulaAttributeDTO.INVENTORY_ATTRIBUTE);
    when(formulaRule11.getAttributePid()).thenReturn(12L);
    when(formulaRule11.getOperator()).thenReturn(FormulaOperatorDTO.NOT_EQUALS);
    when(formulaRule12.getAttribute()).thenReturn(FormulaAttributeDTO.INVENTORY_ATTRIBUTE);
    when(formulaRule12.getAttributePid()).thenReturn(12L);
    when(formulaRule12.getOperator()).thenReturn(FormulaOperatorDTO.NOT_EQUALS);

    // method under test
    assertFalse(validator.isValid(formulaGroup1, ctx));
  }

  @Override
  protected void initializeConstraint() {
    lenient().when(constraint.message()).thenReturn(MESSAGE);
  }
}
