package com.nexage.app.services.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nexage.admin.core.enums.DealPriorityType;
import com.nexage.admin.core.enums.PlacementFormulaStatus;
import com.nexage.admin.core.model.BaseTarget;
import com.nexage.admin.core.model.BidderConfig;
import com.nexage.admin.core.model.Company;
import com.nexage.admin.core.model.DealBidderConfigView;
import com.nexage.admin.core.model.DealRtbProfileViewUsingFormulas;
import com.nexage.admin.core.model.DealTarget;
import com.nexage.admin.core.model.DirectDeal;
import com.nexage.admin.core.model.RuleFormulaPositionView;
import com.nexage.admin.core.repository.BidderConfigRepository;
import com.nexage.admin.core.repository.CompanyRepository;
import com.nexage.admin.core.repository.DealBidderRepository;
import com.nexage.admin.core.repository.DealRtbProfileViewRepository;
import com.nexage.admin.core.repository.DirectDealRepository;
import com.nexage.admin.core.sparta.jpa.model.DealPosition;
import com.nexage.app.dto.DirectDealDTO;
import com.nexage.app.dto.deal.DealBidderDTO;
import com.nexage.app.dto.deal.DealSiteDTO;
import com.nexage.app.dto.deal.RTBProfileDTO;
import com.nexage.app.dto.deals.DealRuleDTO;
import com.nexage.app.dto.deals.DealTargetDTO;
import com.nexage.app.dto.sellingrule.formula.FormulaAttributeDTO;
import com.nexage.app.dto.sellingrule.formula.FormulaGroupDTO;
import com.nexage.app.dto.sellingrule.formula.FormulaGroupingDTO;
import com.nexage.app.dto.sellingrule.formula.FormulaOperatorDTO;
import com.nexage.app.dto.sellingrule.formula.FormulaRuleDTO;
import com.nexage.app.dto.sellingrule.formula.PlacementFormulaDTO;
import com.nexage.app.error.ServerErrorCodes;
import com.nexage.app.security.UserContext;
import com.nexage.app.services.support.DealServiceSupport;
import com.nexage.app.services.validation.DealValidator;
import com.nexage.app.util.assemblers.sellingrule.PlacementFormulaAssembler;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import com.ssp.geneva.common.security.model.SpringUserDetails;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;

@ExtendWith(MockitoExtension.class)
class DealServiceImplTest {
  private static final Long TEST_DEAL_PID = 11L;
  private static final String TEST_DEAL_ID = "test-deal-id";

  @Mock private DirectDealRepository directDealRepository;
  @Mock private UserContext userContext;
  @Mock private BidderConfigRepository bidderConfigRepository;
  @Mock private DealRtbProfileViewRepository dealRtbProfileViewRepository;
  @Mock private PlacementFormulaAssembler placementFormulaAssembler;
  @Mock private DealBidderRepository dealBidderRepository;
  @Mock private CompanyRepository companyRepository;
  @Mock private DealValidator dealValidator;
  @Mock private DealServiceSupport dealServiceSupport;
  @InjectMocks private DealServiceImpl dealService;

  @Test
  void shouldSuccessfullyCreateDealWithPlacementFormula() {
    // given
    PlacementFormulaDTO placementFormula = new PlacementFormulaDTO();
    String formula = "formula";
    long rulePid = 1000L;
    RuleFormulaPositionView ruleFormulaPositionView = new RuleFormulaPositionView();
    ruleFormulaPositionView.setPid(rulePid);
    SpringUserDetails mockUserDetails = mock(SpringUserDetails.class);
    DirectDealDTO directDealDTO =
        DirectDealDTO.builder().placementFormula(placementFormula).autoUpdate(false).build();
    given(mockUserDetails.getPid()).willReturn(100L);
    given(userContext.getCurrentUser()).willReturn(mockUserDetails);
    given(placementFormulaAssembler.applyToString(placementFormula)).willReturn(formula);
    ArgumentCaptor<DirectDeal> dealCaptor = ArgumentCaptor.forClass(DirectDeal.class);

    // when
    dealService.createDeal(directDealDTO);

    // then
    verify(directDealRepository).save(dealCaptor.capture());
    assertEquals(0, dealCaptor.getValue().getPositions().size());
    assertEquals(formula, dealCaptor.getValue().getPlacementFormula());
    assertEquals(PlacementFormulaStatus.NEW, dealCaptor.getValue().getPlacementFormulaStatus());
  }

