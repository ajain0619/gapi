package com.nexage.app.services.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.nullable;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.common.collect.ImmutableSet;
import com.nexage.admin.core.model.Company;
import com.nexage.admin.core.model.DirectDeal;
import com.nexage.admin.core.model.SiteView;
import com.nexage.admin.core.repository.DirectDealRepository;
import com.nexage.admin.core.repository.PositionRepository;
import com.nexage.admin.core.repository.SiteRepository;
import com.nexage.admin.core.sparta.jpa.model.DealPosition;
import com.nexage.admin.core.sparta.jpa.model.DealPublisher;
import com.nexage.admin.core.sparta.jpa.model.DealSite;
import com.nexage.admin.core.sparta.jpa.model.PositionView;
import com.nexage.app.dto.DirectDealDTO;
import com.nexage.app.dto.deal.DealSiteDTO;
import com.nexage.app.dto.deal.PublisherSitePositionDTO;
import com.nexage.app.dto.deals.DealPositionDTO;
import com.nexage.app.dto.deals.DealPublisherDTO;
import com.nexage.app.error.ServerErrorCodes;
import com.nexage.app.security.UserContext;
import com.nexage.app.services.DealDTOService;
import com.nexage.app.services.DealService;
import com.nexage.app.services.DirectDealService;
import com.nexage.app.services.validation.SellerDealValidator;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import com.ssp.geneva.common.error.model.CommonErrorCodes;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

@ExtendWith(MockitoExtension.class)
class SellerDealServiceImplTest {

  @Mock private DirectDealService directDealService;
  @Mock private DirectDealRepository dealRepository;
  @Mock private SiteRepository siteRepository;
  @Mock private PositionRepository positionRepository;
  @Mock private DealService dealService;
  @Mock private DealDTOService dealDTOService;
  @Mock private UserContext userContext;
  @Mock private SellerDealValidator sellerDealValidator;
  @InjectMocks private SellerDealServiceImpl sellerDealService;

  @Test
  void whenInvalidQueryFieldArgumentIsPassed_thenGenevaValidationExceptionIsThrown() {
    Set<String> qf = ImmutableSet.of("bad field argument");
    assertThrows(
        GenevaValidationException.class,
        () ->
            sellerDealService.getPagedDealsAssociatedWithSeller(1L, qf, "", mock(Pageable.class)));
  }

  @Test
  void whenQueryTermArgumentIsEmpty_thenGenevaValidationExceptionIsThrown() {
    Set<String> qf = ImmutableSet.of("description");
    assertThrows(
        GenevaValidationException.class,
        () ->
            sellerDealService.getPagedDealsAssociatedWithSeller(1L, qf, "", mock(Pageable.class)));
  }

  @ParameterizedTest
  @CsvSource({"dealCategory,SSP"})
  void whenDealCategoryValues_andSentAsQfQt(String qf, String qt) {
    when(dealRepository.findAll(nullable(Specification.class), any(Pageable.class)))
        .thenReturn(mock(Page.class));
    sellerDealService.getPagedDealsAssociatedWithSeller(1L, createQf(qf), qt, mock(Pageable.class));
    verify(dealRepository).findAll(nullable(Specification.class), nullable(Pageable.class));
  }

  @Test
  void whenQueryFieldArgumentMapHasNoValue_thenGenevaValidationExceptionIsThrown() {
    var qf = new LinkedHashSet<String>();
    qf.add("{dealCategory=}");
    assertThrows(
        GenevaValidationException.class,
        () ->
            sellerDealService.getPagedDealsAssociatedWithSeller(1L, qf, "", mock(Pageable.class)));
  }

  @Test
  void whenValidFilterArgumentsArePassed_noExceptionIsThrown() {
    when(dealRepository.findAll(nullable(Specification.class), any(Pageable.class)))
        .thenReturn(mock(Page.class));
    sellerDealService.getPagedDealsAssociatedWithSeller(
        1L, createQf("description"), "deal", mock(Pageable.class));
    verify(dealRepository).findAll(nullable(Specification.class), nullable(Pageable.class));
  }

