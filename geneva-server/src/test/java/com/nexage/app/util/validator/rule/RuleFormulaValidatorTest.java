package com.nexage.app.util.validator.rule;

import static com.nexage.app.dto.sellingrule.formula.FormulaAttributeDTO.INVENTORY_ATTRIBUTE;
import static com.nexage.app.dto.sellingrule.formula.FormulaAttributeDTO.SITE_TYPE;
import static com.nexage.app.dto.sellingrule.formula.FormulaGroupingDTO.OR;
import static com.nexage.app.dto.sellingrule.formula.FormulaOperatorDTO.EQUALS;
import static com.nexage.app.dto.sellingrule.formula.FormulaOperatorDTO.NOT_EQUALS;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verifyNoInteractions;

import com.google.common.collect.Lists;
import com.nexage.app.dto.sellingrule.RuleFormulaDTO;
import com.nexage.app.dto.sellingrule.formula.FormulaGroupDTO;
import com.nexage.app.dto.sellingrule.formula.FormulaRuleDTO;
import com.nexage.app.dto.sellingrule.formula.PlacementFormulaDTO;
import com.nexage.app.util.validator.BaseValidatorTest;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

class RuleFormulaValidatorTest extends BaseValidatorTest {

  private static final String BAD_DATA_MESSAGE = "bad data";
  private static final String NULL_ATTRIBUTE_MESSAGE = "null attribute";
  private static final String MORE_THAN_ONE_MESSAGE = "more than one";
  @Mock private RuleFormulaConstraint annotation;
  @InjectMocks private RuleFormulaValidator validator;
  private RuleFormulaDTO ruleFormula;

  @BeforeEach
  public void buildFormula() {
    ruleFormula = buildValidRuleFormula();
  }

  @Test
  void validateNullRuleFormula() {
    assertTrue(validator.isValid(null, ctx));
    verifyNoInteractions(ctx);
  }

  @Test
  void validateNullPlacementFormula() {
    RuleFormulaDTO ruleFormula = RuleFormulaDTO.builder().build();
    assertTrue(validator.isValid(ruleFormula, ctx));
    verifyNoInteractions(ctx);
  }

  @Test
  void shouldBeValidWhenSiteTypeRuleDataContainsAndroidAndCTV_OTT() {
    FormulaRuleDTO formulaRuleDTO = new FormulaRuleDTO(SITE_TYPE, EQUALS, "ANDROID,CTV_OTT", null);
    FormulaGroupDTO formulaGroupDTO = new FormulaGroupDTO(List.of(formulaRuleDTO));
    PlacementFormulaDTO placementFormulaDTO = new PlacementFormulaDTO(OR, List.of(formulaGroupDTO));
    RuleFormulaDTO ruleFormulaDTO =
        RuleFormulaDTO.builder().placementFormula(placementFormulaDTO).build();

    assertTrue(validator.isValid(ruleFormulaDTO, ctx));
  }

  @Test
  void shouldBeValidWhenSiteTypeRuleDataContainsDesktopAndIOS() {
    FormulaRuleDTO formulaRuleDTO = new FormulaRuleDTO(SITE_TYPE, EQUALS, "DESKTOP,IOS", null);
    FormulaGroupDTO formulaGroupDTO = new FormulaGroupDTO(List.of(formulaRuleDTO));
    PlacementFormulaDTO placementFormulaDTO = new PlacementFormulaDTO(OR, List.of(formulaGroupDTO));
    RuleFormulaDTO ruleFormulaDTO =
        RuleFormulaDTO.builder().placementFormula(placementFormulaDTO).build();

    assertTrue(validator.isValid(ruleFormulaDTO, ctx));
  }

  @Test
  void shouldBeValidWhenSiteTypeRuleDataContainsDesktopAndApplication() {
    FormulaRuleDTO formulaRuleDTO =
        new FormulaRuleDTO(SITE_TYPE, EQUALS, "DESKTOP,APPLICATION", null);
    FormulaGroupDTO formulaGroupDTO = new FormulaGroupDTO(List.of(formulaRuleDTO));
    PlacementFormulaDTO placementFormulaDTO = new PlacementFormulaDTO(OR, List.of(formulaGroupDTO));
    RuleFormulaDTO ruleFormulaDTO =
        RuleFormulaDTO.builder().placementFormula(placementFormulaDTO).build();

    assertTrue(validator.isValid(ruleFormulaDTO, ctx));
  }

  @Override
  protected void initializeConstraint() {
    lenient().when(annotation.message()).thenReturn(BAD_DATA_MESSAGE);
    lenient().when(annotation.messageNullAttributePid()).thenReturn(NULL_ATTRIBUTE_MESSAGE);
    lenient()
        .when(annotation.messageMoreThanOneArgumentOccurrence())
        .thenReturn(MORE_THAN_ONE_MESSAGE);
  }

  private RuleFormulaDTO buildValidRuleFormula() {
    FormulaRuleDTO rule11 = new FormulaRuleDTO(INVENTORY_ATTRIBUTE, EQUALS, "dummy11", 11L);
    FormulaRuleDTO rule12 =
        new FormulaRuleDTO(INVENTORY_ATTRIBUTE, NOT_EQUALS, "dummy12,whatishere", 12L);
    FormulaGroupDTO group1 = new FormulaGroupDTO(Lists.newArrayList(rule11, rule12));
    FormulaRuleDTO rule21 = new FormulaRuleDTO(INVENTORY_ATTRIBUTE, EQUALS, "dummy11", 11L);
    FormulaRuleDTO rule22 =
        new FormulaRuleDTO(INVENTORY_ATTRIBUTE, NOT_EQUALS, "dummy12,whatishere", 12L);
    FormulaGroupDTO group2 = new FormulaGroupDTO(Lists.newArrayList(rule21, rule22));
    PlacementFormulaDTO pf = new PlacementFormulaDTO(OR, Lists.newArrayList(group1, group2));
    return RuleFormulaDTO.builder().placementFormula(pf).build();
  }
}
