package com.nexage.app.services.sellingrule.formula.impl;

import static com.nexage.app.dto.sellingrule.formula.FormulaAttributeDTO.INVENTORY_ATTRIBUTE;
import static com.nexage.app.dto.sellingrule.formula.FormulaGroupingDTO.OR;
import static com.nexage.app.dto.sellingrule.formula.FormulaOperatorDTO.EQUALS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import com.nexage.admin.core.model.RuleFormulaCompanyView;
import com.nexage.admin.core.model.RuleFormulaPositionView;
import com.nexage.admin.core.model.RuleFormulaSiteView;
import com.nexage.admin.core.repository.RuleFormulaPositionViewRepository;
import com.nexage.admin.core.specification.RuleSpecification;
import com.nexage.app.dto.sellingrule.InventoryAssignmentsDTO;
import com.nexage.app.dto.sellingrule.RuleFormulaDTO;
import com.nexage.app.dto.sellingrule.formula.FormulaGroupDTO;
import com.nexage.app.dto.sellingrule.formula.FormulaRuleDTO;
import com.nexage.app.dto.sellingrule.formula.PlacementFormulaDTO;
import com.nexage.app.util.assemblers.sellingrule.PlacementFormulaAssembler;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RuleFormulaServiceImplTest {

  private static final Long SELLER_PID = 1L;
  private static final Long PLACEMENT_PID = 3L;
  @Mock private PlacementFormulaAssembler assembler;
  @Mock private RuleFormulaPositionViewRepository ruleFormulaPositionViewRepository;
  @Mock private RuleSpecification ruleSpecification;
  @InjectMocks private RuleFormulaServiceImpl service;

  @Test
  void whenRuleFormulaIsNullDontFail() {
    assertNull(service.processFormula(null, SELLER_PID));
  }

  @Test
  void whenPlacementFormulaIsNullDontFail() {
    RuleFormulaDTO ruleFormula = RuleFormulaDTO.builder().build();
    assertNull(service.processFormula(ruleFormula, SELLER_PID));
  }

  @Test
  void whenRuleManagerReturnNullDontFail() {
    InventoryAssignmentsDTO assignments = service.processFormula(buildRuleFormula(), SELLER_PID);
    assertNotNull(assignments);
    assertTrue(assignments.getPositions().isEmpty());
  }

  @Test
  void whenPlacementDoesntHaveSiteDontIncludeIt() {
    given(
            ruleFormulaPositionViewRepository.findAll(
                RuleSpecification.withDefaultRtbProfiles(Collections.singleton(SELLER_PID), any())))
        .willReturn(List.of(buildPositionView(null)));
    InventoryAssignmentsDTO assignments = service.processFormula(buildRuleFormula(), SELLER_PID);
    assertNotNull(assignments);
    assertTrue(assignments.getPositions().isEmpty());
  }

  @Test
  void whenPlacementSiteDoesntHaveCompanyDontIncludeIt() {
    given(
            ruleFormulaPositionViewRepository.findAll(
                RuleSpecification.withDefaultRtbProfiles(Collections.singleton(SELLER_PID), any())))
        .willReturn(List.of(buildPositionView(buildSiteView(null))));
    InventoryAssignmentsDTO assignments = service.processFormula(buildRuleFormula(), SELLER_PID);
    assertNotNull(assignments);
    assertTrue(assignments.getPositions().isEmpty());
  }

  @Test
  void whenPlacementSiteCompanyPidDeoesntMatchSellerDontIncludeIt() {
    given(
            ruleFormulaPositionViewRepository.findAll(
                RuleSpecification.withDefaultRtbProfiles(Collections.singleton(SELLER_PID), any())))
        .willReturn(List.of(buildPositionView(buildSiteView(buildCompanyView(SELLER_PID + 1)))));
    InventoryAssignmentsDTO assignments = service.processFormula(buildRuleFormula(), SELLER_PID);
    assertNotNull(assignments);
    assertTrue(assignments.getPositions().isEmpty());
  }

  @Test
  void whenPlacementMatchesSellerIncludeIt() {
    given(
            ruleFormulaPositionViewRepository.findAll(
                RuleSpecification.withDefaultRtbProfiles(Collections.singleton(SELLER_PID), any())))
        .willReturn(List.of(buildPositionView(buildSiteView(buildCompanyView(SELLER_PID)))));
    InventoryAssignmentsDTO assignments = service.processFormula(buildRuleFormula(), SELLER_PID);
    assertNotNull(assignments);
    assertFalse(assignments.getPositions().isEmpty());
    assertEquals(PLACEMENT_PID, assignments.getPositions().iterator().next().getPid());
  }

  private RuleFormulaDTO buildRuleFormula() {
    FormulaRuleDTO rule1 = new FormulaRuleDTO(INVENTORY_ATTRIBUTE, EQUALS, "dummy11", 11L);
    FormulaGroupDTO group1 = new FormulaGroupDTO(List.of(rule1));
    PlacementFormulaDTO pf = new PlacementFormulaDTO(OR, List.of(group1));
    return RuleFormulaDTO.builder().placementFormula(pf).build();
  }

  private RuleFormulaPositionView buildPositionView(RuleFormulaSiteView site) {
    RuleFormulaPositionView pv = new RuleFormulaPositionView();
    pv.setSite(site);
    pv.setPid(PLACEMENT_PID);
    return pv;
  }

  private RuleFormulaSiteView buildSiteView(RuleFormulaCompanyView seller) {
    RuleFormulaSiteView sv = new RuleFormulaSiteView();
    sv.setPid(2L);
    sv.setCompany(seller);
    return sv;
  }

  private RuleFormulaCompanyView buildCompanyView(Long pid) {
    RuleFormulaCompanyView cv = new RuleFormulaCompanyView();
    cv.setPid(pid);
    return cv;
  }
}