  @Test
  void shouldSuccessfullyCreateDealWithTargets() {
    // given
    String data = "data";
    String paramName = "paramName";
    SpringUserDetails mockUserDetails = mock(SpringUserDetails.class);
    DealTargetDTO.Builder targetDTOBuilder =
        new DealTargetDTO.Builder()
            .setParamName(paramName)
            .setTargetType(BaseTarget.TargetType.COUNTRY_STATE)
            .setRuleType(BaseTarget.RuleType.NEGATIVE)
            .setData(data);
    Set<DealTargetDTO> targetDTOs = Set.of(targetDTOBuilder.build());
    DirectDealDTO directDealDTO =
        DirectDealDTO.builder().targets(targetDTOs).autoUpdate(null).build();
    given(mockUserDetails.getPid()).willReturn(100L);
    given(userContext.getCurrentUser()).willReturn(mockUserDetails);
    ArgumentCaptor<DirectDeal> dealCaptor = ArgumentCaptor.forClass(DirectDeal.class);

    // when
    dealService.createDeal(directDealDTO);

    // then
    verify(directDealRepository).save(dealCaptor.capture());
    DealTarget target = dealCaptor.getValue().getDealTargets().iterator().next();
    assertEquals(data, target.getData());
    assertEquals(paramName, target.getParamName());
    assertEquals(BaseTarget.TargetType.COUNTRY_STATE, target.getTargetType());
    assertEquals(BaseTarget.RuleType.NEGATIVE, target.getRuleType());
    assertEquals(PlacementFormulaStatus.DONE, dealCaptor.getValue().getPlacementFormulaStatus());
  }

  @Test
  void shouldFailCreatingDealWhenValidationFails() {
    // given
    DirectDealDTO directDealDTO = new DirectDealDTO();
    ServerErrorCodes msg = ServerErrorCodes.SERVER_DEAL_FLOOR_INVALID;
    doThrow(new GenevaValidationException(msg))
        .when(dealValidator)
        .validateAndFixDeal(directDealDTO);

    // when
    GenevaValidationException result =
        assertThrows(GenevaValidationException.class, () -> dealService.createDeal(directDealDTO));

    // then
    assertEquals(msg, result.getErrorCode());
  }

  @Test
  void shouldFailCreatingDealWhenValidationForFormulaFails() {
    // given
    DirectDealDTO directDealDTO = new DirectDealDTO();
    ServerErrorCodes msg = ServerErrorCodes.SERVER_DEAL_CANNOT_USE_FORMULA_AND_EXPLICIT_ASSIGNMENT;
    doThrow(new GenevaValidationException(msg))
        .when(dealValidator)
        .validateForFormula(directDealDTO);

    // when
    GenevaValidationException result =
        assertThrows(GenevaValidationException.class, () -> dealService.createDeal(directDealDTO));

    // then
    assertEquals(msg, result.getErrorCode());
  }

  @Test
  void shouldFailUpdatingDealWhenValidationFails() {
    // given
    DirectDealDTO directDealDTO = DirectDealDTO.builder().pid(TEST_DEAL_PID).build();
    ServerErrorCodes msg = ServerErrorCodes.SERVER_DEAL_FLOOR_INVALID;
    doThrow(new GenevaValidationException(msg))
        .when(dealValidator)
        .validateAndFixDeal(directDealDTO);

    // when
    GenevaValidationException result =
        assertThrows(
            GenevaValidationException.class,
            () -> dealService.updateDeal(TEST_DEAL_PID, directDealDTO));

    // then
    assertEquals(msg, result.getErrorCode());
  }

