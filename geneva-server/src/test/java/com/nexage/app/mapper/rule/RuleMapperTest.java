package com.nexage.app.mapper.rule;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.google.common.collect.Sets;
import com.nexage.admin.core.enums.RuleActionType;
import com.nexage.admin.core.enums.RuleType;
import com.nexage.admin.core.enums.Status;
import com.nexage.admin.core.model.CompanyRule;
import com.nexage.admin.core.model.Rule;
import com.nexage.admin.core.model.RuleDeployedCompany;
import com.nexage.admin.core.model.RuleDeployedPosition;
import com.nexage.admin.core.model.RuleDeployedSite;
import com.nexage.admin.core.model.RuleFormula;
import com.nexage.admin.core.model.RuleIntendedAction;
import com.nexage.admin.core.model.RuleTarget;
import com.nexage.app.dto.sellingrule.IntendedActionDTO;
import com.nexage.app.dto.sellingrule.SellerRuleDTO;
import com.nexage.app.web.support.TestObjectsFactory;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RuleMapperTest {

  @Test
  void testCustomRuleMapper() {

    CompanyRule rule = getTestCompanyRule();
    SellerRuleDTO mapped = RuleMapper.MAPPER.map(rule);
    CompanyRule rule1 = RuleMapper.MAPPER.map(mapped);

    assertEqualsCheckAssigmentsPids(rule, rule1);
  }

  @Test
  void testRuleMapper() {

    CompanyRule rule = TestObjectsFactory.createCompanyRule(2L);
    SellerRuleDTO mapped = RuleMapper.MAPPER.map(rule);
    CompanyRule rule1 = RuleMapper.MAPPER.apply(new CompanyRule(), mapped);

    assertEqualsCheckAssigmentsPids(rule, rule1);
  }

  @Test
  void testRuleMapperApply() {

    CompanyRule rule = getTestCompanyRule();

    IntendedActionDTO newIntendedActionDto =
        IntendedActionDTO.builder()
            .pid(1L)
            .actionType(com.nexage.app.dto.sellingrule.RuleActionType.FILTER)
            .actionData("BLOCKLIST")
            .build();

    SellerRuleDTO updateDto =
        SellerRuleDTO.builder()
            .pid(rule.getPid())
            .description("ModifiedDescription")
            .intendedActions(Sets.newHashSet(newIntendedActionDto))
            .build();

    CompanyRule rule1 = RuleMapper.MAPPER.apply(rule, updateDto);

    assertEquals(
        1L,
        rule1.getRuleIntendedActions().stream()
            .findFirst()
            .orElse(new RuleIntendedAction())
            .getPid()
            .longValue());
    assertEquals("ModifiedDescription", rule1.getDescription());
  }

  private CompanyRule getTestCompanyRule() {
    CompanyRule rule = new CompanyRule();
    rule.setName("TestRule");
    rule.setDescription("Test rule description");
    rule.setRuleType(RuleType.BRAND_PROTECTION);
    rule.setStatus(Status.ACTIVE);
    rule.setOwnerCompanyPid(9L);

    RuleIntendedAction ruleIntendedAction = getRuleIntendedAction(rule);
    rule.setRuleIntendedActions(Sets.newHashSet(ruleIntendedAction));

    RuleTarget ruleTarget = getRuleTarget(rule);
    rule.setRuleTargets(Sets.newHashSet(ruleTarget));

    rule.setDeployedCompanies(Sets.newHashSet(TestObjectsFactory.createRuleDeployedCompany()));
    rule.setDeployedSites(
        Sets.newHashSet(
            TestObjectsFactory.createRuleDeployedSite(),
            TestObjectsFactory.createRuleDeployedSite()));
    rule.setDeployedPositions(Sets.newHashSet(TestObjectsFactory.createRuleDeployedPosition()));

    RuleFormula ruleFormula = getRuleFormula(rule);
    rule.setRuleFormula(ruleFormula);
    return rule;
  }

  RuleIntendedAction getRuleIntendedAction(CompanyRule rule) {
    RuleIntendedAction ruleIntendedAction = TestObjectsFactory.createFilterRuleIntendedAction();
    ruleIntendedAction.setRule(rule);
    return ruleIntendedAction;
  }

  RuleTarget getRuleTarget(CompanyRule rule) {
    RuleTarget ruleTarget = TestObjectsFactory.createRuleTarget();
    ruleTarget.setRule(rule);
    return ruleTarget;
  }

  private RuleFormula getRuleFormula(CompanyRule rule) {
    RuleFormula ruleFormula = new RuleFormula();
    ruleFormula.setPid(100L);
    ruleFormula.setAutoUpdate(true);
    ruleFormula.setRule(rule);
    ruleFormula.setFormula(
        "{\"groupedBy\":\"OR\",\"formulaGroups\":[{\"formulaRules\":[{\"attribute\":\"PLACEMENT_NAME\",\"operator\":\""
            + "CONTAINS\",\"ruleData\":\"Tool,Toy\",\"attributePid\":null}]}]}");
    return ruleFormula;
  }

  @Test
  void testIntendedActionMapper() {
    Rule rule = new CompanyRule();
    RuleIntendedAction action = new RuleIntendedAction();
    action.setRule(rule);
    action.setActionType(RuleActionType.FLOOR);
    action.setActionData("2.00");

    IntendedActionDTO mapped = IntendedActionDTOMapper.MAPPER.map(action);

    RuleIntendedAction action1 = IntendedActionDTOMapper.MAPPER.map(mapped);

    assertEquals(action, action1);
  }

  private void assertEqualsCheckAssigmentsPids(CompanyRule rule, CompanyRule rule1) {
    assertEquals(rule.getPid(), rule1.getPid());
    assertEquals(rule.getDescription(), rule1.getDescription());
    assertEquals(rule.getName(), rule1.getName());
    assertEquals(rule.getRuleType(), rule1.getRuleType());
    assertEquals(rule.getStatus(), rule1.getStatus());
    assertEquals(rule.getOwnerCompanyPid(), rule1.getOwnerCompanyPid());
    assertEquals(rule.getVersion(), rule1.getVersion());
    assertEquals(rule.getRuleFormula(), rule1.getRuleFormula());
    assertEquals(rule.getRuleIntendedActions(), rule1.getRuleIntendedActions());
    assertEquals(rule.getRuleTargets(), rule1.getRuleTargets());

    assertEquals(
        rule.getDeployedCompanies().stream()
            .map(RuleDeployedCompany::getPid)
            .collect(Collectors.toSet()),
        rule1.getDeployedCompanies().stream()
            .map(RuleDeployedCompany::getPid)
            .collect(Collectors.toSet()));
    assertEquals(
        rule.getDeployedSites().stream().map(RuleDeployedSite::getPid).collect(Collectors.toSet()),
        rule1.getDeployedSites().stream()
            .map(RuleDeployedSite::getPid)
            .collect(Collectors.toSet()));
    assertEquals(
        rule.getDeployedPositions().stream()
            .map(RuleDeployedPosition::getPid)
            .collect(Collectors.toSet()),
        rule1.getDeployedPositions().stream()
            .map(RuleDeployedPosition::getPid)
            .collect(Collectors.toSet()));
  }
}
