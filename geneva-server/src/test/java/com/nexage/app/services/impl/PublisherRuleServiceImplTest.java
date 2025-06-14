package com.nexage.app.services.impl;

import static com.nexage.app.dto.sellingrule.RuleType.BRAND_PROTECTION;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.common.collect.Lists;
import com.nexage.admin.core.enums.MatchType;
import com.nexage.admin.core.enums.RuleTargetType;
import com.nexage.admin.core.enums.Status;
import com.nexage.admin.core.model.CompanyRule;
import com.nexage.admin.core.repository.CompanyRuleRepository;
import com.nexage.admin.core.repository.RuleRepository;
import com.nexage.app.dto.sellingrule.InventoryAssignmentsDTO;
import com.nexage.app.dto.sellingrule.RuleFormulaDTO;
import com.nexage.app.dto.sellingrule.RuleTargetDTO;
import com.nexage.app.dto.sellingrule.RuleType;
import com.nexage.app.dto.sellingrule.SellerRuleDTO;
import com.nexage.app.dto.sellingrule.formula.FormulaGroupingDTO;
import com.nexage.app.dto.sellingrule.formula.PlacementFormulaDTO;
import com.nexage.app.error.ServerErrorCodes;
import com.nexage.app.util.assemblers.sellingrule.RuleAssembler;
import com.ssp.geneva.common.error.exception.GenevaException;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import com.ssp.geneva.common.error.model.CommonErrorCodes;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
class PublisherRuleServiceImplTest {
  private static final Long RULE_PID = 101L;
  private static final Long PUBLISHER_PID = 10001L;
  private static final Pageable PAGEABLE = PageRequest.of(0, 10);

  private static final com.nexage.admin.core.enums.RuleType DEAL =
      com.nexage.admin.core.enums.RuleType.DEAL;

  @Mock private RuleRepository ruleRepository;
  @Mock private RuleAssembler ruleAssembler;
  @Mock private CompanyRuleRepository companyRuleRepository;
  @InjectMocks private PublisherRuleServiceImpl publisherRuleService;

  @Mock private SellerRuleDTO dto;
  @Mock private CompanyRule entity;
  @Mock private PlacementFormulaDTO placementFormulaDto;

  @BeforeEach
  void setup() {
    when(entity.getPid()).thenReturn(RULE_PID);
    when(entity.getDeployedCompanies()).thenReturn(Collections.emptySet());
    when(entity.getDeployedSites()).thenReturn(Collections.emptySet());
    when(entity.getDeployedPositions()).thenReturn(Collections.emptySet());

    when(ruleRepository.findActualByPid(RULE_PID)).thenReturn(Optional.of(entity));
    when(ruleRepository.save(entity)).thenReturn(entity);

    when(placementFormulaDto.getGroupedBy()).thenReturn(FormulaGroupingDTO.OR);
  }

  @Test
  void whenServiceCreatesDealRule_andDtoIsValid_thenRuleIsCreated() {
    // given
    mockNewDealRuleDto();
    mockAssemblerOutput();

    // when
    SellerRuleDTO created = publisherRuleService.create(dto);

    // then
    assertNotNull(created);
  }

  @Test
  void whenServiceCreatesDealRule_andDtoHasInvalidRuleType_thenValidationFails() {
    // given
    mockNewDealRuleDto();
    when(dto.getType()).thenReturn(BRAND_PROTECTION);

    // when & then
    expectGenevaValidationException(
        ServerErrorCodes.SERVER_RULE_TYPE_IS_NOT_DEAL, () -> publisherRuleService.create(dto));
  }

  @Test
  void whenServiceCreatesDealRule_andDtoHasRuleFormula_thenValidationFails() {
    // given
    mockNewDealRuleDto();
    RuleFormulaDTO RuleFormulaDTO = mock(RuleFormulaDTO.class);
    when(dto.getRuleFormula()).thenReturn(RuleFormulaDTO);

    // when & then
    expectGenevaValidationException(
        ServerErrorCodes.SERVER_DEAL_RULE_SHOULD_NOT_HAVE_FORMULA,
        () -> publisherRuleService.create(dto));
  }

  @Test
  void whenServiceCreatesDealRule_andDtoHasAssignments_thenValidationFails() {
    // given
    mockNewDealRuleDto();
    InventoryAssignmentsDTO assignmentsDto = mock(InventoryAssignmentsDTO.class);
    when(dto.getAssignments()).thenReturn(assignmentsDto);

    // when & then
    expectGenevaValidationException(
        ServerErrorCodes.SERVER_DEAL_RULE_SHOULD_NOT_HAVE_ASSIGNMENTS,
        () -> publisherRuleService.create(dto));
  }

