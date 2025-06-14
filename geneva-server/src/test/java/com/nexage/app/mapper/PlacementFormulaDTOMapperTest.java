package com.nexage.app.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.nexage.admin.core.model.RuleFormulaPositionView;
import com.nexage.admin.core.model.placementformula.formula.PredicateBuilder;
import com.nexage.admin.core.model.placementformula.formula.impl.Group;
import com.nexage.admin.core.model.placementformula.formula.impl.GroupOperator;
import com.nexage.admin.core.model.placementformula.formula.impl.InventoryAttribute;
import com.nexage.admin.core.model.placementformula.formula.impl.Operator;
import com.nexage.admin.core.model.placementformula.formula.impl.SimpleAttribute;
import com.nexage.app.config.GenevaServerJacksonBeanFactory;
import com.nexage.app.dto.sellingrule.formula.PlacementFormulaDTO;
import com.nexage.app.util.CustomObjectMapper;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import org.junit.jupiter.api.Test;

class PlacementFormulaDTOMapperTest {
  private static CustomObjectMapper mapper =
      GenevaServerJacksonBeanFactory.initCustomObjectMapper();

  @Test
  void shouldMapPlacementFormulaDTOToListOfPredicateBuilderRuleFormulaPositionView()
      throws IOException {
    PlacementFormulaDTO placementFormulaDTO;
    try (InputStream is = getClass().getResourceAsStream("/data/GridPFormulaInvAttr.json")) {
      placementFormulaDTO = mapper.readValue(is, PlacementFormulaDTO.class);
    }

    Group<RuleFormulaPositionView> ruleFormulaPositionViewGroup =
        PlacementFormulaDTOMapper.MAPPER.map(placementFormulaDTO);

    assertNotNull(ruleFormulaPositionViewGroup);
    assertEquals(GroupOperator.OR, ruleFormulaPositionViewGroup.getOperator());
    assertEquals(2, ruleFormulaPositionViewGroup.getItems().size());

    List<? extends PredicateBuilder<RuleFormulaPositionView>> rootMembers =
        ruleFormulaPositionViewGroup.getItems();
    Group<RuleFormulaPositionView> formulaGroup1 =
        (Group<RuleFormulaPositionView>) rootMembers.get(0);

    assertEquals(GroupOperator.AND, formulaGroup1.getOperator());
    final List<? extends PredicateBuilder<RuleFormulaPositionView>> formulaGroup1Members =
        formulaGroup1.getItems();

    SimpleAttribute simpleAttribute1 = (SimpleAttribute) formulaGroup1Members.get(0);
    assertEquals(Operator.EQUALS, simpleAttribute1.getOperator());
    assertEquals("MOBILE_WEB", simpleAttribute1.getValue());

    final InventoryAttribute invAttr1Minsk = (InventoryAttribute) formulaGroup1Members.get(1);
    assertEquals(Operator.EQUALS, invAttr1Minsk.getOperator());
    assertEquals(1L, invAttr1Minsk.getPid().longValue());
    assertEquals("Minsk", invAttr1Minsk.getValue());

    final InventoryAttribute invAttr1Berlin = (InventoryAttribute) formulaGroup1Members.get(2);
    assertEquals(Operator.EQUALS, invAttr1Berlin.getOperator());
    assertEquals(1L, invAttr1Berlin.getPid().longValue());
    assertEquals("Berlin", invAttr1Berlin.getValue());

    InventoryAttribute invAttrEnglish = (InventoryAttribute) formulaGroup1Members.get(3);
    assertEquals(Operator.EQUALS, invAttrEnglish.getOperator());
    assertEquals(3L, invAttrEnglish.getPid().longValue());
    assertEquals("English", invAttrEnglish.getValue());
  }
}
