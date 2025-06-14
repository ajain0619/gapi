package com.nexage.app.services.sellingrule.impl;

import static com.nexage.app.web.support.TestObjectsFactory.createSellerRuleDto;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import com.nexage.admin.core.enums.RuleType;
import com.nexage.admin.core.model.CompanyRule;
import com.nexage.app.dto.sellingrule.InventoryAssignmentsDTO;
import com.nexage.app.dto.sellingrule.RuleFormulaDTO;
import com.nexage.app.dto.sellingrule.SellerRuleDTO;
import com.nexage.app.error.ServerErrorCodes;
import com.nexage.app.mapper.rule.RuleMapper;
import com.nexage.app.web.support.TestObjectsFactory;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import com.ssp.geneva.common.model.search.MultiValueQueryParams;
import com.ssp.geneva.common.model.search.SearchQueryOperator;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@ExtendWith(MockitoExtension.class)
class SellerRuleAPIServiceImplTest extends BaseRuleServiceTest {

  private static final RuleType RULE_TYPE = RuleType.BRAND_PROTECTION;

  @InjectMocks private SellerRuleAPIServiceImpl service;

  @Test
  void shouldFindRuleByPidAndSellerPid() {
    CompanyRule rule = makeCompanyRule();
    SellerRuleDTO expectedDTO = makeDTO(rule);

    given(
            companyRuleRepository.findByPidAndOwnerCompanyPidAndRuleTypeIn(
                RULE_PID, SELLER_PID, Set.of(RULE_TYPE)))
        .willReturn(rule);

    SellerRuleDTO dto = service.findByPidAndSellerPid(RULE_PID, SELLER_PID);

    assertEquals(expectedDTO.getPid(), dto.getPid());
    assertEquals(expectedDTO.getOwnerCompanyPid(), dto.getOwnerCompanyPid());
  }

  @Test
  void shouldReturnNullWhenUnableToFindRuleByPidAndSellerPid() {
    SellerRuleDTO dto = service.findByPidAndSellerPid(RULE_PID, SELLER_PID);

    assertNull(dto);
  }

  @Test
  void shouldFindRulesWhenSearchingByQueryField() {
    CompanyRule rule = makeCompanyRule();
    List<CompanyRule> rules = List.of(rule);

    Pageable pageRequest = PageRequest.of(0, 1, Sort.by("pid").descending());
    Page<CompanyRule> dtoPage = new PageImpl<>(rules, pageRequest, 1);
    MultiValueQueryParams inputParams = createInputMap("some_random_key");
    SellerRuleQueryFieldParameter parameter =
        SellerRuleQueryFieldParameter.createFrom(inputParams, SellerRuleQueryField.values());

    given(companyRuleRepository.findAll(any(Specification.class), any(Pageable.class)))
        .willReturn(dtoPage);

    Page<SellerRuleDTO> dto =
        service.findBySellerPidAndOtherCriteria(SELLER_PID, parameter, pageRequest);

    assertEquals(1, dto.getTotalElements());
  }

  @Test
  void shouldThrowUnsupportedOperationWhenDeletingRule() {
    assertThrows(
        UnsupportedOperationException.class,
        () -> service.deleteByPidAndSellerPid(RULE_PID, SELLER_PID));
  }

  @Test
  void whenCreatingRuleAndAllChecksPassedStoreIt() {
    // given
    SellerRuleDTO rule = createSellerRuleDto();

    // when
    service.create(SELLER_PID, rule);

    // then
    verify(sellerRuleValidator).validateBidManagementAPIRuleDTO(any());
    verify(sellerRuleValidator).validateCommonPartForCreateAndUpdate(any(), any());
    verify(sellerRuleValidator).validateDeployedTargetsAndUpdateRule(any());
    verify(sellerRuleValidator).validateDuplicateName(any());
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
  void whenUpdatingRuleAndAllChecksPassedStoreIt() {
    // given
    SellerRuleDTO rule = createSellerRuleDto();
    given(
            companyRuleRepository.findByPidAndOwnerCompanyPidAndRuleTypeIn(
                rule.getPid(), SELLER_PID, Set.of(RuleType.BRAND_PROTECTION)))
        .willReturn(RuleMapper.MAPPER.map(rule));

    // when
    service.update(SELLER_PID, rule);

    // then
    verify(sellerRuleValidator).validateBidManagementAPIRuleDTO(any());
    verify(sellerRuleValidator).validateCommonPartForCreateAndUpdate(any(), any());
    verify(sellerRuleValidator).validateBidManagementAPIRule(any());
    verify(sellerRuleValidator).validateDeployedTargetsAndUpdateRule(any());
    verify(sellerRuleValidator).validateDuplicateName(any());
    verify(companyRuleRepository).saveAndFlush(any());
  }

  @Test
  void whenUpdatingRuleAndFormulaIsProcessedTargetsAreAssigned() {
    // given
    InventoryAssignmentsDTO assignments = createPublisherAssignments(SELLER_PID);
    RuleFormulaDTO formula = RuleFormulaDTO.builder().build();
    SellerRuleDTO rule = createRule(SELLER_PID, null, formula);
    SellerRuleDTO spy = spy(rule);
    given(formulaService.processFormula(formula, SELLER_PID)).willReturn(assignments);
    given(
            companyRuleRepository.findByPidAndOwnerCompanyPidAndRuleTypeIn(
                rule.getPid(), SELLER_PID, Set.of(RuleType.BRAND_PROTECTION)))
        .willReturn(RuleMapper.MAPPER.map(rule));

    // when
    service.update(SELLER_PID, spy);

    // then
    verify(spy).setAssignments(assignments);
  }

  @Test
  void whenUpdatingRuleAndRuleNotFoundThenThrowException() {
    SellerRuleDTO rule = createSellerRuleDto();
    given(
            companyRuleRepository.findByPidAndOwnerCompanyPidAndRuleTypeIn(
                rule.getPid(), SELLER_PID, Set.of(RuleType.BRAND_PROTECTION)))
        .willReturn(null);

    var exception =
        assertThrows(GenevaValidationException.class, () -> service.update(SELLER_PID, rule));

    assertEquals(ServerErrorCodes.SERVER_RULE_NOT_FOUND, exception.getErrorCode());
  }

  private CompanyRule makeCompanyRule() {
    CompanyRule rule = TestObjectsFactory.createCompanyRule(RULE_PID);
    rule.setOwnerCompanyPid(SELLER_PID);
    rule.setRuleType(RULE_TYPE);
    return rule;
  }

  private SellerRuleDTO makeDTO(CompanyRule rule) {
    return RuleMapper.MAPPER.map(rule);
  }

  private MultiValueQueryParams createInputMap(String key, String... values) {
    MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
    map.addAll(key, Arrays.asList(values));
    return new MultiValueQueryParams(map, SearchQueryOperator.AND);
  }
}