  @Test
  void whenDealIdArgumentsArePassed_noExceptionIsThrown() {
    var qf = new LinkedHashSet<String>();
    qf.add("{dealId=12345}");
    when(dealDTOService.createMultiValueMap(qf))
        .thenReturn(Optional.of(Map.of("dealId", List.of("12345"))));
    when(dealRepository.findAll(nullable(Specification.class), any(Pageable.class)))
        .thenReturn(mock(Page.class));
    sellerDealService.getPagedDealsAssociatedWithSeller(1L, qf, "", mock(Pageable.class));
    verify(dealRepository).findAll(nullable(Specification.class), nullable(Pageable.class));
  }

  @Test
  void whenDescriptionArgumentsArePassed_noExceptionIsThrown() {
    var qf = new LinkedHashSet<String>();
    qf.add("{description=deal}");
    when(dealDTOService.createMultiValueMap(qf))
        .thenReturn(Optional.of(Map.of("description", List.of("deal"))));
    when(dealRepository.findAll(nullable(Specification.class), any(Pageable.class)))
        .thenReturn(mock(Page.class));
    sellerDealService.getPagedDealsAssociatedWithSeller(1L, qf, "", mock(Pageable.class));
    verify(dealRepository).findAll(nullable(Specification.class), nullable(Pageable.class));
  }

  @Test
  void whenDealIdOrDescArgumentsArePassed_noExceptionIsThrown() {
    var qf = new LinkedHashSet<String>();
    qf.add("{dealId=12345,description=deal}");
    when(dealDTOService.createMultiValueMap(qf))
        .thenReturn(
            Optional.of(Map.of("dealId", List.of("12345"), "description", List.of("deal"))));
    when(dealRepository.findAll(nullable(Specification.class), any(Pageable.class)))
        .thenReturn(mock(Page.class));
    sellerDealService.getPagedDealsAssociatedWithSeller(1L, qf, "", mock(Pageable.class));
    verify(dealRepository).findAll(nullable(Specification.class), nullable(Pageable.class));
  }

  @Test
  void whenValidDealCategoryAndDealIdArePassed_noExceptionThrown() {
    var qf = new LinkedHashSet<String>();
    qf.add("{dealId=12345,dealCategory=SSP}");
    when(dealRepository.findAll(nullable(Specification.class), any(Pageable.class)))
        .thenReturn(mock(Page.class));
    when(dealDTOService.createMultiValueMap(qf))
        .thenReturn(
            Optional.of(Map.of("dealId", List.of("12345"), "dealCategory", List.of("SSP"))));
    var out =
        sellerDealService.getPagedDealsAssociatedWithSeller(1L, qf, null, mock(Pageable.class));
    verify(dealRepository).findAll(nullable(Specification.class), nullable(Pageable.class));
  }

  @Test
  void whenValidDealCategoryAndDealDescArePassed_noExceptionThrown() {
    when(userContext.isNexageUser()).thenReturn(true);
    var qf = new LinkedHashSet<String>();
    qf.add("{description=deal,dealCategory=SSP}");
    when(dealRepository.findAll(nullable(Specification.class), any(Pageable.class)))
        .thenReturn(mock(Page.class));
    when(dealDTOService.createMultiValueMap(qf))
        .thenReturn(
            Optional.of(Map.of("description", List.of("deal"), "dealCategory", List.of("SSP"))));

    var out =
        sellerDealService.getPagedDealsAssociatedWithSeller(1L, qf, null, mock(Pageable.class));
    verify(dealRepository).findAll(nullable(Specification.class), nullable(Pageable.class));
  }

  @Test
  void whenValidDealCategoryisPassed_noExceptionThrown() {
    var qf = new LinkedHashSet<String>();
    qf.add("{dealCategory=SSP}");
    when(dealRepository.findAll(nullable(Specification.class), any(Pageable.class)))
        .thenReturn(mock(Page.class));
    when(dealDTOService.createMultiValueMap(qf))
        .thenReturn(Optional.of(Map.of("dealCategory", List.of("SSP"))));
    var out =
        sellerDealService.getPagedDealsAssociatedWithSeller(1L, qf, null, mock(Pageable.class));
    verify(dealRepository).findAll(nullable(Specification.class), nullable(Pageable.class));
  }

  @Test
  void whenNoFilterArgumentsArePassed_noExceptionIsThrown() {
    sellerDealService.getPagedDealsAssociatedWithSeller(1L, null, null, null);
    verify(dealRepository).findAll(nullable(Specification.class), nullable(Pageable.class));
  }

