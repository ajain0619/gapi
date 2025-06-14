package com.nexage.app.services.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nexage.admin.core.model.Company;
import com.nexage.admin.core.model.CompanyView;
import com.nexage.admin.core.model.DirectDeal;
import com.nexage.admin.core.model.SiteView;
import com.nexage.admin.core.repository.CompanyViewRepository;
import com.nexage.admin.core.repository.DealAppAliasRepository;
import com.nexage.admin.core.repository.DealAppBundleDataRepository;
import com.nexage.admin.core.repository.DealDomainRepository;
import com.nexage.admin.core.repository.DealPositionRepository;
import com.nexage.admin.core.repository.DealPublisherRepository;
import com.nexage.admin.core.repository.DealSiteRepository;
import com.nexage.admin.core.repository.DirectDealRepository;
import com.nexage.admin.core.repository.PositionRepository;
import com.nexage.admin.core.repository.PositionViewRepository;
import com.nexage.admin.core.repository.SiteRepository;
import com.nexage.admin.core.sparta.jpa.model.DealPosition;
import com.nexage.admin.core.sparta.jpa.model.DealPublisher;
import com.nexage.admin.core.sparta.jpa.model.DealSite;
import com.nexage.admin.core.sparta.jpa.model.PositionView;
import com.nexage.app.dto.deals.DealPlacementDTO;
import com.nexage.app.dto.deals.DealSellerDTO;
import com.nexage.app.dto.deals.DealSiteDTO;
import com.nexage.app.dto.deals.SpecificAssignedInventoryDTO;
import com.nexage.app.error.ServerErrorCodes;
import com.nexage.app.services.deal.impl.SpecificAssignedInventoryDTOServiceImpl;
import com.nexage.app.services.validation.sellingrule.ZeroCostDealValidator;
import com.nexage.app.util.validator.deals.DealSpecificInventoriesFileParser;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import com.ssp.geneva.common.model.inventory.CompanyType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(MockitoExtension.class)
class SpecificAssignedInventoryDTOServiceImplTest {

  private static final Long DEAL_PID = 1L;
  private static final Long PLACEMENT_PID = 100L;
  private static final Long SELLER_PID = 1L;
  private static final Long SITE_PID = 2L;
  private static final Long DUP_SITE_PID = 10L;
  private static final Long POSITION_PID = 3L;

  @Mock private DealPublisherRepository dealPublisherRepository;
  @Mock private DealSiteRepository dealSiteRepository;
  @Mock private DealPositionRepository dealPositionRepository;
  @Mock private DirectDealRepository dealRepository;
  @Mock private PositionRepository positionRepository;
  @Mock private SiteRepository siteRepository;
  @Mock private EntityManager entityManager;
  @Mock private ZeroCostDealValidator zeroCostDealValidator;
  @Mock private DealDomainRepository dealDomainRepository;
  @Mock private DealAppBundleDataRepository dealAppBundleDataRepository;
  @Mock private DealAppAliasRepository dealAppAliasRepository;
  @Mock private CompanyViewRepository companyViewRepository;
  @Mock private DealSpecificInventoriesFileParser dealSpecificInventoriesFileParser;
  @Mock private MultipartFile inventoryFile;
  @Mock private PositionViewRepository positionViewRepository;

  private SpecificAssignedInventoryDTOServiceImpl specificAssignedInventoryDTOService;

  @BeforeEach
  public void setup() {
    specificAssignedInventoryDTOService =
        new SpecificAssignedInventoryDTOServiceImpl(
            dealPublisherRepository,
            dealSiteRepository,
            dealPositionRepository,
            dealRepository,
            positionRepository,
            siteRepository,
            entityManager,
            zeroCostDealValidator,
            dealDomainRepository,
            dealAppBundleDataRepository,
            dealAppAliasRepository,
            companyViewRepository,
            dealSpecificInventoriesFileParser,
            positionViewRepository);
  }

  @Test
  void updateSpecificInventorySuccess() {
    when(dealRepository.findById(DEAL_PID)).thenReturn(testDeal());
    var mockPositionView = mock(PositionView.class);
    when(entityManager.getReference(PositionView.class, PLACEMENT_PID))
        .thenReturn(mockPositionView);

    specificAssignedInventoryDTOService.createNewAssignedInventory(DEAL_PID, testData());
    verify(dealPositionRepository).saveAll(anyCollection());
    verify(dealSiteRepository).saveAll(List.of());
    verify(dealPublisherRepository).saveAll(List.of());
    verify(dealSiteRepository).deleteByDealPid(DEAL_PID);
    verify(dealPositionRepository).deleteByDealPid(DEAL_PID);
    verify(dealDomainRepository).deleteByDealPid(DEAL_PID);
    verify(dealAppBundleDataRepository).deleteByDealPid(DEAL_PID);
    verify(dealAppAliasRepository).deleteByDealPid(DEAL_PID);
  }

