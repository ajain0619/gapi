package com.nexage.app.services.sellingrule.impl;

import static com.nexage.app.web.support.TestObjectsFactory.createCompanyRule;
import static com.nexage.app.web.support.TestObjectsFactory.createSellerRuleDto;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import com.nexage.admin.core.model.CompanyRule;
import com.nexage.app.dto.sellingrule.InventoryAssignmentsDTO;
import com.nexage.app.dto.sellingrule.RuleFormulaDTO;
import com.nexage.app.dto.sellingrule.SellerRuleDTO;
import com.nexage.app.error.ServerErrorCodes;
import com.nexage.app.mapper.rule.RuleMapper;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SellerRuleServiceImplTest extends BaseRuleServiceTest {

  @InjectMocks private SellerRuleServiceImpl service;

  @Test
  void whenRuleNotFoundReturnNull() {
    // when
    SellerRuleDTO sellerRule = service.findByPidAndSellerPid(RULE_PID, SELLER_PID);

    // then
    assertNull(sellerRule);
  }

  @Test
  void whenRuleFoundReturnIt() {
    // given
    CompanyRule companyRule = createCompanyRule(RULE_PID);
    companyRule.setOwnerCompanyPid(SELLER_PID);
    given(companyRuleRepository.findByPidAndOwnerCompanyPid(RULE_PID, SELLER_PID))
        .willReturn(companyRule);

    // when
    SellerRuleDTO sellerRule = service.findByPidAndSellerPid(RULE_PID, SELLER_PID);

    // then
    assertNotNull(sellerRule);
    assertEquals(companyRule.getPid(), sellerRule.getPid());
    assertEquals(companyRule.getOwnerCompanyPid(), sellerRule.getOwnerCompanyPid());
  }

  @Test
  void whenRuleFoundDeleteIt() {
    // given
    CompanyRule companyRule = createCompanyRule(RULE_PID);
    companyRule.setOwnerCompanyPid(SELLER_PID);
    given(companyRuleRepository.findByPidAndOwnerCompanyPid(RULE_PID, SELLER_PID))
        .willReturn(companyRule);

    // when
    SellerRuleDTO deletedRule = service.deleteByPidAndSellerPid(RULE_PID, SELLER_PID);

    // then
    assertNotNull(deletedRule);
    assertEquals(companyRule.getPid(), deletedRule.getPid());
    verify(companyRuleRepository).delete(RULE_PID);
  }

  @Test
  void whenTryToDeleteButRuleNotFoundThrowException() {
    // when
    GenevaValidationException exception =
        assertThrows(
            GenevaValidationException.class,
            () -> service.deleteByPidAndSellerPid(RULE_PID, SELLER_PID));
    assertEquals(ServerErrorCodes.SERVER_RULE_NOT_FOUND, exception.getErrorCode());
  }

  @Test
  void whenCreatingRuleAndAllChecksPassedStoreIt() {
    // given
    SellerRuleDTO rule = createSellerRuleDto();

    // when
    service.create(SELLER_PID, rule);

    // then
    verify(sellerRuleValidator).validateCommonPartForCreateAndUpdate(any(), any());
    verify(sellerRuleValidator).validateDeployedTargetsAndUpdateRule(any());
    verify(sellerRuleValidator).validateDuplicateName(any());
    verifyNoMoreInteractions(sellerRuleValidator);
    verify(companyRuleRepository).save(any());
  }

  @Test
  void whenCreatingRuleAndFormulaIsProcessedTargetsAreAssigned() {
    // given
    InventoryAssignmentsDTO assignments = createPublisherAssignments(SELLER_PID);
    RuleFormulaDTO formula = RuleFormulaDTO.builder().build();
    SellerRuleDTO rule = createRule(SELLER_PID, null, formula);
    SellerRuleDTO spy = spy(rule);
    given(formulaService.processFormula(formula, SELLER_PID)).willReturn(assignments);

    // when
    service.create(SELLER_PID, spy);

    // then
    verify(spy).setAssignments(assignments);
  }

  @Test
  void whenUpdatingRuleAndFormulaIsProcessedTargetsAreAssigned() {
    // given
    InventoryAssignmentsDTO assignments = createPublisherAssignments(SELLER_PID);
    RuleFormulaDTO formula = RuleFormulaDTO.builder().build();
    SellerRuleDTO rule = createRule(SELLER_PID, null, formula);
    SellerRuleDTO spy = spy(rule);
    given(formulaService.processFormula(formula, SELLER_PID)).willReturn(assignments);
    given(companyRuleRepository.findByPidAndOwnerCompanyPid(rule.getPid(), SELLER_PID))
        .willReturn(RuleMapper.MAPPER.map(rule));

    // when
    service.update(SELLER_PID, spy);

    // then
    verify(spy).setAssignments(assignments);
  }

  @Test
  void whenUpdatingRuleAndAllChecksPassedStoreIt() {
    // given
    SellerRuleDTO rule = createSellerRuleDto();
    given(companyRuleRepository.findByPidAndOwnerCompanyPid(rule.getPid(), SELLER_PID))
        .willReturn(RuleMapper.MAPPER.map(rule));

    // when
    service.update(SELLER_PID, rule);

    // then
    verify(sellerRuleValidator).validateCommonPartForCreateAndUpdate(any(), any());
    verify(sellerRuleValidator).validateDeployedTargetsAndUpdateRule(any());
    verify(sellerRuleValidator).validateDuplicateName(any());
    verifyNoMoreInteractions(sellerRuleValidator);
    verify(companyRuleRepository).saveAndFlush(any());
  }

  @Test
  void whenUpdatingRuleAndRuleNotFoundThenThrowException() {
    SellerRuleDTO rule = createSellerRuleDto();
    given(companyRuleRepository.findByPidAndOwnerCompanyPid(rule.getPid(), SELLER_PID))
        .willReturn(null);
    var exception =
        assertThrows(GenevaValidationException.class, () -> service.update(SELLER_PID, rule));

    assertEquals(ServerErrorCodes.SERVER_RULE_NOT_FOUND, exception.getErrorCode());
  }
}