  @Test
  void shouldFailUpdatingDealWhenValidationForFormulaFails() {
    // given
    DirectDealDTO directDealDTO =
        DirectDealDTO.builder().pid(TEST_DEAL_PID).dealId(TEST_DEAL_ID).build();
    DirectDeal directDeal = new DirectDeal();
    directDeal.setDealId(TEST_DEAL_ID);
    given(directDealRepository.findById(TEST_DEAL_PID)).willReturn(Optional.of(directDeal));
    ServerErrorCodes msg = ServerErrorCodes.SERVER_DEAL_CANNOT_USE_FORMULA_AND_EXPLICIT_ASSIGNMENT;
    doThrow(new GenevaValidationException(msg))
        .when(dealValidator)
        .validateForFormula(directDealDTO);

    // when
    GenevaValidationException result =
        assertThrows(
            GenevaValidationException.class,
            () -> dealService.updateDeal(TEST_DEAL_PID, directDealDTO));

    // then
    assertEquals(msg, result.getErrorCode());
  }

  @Test
  void shouldNullifyImpressionGoalsWhenUpdatedPriorityIsNotGuaranteed() {
    // given
    DirectDeal deal = new DirectDeal();
    deal.setDealId(TEST_DEAL_ID);
    given(directDealRepository.findById(TEST_DEAL_PID)).willReturn(Optional.of(deal));
    DirectDealDTO directDealDTO =
        DirectDealDTO.builder()
            .pid(TEST_DEAL_PID)
            .dealId(TEST_DEAL_ID)
            .guaranteedImpressionGoal(null)
            .dailyImpressionCap(null)
            .stop(new Date(1))
            .start(new Date(0))
            .priorityType(DealPriorityType.OPEN)
            .autoUpdate(null)
            .build();
    ArgumentCaptor<DirectDeal> dealCaptor = ArgumentCaptor.forClass(DirectDeal.class);

    // when
    dealService.updateDeal(TEST_DEAL_PID, directDealDTO);

    // then
    verify(directDealRepository).save(dealCaptor.capture());
    assertNull(dealCaptor.getValue().getGuaranteedImpressionGoal());
    assertNull(dealCaptor.getValue().getDailyImpressionCap());
    assertEquals(PlacementFormulaStatus.DONE, dealCaptor.getValue().getPlacementFormulaStatus());
  }

  @Test
  void shouldThrowExceptionWhenUpdatedDealIsDuplicated() {
    // given
    String dealId = "new-deal-id";
    DirectDealDTO directDealDTO = DirectDealDTO.builder().pid(TEST_DEAL_PID).dealId(dealId).build();
    DirectDeal deal = new DirectDeal();
    deal.setDealId("original-deal-id");
    given(directDealRepository.findById(TEST_DEAL_PID)).willReturn(Optional.of(deal));
    given(directDealRepository.countByDealId(dealId)).willReturn(1);

    // when
    GenevaValidationException result =
        assertThrows(
            GenevaValidationException.class,
            () -> dealService.updateDeal(TEST_DEAL_PID, directDealDTO));

    // then
    assertEquals(ServerErrorCodes.SERVER_DUPLICATE_DEAL_ID, result.getErrorCode());
  }

  @Test
  void shouldThrowWhenServiceUpdatesDealWithInvalidDealId() {
    // given
    DirectDealDTO directDealDTO = DirectDealDTO.builder().pid(TEST_DEAL_PID).build();
    given(directDealRepository.findById(TEST_DEAL_PID)).willReturn(Optional.of(new DirectDeal()));

    // when
    GenevaValidationException result =
        assertThrows(
            GenevaValidationException.class,
            () -> dealService.updateDeal(TEST_DEAL_PID, directDealDTO));

    // then
    assertEquals(ServerErrorCodes.SERVER_INVALID_DEAL_ID, result.getErrorCode());
  }

