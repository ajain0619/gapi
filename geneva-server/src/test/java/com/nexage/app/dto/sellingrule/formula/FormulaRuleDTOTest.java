package com.nexage.app.dto.sellingrule.formula;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class FormulaRuleDTOTest {

  @ParameterizedTest
  @CsvSource({
    "'PLACEMENT_NAME', false",
    "'DOMAIN', true",
    "'APP_BUNDLE', true",
    "'APP_ALIAS', true"
  })
  void shouldCorrectlyIdentifyDomainAppAttribute(
      FormulaAttributeDTO inputFormulaAttribute, boolean result) {

    FormulaRuleDTO formulaRuleDTO =
        new FormulaRuleDTO(inputFormulaAttribute, FormulaOperatorDTO.EQUALS, "Rule_data", 123L);

    assertEquals(result, formulaRuleDTO.isDomainAppAttribute());
  }
}