  @Test
  void whenNoDealIsFound_ExceptionIsThrown() {
    Long sellerId = 1L;
    Long pid = 2L;

    GenevaValidationException exception =
        assertThrows(
            GenevaValidationException.class,
            () -> sellerDealService.getDealAssociatedWithSeller(sellerId, pid));
    assertEquals(ServerErrorCodes.SERVER_DEAL_NOT_FOUND, exception.getErrorCode());
  }

  @Test
  void whenDealIsFound_NoExceptionIsThrown() {
    // given
    Long sellerId = 1L;
    Long pid = 2L;

    DirectDealDTO directDealDTO = DirectDealDTO.builder().build();
    DirectDeal directDeal = new DirectDeal();
    when(dealRepository.findOne(nullable(Specification.class)))
        .thenReturn((Optional.of(directDeal)));

    // when
    sellerDealService.getDealAssociatedWithSeller(sellerId, pid);

    // then
    verify(dealRepository).findOne(nullable(Specification.class));
  }

  @Test
  void whenSomePublishersAreReturnedThatDoNotMatchTheSellerId_TheyAreFilteredOut() {
    // given
    Long sellerId = 1L;
    Long pid = 2L;

    DealPublisher dealPublisher1 = new DealPublisher();
    dealPublisher1.setPubPid(1L);

    DealPublisher dealPublisher2 = new DealPublisher();
    dealPublisher2.setPubPid(2L);

    List<DealPublisher> dealPublishers = List.of(dealPublisher1, dealPublisher2);

    DirectDeal directDeal = new DirectDeal();
    directDeal.setPublishers(dealPublishers);
    when(dealRepository.findOne(nullable(Specification.class)))
        .thenReturn((Optional.of(directDeal)));

    // when
    DirectDealDTO returnedDirectDealDTO =
        sellerDealService.getDealAssociatedWithSeller(sellerId, pid);

    // then
    assertEquals(1, returnedDirectDealDTO.getSellers().size());
    assertEquals(sellerId, returnedDirectDealDTO.getSellers().get(0).getPublisherPid());
  }

  @Test
  void whenGetPublisherMapForDealAndDealNotFound_thenGenevaValidationExceptionIsThrown() {
    when(dealRepository.findOne(nullable(Specification.class)))
        .thenReturn(Optional.<DirectDeal>empty());
    GenevaValidationException exception =
        assertThrows(
            GenevaValidationException.class,
            () -> sellerDealService.getPublisherMapForDeal(1L, 1L),
            ServerErrorCodes.SERVER_DEAL_NOT_FOUND.toString());
    assertEquals(ServerErrorCodes.SERVER_DEAL_NOT_FOUND, exception.getErrorCode());
  }

  @Test
  void
      whenGetDealAssociatedWithSellerAndDealHasSitesFromDifferentSellers_thenSitesAreFilteredCorrectly() {
    DirectDeal directDeal = new DirectDeal();

    DealSite dealSite1 = new DealSite();
    dealSite1.setSitePid(1L);
    DealSite dealSite2 = new DealSite();
    dealSite2.setSitePid(2L);
    DealSite dealSite3 = new DealSite();
    dealSite3.setSitePid(3L);
    DealSite dealSite4 = new DealSite();
    dealSite4.setSitePid(4L);

    List<DealSite> dealSites = List.of(dealSite1, dealSite2, dealSite3, dealSite4);

    directDeal.setSites(dealSites);

    when(dealRepository.findOne(nullable(Specification.class))).thenReturn(Optional.of(directDeal));
    when(siteRepository.findPidsByCompanyPidsWithStatusNotDeleted(any()))
        .thenReturn(Set.of(1L, 2L));

    ArgumentCaptor<List> sitesCaptor = ArgumentCaptor.forClass(List.class);
    ArgumentCaptor<List> positionsCaptor = ArgumentCaptor.forClass(List.class);

    DirectDealDTO returnedDirectDealDTO = sellerDealService.getDealAssociatedWithSeller(1L, 2L);

    assertNotNull(returnedDirectDealDTO.getSites());
    assertEquals(2, returnedDirectDealDTO.getSites().size());
    assertEquals(
        1L, ((DealSiteDTO) returnedDirectDealDTO.getSites().get(0)).getSitePid().longValue());
    assertEquals(
        2L, ((DealSiteDTO) returnedDirectDealDTO.getSites().get(1)).getSitePid().longValue());
  }