  @Test
  void
      shouldRemovePreviousPlacementAssignmentsWhenServiceUpdateDealToReplacePlacementFormulaWithSiteAssignment() {
    // given
    DirectDeal deal = mock(DirectDeal.class);
    given(deal.getPid()).willReturn(TEST_DEAL_PID);
    given(deal.getDealId()).willReturn(TEST_DEAL_ID);

    final List<Long> previousPositionPids = Arrays.asList(1L, 2L, 3L);
    final String[] placementFormulaValueHolder = new String[1];
    placementFormulaValueHolder[0] = "fake-placement-formula";
    when(deal.getPlacementFormula()).then(invocation -> placementFormulaValueHolder[0]);
    doAnswer(
            invocation -> {
              placementFormulaValueHolder[0] = (String) invocation.getArguments()[0];
              return null;
            })
        .when(deal)
        .setPlacementFormula(any());

    List<DealPosition> dealPositions =
        previousPositionPids.stream()
            .map(
                posPid -> {
                  DealPosition dealPosition = new DealPosition();
                  dealPosition.setPid(posPid + 100L);
                  return dealPosition;
                })
            .collect(Collectors.toList());

    given(deal.getPositions()).willReturn(dealPositions);
    given(deal.getSites()).willReturn(new ArrayList<>());
    given(deal.getPublishers()).willReturn(Collections.emptyList());
    given(directDealRepository.findById(TEST_DEAL_PID)).willReturn(Optional.of(deal));
    final List<Long> newSitePids = Arrays.asList(101L, 102L);

    List<DealSiteDTO> dealSiteDtos =
        newSitePids.stream()
            .map(
                sitePid -> {
                  DealSiteDTO dealSiteDto = mock(DealSiteDTO.class);
                  given(dealSiteDto.getPid()).willReturn(null);
                  given(dealSiteDto.getSitePid()).willReturn(sitePid);
                  return dealSiteDto;
                })
            .collect(Collectors.toList());

    // when
    DirectDealDTO directDealDTO =
        DirectDealDTO.builder()
            .pid(TEST_DEAL_PID)
            .dealId(TEST_DEAL_ID)
            .placementFormula(null)
            .autoUpdate(null)
            .sites(dealSiteDtos)
            .positions(Collections.emptyList())
            .sellers(Collections.emptyList())
            .build();
    when(directDealRepository.save(any())).then(invocation -> invocation.getArguments()[0]);

    // when
    DirectDealDTO result = dealService.updateDeal(TEST_DEAL_PID, directDealDTO);

    // then
    assertNotNull(result);
    assertNull(result.getPlacementFormula());
    assertNull(result.getAutoUpdate());
    assertNotNull(result.getSites());
    assertEquals(
        newSitePids,
        result.getSites().stream().map(DealSiteDTO::getSitePid).collect(Collectors.toList()));
    assertNotNull(result.getPositions());
    assertTrue(result.getPositions().isEmpty());
    assertNotNull(result.getSellers());
    assertTrue(result.getSellers().isEmpty());
  }

