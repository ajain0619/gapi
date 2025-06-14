package com.nexage.app.services.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.nexage.admin.core.enums.DealCategory;
import com.nexage.admin.core.enums.RuleType;
import com.nexage.admin.core.enums.Status;
import com.nexage.admin.core.enums.site.Platform;
import com.nexage.admin.core.model.Company;
import com.nexage.admin.core.model.CompanyRule;
import com.nexage.admin.core.model.DealRtbProfileViewUsingFormulas;
import com.nexage.admin.core.model.DirectDeal;
import com.nexage.admin.core.model.SiteView;
import com.nexage.admin.core.repository.DealRtbProfileViewRepository;
import com.nexage.admin.core.repository.DirectDealRepository;
import com.nexage.admin.core.repository.DirectDealViewRepository;
import com.nexage.admin.core.repository.RuleRepository;
import com.nexage.admin.core.repository.SiteViewRepository;
import com.nexage.admin.core.sparta.jpa.model.DealPosition;
import com.nexage.admin.core.sparta.jpa.model.DealRule;
import com.nexage.admin.core.sparta.jpa.model.DealSite;
import com.nexage.admin.core.sparta.jpa.model.PositionView;
import com.nexage.app.dto.DirectDealDTO;
import com.nexage.app.dto.deal.DealBuyerDTO;
import com.nexage.app.dto.deal.PublisherSitePositionDTO;
import com.nexage.app.dto.deal.RTBProfileDTO;
import com.nexage.app.dto.deals.DealDTO;
import com.nexage.app.dto.sellingrule.SellerRuleDTO;
import com.nexage.app.error.ServerErrorCodes;
import com.nexage.app.mapper.deal.DirectDealDTOMapperWithoutSuppliersAndBidders;
import com.nexage.app.services.DealService;
import com.nexage.app.util.assemblers.sellingrule.PlacementFormulaAssembler;
import com.nexage.app.util.assemblers.sellingrule.RuleAssembler;
import com.nexage.app.web.support.TestObjectsFactory;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class DirectDealServiceImplTest {
  @Mock private DirectDealViewRepository mockDirectDealViewRepository;
  @Mock private SiteViewRepository siteViewRepository;
  @Mock private DealRtbProfileViewRepository dealRtbProfileViewRepository;
  @Mock private RuleRepository ruleRepository;
  @Mock private RuleAssembler ruleAssembler;
  @Mock private DirectDealRepository directDealRepository;
  @Mock private Pageable pageable;
  @Mock private PlacementFormulaAssembler placementFormulaAssembler;
  @Mock private Validator validator;
  @Mock private DirectDealDTO directDealDTO;
  @Mock private List<DealBuyerDTO> dealBuyerDTOS;
  @Mock private DealService dealService;
  @Mock private DirectDeal deal;
  @InjectMocks private DirectDealServiceImpl directDealService;

  Page<DirectDeal> pagedEntity;
  Page<DirectDealDTO> pagedDirectDeal;

  @BeforeEach
  void setup() {
    pagedEntity = new PageImpl(TestObjectsFactory.gimme(10, DirectDeal.class));
    pagedDirectDeal = pagedEntity.map(DirectDealDTOMapperWithoutSuppliersAndBidders.MAPPER::map);

    lenient().when(validator.validate(eq(directDealDTO))).thenReturn(Collections.emptySet());
  }

  @Test
  void shouldCreateDealFromNotNullDealCategory() {
    when(dealService.createDeal(directDealDTO)).thenReturn(directDealDTO);
    when(directDealDTO.getDealCategory()).thenReturn(DealCategory.SSP.asInt());
    assertEquals(directDealDTO, directDealService.createDeal(directDealDTO));
  }

  @Test
  void shouldCreateDealFromNullDealCategory() {
    when(dealService.createDeal(directDealDTO)).thenReturn(directDealDTO);
    when(directDealDTO.getDealCategory()).thenReturn(null);
    doNothing().when(directDealDTO).setDealCategory(anyInt());
    assertEquals(directDealDTO, directDealService.createDeal(directDealDTO));
  }

  @Test
  void shouldUpdateDealFromNotNullDealCategory() {
    long dealId = 1000L;
    when(dealService.updateDeal(dealId, directDealDTO)).thenReturn(directDealDTO);
    when(deal.getDealCategory()).thenReturn(DealCategory.SSP.asInt());
    when(directDealDTO.getDealCategory()).thenReturn(DealCategory.SSP.asInt());
    when(directDealRepository.findById(dealId)).thenReturn(Optional.of(deal));
    assertEquals(directDealDTO, directDealService.updateDeal(dealId, directDealDTO));
  }

  @Test
  void shouldUpdateDealFromNullDealCategory() {
    long dealId = 1000L;
    when(dealService.updateDeal(dealId, directDealDTO)).thenReturn(directDealDTO);
    doNothing().when(deal).setDealCategory(anyInt());
    doNothing().when(directDealDTO).setDealCategory(anyInt());
    when(deal.getDealCategory()).thenReturn(null);
    when(directDealDTO.getDealCategory()).thenReturn(null);
    when(directDealRepository.findById(dealId)).thenReturn(Optional.of(deal));
    assertEquals(directDealDTO, directDealService.updateDeal(dealId, directDealDTO));
  }

  @Test
  void shouldUpdateDealStatus() {
    // given
    long pid = 123L;
    DirectDeal deal = new DirectDeal();
    deal.setPid(pid);
    when(directDealRepository.findById(pid)).thenReturn(Optional.of(deal));

    // when
    directDealService.updateDealStatus(pid, DirectDeal.DealStatus.Active);

    // then
    assertEquals(DirectDeal.DealStatus.Active, deal.getStatus());
    verify(directDealRepository).findById(pid);
    verify(directDealRepository).save(deal);
  }

  @Test
  void shouldThrowNotFoundExceptionWhenUpdatingDealStatus() {
    // given
    long pid = 123L;
    when(directDealRepository.findById(pid)).thenReturn(Optional.empty());

    // when
    var result =
        assertThrows(
            GenevaValidationException.class,
            () -> directDealService.updateDealStatus(pid, DirectDeal.DealStatus.Active));

    // then
    assertEquals(ServerErrorCodes.SERVER_DEAL_NOT_FOUND, result.getErrorCode());
    verify(directDealRepository).findById(pid);
    verifyNoMoreInteractions(directDealRepository);
  }

  @Test
  void shouldGetAllBuyers() {
    when(dealService.getAllBuyers()).thenReturn(dealBuyerDTOS);
    assertEquals(dealBuyerDTOS, directDealService.getAllBuyers());
  }

  @Test
  void shouldGetDealsNoCriteria() {
    when(directDealRepository.findAll(any(Specification.class), any(Pageable.class)))
        .thenReturn(pagedEntity);

    Page<DealDTO> returnedPage =
        directDealService.getPagedDealsWithRules(Optional.empty(), pageable);

    assertEquals(
        pagedDirectDeal.getContent().get(0).getPid(), returnedPage.getContent().get(0).getPid());
    assertEquals(pagedDirectDeal.getTotalElements(), returnedPage.getTotalElements());
  }

  @Test
  void shouldGetDealWithDealPidAndPlacementFormulaAndAutoUpdate() {
    DirectDeal directDeal0 = pagedEntity.getContent().get(0);
    Long directDeal0Pid = directDeal0.getPid();
    directDeal0.setPlacementFormula(
        "{\"groupedBy\":\"OR\",\"formulaGroups\":[{\"formulaRules\":[{\"attribute\":\"PUBLISHER_NAME\",\"operator\":\"EQUALS\",\"ruleData\":\"DanTestSeller\"}]}]}");
    when(directDealRepository.findById(directDeal0Pid)).thenReturn(Optional.of(directDeal0));

    // method under test
    DirectDealDTO directDealDTO = directDealService.getDeal(directDeal0Pid);

    assertEquals(1, directDealDTO.getPlacementFormula().getFormulaGroups().size());
    assertEquals(directDeal0.getAutoUpdate(), directDealDTO.getAutoUpdate());
  }

  @Test
  void shouldThrowDealPidNotFoundException() {
    expectGenevaValidationException(
        ServerErrorCodes.SERVER_DEAL_NOT_FOUND, () -> directDealService.getDeal(2));
  }

  @Test
  void shouldThrowDealNotFoundExceptionForPublisherMapForDeal() {
    expectGenevaValidationException(
        ServerErrorCodes.SERVER_DEAL_NOT_FOUND, () -> directDealService.getPublisherMapForDeal(2));
  }

  @Test
  void shouldGetDealWithDealPidAndNullPlacementFormulaAndNullAutoUpdate() {
    DirectDeal directDeal0 = pagedEntity.getContent().get(0);
    directDeal0.setPlacementFormula(null);
    Long directDeal0Pid = directDeal0.getPid();
    when(directDealRepository.findById(directDeal0Pid)).thenReturn(Optional.of(directDeal0));

    // method under test
    DirectDealDTO directDealDTO = directDealService.getDeal(directDeal0Pid);

    assertEquals(directDeal0Pid, directDealDTO.getPid());
    // not set
    assertNull(directDealDTO.getPlacementFormula());
    assertNull(directDealDTO.getAutoUpdate());

    verifyNoInteractions(placementFormulaAssembler);
  }

  @Test
  void shouldGetAllDealsWithNullPlacementFormulaAndNullAutoUpdate() {
    DirectDeal directDeal0 = pagedEntity.getContent().get(0);
    when(directDealRepository.findAll()).thenReturn(Collections.singletonList(directDeal0));

    // method under test
    List<DirectDealDTO> list = directDealService.getAllDeals();

    assertEquals(1, list.size());
    DirectDealDTO directDealDTO = list.get(0);
    assertEquals(directDeal0.getPid(), directDealDTO.getPid());
    // not set
    assertNull(directDealDTO.getPlacementFormula());
    assertNull(directDealDTO.getAutoUpdate());
  }

  @Test
  void shouldReturnAllDealsWithRules() {
    // given
    DirectDeal directDeal0 = pagedEntity.getContent().get(0);
    directDeal0.setRules(List.of(new DealRule()));
    DirectDeal directDeal1 = pagedEntity.getContent().get(1);
    directDeal1.setRules(List.of(new DealRule()));
    when(directDealRepository.findByRulesNotNull()).thenReturn(List.of(directDeal0, directDeal1));

    // when
    List<DirectDealDTO> result = directDealService.getAllDealsWithRules();

    // then
    assertEquals(2, result.size());
    assertEquals(directDeal0.getPid(), result.get(0).getPid());
    assertEquals(directDeal1.getPid(), result.get(1).getPid());
    assertEquals(directDeal0.getRules().size(), result.get(0).getRules().size());
    assertEquals(directDeal1.getRules().size(), result.get(1).getRules().size());
    verify(directDealRepository).findByRulesNotNull();
  }

  @Test
  void shouldGetAllDealsWithRules() {
    DirectDeal directDeal0 = pagedEntity.getContent().get(0);
    when(directDealRepository.findByRulesNotNull())
        .thenReturn(Collections.singletonList(directDeal0));
    List<DirectDealDTO> list = directDealService.getAllDealsWithRules();

    assertEquals(1, list.size());
    DirectDealDTO directDealDTO = list.get(0);
    assertEquals(directDeal0.getPid(), directDealDTO.getPid());
  }

  @Test
  void shouldGetDealsWithDescriptionCriteria() {
    when(directDealRepository.findAll(any(Specification.class), any(Pageable.class)))
        .thenReturn(pagedEntity);

    Page<DealDTO> returnedPage =
        directDealService.getPagedDealsWithRules(Optional.of("description:blah"), pageable);

    assertEquals(
        pagedDirectDeal.getContent().get(0).getPid(), returnedPage.getContent().get(0).getPid());
    assertEquals(pagedDirectDeal.getTotalElements(), returnedPage.getTotalElements());
  }

  @Test
  void shouldGetDealsWithTierCriteria() {
    when(directDealRepository.findAll(any(Specification.class), any(Pageable.class)))
        .thenReturn(pagedEntity);

    Page<DealDTO> returnedPage =
        directDealService.getPagedDealsWithRules(Optional.of("tier:open"), pageable);

    assertEquals(
        pagedDirectDeal.getContent().get(0).getPid(), returnedPage.getContent().get(0).getPid());
    assertEquals(pagedDirectDeal.getTotalElements(), returnedPage.getTotalElements());
  }

  @Test
  void shouldThrowOnInvalidTierWhenGettingDealsWithTierCriteria() {

    expectGenevaValidationException(
        ServerErrorCodes.SERVER_INVALID_INPUT,
        () -> directDealService.getPagedDealsWithRules(Optional.of("tier:invalidTier"), pageable));
  }

  private void expectGenevaValidationException(
      ServerErrorCodes expectedErrorMessage, Runnable runnable) {
    GenevaValidationException e =
        assertThrows(
            GenevaValidationException.class,
            runnable::run,
            "GenevaValidationException expected but not thrown");
    assertEquals(expectedErrorMessage, e.getErrorCode());
  }

  @Test
  void shouldMapSitesCorrectlyWhenGettingPublisherMapForDealAndDealIsFound() {
    Company company = createCompany(1L, "Company1");
    SiteView site1 = createSite(1L, "Site1", company);
    SiteView site2 = createSite(2L, "Site2", company);

    DirectDeal directDeal = createDirectDeal(List.of(1L, 2L, 3L), List.of());

    when(directDealRepository.findById(1L)).thenReturn(Optional.of(directDeal));
    when(siteViewRepository.findAllById(any(List.class))).thenReturn(List.of(site1, site2));

    List<PublisherSitePositionDTO> publisherMapForDeal =
        directDealService.getPublisherMapForDeal(1L);
    assertNotNull(publisherMapForDeal);
    assertEquals(1, publisherMapForDeal.size());
    assertEquals(1L, publisherMapForDeal.get(0).getPublisherId());
    assertEquals("Company1", publisherMapForDeal.get(0).getPublisherName());
    assertNotNull(publisherMapForDeal.get(0).getSites());
    assertEquals(2, publisherMapForDeal.get(0).getSites().size());
    assertEquals(1L, publisherMapForDeal.get(0).getSites().get(0).getSiteId().longValue());
    assertEquals("Site1", publisherMapForDeal.get(0).getSites().get(0).getSiteName());
    assertEquals(2L, publisherMapForDeal.get(0).getSites().get(1).getSiteId().longValue());
    assertEquals("Site2", publisherMapForDeal.get(0).getSites().get(1).getSiteName());
  }

  @Test
  void shouldMapSitesFromDifferentCompaniesCorrectlyWhenGettingPublisherMapForDealAndDealIsFound() {
    Company company1 = createCompany(1L, "Company1");
    Company company2 = createCompany(2L, "Company2");

    SiteView site1 = createSite(1L, "Site1", company1);
    SiteView site2 = createSite(2L, "Site2", company2);

    DirectDeal directDeal = createDirectDeal(List.of(1L, 2L, 3L), List.of());

    when(directDealRepository.findById(1L)).thenReturn(Optional.of(directDeal));
    when(siteViewRepository.findAllById(any(List.class))).thenReturn(List.of(site1, site2));

    List<PublisherSitePositionDTO> publisherMapForDeal =
        directDealService.getPublisherMapForDeal(1L);

    assertNotNull(publisherMapForDeal);
    assertEquals(2, publisherMapForDeal.size());

    publisherMapForDeal =
        publisherMapForDeal.stream()
            .sorted(Comparator.comparingLong(PublisherSitePositionDTO::getPublisherId))
            .collect(Collectors.toList());

    assertEquals(1L, publisherMapForDeal.get(0).getPublisherId());
    assertEquals("Company1", publisherMapForDeal.get(0).getPublisherName());
    assertNotNull(publisherMapForDeal.get(0).getSites());
    assertEquals(1, publisherMapForDeal.get(0).getSites().size());
    assertEquals("Site1", publisherMapForDeal.get(0).getSites().get(0).getSiteName());
    assertEquals(1L, publisherMapForDeal.get(0).getSites().get(0).getSiteId().longValue());

    assertEquals(2L, publisherMapForDeal.get(1).getPublisherId());
    assertEquals("Company2", publisherMapForDeal.get(1).getPublisherName());
    assertNotNull(publisherMapForDeal.get(1).getSites());
    assertEquals(1, publisherMapForDeal.get(1).getSites().size());
    assertEquals("Site2", publisherMapForDeal.get(1).getSites().get(0).getSiteName());
    assertEquals(2L, publisherMapForDeal.get(1).getSites().get(0).getSiteId().longValue());
  }

  @Test
  void shouldMapPositionsCorrectlyWhenGettingPublisherMapForDealAndDealIsFound() {
    Company company = createCompany(1L, "Company1");

    SiteView site1 = createSite(1L, "Site1", company);
    SiteView site2 = createSite(2L, "Site2", company);

    DealPosition dealPosition11 = createDealPosition(11L, "Site1Position1", site1);
    DealPosition dealPosition12 = createDealPosition(12L, "Site1Position2", site1);
    DealPosition dealPosition21 = createDealPosition(21L, "Site2Position1", site2);
    DealPosition dealPosition22 = createDealPosition(22L, "Site2Position2", site2);

    DirectDeal directDeal =
        createDirectDeal(
            List.of(), List.of(dealPosition11, dealPosition12, dealPosition21, dealPosition22));

    when(directDealRepository.findById(1L)).thenReturn(Optional.of(directDeal));

    List<PublisherSitePositionDTO> publisherMapForDeal =
        directDealService.getPublisherMapForDeal(1L);
    assertNotNull(publisherMapForDeal);
    assertEquals(1, publisherMapForDeal.size());
    assertEquals(1L, publisherMapForDeal.get(0).getPublisherId());
    assertEquals("Company1", publisherMapForDeal.get(0).getPublisherName());
    assertNotNull(publisherMapForDeal.get(0).getSites());
    assertEquals(2, publisherMapForDeal.get(0).getSites().size());

    assertEquals("Site1", publisherMapForDeal.get(0).getSites().get(0).getSiteName());
    assertEquals(1L, publisherMapForDeal.get(0).getSites().get(0).getSiteId().longValue());
    assertNotNull(publisherMapForDeal.get(0).getSites().get(1).getPostitionNames());
    assertEquals(2L, publisherMapForDeal.get(0).getSites().get(0).getPostitionNames().size());
    assertNotNull(publisherMapForDeal.get(0).getSites().get(0).getPostitionNames());
    assertEquals(2L, publisherMapForDeal.get(0).getSites().get(0).getPostitionNames().size());
    assertEquals(
        "Site1Position1",
        publisherMapForDeal.get(0).getSites().get(0).getPostitionNames().get(0).getPositionName());
    assertEquals(
        11L,
        publisherMapForDeal.get(0).getSites().get(0).getPostitionNames().get(0).getPositionId());
    assertEquals(
        "Site1Position2",
        publisherMapForDeal.get(0).getSites().get(0).getPostitionNames().get(1).getPositionName());
    assertEquals(
        12L,
        publisherMapForDeal.get(0).getSites().get(0).getPostitionNames().get(1).getPositionId());

    assertEquals("Site2", publisherMapForDeal.get(0).getSites().get(1).getSiteName());
    assertEquals(2L, publisherMapForDeal.get(0).getSites().get(1).getSiteId().longValue());
    assertNotNull(publisherMapForDeal.get(0).getSites().get(1).getPostitionNames());
    assertEquals(2L, publisherMapForDeal.get(0).getSites().get(1).getPostitionNames().size());
    assertEquals(
        "Site2Position1",
        publisherMapForDeal.get(0).getSites().get(1).getPostitionNames().get(0).getPositionName());
    assertEquals(
        21L,
        publisherMapForDeal.get(0).getSites().get(1).getPostitionNames().get(0).getPositionId());
    assertEquals(
        "Site2Position2",
        publisherMapForDeal.get(0).getSites().get(1).getPostitionNames().get(1).getPositionName());
    assertEquals(
        22L,
        publisherMapForDeal.get(0).getSites().get(1).getPostitionNames().get(1).getPositionId());
  }

  @Test
  void
      shouldMapPositionsFromDifferentCompaniesCorrectlyWhenGettingPublisherMapForDealAndDealIsFound() {
    Company company1 = createCompany(1L, "Company1");
    Company company2 = createCompany(2L, "Company2");

    SiteView site1 = createSite(1L, "Site1", company1);
    SiteView site2 = createSite(2L, "Site2", company2);

    DealPosition dealPosition11 = createDealPosition(11L, "Site1Position1", site1);
    DealPosition dealPosition12 = createDealPosition(12L, "Site1Position2", site1);
    DealPosition dealPosition21 = createDealPosition(21L, "Site2Position1", site2);
    DealPosition dealPosition22 = createDealPosition(22L, "Site2Position2", site2);

    DirectDeal directDeal =
        createDirectDeal(
            List.of(), List.of(dealPosition11, dealPosition12, dealPosition21, dealPosition22));

    when(directDealRepository.findById(1L)).thenReturn(Optional.of(directDeal));

    List<PublisherSitePositionDTO> publisherMapForDeal =
        directDealService.getPublisherMapForDeal(1L);

    assertNotNull(publisherMapForDeal);
    assertEquals(2, publisherMapForDeal.size());

    publisherMapForDeal =
        publisherMapForDeal.stream()
            .sorted(Comparator.comparingLong(PublisherSitePositionDTO::getPublisherId))
            .collect(Collectors.toList());

    validatePublisherMapForDeal(
        publisherMapForDeal.get(0), 1L, "Company1", "Site1", "Site1Position1", "Site1Position2");
    assertEquals(
        11L,
        publisherMapForDeal.get(0).getSites().get(0).getPostitionNames().get(0).getPositionId());
    assertEquals(
        12L,
        publisherMapForDeal.get(0).getSites().get(0).getPostitionNames().get(1).getPositionId());

    validatePublisherMapForDeal(
        publisherMapForDeal.get(1), 2L, "Company2", "Site2", "Site2Position1", "Site2Position2");
    assertEquals(
        21L,
        publisherMapForDeal.get(1).getSites().get(0).getPostitionNames().get(0).getPositionId());
    assertEquals(
        22L,
        publisherMapForDeal.get(1).getSites().get(0).getPostitionNames().get(1).getPositionId());
  }

  private void validatePublisherMapForDeal(
      PublisherSitePositionDTO publisherSitePositionDTO,
      long id,
      String companyName,
      String siteName,
      String position1Name,
      String position2Name) {
    // Publisher
    assertEquals(id, publisherSitePositionDTO.getPublisherId());
    assertEquals(companyName, publisherSitePositionDTO.getPublisherName());
    assertNotNull(publisherSitePositionDTO.getSites());
    assertEquals(1, publisherSitePositionDTO.getSites().size());

    // Site
    assertEquals(siteName, publisherSitePositionDTO.getSites().get(0).getSiteName());
    assertEquals(id, publisherSitePositionDTO.getSites().get(0).getSiteId().longValue());

    // Position
    assertNotNull(publisherSitePositionDTO.getSites().get(0).getPostitionNames());
    assertEquals(2, publisherSitePositionDTO.getSites().get(0).getPostitionNames().size());
    assertEquals(
        position1Name,
        publisherSitePositionDTO.getSites().get(0).getPostitionNames().get(0).getPositionName());
    assertEquals(
        position2Name,
        publisherSitePositionDTO.getSites().get(0).getPostitionNames().get(1).getPositionName());
  }

  @Test
  void shouldReturnRtbProfileDtoWhenGettingSupplier() {
    // given
    long pid = 123L;
    DealRtbProfileViewUsingFormulas rtbProfile = new DealRtbProfileViewUsingFormulas();
    ReflectionTestUtils.setField(rtbProfile, "pid", pid);
    ReflectionTestUtils.setField(rtbProfile, "platform", "ANDROID");
    given(dealRtbProfileViewRepository.findById(pid)).willReturn(Optional.of(rtbProfile));

    // when
    RTBProfileDTO dto = directDealService.getSupplier(pid);

    // then
    assertEquals(pid, dto.getPid());
    assertEquals(pid, dto.getRtbProfilePid());
    assertEquals(Platform.ANDROID, dto.getPlatform());
  }

  @Test
  void shouldReturnAListOfRtbProfileDtoWhenGettingAllNonarchivedSuppliers() {
    // given
    long pid1 = 1L;
    DealRtbProfileViewUsingFormulas rtbProfile1 = new DealRtbProfileViewUsingFormulas();
    ReflectionTestUtils.setField(rtbProfile1, "pid", pid1);
    ReflectionTestUtils.setField(rtbProfile1, "platform", "ANDROID");

    long pid2 = 2L;
    DealRtbProfileViewUsingFormulas rtbProfile2 = new DealRtbProfileViewUsingFormulas();
    ReflectionTestUtils.setField(rtbProfile2, "pid", pid2);
    ReflectionTestUtils.setField(rtbProfile2, "platform", "IPHONE");

    given(dealRtbProfileViewRepository.findAll(any(Specification.class)))
        .willReturn(List.of(rtbProfile1, rtbProfile2));

    // when
    List<RTBProfileDTO> dtos = directDealService.getAllNonarchivedSuppliers();

    // then
    assertEquals(2, dtos.size());
    assertEquals(pid2, dtos.get(1).getPid());
    assertEquals(pid2, dtos.get(1).getRtbProfilePid());
    assertEquals(Platform.IPHONE, dtos.get(1).getPlatform());
  }

  private DirectDeal createDirectDeal(List<Long> sitePids, List<DealPosition> positions) {
    DirectDeal directDeal = new DirectDeal();
    sitePids.forEach(sitePid -> addDealSite(directDeal, sitePid));
    directDeal.getPositions().addAll(positions);
    return directDeal;
  }

  private void addDealSite(DirectDeal directDeal, long sitePid) {
    DealSite dealSite = new DealSite();
    dealSite.setDeal(directDeal);
    dealSite.setSitePid(sitePid);
    directDeal.getSites().add(dealSite);
  }

  private Company createCompany(long pid, String name) {
    Company company = new Company();
    company.setPid(pid);
    company.setName(name);
    return company;
  }

  private SiteView createSite(long pid, String name, Company company) {
    SiteView site = new SiteView();
    site.setPid(pid);
    site.setName(name);
    site.setCompany(company);
    site.setCompanyPid(company.getPid());
    return site;
  }

  private DealPosition createDealPosition(long pid, String name, SiteView site) {
    DealPosition dealPosition = new DealPosition();
    PositionView position = new PositionView();
    position.setPid(pid);
    position.setSiteView(site);
    position.setSitePid(site.getPid());
    position.setName(name);
    dealPosition.setPositionView(position);
    return dealPosition;
  }

  @Test
  void shouldFindRulesByDealPid() {
    // given
    Long pid = 1L;

    CompanyRule companyRule = makeCompanyRule(pid, RuleType.DEAL, Status.ACTIVE);
    List<CompanyRule> rules = List.of(companyRule);

    when(ruleRepository.findAllActiveDealRulesAssosiatedWithDeal(pid)).thenReturn(rules);
    when(ruleAssembler.make(companyRule, RuleAssembler.DEFAULT_FIELDS))
        .thenReturn(SellerRuleDTO.builder().build());

    // when
    List<SellerRuleDTO> list = directDealService.findRulesByDealPid(pid);

    // then
    assertEquals(1, list.size());
  }

  private CompanyRule makeCompanyRule(long pid, RuleType ruleType, Status status) {
    CompanyRule companyRule = TestObjectsFactory.createCompanyRule(pid);
    companyRule.setPid(pid);
    companyRule.setRuleType(ruleType);
    companyRule.setStatus(status);
    return companyRule;
  }
}