  @Test
  void
      whenGetDealAssociatedWithSellerAndDealHasPositionsFromDifferentSellers_thenPositionsAreFilteredCorrectly() {
    DirectDeal directDeal = new DirectDeal();

    DealPosition dealPosition1 = new DealPosition();
    dealPosition1.setPositionPid(1L);
    DealPosition dealPosition2 = new DealPosition();
    dealPosition2.setPositionPid(2L);
    DealPosition dealPosition3 = new DealPosition();
    dealPosition3.setPositionPid(3L);
    DealPosition dealPosition4 = new DealPosition();
    dealPosition4.setPositionPid(4L);

    List<DealPosition> dealPositions =
        List.of(dealPosition1, dealPosition2, dealPosition3, dealPosition4);

    directDeal.setPositions(dealPositions);

    when(dealRepository.findOne(nullable(Specification.class))).thenReturn(Optional.of(directDeal));
    when(positionRepository.findPidsByCompanyPid(any(Long.class))).thenReturn(Set.of(1L, 2L));

    DirectDealDTO directDealDTO = sellerDealService.getDealAssociatedWithSeller(1L, 1L);

    assertNotNull(directDealDTO.getPositions());
    assertEquals(2, directDealDTO.getPositions().size());
    assertEquals(
        1L, ((DealPositionDTO) directDealDTO.getPositions().get(0)).getPositionPid().longValue());
    assertEquals(
        2L, ((DealPositionDTO) directDealDTO.getPositions().get(1)).getPositionPid().longValue());
  }

  @Test
  void
      whenGetPublisherMapForDealAndDealHasSitesFromDifferentSellers_thenSitesAreFilteredCorrectly() {
    DirectDeal directDeal = createDirectDeal(List.of(1L, 2L, 3L, 4L), List.of());

    when(dealRepository.findOne(nullable(Specification.class))).thenReturn(Optional.of(directDeal));
    when(siteRepository.findPidsByCompanyPidsWithStatusNotDeleted(any()))
        .thenReturn(Set.of(1L, 2L));

    ArgumentCaptor<List> sitesCaptor = ArgumentCaptor.forClass(List.class);
    ArgumentCaptor<List> positionsCaptor = ArgumentCaptor.forClass(List.class);

    List<PublisherSitePositionDTO> publisherMapForDeal =
        sellerDealService.getPublisherMapForDeal(1L, 2L);

    verify(directDealService)
        .getPublisherMapForDeal(sitesCaptor.capture(), positionsCaptor.capture());

    assertNotNull(sitesCaptor.getValue());
    assertEquals(2, sitesCaptor.getValue().size());
    assertEquals(1L, ((DealSite) sitesCaptor.getValue().get(0)).getSitePid().longValue());
    assertEquals(2L, ((DealSite) sitesCaptor.getValue().get(1)).getSitePid().longValue());

    assertNotNull(positionsCaptor.getValue());
    assertEquals(0, positionsCaptor.getValue().size());
  }

  @Test
  void
      whenGetPublisherMapForDealAndDealHasPositionsFromDifferentSellers_thenPositionsAreFilteredCorrectly() {
    Company company1 = createCompany(1L, "Company1");
    Company company2 = createCompany(2L, "Company2");

    SiteView site1 = createSite(1L, "Site1", company1);
    SiteView site2 = createSite(2L, "Site2", company2);

    DealPosition dealPosition11 = createDealPosition(11L, "Site1Position11", site1);
    DealPosition dealPosition12 = createDealPosition(12L, "Site1Position12", site1);
    DealPosition dealPosition21 = createDealPosition(21L, "Site2Position21", site2);
    DealPosition dealPosition22 = createDealPosition(22L, "Site2Position22", site2);

    DirectDeal directDeal =
        createDirectDeal(
            List.of(), List.of(dealPosition11, dealPosition12, dealPosition21, dealPosition22));

    when(dealRepository.findOne(nullable(Specification.class))).thenReturn(Optional.of(directDeal));
    when(siteRepository.findPidsByCompanyPidsWithStatusNotDeleted(any())).thenReturn(Set.of(1L));

    ArgumentCaptor<List> sitesCaptor = ArgumentCaptor.forClass(List.class);
    ArgumentCaptor<List> positionsCaptor = ArgumentCaptor.forClass(List.class);

    List<PublisherSitePositionDTO> publisherMapForDeal =
        sellerDealService.getPublisherMapForDeal(1L, 1L);

    verify(directDealService)
        .getPublisherMapForDeal(sitesCaptor.capture(), positionsCaptor.capture());

    assertNotNull(sitesCaptor.getValue());
    assertEquals(0, sitesCaptor.getValue().size());

    assertNotNull(positionsCaptor.getValue());
    assertEquals(2, positionsCaptor.getValue().size());
    assertEquals(
        "Site1Position11",
        ((DealPosition) positionsCaptor.getValue().get(0)).getPositionView().getName());
    assertEquals(
        "Site1Position12",
        ((DealPosition) positionsCaptor.getValue().get(1)).getPositionView().getName());
  }

