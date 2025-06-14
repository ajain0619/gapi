package com.nexage.app.services.validation;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

import com.nexage.app.dto.sellingrule.formula.FormulaAttributeDTO;
import com.nexage.app.dto.sellingrule.formula.FormulaOperatorDTO;
import com.nexage.app.dto.sellingrule.formula.FormulaRuleDTO;
import com.nexage.app.util.validator.BaseValidatorTest;
import com.nexage.app.util.validator.FormulaRuleConstraint;
import com.nexage.app.util.validator.FormulaRuleValidator;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;

class FormulaRuleValidatorTest extends BaseValidatorTest {
  @Mock private FormulaRuleConstraint constraint;
  private static final String MESSAGE = "test message";
  @InjectMocks private FormulaRuleValidator validator;
  @Mock private FormulaRuleDTO formulaRule11;

  @Test
  void shouldReturnFalseForFormulaRuleWithNullAttributePidForInventoryAttribute() {

    when(formulaRule11.getAttribute()).thenReturn(FormulaAttributeDTO.INVENTORY_ATTRIBUTE);
    when(formulaRule11.getOperator()).thenReturn(FormulaOperatorDTO.EQUALS);
    when(formulaRule11.getAttributePid()).thenReturn(null);

    assertFalse(validator.isValid(formulaRule11, ctx));
  }

  @Test
  void shouldReturnFalseForFormulaRuleWithEmptyRuleDataForInventoryAttribute() {
    when(formulaRule11.getAttribute()).thenReturn(FormulaAttributeDTO.INVENTORY_ATTRIBUTE);
    when(formulaRule11.getOperator()).thenReturn(FormulaOperatorDTO.EQUALS);
    when(formulaRule11.getAttributePid()).thenReturn(11L);
    when(formulaRule11.getRuleData()).thenReturn("");

    // method under test
    assertFalse(validator.isValid(formulaRule11, ctx));
  }

  @Test
  void shouldReturnTrueForFormulaRuleWithNonEmptyRuleDataForInventoryAttribute() {
    when(formulaRule11.getAttribute()).thenReturn(FormulaAttributeDTO.INVENTORY_ATTRIBUTE);
    when(formulaRule11.getOperator()).thenReturn(FormulaOperatorDTO.EQUALS);
    when(formulaRule11.getAttributePid()).thenReturn(12L);
    when(formulaRule11.getRuleData()).thenReturn("dummy12,whatishere");

    // method under test
    assertTrue(validator.isValid(formulaRule11, ctx));
  }

  @ParameterizedTest
  @MethodSource("getDomainFormulaAttributes")
  void shouldReturnTrueForFormulaRuleWithDomainAttributes(FormulaAttributeDTO attributeDTO) {
    FormulaRuleDTO formulaRuleDTO = getPlacementFormulaDTOwithDomainAppAttribute(attributeDTO);

    assertTrue(validator.isValid(formulaRuleDTO, ctx));
  }

  private static Stream<Arguments> getDomainFormulaAttributes() {
    return Stream.of(
        Arguments.of(FormulaAttributeDTO.DOMAIN),
        Arguments.of(FormulaAttributeDTO.APP_ALIAS),
        Arguments.of(FormulaAttributeDTO.APP_BUNDLE));
  }

  private FormulaRuleDTO getPlacementFormulaDTOwithDomainAppAttribute(
      FormulaAttributeDTO attribute) {
    FormulaRuleDTO formulaRuleDTO = new FormulaRuleDTO();
    String ruleData =
        "{\"pid\":2,\"fileName\":\"test123.csv\",\"fileType\":\"" + attribute.toString() + "\"}";
    formulaRuleDTO.setRuleData(ruleData);
    formulaRuleDTO.setAttribute(attribute);
    formulaRuleDTO.setOperator(FormulaOperatorDTO.EQUALS);
    return formulaRuleDTO;
  }

  @Override
  protected void initializeConstraint() {
    lenient().when(constraint.message()).thenReturn(MESSAGE);
  }
}