  @Test
  void shouldCreateDealWithAllSellersAndBidders() {
    // given
    // given
    DirectDealDTO directDealDTO =
        DirectDealDTO.builder()
            .allSellers(true)
            .allBidders(true)
            .placementFormula(null)
            .autoUpdate(null)
            .external(false)
            .build();

    DealBidderConfigView configView = new DealBidderConfigView();
    configView.setCompanyPid(111L);
    configView.setPid(11L);
    configView.setName("Test bidder");
    given(dealBidderRepository.findAll()).willReturn(Collections.singletonList(configView));
    given(bidderConfigRepository.findById(anyLong())).willReturn(Optional.of(new BidderConfig()));
    given(companyRepository.findAll(any(Specification.class)))
        .willReturn(Collections.singletonList(new Company()));

    SpringUserDetails mockUserDetails = mock(SpringUserDetails.class);
    given(mockUserDetails.getPid()).willReturn(100L);
    given(userContext.getCurrentUser()).willReturn(mockUserDetails);
    given(directDealRepository.save(any(DirectDeal.class))).willReturn(new DirectDeal());

    ArgumentCaptor<DirectDeal> dealCaptor = ArgumentCaptor.forClass(DirectDeal.class);

    // when
    dealService.createDeal(directDealDTO);

    // then
    verify(directDealRepository).save(dealCaptor.capture());
    assertTrue(dealCaptor.getValue().isAllSellers());
    assertEquals(1, dealCaptor.getValue().getPublishers().size());
    assertTrue(dealCaptor.getValue().isAllBidders());
    assertFalse(dealCaptor.getValue().isExternal());
    assertEquals(1, dealCaptor.getValue().getBidders().size());
    assertEquals(PlacementFormulaStatus.DONE, dealCaptor.getValue().getPlacementFormulaStatus());
  }

  @Test
  void shouldCreateDealWithAllSellersBidderProfilesRules() {
    // given
    DealBidderDTO dealBidderDTO =
        new DealBidderDTO.Builder()
            .setBidderPid(1L)
            .setFilterAdomains(List.of("adomainFake"))
            .setFilterSeats(Collections.emptyList())
            .build();

    RTBProfileDTO rtbProfileDTO = RTBProfileDTO.builder().rtbProfilePid(1L).build();
    DealRtbProfileViewUsingFormulas rtbProfileViewUsingFormulas =
        new DealRtbProfileViewUsingFormulas();
    given(dealRtbProfileViewRepository.findById(1L))
        .willReturn(Optional.of(rtbProfileViewUsingFormulas));
    when(bidderConfigRepository.findById(1L)).thenReturn(Optional.of(mock(BidderConfig.class)));

    DealRuleDTO dealRuleDTO = new DealRuleDTO.Builder().setRulePid(1L).build();

    DirectDealDTO directDealDTO =
        DirectDealDTO.builder()
            .allSellers(true)
            .allBidders(false)
            .autoUpdate(null)
            .bidders(List.of(dealBidderDTO))
            .profiles(List.of(rtbProfileDTO))
            .rules(Set.of(dealRuleDTO))
            .external(false)
            .build();

    Company company = new Company();
    given(companyRepository.findAll(any(Specification.class)))
        .willReturn(Collections.singletonList(company));

    SpringUserDetails mockUserDetails = mock(SpringUserDetails.class);
    given(mockUserDetails.getPid()).willReturn(100L);
    given(userContext.getCurrentUser()).willReturn(mockUserDetails);
    given(directDealRepository.save(any(DirectDeal.class))).willReturn(new DirectDeal());
    ArgumentCaptor<DirectDeal> dealCaptor = ArgumentCaptor.forClass(DirectDeal.class);

    // when
    dealService.createDeal(directDealDTO);

    // then
    verify(directDealRepository).save(dealCaptor.capture());
    verify(dealRtbProfileViewRepository).findById(1L);
    assertTrue(dealCaptor.getValue().isAllSellers());
    assertEquals(1, dealCaptor.getValue().getPublishers().size());
    assertFalse(dealCaptor.getValue().isAllBidders());
    assertEquals(1, dealCaptor.getValue().getBidders().size());
    assertEquals(1, dealCaptor.getValue().getProfiles().size());
    assertEquals(1, dealCaptor.getValue().getRules().size());
    assertEquals(PlacementFormulaStatus.DONE, dealCaptor.getValue().getPlacementFormulaStatus());
  }

