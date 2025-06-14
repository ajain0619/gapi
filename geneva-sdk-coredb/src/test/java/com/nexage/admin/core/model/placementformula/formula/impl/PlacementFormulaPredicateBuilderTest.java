package com.nexage.admin.core.model.placementformula.formula.impl;

import static com.nexage.admin.core.model.placementformula.formula.impl.FormulaAttributeInfo.PLACEMENT_TYPE;
import static com.nexage.admin.core.model.placementformula.formula.impl.FormulaAttributeInfo.SITE_TYPE;
import static com.nexage.admin.core.model.placementformula.formula.impl.GroupOperator.OR;
import static com.nexage.admin.core.model.placementformula.formula.impl.Operator.EQUALS;
import static com.nexage.admin.core.model.placementformula.formula.impl.Operator.NOT_EQUALS;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.nexage.admin.core.model.RuleFormulaPositionView;
import com.nexage.admin.core.model.placementformula.formula.PredicateBuilder;
import java.util.List;
import org.junit.jupiter.api.Test;

class PlacementFormulaPredicateBuilderTest {
  @Test
  void test() {

    final Group<RuleFormulaPositionView> result =
        PlacementFormulaPredicateBuilder.betweenGroups(OR)
            .betweenGroupItems(GroupOperator.AND)
            .addGroup(
                new SimpleAttribute(SITE_TYPE, EQUALS, "MOBILE_WEB"),
                new InventoryAttribute(1L, EQUALS, "Minsk,Berlin"),
                new InventoryAttribute(3L, EQUALS, "English"))
            .addGroup(
                new SimpleAttribute(PLACEMENT_TYPE, NOT_EQUALS, "BANNER"),
                new InventoryAttribute(2L, NOT_EQUALS, "Male"))
            .build();

    assertEquals(OR, result.getOperator());
    assertEquals(2, result.getItems().size());
    List<? extends PredicateBuilder<RuleFormulaPositionView>> rootMembers = result.getItems();
    Group<RuleFormulaPositionView> formulaGroup1 =
        (Group<RuleFormulaPositionView>) rootMembers.get(0);
    assertEquals(GroupOperator.AND, formulaGroup1.getOperator());
    final List<? extends PredicateBuilder<RuleFormulaPositionView>> formulaGroup1Members =
        formulaGroup1.getItems();

    SimpleAttribute simpleAttribute1 = (SimpleAttribute) formulaGroup1Members.get(0);
    assertEquals(EQUALS, simpleAttribute1.getOperator());
    assertEquals("MOBILE_WEB", simpleAttribute1.getValue());

    final InventoryAttribute invAttr1MinskBerlin = (InventoryAttribute) formulaGroup1Members.get(1);
    assertEquals(EQUALS, invAttr1MinskBerlin.getOperator());
    assertEquals(1L, invAttr1MinskBerlin.getPid().longValue());
    assertEquals("Minsk,Berlin", invAttr1MinskBerlin.getValue());

    InventoryAttribute invAttrEnglish = (InventoryAttribute) formulaGroup1Members.get(2);
    assertEquals(EQUALS, invAttrEnglish.getOperator());
    assertEquals(3L, invAttrEnglish.getPid().longValue());
    assertEquals("English", invAttrEnglish.getValue());
  }
}