  @Test
  void whenServiceUpdatesDealRule_andDtoIsValid_thenRuleIsUpdated() {
    // given
    mockExistingDealRuleDto();
    mockAssemblerOutput();

    // when
    SellerRuleDTO created = publisherRuleService.update(RULE_PID, dto);

    // then
    assertNotNull(created);
  }

  @Test
  void whenServiceUpdatesDealRule_andDtoHasInvalidRuleType_thenValidationFails() {
    // given
    mockNewDealRuleDto();
    when(dto.getType()).thenReturn(BRAND_PROTECTION);

    // when & then
    expectGenevaValidationException(
        ServerErrorCodes.SERVER_RULE_TYPE_IS_NOT_DEAL,
        () -> publisherRuleService.update(RULE_PID, dto));
  }

  @Test
  void whenServiceUpdatesDealRule_andDtoHasRuleFormula_thenValidationFails() {
    // given
    mockExistingDealRuleDto();
    RuleFormulaDTO RuleFormulaDTO = mock(RuleFormulaDTO.class);
    when(dto.getRuleFormula()).thenReturn(RuleFormulaDTO);

    // when & then
    expectGenevaValidationException(
        ServerErrorCodes.SERVER_DEAL_RULE_SHOULD_NOT_HAVE_FORMULA,
        () -> publisherRuleService.update(RULE_PID, dto));
  }

  @Test
  void whenServiceUpdatesDealRule_andDtoHasAssignments_thenValidationFails() {
    // given
    mockExistingDealRuleDto();
    InventoryAssignmentsDTO assignmentsDto = mock(InventoryAssignmentsDTO.class);
    when(dto.getAssignments()).thenReturn(assignmentsDto);

    // when & then
    expectGenevaValidationException(
        ServerErrorCodes.SERVER_DEAL_RULE_SHOULD_NOT_HAVE_ASSIGNMENTS,
        () -> publisherRuleService.update(RULE_PID, dto));
  }

  @Test
  void whenServiceCreatesDealRuleWithBidTargets_andDtoIsValid_thenRuleIsCreated() {
    // given
    mockNewDealRuleWithBidTargetsDto();
    mockAssemblerOutput();

    // when
    SellerRuleDTO created = publisherRuleService.create(dto);

    // then
    assertNotNull(created);
  }

  @Test
  void whenServiceUpdatesDealRuleWithBidTargets_andDtoIsValid_thenRuleIsUpdated() {
    // given
    mockExistingDealRuleWithBidTargetsDto();
    mockAssemblerOutput();

    // when
    SellerRuleDTO created = publisherRuleService.update(RULE_PID, dto);

    // then
    assertNotNull(created);
  }

  @Test
  void whenSearchingForRules_thenResponseIsCorrect() {
    mockExistingRuleDto(BRAND_PROTECTION);
    CompanyRule rule = new CompanyRule();
    rule.setPid(RULE_PID);

    given(
            companyRuleRepository.findAll(
                ArgumentMatchers.<Specification<CompanyRule>>any(), eq(PAGEABLE)))
        .willReturn(new PageImpl<>(Lists.newArrayList(rule)));
    given(ruleAssembler.make(rule)).willReturn(dto);

    Page<SellerRuleDTO> result =
        publisherRuleService.findRulesByPidAndTypeAndStatusWithPagination(
            PUBLISHER_PID, BRAND_PROTECTION.name(), Status.ACTIVE.name(), PAGEABLE, null, null);

    assertEquals(1, result.getTotalElements());
    assertEquals(dto.getPid(), result.getContent().get(0).getPid());
  }

  @Test
  void whenFindingRulesWithInvalidQueryTermThenGenevaValidationExceptionIsThrown() {
    // given
    Set<String> queryFields = Set.of("pid");
    String invalidQueryTerm = "foo";

    // when & then
    assertThrows(
        GenevaValidationException.class,
        () ->
            publisherRuleService.findRulesByPidAndTypeAndStatusWithPagination(
                PUBLISHER_PID, null, null, PAGEABLE, queryFields, invalidQueryTerm));
  }

  @Test
  void shouldThrowRulePidNotFoundExceptionWhenGivenRulePid() {
    expectGenevaValidationException(
        ServerErrorCodes.SERVER_RULE_DOESNT_EXIST, () -> publisherRuleService.find(1L));
  }

  @Test
  void shouldSoftDeleteCompanyRuleByRulePidAndTypeDeal() {
    // given
    CompanyRule companyRule = new CompanyRule();
    companyRule.setPid(RULE_PID);
    companyRule.setRuleType(DEAL);
    when(ruleRepository.findByPidAndRuleType(RULE_PID, DEAL)).thenReturn(Optional.of(companyRule));

    // when
    publisherRuleService.delete(RULE_PID);

    // then
    verify(ruleRepository).save(argThat(rule -> rule.getStatus() == Status.DELETED));
  }