  @Test
  void updateSpecificInventoryWhenNoPositions() {
    when(dealRepository.findById(DEAL_PID)).thenReturn(testDeal());

    specificAssignedInventoryDTOService.createNewAssignedInventory(
        DEAL_PID, testDataNoPlacements());
    verify(dealSiteRepository).saveAll(anyCollection());
    verify(dealPublisherRepository).saveAll(List.of());
  }

  @Test
  void updateInventoryDealNotFound() {
    when(dealRepository.findById(DEAL_PID)).thenReturn(Optional.empty());
    SpecificAssignedInventoryDTO dealAssignedInventoryDTO = testData();
    var exception =
        assertThrows(
            GenevaValidationException.class,
            () ->
                specificAssignedInventoryDTOService.createNewAssignedInventory(
                    DEAL_PID, dealAssignedInventoryDTO));
    assertEquals(ServerErrorCodes.SERVER_DEAL_NOT_FOUND, exception.getErrorCode());
  }

  @Test
  void getSpecificInventorySuccess() {
    when(dealPublisherRepository.findByDealPid(DEAL_PID)).thenReturn(dealPublisherData());
    when(dealSiteRepository.findByDealPid(DEAL_PID)).thenReturn(dealSiteData());
    when(dealPositionRepository.findByDealPid(DEAL_PID)).thenReturn(dealPositionData());

    var mockPositionView = mock(PositionView.class);
    when(mockPositionView.getName()).thenReturn("position name");
    when(mockPositionView.getMemo()).thenReturn("position memo");
    when(mockPositionView.getPid()).thenReturn(POSITION_PID);
    when(mockPositionView.getSitePid()).thenReturn(SITE_PID);
    when(positionRepository.findAllByPidIn(List.of(POSITION_PID)))
        .thenReturn(List.of(mockPositionView));

    var out = specificAssignedInventoryDTOService.getAssignedInventory(DEAL_PID);
    assertEquals(2, out.getContent().size());

    var seller = out.getContent().iterator().next();
    assertEquals(1, seller.getSites().size());
    assertEquals(1, seller.getSellerPid().longValue());
    var site = seller.getSites().get(0);
    assertEquals(2, site.getSitePid().longValue());
    assertEquals(1, site.getPlacements().size());

    var placement = site.getPlacements().get(0);
    assertEquals(3, placement.getPlacementPid().longValue());
  }

  @Test
  void getSpecificInventoryNoPositions() {
    when(dealPublisherRepository.findByDealPid(DEAL_PID)).thenReturn(dealPublisherData());
    when(dealSiteRepository.findByDealPid(DEAL_PID)).thenReturn(dealSiteData());
    when(dealPositionRepository.findByDealPid(DEAL_PID)).thenReturn(List.of());

    var out = specificAssignedInventoryDTOService.getAssignedInventory(DEAL_PID);
    assertEquals(2, out.getContent().size());

    var seller = out.getContent().iterator().next();
    assertEquals(1, seller.getSites().size());
    assertEquals(1, seller.getSellerPid().longValue());
    var site = seller.getSites().get(0);
    assertEquals(2, site.getSitePid().longValue());
    assertTrue(site.getPlacements().isEmpty());
  }

  @Test
  void getInventoryMissingSitesAndSellers() {
    when(dealPublisherRepository.findByDealPid(DEAL_PID)).thenReturn(new ArrayList<>());
    when(dealSiteRepository.findByDealPid(DEAL_PID)).thenReturn(new ArrayList<>());
    when(dealPositionRepository.findByDealPid(DEAL_PID)).thenReturn(dealPositionData());
    when(positionRepository.findAllByPidIn(anyCollection())).thenReturn(testPositionViews());
    when(siteRepository.findBySitePidIn(anyCollection())).thenReturn(testSiteView());

    var out = specificAssignedInventoryDTOService.getAssignedInventory(DEAL_PID);
    assertEquals(1, out.getContent().size());
    var seller = out.getContent().iterator().next();
    assertEquals(1, seller.getSites().size());
    assertEquals(1, seller.getSellerPid().longValue());
  }

