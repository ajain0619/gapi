package com.nexage.app.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.nexage.admin.core.model.RuleFormulaPositionView;
import com.nexage.admin.core.model.placementformula.formula.PredicateBuilder;
import com.nexage.admin.core.model.placementformula.formula.impl.InventoryAttribute;
import com.nexage.admin.core.model.placementformula.formula.impl.SimpleAttribute;
import com.nexage.app.dto.sellingrule.formula.FormulaAttributeDTO;
import com.nexage.app.dto.sellingrule.formula.FormulaGroupDTO;
import com.nexage.app.dto.sellingrule.formula.FormulaOperatorDTO;
import com.nexage.app.dto.sellingrule.formula.FormulaRuleDTO;
import java.util.List;
import org.apache.commons.lang.math.RandomUtils;
import org.junit.jupiter.api.Test;

class FormulaGroupDTOMapperTest {

  @Test
  void shouldMapFormulaGroupDTOToListOfPredicateBuilderRuleFormulaPositionView() {
    Long attributePid = RandomUtils.nextLong();
    String ruleData = "ruleData";
    FormulaOperatorDTO formulaOperatorDTO = FormulaOperatorDTO.EQUALS;
    FormulaAttributeDTO formulaAttributeDTO = FormulaAttributeDTO.INVENTORY_ATTRIBUTE;

    FormulaRuleDTO formulaRuleDTO = new FormulaRuleDTO();
    formulaRuleDTO.setAttributePid(attributePid);
    formulaRuleDTO.setOperator(formulaOperatorDTO);
    formulaRuleDTO.setRuleData(ruleData);
    formulaRuleDTO.setAttribute(formulaAttributeDTO);

    FormulaRuleDTO formulaRuleDTO2 = new FormulaRuleDTO();
    formulaRuleDTO2.setAttributePid(attributePid);
    formulaRuleDTO2.setOperator(formulaOperatorDTO);
    formulaRuleDTO2.setRuleData(ruleData);
    formulaRuleDTO2.setAttribute(FormulaAttributeDTO.PLACEMENT_NAME);

    FormulaRuleDTO formulaRuleDTO3 = new FormulaRuleDTO();
    formulaRuleDTO3.setAttributePid(attributePid);
    formulaRuleDTO3.setOperator(formulaOperatorDTO);
    formulaRuleDTO3.setRuleData(ruleData);
    formulaRuleDTO3.setAttribute(FormulaAttributeDTO.DOMAIN);

    FormulaGroupDTO formulaGroupDTO = new FormulaGroupDTO();
    formulaGroupDTO.setFormulaRules(List.of(formulaRuleDTO, formulaRuleDTO2, formulaRuleDTO3));

    List<PredicateBuilder<RuleFormulaPositionView>> predicateBuilders =
        FormulaGroupDTOMapper.MAPPER.map(formulaGroupDTO);

    InventoryAttribute inventoryAttribute = (InventoryAttribute) predicateBuilders.get(0);

    assertNotNull(inventoryAttribute);
    assertEquals(attributePid, inventoryAttribute.getPid());
    assertEquals(ruleData, inventoryAttribute.getValue());
    assertEquals(formulaOperatorDTO.toString(), inventoryAttribute.getOperator().toString());

    SimpleAttribute simpleAttribute = (SimpleAttribute) predicateBuilders.get(1);

    assertNotNull(simpleAttribute);
    assertEquals(
        FormulaAttributeDTO.PLACEMENT_NAME.getAttributeInfo(), simpleAttribute.getAttributeInfo());
    assertEquals(ruleData, simpleAttribute.getValue());
    assertEquals(formulaOperatorDTO.toString(), simpleAttribute.getOperator().toString());

    PredicateBuilder<RuleFormulaPositionView> positionViewPredicateBuilder =
        predicateBuilders.get(2);
    assertNull(positionViewPredicateBuilder);
  }
}
