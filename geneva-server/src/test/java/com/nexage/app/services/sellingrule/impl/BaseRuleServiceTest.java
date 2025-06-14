package com.nexage.app.services.sellingrule.impl;

import static com.nexage.app.web.support.TestObjectsFactory.createSellerRuleDtoBuilder;

import com.nexage.admin.core.repository.CompanyRuleRepository;
import com.nexage.app.dto.sellingrule.InventoryAssignmentsDTO;
import com.nexage.app.dto.sellingrule.PublisherAssignmentDTO;
import com.nexage.app.dto.sellingrule.RuleFormulaDTO;
import com.nexage.app.dto.sellingrule.RuleTargetDTO;
import com.nexage.app.dto.sellingrule.SellerRuleDTO;
import com.nexage.app.services.sellingrule.formula.RuleFormulaService;
import com.nexage.app.services.validation.sellingrule.SellerRuleValidator;
import java.util.Set;
import org.mockito.Mock;

abstract class BaseRuleServiceTest {

  static final Long RULE_PID = 123L;
  static final Long SELLER_PID = 456L;

  @Mock CompanyRuleRepository companyRuleRepository;
  @Mock RuleFormulaService formulaService;
  @Mock SellerRuleValidator sellerRuleValidator;

  InventoryAssignmentsDTO createPublisherAssignments(long sellerPid) {
    return InventoryAssignmentsDTO.builder()
        .publishers(Set.of(PublisherAssignmentDTO.builder().pid(sellerPid).build()))
        .build();
  }

  SellerRuleDTO createRule(
      Long sellerPid,
      RuleTargetDTO target,
      InventoryAssignmentsDTO assignments,
      RuleFormulaDTO formula) {
    SellerRuleDTO.SellerRuleDTOBuilder builder =
        createSellerRuleDtoBuilder().ownerCompanyPid(sellerPid);
    if (target != null) {
      builder.targets(Set.of(target));
    }
    if (assignments != null) {
      builder.assignments(assignments);
    }
    if (formula != null) {
      builder.ruleFormula(formula);
    }
    return builder.build();
  }

  SellerRuleDTO createRule(
      Long sellerPid, InventoryAssignmentsDTO assignments, RuleFormulaDTO formula) {
    return createRule(sellerPid, null, assignments, formula);
  }
}