  @Test
  void shouldUpdateSpecificInventoryAssociatedWithSellerSuccess() {
    when(dealRepository.findById(DEAL_PID)).thenReturn(testDeal());
    var mockPositionView = mock(PositionView.class);
    when(entityManager.getReference(PositionView.class, PLACEMENT_PID))
        .thenReturn(mockPositionView);
    specificAssignedInventoryDTOService.createNewAssignedInventoryAssociatedWithSeller(
        SELLER_PID, DEAL_PID, testData());
    verify(dealPositionRepository).saveAll(anyCollection());
    verify(dealSiteRepository).saveAll(List.of());
    verify(dealPublisherRepository).saveAll(List.of());
    verify(dealSiteRepository).deleteByDealPid(DEAL_PID);
    verify(dealPositionRepository).deleteByDealPid(DEAL_PID);
    verify(dealDomainRepository).deleteByDealPid(DEAL_PID);
    verify(dealAppBundleDataRepository).deleteByDealPid(DEAL_PID);
    verify(dealAppAliasRepository).deleteByDealPid(DEAL_PID);
  }

  @Test
  void shouldUpdateSpecificInventoryAssociatedWithSellerSuccessEvenInDuplicatePosition() {
    when(dealRepository.findById(DEAL_PID)).thenReturn(testDeal());
    when(dealPositionRepository.saveAll(any())).thenReturn(savePosition());
    var mockPositionView = mock(PositionView.class);
    when(entityManager.getReference(PositionView.class, PLACEMENT_PID))
        .thenReturn(mockPositionView);
    ArgumentCaptor<HashSet> placementsToSave = ArgumentCaptor.forClass(HashSet.class);
    var out =
        specificAssignedInventoryDTOService.createNewAssignedInventory(
            DEAL_PID, testDataWithSiteWithDuplicatePosition());
    verify(dealPositionRepository).saveAll(placementsToSave.capture());
    assertEquals(1, placementsToSave.getAllValues().size());
    Iterator<DealSellerDTO> it = out.getContent().iterator();
    var seller = it.next();
    var site = seller.getSites().get(0);
    var placement = site.getPlacements().get(0);
    assertEquals(PLACEMENT_PID, placement.getPlacementPid().longValue());
    assertEquals(1L, placement.getPid().longValue());
  }

  @Test
  void shouldUpdateSpecificInventoryAssociatedWithSellerSuccessEvenInDuplicateSites() {
    when(dealRepository.findById(DEAL_PID)).thenReturn(testDeal());
    when(dealSiteRepository.saveAll(any())).thenReturn(saveSite());
    ArgumentCaptor<HashSet> sitesToSave = ArgumentCaptor.forClass(HashSet.class);
    var out =
        specificAssignedInventoryDTOService.createNewAssignedInventory(
            DEAL_PID, testDataWithDuplicateSiteWithNoPosition());
    verify(dealSiteRepository).saveAll(sitesToSave.capture());
    assertEquals(1, sitesToSave.getAllValues().size());
    Iterator<DealSellerDTO> it = out.getContent().iterator();
    var seller = it.next();
    var site = seller.getSites().get(0);
    assertEquals(DUP_SITE_PID, site.getSitePid().longValue());
    assertEquals(1L, site.getPid().longValue());
  }

  @Test
  void shouldUpdateSpecificInventoryAssociatedWithSellerSuccessEvenInDuplicateSellers() {
    when(dealRepository.findById(DEAL_PID)).thenReturn(testDeal());
    when(dealPublisherRepository.saveAll(any())).thenReturn(savePublisher());
    ArgumentCaptor<HashSet> sellersToSave = ArgumentCaptor.forClass(HashSet.class);
    var out =
        specificAssignedInventoryDTOService.createNewAssignedInventory(
            DEAL_PID, testDataWithDuplicateSellerWithNoSite());
    verify(dealPublisherRepository).saveAll(sellersToSave.capture());
    assertEquals(1, sellersToSave.getAllValues().size());
    out.getContent().stream().forEach(seller -> assertEquals(SELLER_PID, seller.getPid()));
  }

  @Test
  void shouldUpdateSpecificInventoryAssociatedWithSellerWhenNoPositions() {
    when(dealRepository.findById(DEAL_PID)).thenReturn(testDeal());

    specificAssignedInventoryDTOService.createNewAssignedInventoryAssociatedWithSeller(
        SELLER_PID, DEAL_PID, testDataNoPlacements());
    verify(dealSiteRepository).saveAll(anyCollection());
    verify(dealPublisherRepository).saveAll(List.of());
  }