  @Test
  void shouldThrowNotFoundExceptionWhenCreatingDealWithNonExistingRTBProfile() {
    // given
    long rtbProfilePid = 1L;
    RTBProfileDTO rtbProfile = RTBProfileDTO.builder().rtbProfilePid(rtbProfilePid).build();
    DirectDealDTO directDealDTO = DirectDealDTO.builder().profiles(List.of(rtbProfile)).build();
    given(dealRtbProfileViewRepository.findById(rtbProfilePid)).willReturn(Optional.empty());

    // when
    var ex =
        assertThrows(GenevaValidationException.class, () -> dealService.createDeal(directDealDTO));

    // then
    assertEquals(ServerErrorCodes.SERVER_RTB_PROFILE_NOT_FOUND, ex.getErrorCode());
  }

  @Test
  void shouldThrowNotFoundExceptionWhenUpdatingDealWithNonExistingRTBProfile() {
    // given
    DealRuleDTO dealRuleDTO = new DealRuleDTO();
    DirectDeal deal = new DirectDeal();
    deal.setDealId(TEST_DEAL_ID);
    given(directDealRepository.findById(TEST_DEAL_PID)).willReturn(Optional.of(deal));
    long rtbProfilePid = 1L;
    RTBProfileDTO rtbProfile = RTBProfileDTO.builder().rtbProfilePid(rtbProfilePid).build();
    DirectDealDTO directDealDTO =
        DirectDealDTO.builder()
            .pid(TEST_DEAL_PID)
            .dealId(TEST_DEAL_ID)
            .guaranteedImpressionGoal(10000L)
            .dailyImpressionCap(10000L)
            .stop(new Date(1))
            .start(new Date(0))
            .rules(Set.of(dealRuleDTO))
            .floor(new BigDecimal("10.00"))
            .dealCategory(2)
            .priorityType(DealPriorityType.OPEN)
            .profiles(List.of(rtbProfile))
            .build();
    given(dealRtbProfileViewRepository.findById(rtbProfilePid)).willReturn(Optional.empty());

    // when
    var ex =
        assertThrows(
            GenevaValidationException.class,
            () -> dealService.updateDeal(TEST_DEAL_PID, directDealDTO));

    // then
    assertEquals(ServerErrorCodes.SERVER_RTB_PROFILE_NOT_FOUND, ex.getErrorCode());
  }

  @Test
  void shouldCreateDealWithDealCategoryNull() {
    // given
    DirectDealDTO directDealDTO =
        DirectDealDTO.builder()
            .allSellers(true)
            .allBidders(true)
            .placementFormula(null)
            .autoUpdate(null)
            .dealCategory(null)
            .external(false)
            .build();
    SpringUserDetails mockUserDetails = mock(SpringUserDetails.class);
    given(mockUserDetails.getPid()).willReturn(100L);
    given(userContext.getCurrentUser()).willReturn(mockUserDetails);
    given(directDealRepository.save(any(DirectDeal.class))).willReturn(new DirectDeal());
    ArgumentCaptor<DirectDeal> dealCaptor = ArgumentCaptor.forClass(DirectDeal.class);

    // when
    dealService.createDeal(directDealDTO);

    // then
    verify(directDealRepository).save(dealCaptor.capture());
    assertEquals(1, dealCaptor.getValue().getDealCategory());
    assertEquals(PlacementFormulaStatus.DONE, dealCaptor.getValue().getPlacementFormulaStatus());
  }

  @Test
  void shouldCreateDealWithDealCategoryIsNotNull() {
    // given
    DirectDealDTO directDealDTO =
        DirectDealDTO.builder()
            .allSellers(true)
            .allBidders(true)
            .placementFormula(null)
            .autoUpdate(null)
            .dealCategory(2)
            .external(false)
            .build();
    SpringUserDetails mockUserDetails = mock(SpringUserDetails.class);
    given(mockUserDetails.getPid()).willReturn(100L);
    given(userContext.getCurrentUser()).willReturn(mockUserDetails);
    given(directDealRepository.save(any(DirectDeal.class))).willReturn(new DirectDeal());
    ArgumentCaptor<DirectDeal> dealCaptor = ArgumentCaptor.forClass(DirectDeal.class);

    // when
    dealService.createDeal(directDealDTO);

    // then
    verify(directDealRepository).save(dealCaptor.capture());
    assertEquals(2, dealCaptor.getValue().getDealCategory());
    assertEquals(PlacementFormulaStatus.DONE, dealCaptor.getValue().getPlacementFormulaStatus());
  }