  @Test
  void shouldCreateDealAssociatedWithSellerForSellerAdminOrManager() {
    DirectDealDTO directDealDTO = buildDirectDealDto(3, 1L);
    when(dealService.createDeal(any())).thenReturn(directDealDTO);
    assertEquals(
        3,
        sellerDealService
            .createDealAssociatedWithSeller(1L, buildDirectDealDto(null, 1L))
            .getDealCategory());
    verify(sellerDealValidator).validateSeller(anyLong(), any());
    verify(sellerDealValidator).areAllSellerSitesAllowedForUser(anyLong(), any());
    verify(sellerDealValidator).validateDealCategory(any());
    verify(sellerDealValidator).validateVisibility(any());
  }

  @Test
  void shouldThrowGenevaValidationExceptionWhenSellerIdNull() {
    DirectDealDTO directDealDTO = buildDirectDealDto(null, 1l);
    GenevaValidationException exception =
        assertThrows(
            GenevaValidationException.class,
            () -> sellerDealService.createDealAssociatedWithSeller(null, directDealDTO));
    assertEquals(CommonErrorCodes.COMMON_BAD_REQUEST, exception.getErrorCode());
  }

  @Test
  void shouldThrowGenevaValidationExceptionWhenDirectDTONull() {
    GenevaValidationException exception =
        assertThrows(
            GenevaValidationException.class,
            () -> sellerDealService.createDealAssociatedWithSeller(1l, null));
    assertEquals(CommonErrorCodes.COMMON_BAD_REQUEST, exception.getErrorCode());
  }

  @Test
  void shouldUpdateDealAssociatedWithSeller() {
    DirectDealDTO directDealDTO = buildDirectDealDto(2, 1L);
    when(dealService.updateDeal(anyLong(), any())).thenReturn(directDealDTO);
    assertEquals(
        2,
        sellerDealService
            .updateDealAssociatedWithSeller(1L, 100L, buildDirectDealDto(2, 1L))
            .getDealCategory());
  }

  @Test
  void shouldThrowSellerDealCategoryCannotBeNullExceptionWhenDealCategoryNull() {
    DirectDealDTO directDealDTO = buildDirectDealDto(null, 1l);
    GenevaValidationException exception =
        assertThrows(
            GenevaValidationException.class,
            () -> sellerDealService.updateDealAssociatedWithSeller(1l, 100l, directDealDTO));
    assertEquals(
        ServerErrorCodes.SERVER_SELLER_DEAL_CATEGORY_CANNOT_BE_NULL, exception.getErrorCode());
  }

  private DirectDealDTO buildDirectDealDto(Integer dealCategoryId) {
    DirectDealDTO directDealDTO = new DirectDealDTO();
    directDealDTO.setDealCategory(dealCategoryId);
    return directDealDTO;
  }

  private DirectDealDTO buildDirectDealDto(Integer dealCategoryId, Long seller) {
    DirectDealDTO directDealDTO = new DirectDealDTO();
    directDealDTO.setDealCategory(dealCategoryId);
    directDealDTO
        .getSellers()
        .add(new DealPublisherDTO.Builder().setPid(seller + 1).setPublisherPid(seller).build());
    return directDealDTO;
  }

  private DirectDeal createDirectDeal(List<Long> sitePids, List<DealPosition> positions) {
    DirectDeal directDeal = new DirectDeal();
    sitePids.stream().forEach(sitePid -> addDealSite(directDeal, sitePid));
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
    position.setName(name);
    dealPosition.setPositionView(position);
    return dealPosition;
  }

  private Set<String> createQf(String... fields) {
    return new HashSet<>(List.of(fields));
  }
}