  @Test
  void shouldUpdateInventoryDealAssociatedWithSellerNotFound() {
    when(dealRepository.findById(DEAL_PID)).thenReturn(Optional.empty());
    SpecificAssignedInventoryDTO dealAssignedInventoryDTO = testData();
    var exception =
        assertThrows(
            GenevaValidationException.class,
            () ->
                specificAssignedInventoryDTOService.createNewAssignedInventoryAssociatedWithSeller(
                    SELLER_PID, DEAL_PID, dealAssignedInventoryDTO));
    assertEquals(ServerErrorCodes.SERVER_DEAL_NOT_FOUND, exception.getErrorCode());
  }

  @Test
  void shouldPassWhenSellersAndSitesAndPositionsInAllowListForZeroCostDeals() {
    var optionalDirectDeal = testDeal();
    var specificAssignedInventoryDTO = testData();
    optionalDirectDeal.get().setPlacementFormula("Formula");

    when(dealRepository.findById(DEAL_PID)).thenReturn(optionalDirectDeal);

    specificAssignedInventoryDTOService.createNewAssignedInventory(
        DEAL_PID, specificAssignedInventoryDTO);
    verify(zeroCostDealValidator)
        .validateZeroCostDeals(optionalDirectDeal.orElseThrow(), specificAssignedInventoryDTO);
    verify(dealRepository).findById(DEAL_PID);
    verify(dealRepository).save(any());
  }

  @Test
  void shouldThrowWhenZeroCostDealValidatorThrows() {
    var optionalDirectDeal = testDeal();
    var specificAssignedInventoryDTO = testData();

    when(dealRepository.findById(DEAL_PID)).thenReturn(optionalDirectDeal);

    doThrow(new GenevaValidationException(ServerErrorCodes.SERVER_DEAL_ZERO_COST_SELLER_DISALLOWED))
        .when(zeroCostDealValidator)
        .validateZeroCostDeals(optionalDirectDeal.orElseThrow(), specificAssignedInventoryDTO);

    GenevaValidationException exception =
        assertThrows(
            GenevaValidationException.class,
            () ->
                specificAssignedInventoryDTOService.createNewAssignedInventory(
                    DEAL_PID, specificAssignedInventoryDTO));
    assertEquals(
        ServerErrorCodes.SERVER_DEAL_ZERO_COST_SELLER_DISALLOWED, exception.getErrorCode());
    verify(zeroCostDealValidator)
        .validateZeroCostDeals(optionalDirectDeal.orElseThrow(), specificAssignedInventoryDTO);
  }

  @Test
  void shouldPassWhenValidBulkInventoryFileGiven() {

    List<CompanyView> cv = new ArrayList<>();
    cv.add(new CompanyView(1L, null));

    List<SiteView> sv = new ArrayList<>();
    sv.add(new SiteView(21L, null, 2L, null));

    List<PositionView> pv = new ArrayList<>();
    pv.add(
        PositionView.builder()
            .pid(311L)
            .sitePid(31L)
            .siteView(new SiteView(31L, null, 3L, null))
            .build());

    when(positionViewRepository.findAllByPidsIn(any()))
        .thenReturn(pv.stream().collect(Collectors.toList()));
    when(siteRepository.findBySitePidIn(any()))
        .thenReturn(sv.stream().collect(Collectors.toList()));
    when(companyViewRepository.findCompaniesByIds(any()))
        .thenReturn(cv.stream().collect(Collectors.toList()));
    List[] res = new List[] {cv, sv, pv};
    when(dealSpecificInventoriesFileParser.processSpecificInventoriesFile(any())).thenReturn(res);

    var out = specificAssignedInventoryDTOService.processBulkInventories(inventoryFile);
    assertEquals(3, out.getContent().size());

    Iterator<DealSellerDTO> it = out.getContent().iterator();
    var seller = it.next();
    assertEquals(1, seller.getSites().size());
    assertEquals(3, seller.getSellerPid().longValue());
    var site = seller.getSites().get(0);
    assertEquals(31, site.getSitePid().longValue());
    assertEquals(1, site.getPlacements().size());
    var placement = site.getPlacements().get(0);
    assertEquals(311, placement.getPlacementPid().longValue());

    seller = it.next();
    assertEquals(1, seller.getSites().size());
    assertEquals(2, seller.getSellerPid().longValue());
    site = seller.getSites().get(0);
    assertEquals(21, site.getSitePid().longValue());
    assertEquals(0, site.getPlacements().size());

    seller = it.next();
    assertEquals(1, seller.getSellerPid().longValue());
    assertEquals(0, seller.getSites().size());
  }