  @Test
  void shouldSetPlacementFormulaStatusIsNotChangesWhenPlacementFormulaIsSame() {
    // given
    PlacementFormulaDTO placementFormula = new PlacementFormulaDTO();
    String formula = "formula";
    DirectDeal deal = new DirectDeal();
    deal.setDealId(TEST_DEAL_ID);
    deal.setPlacementFormulaStatus(PlacementFormulaStatus.IN_PROGRESS);
    deal.setPlacementFormula(formula);
    given(directDealRepository.findById(TEST_DEAL_PID)).willReturn(Optional.of(deal));
    given(placementFormulaAssembler.applyToString(placementFormula)).willReturn(formula);

    DirectDealDTO directDealDTO =
        DirectDealDTO.builder()
            .pid(TEST_DEAL_PID)
            .dealId(TEST_DEAL_ID)
            .stop(new Date(1))
            .start(new Date(0))
            .priorityType(DealPriorityType.OPEN)
            .autoUpdate(true)
            .placementFormula(placementFormula)
            .build();
    ArgumentCaptor<DirectDeal> dealCaptor = ArgumentCaptor.forClass(DirectDeal.class);

    // when
    dealService.updateDeal(TEST_DEAL_PID, directDealDTO);

    // then
    verify(directDealRepository).save(dealCaptor.capture());
    assertEquals(formula, dealCaptor.getValue().getPlacementFormula());
  }

  @Test
  void shouldSuccessfullyCreateDealWithExternalDealId() {
    // given
    DirectDealDTO directDealDTO = DirectDealDTO.builder().external(true).build();

    SpringUserDetails mockUserDetails = mock(SpringUserDetails.class);
    given(mockUserDetails.getPid()).willReturn(100L);
    given(userContext.getCurrentUser()).willReturn(mockUserDetails);
    ArgumentCaptor<DirectDeal> dealCaptor = ArgumentCaptor.forClass(DirectDeal.class);

    // when
    dealService.createDeal(directDealDTO);

    // then
    verify(directDealRepository).save(dealCaptor.capture());
    assertTrue(dealCaptor.getValue().isExternal());
  }

  @Test
  void shouldSuccessfullyUpdateDealWithExternalDealId() {
    // given
    DirectDeal deal = new DirectDeal();
    deal.setDealId(TEST_DEAL_ID);
    given(directDealRepository.findById(TEST_DEAL_PID)).willReturn(Optional.of(deal));
    DirectDealDTO directDealDTO =
        DirectDealDTO.builder().pid(TEST_DEAL_PID).dealId(TEST_DEAL_ID).external(true).build();

    // when
    dealService.updateDeal(TEST_DEAL_PID, directDealDTO);

    // then
    ArgumentCaptor<DirectDeal> dealCaptor = ArgumentCaptor.forClass(DirectDeal.class);
    verify(directDealRepository).save(dealCaptor.capture());
    assertTrue(dealCaptor.getValue().isExternal());
  }

  private PlacementFormulaDTO createPlacementFormulaDTO(
      FormulaAttributeDTO formulaAttributeDTO,
      FormulaOperatorDTO formulaOperatorDTO,
      String ruleData) {

    FormulaRuleDTO formulaRuleDTO = new FormulaRuleDTO();
    formulaRuleDTO.setAttribute(formulaAttributeDTO);
    formulaRuleDTO.setOperator(formulaOperatorDTO);
    formulaRuleDTO.setRuleData(ruleData);

    FormulaGroupDTO formulaGroupDTO = new FormulaGroupDTO(List.of(formulaRuleDTO));
    return new PlacementFormulaDTO(FormulaGroupingDTO.OR, List.of(formulaGroupDTO));
  }
}
