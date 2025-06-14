package com.nexage.app.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.nexage.admin.core.model.RuleFormulaPositionView;
import com.nexage.admin.core.model.placementformula.formula.PredicateBuilder;
import com.nexage.admin.core.model.placementformula.formula.impl.InventoryAttribute;
import com.nexage.admin.core.model.placementformula.formula.impl.SimpleAttribute;
import com.nexage.app.dto.sellingrule.formula.FormulaAttributeDTO;
import com.nexage.app.dto.sellingrule.formula.FormulaOperatorDTO;
import com.nexage.app.dto.sellingrule.formula.FormulaRuleDTO;
import java.util.stream.Stream;
import org.apache.commons.lang.math.RandomUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

class FormulaRuleDTOMapperTest {

  @Test
  void shouldMapFormulaRuleDTOToInventoryAttribute() {
    Long attributePid = RandomUtils.nextLong();
    String ruleData = "ruleData";
    FormulaOperatorDTO formulaOperatorDTO = FormulaOperatorDTO.EQUALS;
    FormulaAttributeDTO formulaAttributeDTO = FormulaAttributeDTO.INVENTORY_ATTRIBUTE;

    FormulaRuleDTO formulaRuleDTO = new FormulaRuleDTO();
    formulaRuleDTO.setAttributePid(attributePid);
    formulaRuleDTO.setOperator(formulaOperatorDTO);
    formulaRuleDTO.setRuleData(ruleData);
    formulaRuleDTO.setAttribute(formulaAttributeDTO);

    InventoryAttribute inventoryAttribute =
        (InventoryAttribute) FormulaRuleDTOMapper.MAPPER.map(formulaRuleDTO);
    assertNotNull(inventoryAttribute);
    assertEquals(attributePid, inventoryAttribute.getPid());
    assertEquals(ruleData, inventoryAttribute.getValue());
    assertEquals(formulaOperatorDTO.toString(), inventoryAttribute.getOperator().toString());
  }

  @ParameterizedTest()
  @MethodSource("getSimpleFormulaAttributeDTO")
  void shouldMapFormulaRuleDTOToSimpleAttribute(FormulaAttributeDTO formulaAttributeDTO) {
    Long attributePid = RandomUtils.nextLong();
    String ruleData = "ruleData";
    FormulaOperatorDTO formulaOperatorDTO = FormulaOperatorDTO.EQUALS;

    FormulaRuleDTO formulaRuleDTO = new FormulaRuleDTO();
    formulaRuleDTO.setAttributePid(attributePid);
    formulaRuleDTO.setOperator(formulaOperatorDTO);
    formulaRuleDTO.setRuleData(ruleData);
    formulaRuleDTO.setAttribute(formulaAttributeDTO);

    SimpleAttribute simpleAttribute =
        (SimpleAttribute) FormulaRuleDTOMapper.MAPPER.map(formulaRuleDTO);
    assertNotNull(simpleAttribute);
    assertEquals(formulaAttributeDTO.getAttributeInfo(), simpleAttribute.getAttributeInfo());
    assertEquals(ruleData, simpleAttribute.getValue());
    assertEquals(formulaOperatorDTO.toString(), simpleAttribute.getOperator().toString());
  }

  @ParameterizedTest()
  @MethodSource("getDomainFormulaAttributeDTO")
  void shouldMapFormulaRuleDTOToNullForDomainAttribute(FormulaAttributeDTO formulaAttributeDTO) {
    Long attributePid = RandomUtils.nextLong();
    String ruleData = "ruleData";
    FormulaOperatorDTO formulaOperatorDTO = FormulaOperatorDTO.EQUALS;
    // FormulaAttributeDTO formulaAttributeDTO = FormulaAttributeDTO.DOMAIN;

    FormulaRuleDTO formulaRuleDTO = new FormulaRuleDTO();
    formulaRuleDTO.setAttributePid(attributePid);
    formulaRuleDTO.setOperator(formulaOperatorDTO);
    formulaRuleDTO.setRuleData(ruleData);
    formulaRuleDTO.setAttribute(formulaAttributeDTO);

    PredicateBuilder<RuleFormulaPositionView> positionViewPredicateBuilder =
        FormulaRuleDTOMapper.MAPPER.map(formulaRuleDTO);
    assertNull(positionViewPredicateBuilder);
  }

  private static Stream<FormulaAttributeDTO> getDomainFormulaAttributeDTO() {
    return Stream.of(
        FormulaAttributeDTO.DOMAIN, FormulaAttributeDTO.APP_ALIAS, FormulaAttributeDTO.APP_BUNDLE);
  }

  private static Stream<FormulaAttributeDTO> getSimpleFormulaAttributeDTO() {
    return Stream.of(
        FormulaAttributeDTO.LONG_FORM,
        FormulaAttributeDTO.PLACEMENT_NAME,
        FormulaAttributeDTO.PLACEMENT_TYPE,
        FormulaAttributeDTO.PUBLISHER_NAME,
        FormulaAttributeDTO.SITE_IAB_CATEGORY,
        FormulaAttributeDTO.SITE_NAME,
        FormulaAttributeDTO.SITE_TYPE);
  }
}