  @Test
  void shouldReturnInvalidEntriesErrorWhenInValidBulkInventoryFileGiven() {

    List<PositionView> pvFromRepo = new ArrayList<>();
    pvFromRepo.add(
        PositionView.builder()
            .pid(311L)
            .sitePid(31L)
            .siteView(new SiteView(31L, null, 3L, null))
            .build());

    List<SiteView> svFromRepo = new ArrayList<>();
    svFromRepo.add(new SiteView(21L, null, 2L, null));

    List<CompanyView> cvFromRepo = new ArrayList<>();
    cvFromRepo.add(new CompanyView(1L, null));

    when(positionViewRepository.findAllByPidsIn(any())).thenReturn(pvFromRepo);
    when(siteRepository.findBySitePidIn(any())).thenReturn(svFromRepo);
    when(companyViewRepository.findCompaniesByIds(any())).thenReturn(cvFromRepo);

    ArrayList<CompanyView> cv = new ArrayList<>();
    cv.add(new CompanyView(1L, null));
    cv.add(new CompanyView(11L, null));
    ArrayList<SiteView> sv = new ArrayList<>();
    sv.add(new SiteView(21L, null, 2L, null));
    sv.add(new SiteView(22L, null, 2L, null));

    ArrayList<PositionView> pv = new ArrayList<>();
    pv.add(
        PositionView.builder()
            .pid(311L)
            .sitePid(31L)
            .siteView(new SiteView(31L, null, 3L, null))
            .build());
    pv.add(
        PositionView.builder()
            .pid(312L)
            .sitePid(31L)
            .siteView(new SiteView(31L, null, 3L, null))
            .build());

    List[] res = new List[] {cv, sv, pv};
    when(dealSpecificInventoriesFileParser.processSpecificInventoriesFile(any())).thenReturn(res);

    GenevaValidationException exception =
        assertThrows(
            GenevaValidationException.class,
            () -> specificAssignedInventoryDTOService.processBulkInventories(inventoryFile));
    assertEquals(ServerErrorCodes.SERVER_INVALID_ENTRIES, exception.getErrorCode());
    assertEquals(3, exception.getMessageParams()[0]);
    assertEquals("\"3,31,312\",\"2,22\",\"11\"", exception.getMessageParams()[1]);
  }

  @Test
  void shouldReturnEmptyResponseWhenParserReturnEmptyViewSets() {

    List[] res =
        new List[] {Collections.emptyList(), Collections.emptyList(), Collections.emptyList()};
    when(dealSpecificInventoriesFileParser.processSpecificInventoriesFile(any())).thenReturn(res);

    var out = specificAssignedInventoryDTOService.processBulkInventories(inventoryFile);
    assertEquals(0, out.getContent().size());
  }

  private List<DealPublisher> dealPublisherData() {
    var dealPublisher = new DealPublisher();
    dealPublisher.setPubPid(SELLER_PID);
    var companyView = new CompanyView(SELLER_PID, "name", CompanyType.SELLER, true);
    dealPublisher.setCompanyView(companyView);
    var publisherList = new ArrayList<DealPublisher>();
    publisherList.add(dealPublisher);
    return publisherList;
  }

  private List<DealSite> dealSiteData() {
    var dealSite = new DealSite();
    dealSite.setSitePid(SITE_PID);

    var siteView = new SiteView();
    var company = new Company();
    company.setPid(SELLER_PID);
    siteView.setCompany(company);
    dealSite.setSiteView(siteView);
    var siteList = new ArrayList<DealSite>();
    siteList.add(dealSite);
    return siteList;
  }

  private List<DealPosition> dealPositionData() {
    var dealPosition = new DealPosition();
    dealPosition.setPositionPid(POSITION_PID);
    var positionView = new PositionView();
    positionView.setPid(POSITION_PID);
    positionView.setSitePid(SITE_PID);
    dealPosition.setPositionView(positionView);
    return List.of(dealPosition);
  }

  private List<PositionView> testPositionViews() {
    var positionView = new PositionView();
    positionView.setSitePid(SITE_PID);
    positionView.setPid(POSITION_PID);

    return List.of(positionView);
  }