  @Test
  void shouldThrowNotFoundWhenCompanyRuleDoesNotExist() {
    // when
    when(ruleRepository.findByPidAndRuleType(any(), any())).thenReturn(Optional.empty());

    // then
    GenevaException exception =
        assertThrows(GenevaValidationException.class, () -> publisherRuleService.delete(RULE_PID));
    assertEquals(ServerErrorCodes.SERVER_COMPANY_NOT_FOUND, exception.getErrorCode());
  }

  @Test
  void shouldThrowExceptionWhenQueryPassedWithInvalidStatus() {
    // given
    String invalidStatus = "INVALID_STATUS";

    // when
    GenevaException exception =
        assertThrows(
            GenevaValidationException.class,
            () ->
                publisherRuleService.findRulesByPidAndTypeAndStatusWithPagination(
                    PUBLISHER_PID, null, invalidStatus, null, null, null));

    // then
    assertEquals(CommonErrorCodes.COMMON_BAD_REQUEST, exception.getErrorCode());
  }

  private void expectGenevaValidationException(
      ServerErrorCodes expectedErrorMessage, Runnable runnable) {
    GenevaValidationException gve =
        assertThrows(
            GenevaValidationException.class,
            runnable::run,
            "GenevaValidationException expected but not thrown");
    assertEquals(expectedErrorMessage, gve.getErrorCode());
  }

  private void mockNewDealRuleDto() {
    mockNewRuleDto();
    when(dto.getOwnerCompanyPid()).thenReturn(null);

    when(entity.getOwnerCompanyPid()).thenReturn(null);
  }

  private void mockNewRuleDto() {
    mockRuleDto(RuleType.DEAL);
    when(dto.getPid()).thenReturn(null);
    when(dto.getVersion()).thenReturn(null);

    when(entity.getPid()).thenReturn(null);
    when(entity.getVersion()).thenReturn(null);
  }

  private void mockExistingDealRuleDto() {
    mockExistingRuleDto(RuleType.DEAL);
    when(dto.getOwnerCompanyPid()).thenReturn(null);

    when(entity.getOwnerCompanyPid()).thenReturn(null);
  }

  private void mockExistingRuleDto(RuleType ruleType) {
    mockRuleDto(ruleType);
    when(dto.getPid()).thenReturn(RULE_PID);
    when(dto.getVersion()).thenReturn(1);

    when(entity.getPid()).thenReturn(RULE_PID);
    when(entity.getVersion()).thenReturn(1);
  }

  private void mockRuleDto(RuleType ruleType) {
    when(dto.getType()).thenReturn(ruleType);
    when(dto.getStatus()).thenReturn(Status.ACTIVE);
    when(dto.getName()).thenReturn("Test Rule");
  }

  private void mockNewDealRuleWithBidTargetsDto() {
    mockNewDealRuleDto();
    mockRuleWithBidTargetsDto();
    when(dto.getOwnerCompanyPid()).thenReturn(null);
    when(entity.getOwnerCompanyPid()).thenReturn(null);
  }

  private void mockExistingDealRuleWithBidTargetsDto() {
    mockExistingDealRuleDto();
    mockRuleWithBidTargetsDto();
  }

  private void mockRuleWithBidTargetsDto() {
    when(dto.getStatus()).thenReturn(Status.ACTIVE);
    when(dto.getName()).thenReturn("Test Rule");
    Set<RuleTargetDTO> targets = new HashSet<>();
    targets.add(
        RuleTargetDTO.builder()
            .targetType(RuleTargetType.CREATIVE_LANGUAGE)
            .matchType(MatchType.EXCLUDE_LIST)
            .status(Status.ACTIVE)
            .data("attributeData")
            .build());
    targets.add(
        RuleTargetDTO.builder()
            .targetType(RuleTargetType.CREATIVE_ID)
            .matchType(MatchType.INCLUDE_LIST)
            .status(Status.ACTIVE)
            .data("[{\"tag_id\":1},{\"tag_id\":2},{\"tag_id\":3}]")
            .build());
    when(dto.getTargets()).thenReturn(targets);
  }

  private void mockAssemblerOutput() {
    when(ruleAssembler.apply(any(), any())).thenReturn(entity);
    SellerRuleDTO newDto = mock(SellerRuleDTO.class);
    when(ruleAssembler.make(any())).thenReturn(newDto);
    when(ruleAssembler.make(any(), any())).thenReturn(newDto);
  }
}