  private List<SiteView> testSiteView() {
    var siteView = new SiteView();
    siteView.setPid(SITE_PID);
    siteView.setName("site name");

    var company = new Company();
    company.setPid(SELLER_PID);
    company.setName("comp name");
    siteView.setCompany(company);
    return List.of(siteView);
  }

  private SpecificAssignedInventoryDTO testData() {
    var placement = new DealPlacementDTO();
    placement.setPlacementPid(PLACEMENT_PID);
    placement.setPlacementName("placement");
    placement.setPlacementMemo("memo");

    var site = new DealSiteDTO();
    site.setSitePid(10L);
    site.setSiteName("site name");
    site.setPlacements(List.of(placement));

    var seller = new DealSellerDTO();
    seller.setSellerPid(5L);
    seller.setSellerName("seller name");
    seller.setSites(List.of(site));

    var inventory = new SpecificAssignedInventoryDTO();
    inventory.setContent(List.of(seller));
    return inventory;
  }

  private SpecificAssignedInventoryDTO testDataWithSiteWithDuplicatePosition() {
    var placement = new DealPlacementDTO();
    placement.setPlacementPid(PLACEMENT_PID);
    placement.setPlacementName("placement");
    placement.setPlacementMemo("memo");

    var placement1 = new DealPlacementDTO();
    placement1.setPlacementPid(PLACEMENT_PID);
    placement1.setPlacementName("placement");
    placement1.setPlacementMemo("memo");

    var placementsList = new ArrayList<DealPlacementDTO>();
    placementsList.add(placement);
    placementsList.add(placement1);

    var site = new DealSiteDTO();
    site.setSitePid(SITE_PID);
    site.setSiteName("site name");
    site.setPlacements(placementsList);

    var seller = new DealSellerDTO();
    seller.setSellerPid(5L);
    seller.setSellerName("seller name");
    seller.setSites(List.of(site));

    var inventory = new SpecificAssignedInventoryDTO();
    inventory.setContent(List.of(seller));
    return inventory;
  }

  private List<DealPosition> savePosition() {
    var pos = new DealPosition();
    pos.setPositionPid(100L);
    pos.setPid(1L);
    return Arrays.asList(pos);
  }

  private SpecificAssignedInventoryDTO testDataWithDuplicateSiteWithNoPosition() {
    var site = new DealSiteDTO();
    site.setSitePid(DUP_SITE_PID);
    site.setSiteName("site name");

    var site1 = new DealSiteDTO();
    site1.setSitePid(DUP_SITE_PID);
    site1.setSiteName("site name");

    var siteList = new ArrayList<DealSiteDTO>();
    siteList.add(site);
    siteList.add(site1);

    var seller = new DealSellerDTO();
    seller.setSellerPid(5L);
    seller.setSellerName("seller name");
    seller.setSites(siteList);

    var inventory = new SpecificAssignedInventoryDTO();
    inventory.setContent(List.of(seller));
    return inventory;
  }

  private List<DealSite> saveSite() {
    var site = new DealSite();
    site.setSitePid(10L);
    site.setPid(1L);
    return Arrays.asList(site);
  }

  private SpecificAssignedInventoryDTO testDataWithDuplicateSellerWithNoSite() {
    var seller = new DealSellerDTO();
    seller.setSellerPid(SELLER_PID);
    seller.setSellerName("seller name");

    var seller1 = new DealSellerDTO();
    seller1.setSellerPid(SELLER_PID);
    seller1.setSellerName("seller name");

    var sellerList = new ArrayList<DealSellerDTO>();
    sellerList.add(seller1);
    sellerList.add(seller);

    var inventory = new SpecificAssignedInventoryDTO();
    inventory.setContent(sellerList);
    return inventory;
  }

  private List<DealPublisher> savePublisher() {
    var pub = new DealPublisher();
    pub.setPid(1L);
    pub.setPubPid(1L);
    return Arrays.asList(pub);
  }

  private SpecificAssignedInventoryDTO testDataNoPlacements() {
    var site = new DealSiteDTO();
    site.setSitePid(10L);
    site.setSiteName("site name");
    site.setPlacements(List.of());

    var seller = new DealSellerDTO();
    seller.setSellerPid(5L);
    seller.setSellerName("seller name");
    seller.setSites(List.of(site));

    var inventory = new SpecificAssignedInventoryDTO();
    inventory.setContent(List.of(seller));
    return inventory;
  }

  private Optional<DirectDeal> testDeal() {
    var deal = new DirectDeal();
    deal.setPid(DEAL_PID);
    return Optional.of(deal);
  }
}
