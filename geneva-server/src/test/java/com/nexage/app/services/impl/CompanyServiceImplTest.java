package com.nexage.app.services.impl;

import static com.nexage.app.web.support.TestObjectsFactory.createCompany;
import static com.nexage.app.web.support.TestObjectsFactory.createUser;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toSet;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.nexage.admin.core.bidder.model.BDRAdvertiser;
import com.nexage.admin.core.bidder.model.BDRExchange;
import com.nexage.admin.core.bidder.model.BdrExchangeCompany;
import com.nexage.admin.core.bidder.model.BdrInsertionOrder;
import com.nexage.admin.core.dto.BuyerMetadataDTO;
import com.nexage.admin.core.dto.SeatHolderMetadataDTO;
import com.nexage.admin.core.dto.SellerMetadataDTO;
import com.nexage.admin.core.enums.AlterReserve;
import com.nexage.admin.core.enums.PublisherDataProtectionRole;
import com.nexage.admin.core.enums.SellerDomainVerificationAuthLevel;
import com.nexage.admin.core.enums.Status;
import com.nexage.admin.core.model.Advertiser;
import com.nexage.admin.core.model.BaseModel;
import com.nexage.admin.core.model.Campaign;
import com.nexage.admin.core.model.Company;
import com.nexage.admin.core.model.Creative;
import com.nexage.admin.core.model.RTBProfile;
import com.nexage.admin.core.model.SellerAttributes;
import com.nexage.admin.core.model.SellerSeat;
import com.nexage.admin.core.model.Site;
import com.nexage.admin.core.model.User;
import com.nexage.admin.core.model.User.Role;
import com.nexage.admin.core.projections.BuyerMetaDataForCompanyProjection;
import com.nexage.admin.core.projections.SellerMetaDataForCompanyProjection;
import com.nexage.admin.core.repository.AdvertiserRepository;
import com.nexage.admin.core.repository.BDRAdvertiserRepository;
import com.nexage.admin.core.repository.BdrExchangeCompanyRepository;
import com.nexage.admin.core.repository.BdrExchangeRepository;
import com.nexage.admin.core.repository.BdrInsertionOrderRepository;
import com.nexage.admin.core.repository.BidderConfigRepository;
import com.nexage.admin.core.repository.CampaignRepository;
import com.nexage.admin.core.repository.CompanyRepository;
import com.nexage.admin.core.repository.CreativeRepository;
import com.nexage.admin.core.repository.RTBProfileRepository;
import com.nexage.admin.core.repository.RegionRepository;
import com.nexage.admin.core.repository.SellerAttributesRepository;
import com.nexage.admin.core.repository.SellerSeatRepository;
import com.nexage.admin.core.repository.SiteRepository;
import com.nexage.admin.core.repository.UserRepository;
import com.nexage.admin.core.repository.UserRestrictedSiteRepository;
import com.nexage.admin.core.sparta.jpa.model.SellerType;
import com.nexage.admin.dw.dashboard.dao.DashboardDao;
import com.nexage.admin.dw.dashboard.model.BuyerKeyMetrics;
import com.nexage.app.error.ServerErrorCodes;
import com.nexage.app.queue.model.event.SyncEvent;
import com.nexage.app.queue.producer.CompanySyncProducer;
import com.nexage.app.security.LoginUserContext;
import com.nexage.app.services.BidderConfigService;
import com.nexage.app.services.SeatHolderService;
import com.nexage.app.services.SellerSiteService;
import com.nexage.app.services.TransparencyService;
import com.nexage.app.services.validation.RevenueShareUpdateValidator;
import com.nexage.app.util.RTBProfileUtil;
import com.nexage.app.util.validator.CompanyValidator;
import com.nexage.app.web.support.EntitlementsTestUtil;
import com.nexage.app.web.support.TestObjectsFactory;
import com.ssp.geneva.common.error.exception.GenevaAppRuntimeException;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import com.ssp.geneva.common.model.inventory.CompanyType;
import com.ssp.geneva.common.security.error.GenevaSecurityException;
import com.ssp.geneva.common.security.error.SecurityErrorCodes;
import com.ssp.geneva.common.security.model.SpringUserDetails;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class CompanyServiceImplTest {
  private static final Integer ACTIVE_IOS = 12;
  private static final int DEFAULT_HUMAN_PREBID_SAMPLE_RATE = 30;
  private static final int DEFAULT_HUMAN_POSTBID_SAMPLE_RATE = 60;
  @Mock private SellerSeatRepository sellerSeatRepository;
  @Mock private BdrInsertionOrderRepository bdrInsertionOrderRepository;
  @Mock private BDRAdvertiserRepository bdrAdvertiserRepository;
  @Mock private BdrExchangeCompanyRepository bdrExchangeCompanyRepository;
  @Mock private BdrExchangeRepository exchangeRepository;
  @Mock private CompanyRepository companyRepository;
  @Mock private UserRestrictedSiteRepository userRestrictedSiteRepository;
  @Mock private SiteRepository siteRepository;
  @Mock private CampaignRepository campaignRepository;
  @Mock private SellerAttributesRepository sellerAttributesRepository;
  @Mock private RTBProfileRepository rtbProfileRepository;
  @Mock private LoginUserContext userContext;
  @Mock private SeatHolderService seatHolderService;
  @Mock private DashboardDao dashboardDao;
  @Mock private TransparencyService transparencyService;
  @Mock private RegionRepository regionRepository;
  @Mock private SellerSiteService sellerSiteService;
  @Mock private RevenueShareUpdateValidator revenueShareUpdateValidator;
  @Mock private RTBProfileUtil rtbProfileUtil;
  @Mock private CompanySyncProducer companySyncProducer;
  @Mock private CreativeRepository creativeRepository;
  @Mock private AdvertiserRepository advertiserRepository;
  @Mock private BidderConfigRepository bidderConfigRepository;
  @Mock private EntityManager entityManager;
  @Mock private BdrExchangeRepository bdrExchangeRepository;
  @Spy private BidderConfigService bidderConfigService;
  @Spy private UserRepository userRepository;
  @Mock private CompanyValidator companyValidator;

  private CompanyServiceImpl companyService;

  @BeforeEach
  void setUp() {
    Company company = createCompany(CompanyType.NEXAGE);
    User user = createUser(User.Role.ROLE_ADMIN, company);
    loggedUser(user);
    companyService =
        new CompanyServiceImpl(
            userContext,
            sellerSeatRepository,
            bdrInsertionOrderRepository,
            bdrAdvertiserRepository,
            bdrExchangeCompanyRepository,
            bdrExchangeRepository,
            userRepository,
            companyRepository,
            userRestrictedSiteRepository,
            siteRepository,
            creativeRepository,
            campaignRepository,
            bidderConfigRepository,
            bidderConfigService,
            sellerAttributesRepository,
            rtbProfileRepository,
            seatHolderService,
            dashboardDao,
            transparencyService,
            sellerSiteService,
            revenueShareUpdateValidator,
            rtbProfileUtil,
            companySyncProducer,
            advertiserRepository,
            entityManager,
            companyValidator);
    ReflectionTestUtils.setField(
        companyService, "defaultHumanPrebidSampleRate", DEFAULT_HUMAN_PREBID_SAMPLE_RATE);
    ReflectionTestUtils.setField(
        companyService, "defaultHumanPostbidSampleRate", DEFAULT_HUMAN_POSTBID_SAMPLE_RATE);
  }

  @Test
  void shouldGetAllCompaniesByTypeAndCheckMetaDataIsFilledIn() {
    // given
    Company buyerCompany = createCompany(CompanyType.BUYER);
    buyerCompany.setAdsourceNames(null);

    Set<String> expectedAdSourceNames = new HashSet<>(Arrays.asList("a", "b"));
    BuyerMetadataDTO metadata = new BuyerMetadataDTO();
    metadata.setAdsourceNames(expectedAdSourceNames);

    List<BuyerMetaDataForCompanyProjection> buyerMetaDataForCompanyProjections =
        mockBuyerMetadataForCompanyProjections(buyerCompany.getPid());
    when(companyRepository.findAllBuyerMetaDataForCompanyProjections())
        .thenReturn(buyerMetaDataForCompanyProjections);

    when(companyRepository.findByType(any(CompanyType.class)))
        .thenReturn(Lists.newArrayList(buyerCompany));

    // when
    List<Company> buyers = companyService.getAllCompaniesByType(CompanyType.BUYER, null, null);

    // then
    assertEquals(buyers.get(0), buyerCompany);
    assertEquals(buyerCompany.getAdsourceNames(), metadata.getAdsourceNames());
    assertEquals(buyers.get(0).getAdsourceNames(), metadata.getAdsourceNames());
  }

  private List<BuyerMetaDataForCompanyProjection> mockBuyerMetadataForCompanyProjections(long pid) {
    List<BuyerMetaDataForCompanyProjection> buyerMetaDataForCompanyProjections = new ArrayList<>();
    BuyerMetaDataForCompanyProjection buyerMetaData = mock(BuyerMetaDataForCompanyProjection.class);
    given(buyerMetaData.getCompany()).willReturn(pid);
    given(buyerMetaData.getAdsource()).willReturn("a");
    buyerMetaDataForCompanyProjections.add(buyerMetaData);
    BuyerMetaDataForCompanyProjection buyerMetaData2 =
        mock(BuyerMetaDataForCompanyProjection.class);
    given(buyerMetaData2.getCompany()).willReturn(pid);
    given(buyerMetaData2.getAdsource()).willReturn("b");
    buyerMetaDataForCompanyProjections.add(buyerMetaData2);

    return buyerMetaDataForCompanyProjections;
  }

  @Test
  void shouldGetAllCompaniesByTypeAndCheckSellerMetaDataIsFilledIn() {
    // given
    Company sellerCompany = createCompany(CompanyType.SELLER);

    SellerMetadataDTO metadata = new SellerMetadataDTO();
    metadata.setNumberOfUsers(1);
    metadata.setNumberOfMediationSites(1);
    metadata.setNumberOfRtbTags(1);
    metadata.setNumberOfHbSites(1);

    List<SellerMetaDataForCompanyProjection> sellerMetaDataForCompanyProjections =
        mockSellerMetaDataForCompanyProjections(sellerCompany.getPid());
    when(companyRepository.findAllSellerMetaDataForCompanyProjections())
        .thenReturn(sellerMetaDataForCompanyProjections);

    when(companyRepository.findByType(any(CompanyType.class)))
        .thenReturn(Lists.newArrayList(sellerCompany));

    // when
    List<Company> sellers = companyService.getAllCompaniesByType(CompanyType.SELLER, null, null);

    // then
    assertEquals(sellers.get(0), sellerCompany);
    assertEquals(sellerCompany.getNumberOfUsers(), metadata.getNumberOfUsers());
    assertEquals(sellerCompany.getNumberOfMediationSites(), metadata.getNumberOfMediationSites());
    assertEquals(sellerCompany.getNumberOfRtbTags(), metadata.getNumberOfRtbTags());
    assertEquals(true, sellerCompany.getHasHeaderBiddingSites());
  }

  private List<SellerMetaDataForCompanyProjection> mockSellerMetaDataForCompanyProjections(
      long pid) {
    List<SellerMetaDataForCompanyProjection> sellerMetaDataForCompanyProjections =
        new ArrayList<>();
    SellerMetaDataForCompanyProjection sellerMetaData =
        mock(SellerMetaDataForCompanyProjection.class);
    given(sellerMetaData.getCompany()).willReturn(pid);
    given(sellerMetaData.getSites()).willReturn(1);
    given(sellerMetaData.getTags()).willReturn(1);
    given(sellerMetaData.getUsers()).willReturn(1);
    given(sellerMetaData.getHbsites()).willReturn(1);
    sellerMetaDataForCompanyProjections.add(sellerMetaData);

    return sellerMetaDataForCompanyProjections;
  }

  @Test
  void shouldGetAllCompaniesByTypeWhenSellerUserLoggedIn() {
    // given
    Company c1 = createCompany(CompanyType.SELLER);
    Company c2 = createCompany(CompanyType.SELLER);
    User sellerSeatUser = createUser(User.Role.ROLE_ADMIN, c1, c2);

    loggedUser(sellerSeatUser);

    List<SellerMetaDataForCompanyProjection> sellerMetaDataForCompanyProjections =
        new ArrayList<>();
    SellerMetaDataForCompanyProjection sellerMetaData =
        mock(SellerMetaDataForCompanyProjection.class);
    sellerMetaDataForCompanyProjections.add(sellerMetaData);
    when(companyRepository.findSellerMetaDataForCompanyProjectionsByCompanyPid(anyLong()))
        .thenReturn(sellerMetaDataForCompanyProjections);

    when(companyRepository.findByPidIn(Set.of(c1.getPid(), c2.getPid())))
        .thenReturn(Lists.newArrayList(c1, c2));

    // when
    List<Company> sellers = companyService.getAllCompaniesByType(CompanyType.SELLER, null, null);

    // then
    assertEquals(Lists.newArrayList(c1, c2), sellers);
  }

  @Test
  void shouldThrowExceptionWhenCreatingSellerAttributesWithHbPercentageTooLow() {
    // given
    Company sellerCompany = createCompany(CompanyType.SELLER);
    SellerAttributes sellerAttributes = new SellerAttributes();
    sellerAttributes.setHbThrottleEnabled(true);
    sellerAttributes.setHbThrottlePercentage(-10);
    sellerCompany.setSellerAttributes(sellerAttributes);

    // when & then
    GenevaValidationException exception =
        assertThrows(
            GenevaValidationException.class, () -> companyService.createCompany(sellerCompany));
    assertEquals(ServerErrorCodes.SERVER_HB_THROTTLE_PERCENTAGE_INVALID, exception.getErrorCode());
  }

  @Test
  void shouldThrowExceptionWhenCreatingSellerAttributesWithHbPercentageTooHigh() {
    // given
    Company sellerCompany = createCompany(CompanyType.SELLER);
    SellerAttributes sellerAttributes = new SellerAttributes();
    sellerAttributes.setHbThrottleEnabled(true);
    sellerAttributes.setHbThrottlePercentage(1000);
    sellerCompany.setSellerAttributes(sellerAttributes);

    // when & then
    GenevaValidationException exception =
        assertThrows(
            GenevaValidationException.class, () -> companyService.createCompany(sellerCompany));
    assertEquals(ServerErrorCodes.SERVER_HB_THROTTLE_PERCENTAGE_INVALID, exception.getErrorCode());
  }

  @Test
  void shouldThrowExceptionWhenUpdatingSellerAttributesAsSellerAndHbFieldsNotIdentical() {
    // given
    Company sellerCompanyInput = createCompany(CompanyType.SELLER);
    SellerAttributes sellerAttributes = new SellerAttributes();
    sellerAttributes.setHbThrottleEnabled(true);
    sellerAttributes.setHbThrottlePercentage(10);
    sellerCompanyInput.setSellerAttributes(sellerAttributes);

    Company sellerCompanyFromDb = createCompany(CompanyType.SELLER);
    SellerAttributes saFromDb = new SellerAttributes();
    saFromDb.setHbThrottleEnabled(false);
    sellerCompanyFromDb.setSellerAttributes(saFromDb);
    when(companyRepository.findById(any())).thenReturn(Optional.of(sellerCompanyFromDb));

    // when & then
    var exception =
        assertThrows(
            GenevaSecurityException.class, () -> companyService.updateCompany(sellerCompanyInput));
    assertEquals(SecurityErrorCodes.SECURITY_NOT_AUTHORIZED, exception.getErrorCode());
  }

  @Test
  void shouldThrowNotFoundWhenCompanyDoesNotExist() {
    // when
    when(companyRepository.findById(anyLong())).thenReturn(Optional.empty());
    Company sellerCompanyInput = createCompany(CompanyType.SELLER);

    // then
    var exception =
        assertThrows(
            GenevaValidationException.class,
            () -> companyService.updateCompany(sellerCompanyInput));
    assertEquals(ServerErrorCodes.SERVER_COMPANY_NOT_FOUND, exception.getErrorCode());
  }

  @Test
  void shouldUpdateCompanyWhenUpdatingSellerAttributesAsSellerAndHbFieldsIdentical() {
    // given
    when(userContext.getType()).thenReturn(CompanyType.SELLER);
    when(userContext.doSameOrNexageAffiliation(any(Long.class))).thenReturn(true);

    Company sellerCompanyInput = createCompany(CompanyType.SELLER);

    SellerAttributes sellerAttributes = new SellerAttributes();
    sellerAttributes.setHbThrottleEnabled(true);
    sellerAttributes.setHbThrottlePercentage(10);
    sellerAttributes.setLimitEnabled(true);
    sellerCompanyInput.setSellerAttributes(sellerAttributes);

    when(companyRepository.save(any())).thenReturn(sellerCompanyInput);

    // Not really core to the test, but updating a company requires that there is a seller metadata
    // object
    List<SellerMetaDataForCompanyProjection> sellerMetaDataForCompanyProjections =
        new ArrayList<>();
    SellerMetaDataForCompanyProjection sellerMetaData =
        mock(SellerMetaDataForCompanyProjection.class);
    sellerMetaDataForCompanyProjections.add(sellerMetaData);
    when(companyRepository.findSellerMetaDataForCompanyProjectionsByCompanyPid(anyLong()))
        .thenReturn(sellerMetaDataForCompanyProjections);

    Company sellerCompanyFromDb = createCompany(CompanyType.SELLER);
    SellerAttributes saFromDb = new SellerAttributes();
    saFromDb.setHbThrottleEnabled(true);
    saFromDb.setHbThrottlePercentage(10);
    saFromDb.setLimitEnabled(false);
    sellerCompanyFromDb.setSellerAttributes(saFromDb);
    when(companyRepository.findById(any())).thenReturn(Optional.of(sellerCompanyFromDb));

    // when
    Company updated = companyService.updateCompany(sellerCompanyInput);

    // then
    assertEquals(sellerCompanyInput.getPid(), updated.getPid());
  }

  @Test
  void shouldUpdateCompanyWhenUpdatingSellerAttributesAsNexageAndHbFieldsNotIdentical() {
    // given
    when(userContext.getType()).thenReturn(CompanyType.NEXAGE);
    when(userContext.doSameOrNexageAffiliation(any(Long.class))).thenReturn(true);

    Company sellerCompanyInput = createCompany(CompanyType.SELLER);

    SellerAttributes sellerAttributes = new SellerAttributes();
    sellerAttributes.setHbThrottleEnabled(true);
    sellerAttributes.setHbThrottlePercentage(15);
    sellerAttributes.setLimitEnabled(true);
    sellerCompanyInput.setSellerAttributes(sellerAttributes);

    when(companyRepository.save(any())).thenReturn(sellerCompanyInput);

    // Not really core to the test, but updating a company requires that there is a seller metadata
    // object
    List<SellerMetaDataForCompanyProjection> sellerMetaDataForCompanyProjections =
        new ArrayList<>();
    SellerMetaDataForCompanyProjection sellerMetaData =
        mock(SellerMetaDataForCompanyProjection.class);
    sellerMetaDataForCompanyProjections.add(sellerMetaData);
    when(companyRepository.findSellerMetaDataForCompanyProjectionsByCompanyPid(anyLong()))
        .thenReturn(sellerMetaDataForCompanyProjections);

    Company sellerCompanyFromDb = createCompany(CompanyType.SELLER);
    SellerAttributes saFromDb = new SellerAttributes();
    saFromDb.setHbThrottleEnabled(true);
    saFromDb.setHbThrottlePercentage(10);
    saFromDb.setLimitEnabled(false);
    sellerCompanyFromDb.setSellerAttributes(saFromDb);
    when(companyRepository.findById(any())).thenReturn(Optional.of(sellerCompanyFromDb));

    // when
    Company updated = companyService.updateCompany(sellerCompanyInput);

    // then
    assertEquals(sellerCompanyInput.getPid(), updated.getPid());
  }

  @Test
  void shouldCreateSellerCompanyWithAdFeedbackTrue() {
    // given
    Company sellerCompany = createCompany(CompanyType.SELLER);
    SellerAttributes sellerAttributes = new SellerAttributes();
    sellerAttributes.setAdFeedbackOptOut(true);
    sellerCompany.setSellerAttributes(sellerAttributes);

    when(companyRepository.save(any())).thenReturn(sellerCompany);

    // when
    var createdCompany = companyService.createCompany(sellerCompany);

    // then
    verifyPublish(createdCompany);
  }

  @Test
  void shouldCreateBdrExchangeCompanyWhenCreatingSeatHolderCompany() {
    // given
    Company sellerCompany = createCompany(CompanyType.SEATHOLDER);
    when(companyRepository.save(any())).thenReturn(sellerCompany);
    when(bdrExchangeRepository.findById(1L)).thenReturn(Optional.of(new BDRExchange()));

    // when
    Company createdCompany = companyService.createCompany(sellerCompany);

    // then
    verifyPublish(createdCompany);
    verify(bdrExchangeRepository).findById(1L);
    verify(bdrExchangeCompanyRepository).save(any(BdrExchangeCompany.class));
    verifyNoMoreInteractions(exchangeRepository);
    verifyNoMoreInteractions(bdrExchangeCompanyRepository);
  }

  @Test
  void shouldCreateSellerCompanyWithAdServingEnabled() {
    // given
    Company sellerCompany = createCompany(CompanyType.SELLER);
    sellerCompany.setSellerAttributes(new SellerAttributes());
    sellerCompany.setAdServingEnabled(true);
    Advertiser advertiser = Advertiser.createHouseAdvertiser(sellerCompany.getPid());

    when(companyRepository.save(any())).thenReturn(sellerCompany);
    when(advertiserRepository.save(advertiser))
        .thenReturn(Advertiser.createHouseAdvertiser(sellerCompany.getPid()));

    // when
    Company createdCompany = companyService.createCompany(sellerCompany);

    // then
    verifyPublish(createdCompany);
    verify(advertiserRepository).save(any(Advertiser.class));
    verifyNoMoreInteractions(advertiserRepository);
  }

  @Test
  void shouldCreateSellerCompanyWithAdFeedbackFalse() {
    // given
    Company sellerCompany = createCompany(CompanyType.SELLER);
    SellerAttributes sellerAttributes = new SellerAttributes();
    sellerAttributes.setAdFeedbackOptOut(false);
    sellerCompany.setSellerAttributes(sellerAttributes);

    when(companyRepository.save(any())).thenReturn(sellerCompany);

    // when
    var createdCompany = companyService.createCompany(sellerCompany);

    // then
    verifyPublish(createdCompany);
  }

  @Test
  void shouldCreateSellerCompanyWithHumanOptOutTrue() {
    // given
    Company sellerCompany = TestObjectsFactory.createCompany(CompanyType.SELLER);
    SellerAttributes sellerAttributes = new SellerAttributes();
    sellerAttributes.setHumanOptOut(true);
    sellerCompany.setSellerAttributes(sellerAttributes);

    when(companyRepository.save(any())).thenReturn(sellerCompany);

    // when
    Company testCompany = companyService.createCompany(sellerCompany);

    // then
    assertEquals(true, testCompany.getSellerAttributes().getHumanOptOut());
  }

  @Test
  void shouldCreateSellerCompanyWithHumanOptOutFalse() {
    // given
    Company sellerCompany = TestObjectsFactory.createCompany(CompanyType.SELLER);
    SellerAttributes sellerAttributes = new SellerAttributes();
    sellerAttributes.setHumanOptOut(false);
    sellerCompany.setSellerAttributes(sellerAttributes);

    when(companyRepository.save(any())).thenReturn(sellerCompany);

    // when
    Company testCompany = companyService.createCompany(sellerCompany);

    // then
    assertEquals(false, testCompany.getSellerAttributes().getHumanOptOut());
  }

  @Test
  void shouldCreateSellerCompanyWithBuyerTransparencyOptOutTrue() {
    // given
    Company sellerCompany = createCompany(CompanyType.SELLER);
    SellerAttributes sellerAttributes = new SellerAttributes();
    sellerAttributes.setBuyerTransparencyOptOut(true);
    sellerCompany.setSellerAttributes(sellerAttributes);

    when(companyRepository.save(any())).thenReturn(sellerCompany);

    // when
    var createdCompany = companyService.createCompany(sellerCompany);

    // then
    verifyPublish(createdCompany);
  }

  @Test
  void shouldCreateSellerCompanyWithBuyerTransparencyOptOutFalse() {
    // given
    Company sellerCompany = createCompany(CompanyType.SELLER);
    SellerAttributes sellerAttributes = new SellerAttributes();
    sellerAttributes.setBuyerTransparencyOptOut(false);
    sellerCompany.setSellerAttributes(sellerAttributes);

    when(companyRepository.save(any())).thenReturn(sellerCompany);

    // when
    var createdCompany = companyService.createCompany(sellerCompany);

    // then
    verifyPublish(createdCompany);
  }

  @Test
  void shouldCreateSellerCompanyWithRevenueGroupPid() {
    // given
    Company sellerCompany = createCompany(CompanyType.SELLER);

    SellerAttributes sa = new SellerAttributes();
    sa.setRevenueGroupPid(1L);
    sellerCompany.setSellerAttributes(sa);
    when(companyRepository.save(any())).thenReturn(sellerCompany);

    // when
    var createdCompany = companyService.createCompany(sellerCompany);

    // then
    assertEquals(1L, createdCompany.getSellerAttributes().getRevenueGroupPid().longValue());
    verifyPublish(createdCompany);
  }

  @Test
  void shouldCreateSellerCompanyWithSellerTypeDirect() {
    // given
    Company sellerCompany = createCompany(CompanyType.SELLER);
    SellerAttributes sellerAttributes = new SellerAttributes();
    sellerAttributes.setSellerType(SellerType.DIRECT);

    sellerCompany.setSellerAttributes(sellerAttributes);

    when(companyRepository.save(any())).thenReturn(sellerCompany);

    // when
    var createdCompany = companyService.createCompany(sellerCompany);

    // then
    verifyPublish(createdCompany);
  }

  @Test
  void shouldNotOverrideExistingValueWithNullWhenUpdatingDhReportingIdToNullInCompany() {
    // given
    when(userContext.doSameOrNexageAffiliation(any(Long.class))).thenReturn(true);
    Company company = createCompany(CompanyType.SELLER);
    company.setDhReportingId(null);
    when(companyRepository.save(any())).thenReturn(company);

    // Not really core to the test, but updating a company requires that there is a seller metadata
    // object
    List<SellerMetaDataForCompanyProjection> sellerMetaDataForCompanyProjections =
        new ArrayList<>();
    SellerMetaDataForCompanyProjection sellerMetaData =
        mock(SellerMetaDataForCompanyProjection.class);
    sellerMetaDataForCompanyProjections.add(sellerMetaData);
    when(companyRepository.findSellerMetaDataForCompanyProjectionsByCompanyPid(anyLong()))
        .thenReturn(sellerMetaDataForCompanyProjections);

    Company companyFromDb = createCompany(CompanyType.SELLER);
    companyFromDb.setDhReportingId("dhReportingId");
    when(companyRepository.findById(any())).thenReturn(Optional.of(companyFromDb));

    // when
    Company updatedCompany = companyService.updateCompany(company);

    // then
    assertEquals(companyFromDb.getDhReportingId(), updatedCompany.getDhReportingId());
  }

  @Test
  void
      shouldNotOverrideExistingValueWithNullWhenUpdatingFraudDetectionJavascriptEnabledToNullInCompany() {
    // given
    when(userContext.doSameOrNexageAffiliation(any(Long.class))).thenReturn(true);
    Company company = createCompany(CompanyType.SELLER);
    company.setFraudDetectionJavascriptEnabled(null);
    when(companyRepository.save(any())).thenReturn(company);

    // Not really core to the test, but updating a company requires that there is a seller metadata
    // object
    List<SellerMetaDataForCompanyProjection> sellerMetaDataForCompanyProjections =
        new ArrayList<>();
    SellerMetaDataForCompanyProjection sellerMetaData =
        mock(SellerMetaDataForCompanyProjection.class);
    sellerMetaDataForCompanyProjections.add(sellerMetaData);
    when(companyRepository.findSellerMetaDataForCompanyProjectionsByCompanyPid(anyLong()))
        .thenReturn(sellerMetaDataForCompanyProjections);

    Company companyFromDb = createCompany(CompanyType.SELLER);
    companyFromDb.setFraudDetectionJavascriptEnabled(false);
    when(companyRepository.findById(any())).thenReturn(Optional.of(companyFromDb));

    // when
    Company updatedCompany = companyService.updateCompany(company);

    // then
    assertEquals(
        companyFromDb.getFraudDetectionJavascriptEnabled(),
        updatedCompany.getFraudDetectionJavascriptEnabled());
  }

  @Test
  void shouldUpdateCompanyWithVideoUseInboundSiteOrApp() {
    // given
    when(userContext.getType()).thenReturn(CompanyType.NEXAGE);
    when(userContext.doSameOrNexageAffiliation(any(Long.class))).thenReturn(true);

    Company company = createCompany(CompanyType.SELLER);

    SellerAttributes sellerAttributes = new SellerAttributes();
    sellerAttributes.setVideoUseInboundSiteOrApp(true);

    company.setSellerAttributes(sellerAttributes);

    when(companyRepository.save(any())).thenReturn(company);
    when(companyRepository.findById(any())).thenReturn(Optional.of(company));

    List<SellerMetaDataForCompanyProjection> sellerMetaDataForCompanyProjections =
        new ArrayList<>();
    SellerMetaDataForCompanyProjection sellerMetaData =
        mock(SellerMetaDataForCompanyProjection.class);
    sellerMetaDataForCompanyProjections.add(sellerMetaData);
    when(companyRepository.findSellerMetaDataForCompanyProjectionsByCompanyPid(anyLong()))
        .thenReturn(sellerMetaDataForCompanyProjections);

    // when
    Company updatedCompany = companyService.updateCompany(company);

    // then
    assertEquals(true, updatedCompany.getSellerAttributes().getVideoUseInboundSiteOrApp());
  }

  @Test
  void shouldUpdateCompanyWithPublisherDataProtectionRole() {
    // given
    when(userContext.getType()).thenReturn(CompanyType.NEXAGE);
    when(userContext.doSameOrNexageAffiliation(any(Long.class))).thenReturn(true);

    Company company = createCompany(CompanyType.SELLER);

    SellerAttributes sellerAttributes = new SellerAttributes();
    sellerAttributes.setPublisherDataProtectionRole(0);

    company.setSellerAttributes(sellerAttributes);

    when(companyRepository.save(any())).thenReturn(company);
    when(companyRepository.findById(any())).thenReturn(Optional.of(company));

    List<SellerMetaDataForCompanyProjection> sellerMetaDataForCompanyProjections =
        new ArrayList<>();
    SellerMetaDataForCompanyProjection sellerMetaData =
        mock(SellerMetaDataForCompanyProjection.class);
    sellerMetaDataForCompanyProjections.add(sellerMetaData);
    when(companyRepository.findSellerMetaDataForCompanyProjectionsByCompanyPid(anyLong()))
        .thenReturn(sellerMetaDataForCompanyProjections);

    // when
    Company updatedCompany = companyService.updateCompany(company);

    // then
    assertEquals(
        PublisherDataProtectionRole.THIRD_PARTY_TCF.getExternalValue(),
        updatedCompany.getSellerAttributes().getPublisherDataProtectionRole());
  }

  @Test
  void shouldUpdateCompanyWithSellerDomainVerificationAuthLevel() {
    // given
    when(userContext.getType()).thenReturn(CompanyType.NEXAGE);
    when(userContext.doSameOrNexageAffiliation(any(Long.class))).thenReturn(true);

    Company company = createCompany(CompanyType.SELLER);

    SellerAttributes sellerAttributes = new SellerAttributes();
    sellerAttributes.setSellerDomainVerificationAuthLevel(
        SellerDomainVerificationAuthLevel.ALLOW_BASED_ON_BIDDER);

    company.setSellerAttributes(sellerAttributes);

    when(companyRepository.save(any())).thenReturn(company);
    when(companyRepository.findById(any())).thenReturn(Optional.of(company));

    List<SellerMetaDataForCompanyProjection> sellerMetaDataForCompanyProjections =
        new ArrayList<>();
    SellerMetaDataForCompanyProjection sellerMetaData =
        mock(SellerMetaDataForCompanyProjection.class);
    sellerMetaDataForCompanyProjections.add(sellerMetaData);
    when(companyRepository.findSellerMetaDataForCompanyProjectionsByCompanyPid(anyLong()))
        .thenReturn(sellerMetaDataForCompanyProjections);

    // when
    Company updatedCompany = companyService.updateCompany(company);

    // then
    assertEquals(
        SellerDomainVerificationAuthLevel.ALLOW_BASED_ON_BIDDER,
        updatedCompany.getSellerAttributes().getSellerDomainVerificationAuthLevel());
  }

  @Test
  void shouldThrowExceptionWhenCreatingCompanyWithNotExistRegion() {
    // given
    Company sellerCompany = createCompany(CompanyType.SELLER);
    SellerAttributes sellerAttributes = new SellerAttributes();
    sellerAttributes.setHbThrottleEnabled(true);
    sellerAttributes.setHbThrottlePercentage(-10);
    sellerCompany.setSellerAttributes(sellerAttributes);
    sellerCompany.setRegionId(1L);

    // when & then
    assertThrows(
        GenevaValidationException.class, () -> companyService.createCompany(sellerCompany));
  }

  @Test
  void shouldCreateCompanyWithSellerSeat() {
    // given
    Company sellerCompany = createCompany(CompanyType.SELLER);
    SellerAttributes sellerAttributes = setSellerAttributes();
    sellerCompany.setSellerAttributes(sellerAttributes);
    sellerCompany.setRegionId(2L);
    long sellerSeatPid = 123L;
    sellerCompany.setSellerSeatPid(sellerSeatPid);
    when(companyRepository.save(any())).thenReturn(sellerCompany);

    SellerSeat sellerSeat = setSellerSeat(sellerSeatPid);
    when(sellerSeatRepository.findById(sellerSeatPid)).thenReturn(Optional.of(sellerSeat));

    when(userRepository.findAllBySellerSeat_Pid(sellerSeatPid)).thenReturn(null);

    // when
    Company createdCompany = companyService.createCompany(sellerCompany);

    // then
    assertEquals(createdCompany.getSellerSeatPid(), sellerSeatPid);
    verifyPublish(createdCompany);
  }

  @Test
  void shouldCreateCompanyWithSellerSeatAndUpdateUserCompanyRelations() {
    // given
    Company sellerCompany = createCompany(CompanyType.SELLER);
    SellerAttributes sellerAttributes = setSellerAttributes();
    sellerCompany.setSellerAttributes(sellerAttributes);
    sellerCompany.setRegionId(2L);
    long sellerSeatPid = 123L;
    sellerCompany.setSellerSeatPid(sellerSeatPid);
    when(companyRepository.save(any())).thenReturn(sellerCompany);

    SellerSeat sellerSeat = setSellerSeat(sellerSeatPid);
    when(sellerSeatRepository.findById(sellerSeatPid)).thenReturn(Optional.of(sellerSeat));

    List<User> users = new ArrayList<>();
    User user = createUser();
    users.add(user);
    when(userRepository.findAllBySellerSeat_Pid(sellerSeatPid)).thenReturn(users);

    // when
    Company createdCompany = companyService.createCompany(sellerCompany);

    // then
    assertEquals(createdCompany.getSellerSeatPid(), sellerSeatPid);
    // we have only one user anyway
    users.forEach(u -> assertEquals(user.getCompanies().size(), 2));
    List<Long> companyPids =
        user.getCompanies().stream().map(BaseModel::getPid).collect(Collectors.toList());
    assertTrue(companyPids.contains(createdCompany.getPid()));

    verifyPublish(createdCompany);
  }

  /**
   * In this case (non Seller type) of Company methods related to seller seat logic should not be
   * invoked
   */
  @Test
  void shouldCreateCompanyOfNonSellerType() {
    // given
    Company buyerCompany = createCompany(CompanyType.BUYER);
    when(companyRepository.save(any())).thenReturn(buyerCompany);

    // when
    Company createdCompany = companyService.createCompany(buyerCompany);

    // then
    assertTrue(createdCompany.getExternalAdVerificationEnabled());
    assertTrue(createdCompany.getFraudDetectionJavascriptEnabled());
    verify(sellerSeatRepository, never()).findById(anyLong());
    verify(userRepository, never()).findAllBySellerSeat_Pid(anyLong());
  }

  @Test
  void shouldCreateCompanyOfNonSellerTypeWithCpiConversionNoticeUrlSetAndNotEnabled() {
    // given
    Company buyerCompany = createCompany(CompanyType.BUYER);
    buyerCompany.setCpiConversionNoticeUrl("cpi_conversion_notice_url");
    buyerCompany.setCpiTrackingEnabled(false);

    when(companyRepository.save(any())).thenReturn(buyerCompany);

    // when
    Company createdCompany = companyService.createCompany(buyerCompany);

    // then
    assertTrue(createdCompany.getExternalAdVerificationEnabled());
    assertTrue(createdCompany.getFraudDetectionJavascriptEnabled());
    assertNull(createdCompany.getCpiConversionNoticeUrl());
    verify(sellerSeatRepository, never()).findById(anyLong());
    verify(userRepository, never()).findAllBySellerSeat_Pid(anyLong());
  }

  @Test
  void shouldCreateCompanyOfNonSellerTypeWithCpiConversionNoticeUrlSetAndEnabled() {
    // given
    String cpiConversionNoticeUrl = "cpi_conversion_notice_url";
    Company buyerCompany = createCompany(CompanyType.BUYER);
    buyerCompany.setCpiConversionNoticeUrl(cpiConversionNoticeUrl);
    buyerCompany.setCpiTrackingEnabled(true);

    when(companyRepository.save(any())).thenReturn(buyerCompany);

    // when
    Company createdCompany = companyService.createCompany(buyerCompany);

    // then
    assertTrue(createdCompany.getExternalAdVerificationEnabled());
    assertTrue(createdCompany.getFraudDetectionJavascriptEnabled());
    assertEquals(cpiConversionNoticeUrl, createdCompany.getCpiConversionNoticeUrl());
    verify(sellerSeatRepository, never()).findById(anyLong());
    verify(userRepository, never()).findAllBySellerSeat_Pid(anyLong());
  }

  @Test
  void shouldCreateCompanyOfNonSellerTypeWithExternalAdVerificationEnabledFlag() {
    // given
    Company buyerCompany = createCompany(CompanyType.BUYER);
    buyerCompany.setExternalAdVerificationEnabled(false);
    when(companyRepository.save(any())).thenReturn(buyerCompany);

    // when
    Company createdCompany = companyService.createCompany(buyerCompany);

    // then
    assertFalse(createdCompany.getExternalAdVerificationEnabled());
    verify(sellerSeatRepository, never()).findById(anyLong());
    verify(userRepository, never()).findAllBySellerSeat_Pid(anyLong());
  }

  @Test
  void shouldCreateCompanyWithUnknownSellerSeat() {
    // given
    Company sellerCompany = createCompany(CompanyType.SELLER);
    SellerAttributes sellerAttributes = setSellerAttributes();
    sellerCompany.setSellerAttributes(sellerAttributes);
    sellerCompany.setRegionId(2L);
    long sellerSeatPid = 123L;
    sellerCompany.setSellerSeatPid(sellerSeatPid);
    when(sellerSeatRepository.findById(sellerSeatPid)).thenReturn(Optional.empty());

    // when & then
    var exception =
        assertThrows(
            GenevaValidationException.class, () -> companyService.createCompany(sellerCompany));
    assertEquals(ServerErrorCodes.SERVER_SELLER_SEAT_NOT_FOUND, exception.getErrorCode());
  }

  @Test
  void shouldPersistBdrExchangeCompanyBasedOnIdWhenCreatingSeatholderCompanyForExistingExchange() {
    // given
    Company testCompany = createCompany(CompanyType.SEATHOLDER);
    when(bdrExchangeRepository.findById(1L)).thenReturn(Optional.of(mock(BDRExchange.class)));
    when(companyRepository.save(any())).thenReturn(testCompany);

    // when
    companyService.createCompany(testCompany);

    // then
    verify(bdrExchangeCompanyRepository).save(any());
  }

  @Test
  void shouldThrowGenevaValidationExceptionWhenCreatingSeatholderCompanyForNonExistingExchange() {
    // given
    Company testCompany = createCompany(CompanyType.SEATHOLDER);
    when(companyRepository.save(any())).thenReturn(testCompany);

    // when & then
    GenevaValidationException exception =
        assertThrows(
            GenevaValidationException.class, () -> companyService.createCompany(testCompany));
    assertEquals(ServerErrorCodes.SERVER_TARGET_INVALID_EXCHANGE_ID, exception.getErrorCode());
  }

  @Test
  void shouldThrowExceptionWhenUpdatingCompanyWithoutPermissionRole() {
    // given
    when(userContext.doSameOrNexageAffiliation(any(Long.class))).thenReturn(true);

    Company sellerCompanyInput = createCompany(CompanyType.SELLER);
    SellerAttributes sellerAttributes = setSellerAttributes();
    sellerCompanyInput.setSellerAttributes(sellerAttributes);
    sellerCompanyInput.setRegionId(2L);

    Company sellerCompanyFromDb = createCompany(CompanyType.SELLER);
    SellerAttributes saFromDb = setSellerAttributes();
    sellerCompanyFromDb.setSellerAttributes(saFromDb);

    when(companyRepository.findById(any())).thenReturn(Optional.of(sellerCompanyFromDb));
    sellerCompanyInput.setRegionId(1L);

    // when & then
    var exception =
        assertThrows(
            GenevaSecurityException.class, () -> companyService.updateCompany(sellerCompanyInput));
    assertEquals(SecurityErrorCodes.SECURITY_NOT_AUTHORIZED, exception.getErrorCode());
  }

  @Test
  void shouldThrowExceptionWhenUpdatingCompanyWithoutNexageAffiliation() {
    // given
    when(userContext.doSameOrNexageAffiliation(any(Long.class))).thenReturn(false);
    Company sellerCompanyInput = createCompany(CompanyType.SELLER);
    Company sellerCompanyFromDb = createCompany(CompanyType.SELLER);
    when(companyRepository.findById(any())).thenReturn(Optional.of(sellerCompanyFromDb));
    sellerCompanyInput.setRegionId(1L);

    // when & then
    var exception =
        assertThrows(
            GenevaSecurityException.class, () -> companyService.updateCompany(sellerCompanyInput));
    assertEquals(SecurityErrorCodes.SECURITY_NOT_AUTHORIZED, exception.getErrorCode());
  }

  @Test
  void shouldUpdateCompanyWithSellerSeat() {
    // given
    when(userContext.getType()).thenReturn(CompanyType.NEXAGE);
    when(userContext.doSameOrNexageAffiliation(any(Long.class))).thenReturn(true);
    when(userContext.isNexageAdminOrManager()).thenReturn(true);

    long sellerSeatPid = 321L;
    Company sellerCompanyInput = createCompany(CompanyType.SELLER);
    sellerCompanyInput.setSellerSeatPid(sellerSeatPid);
    SellerAttributes sellerAttributes = setSellerAttributes();
    sellerCompanyInput.setSellerAttributes(sellerAttributes);
    sellerCompanyInput.setRegionId(2L);
    sellerCompanyInput.setCurrency("USD");
    when(companyRepository.save(any())).thenReturn(sellerCompanyInput);

    // Not really core to the test, but updating a company requires that there is a seller
    // metadata object
    List<SellerMetaDataForCompanyProjection> sellerMetaDataForCompanyProjections =
        new ArrayList<>();
    SellerMetaDataForCompanyProjection sellerMetaData =
        mock(SellerMetaDataForCompanyProjection.class);
    sellerMetaDataForCompanyProjections.add(sellerMetaData);
    when(companyRepository.findSellerMetaDataForCompanyProjectionsByCompanyPid(anyLong()))
        .thenReturn(sellerMetaDataForCompanyProjections);

    Company sellerCompanyFromDb = createCompany(CompanyType.SELLER);
    SellerAttributes saFromDb = setSellerAttributes();
    sellerCompanyFromDb.setSellerAttributes(saFromDb);

    SellerSeat sellerSeat = setSellerSeat(sellerSeatPid);
    when(sellerSeatRepository.findById(sellerSeatPid)).thenReturn(Optional.of(sellerSeat));

    when(companyRepository.findById(any())).thenReturn(Optional.of(sellerCompanyFromDb));
    sellerCompanyInput.setRegionId(2L);

    when(userRepository.findAllBySellerSeat_Pid(sellerSeatPid)).thenReturn(null);

    // when
    Company updated = companyService.updateCompany(sellerCompanyInput);

    // then
    assertEquals(updated.getSellerSeatPid(), sellerSeatPid);
  }

  @Test
  void shouldUpdateCompanyWithSellerSeatAndUpdateUserCompanyRelations() {
    // given
    when(userContext.getType()).thenReturn(CompanyType.NEXAGE);
    when(userContext.doSameOrNexageAffiliation(any(Long.class))).thenReturn(true);
    when(userContext.isNexageAdminOrManager()).thenReturn(true);

    long sellerSeatPid = 321L;
    Company sellerCompanyInput = createCompany(CompanyType.SELLER);
    sellerCompanyInput.setSellerSeatPid(sellerSeatPid);
    SellerAttributes sellerAttributes = setSellerAttributes();
    sellerCompanyInput.setSellerAttributes(sellerAttributes);
    sellerCompanyInput.setRegionId(2L);
    sellerCompanyInput.setCurrency("USD");
    when(companyRepository.save(any())).thenReturn(sellerCompanyInput);

    // Not really core to the test, but updating a company requires that there is a seller
    // metadata object
    List<SellerMetaDataForCompanyProjection> sellerMetaDataForCompanyProjections =
        new ArrayList<>();
    SellerMetaDataForCompanyProjection sellerMetaData =
        mock(SellerMetaDataForCompanyProjection.class);
    sellerMetaDataForCompanyProjections.add(sellerMetaData);
    when(companyRepository.findSellerMetaDataForCompanyProjectionsByCompanyPid(anyLong()))
        .thenReturn(sellerMetaDataForCompanyProjections);

    Company sellerCompanyFromDb = createCompany(CompanyType.SELLER);
    SellerAttributes saFromDb = setSellerAttributes();
    sellerCompanyFromDb.setSellerAttributes(saFromDb);

    SellerSeat sellerSeat = setSellerSeat(sellerSeatPid);
    when(sellerSeatRepository.findById(sellerSeatPid)).thenReturn(Optional.of(sellerSeat));

    when(companyRepository.findById(any())).thenReturn(Optional.of(sellerCompanyFromDb));
    sellerCompanyInput.setRegionId(2L);

    List<User> users = new ArrayList<>();
    User user = createUser();
    users.add(user);
    when(userRepository.findAllBySellerSeat_Pid(sellerSeatPid)).thenReturn(users);

    // when
    Company updated = companyService.updateCompany(sellerCompanyInput);

    // then
    assertEquals(updated.getSellerSeatPid(), sellerSeatPid);

    // we have only one user anyway
    users.forEach(u -> assertEquals(user.getCompanies().size(), 2));
    List<Long> companyPids =
        user.getCompanies().stream().map(BaseModel::getPid).collect(Collectors.toList());
    assertTrue(companyPids.contains(updated.getPid()));
  }

  @Test
  void shouldThrowExceptionWhenUpdatingCompanyWithUnknownSellerSeat() {
    // given
    when(userContext.getType()).thenReturn(CompanyType.NEXAGE);
    when(userContext.doSameOrNexageAffiliation(any(Long.class))).thenReturn(true);
    when(userContext.isNexageAdminOrManager()).thenReturn(true);

    long sellerSeatPid = 321L;
    Company sellerCompanyInput = createCompany(CompanyType.SELLER);
    sellerCompanyInput.setSellerSeatPid(sellerSeatPid);
    SellerAttributes sellerAttributes = setSellerAttributes();
    sellerCompanyInput.setSellerAttributes(sellerAttributes);
    sellerCompanyInput.setRegionId(2L);
    sellerCompanyInput.setCurrency("USD");

    Company sellerCompanyFromDb = createCompany(CompanyType.SELLER);
    SellerAttributes saFromDb = setSellerAttributes();
    sellerCompanyFromDb.setSellerAttributes(saFromDb);

    // return null to simulate unknown (not found) seller seat
    when(sellerSeatRepository.findById(sellerSeatPid)).thenReturn(Optional.empty());

    when(companyRepository.findById(any())).thenReturn(Optional.of(sellerCompanyFromDb));
    sellerCompanyInput.setRegionId(2L);

    // when & then
    var exception =
        assertThrows(
            GenevaValidationException.class,
            () -> companyService.updateCompany(sellerCompanyInput));
    assertEquals(ServerErrorCodes.SERVER_SELLER_SEAT_NOT_FOUND, exception.getErrorCode());
  }

  @Test
  void shouldUpdateCompanyOfNonSellerType() {
    // given
    Company inputCompany = createCompany(CompanyType.BUYER);
    Company dbCompany = createCompany(CompanyType.BUYER);

    dbCompany.setExternalAdVerificationEnabled(true);
    dbCompany.setFraudDetectionJavascriptEnabled(true);

    when(companyRepository.findById(inputCompany.getPid())).thenReturn(Optional.of(dbCompany));
    when(companyRepository.save(inputCompany)).thenReturn(inputCompany);

    List<BuyerMetaDataForCompanyProjection> buyerMetaDataForCompanyProjections = new ArrayList<>();
    BuyerMetaDataForCompanyProjection buyerMetaData = mock(BuyerMetaDataForCompanyProjection.class);
    buyerMetaDataForCompanyProjections.add(buyerMetaData);
    when(companyRepository.findBuyerMetaDataForCompanyProjectionsByCompanyPid(
            inputCompany.getPid()))
        .thenReturn(buyerMetaDataForCompanyProjections);

    when(userContext.doSameOrNexageAffiliation(any(Long.class))).thenReturn(true);

    // when
    Company updatedCompany = companyService.updateCompany(inputCompany);

    // then
    assertTrue(updatedCompany.getExternalAdVerificationEnabled());
    assertTrue(updatedCompany.getFraudDetectionJavascriptEnabled());
  }

  @Test
  void shouldUpdateCompanyOfNonSellerTypeWithCpiConversionNoticeUrlSetAndNotEnabled() {
    // given
    Company inputCompany = createCompany(CompanyType.BUYER);
    inputCompany.setCpiConversionNoticeUrl("cpi_conversion_notice_url");
    inputCompany.setCpiTrackingEnabled(false);

    Company dbCompany = createCompany(CompanyType.BUYER);

    dbCompany.setExternalAdVerificationEnabled(true);
    dbCompany.setFraudDetectionJavascriptEnabled(true);

    when(companyRepository.findById(inputCompany.getPid())).thenReturn(Optional.of(dbCompany));
    when(companyRepository.save(inputCompany)).thenReturn(inputCompany);

    List<BuyerMetaDataForCompanyProjection> buyerMetaDataForCompanyProjections = new ArrayList<>();
    BuyerMetaDataForCompanyProjection buyerMetaData = mock(BuyerMetaDataForCompanyProjection.class);
    buyerMetaDataForCompanyProjections.add(buyerMetaData);
    when(companyRepository.findBuyerMetaDataForCompanyProjectionsByCompanyPid(
            inputCompany.getPid()))
        .thenReturn(buyerMetaDataForCompanyProjections);

    when(userContext.doSameOrNexageAffiliation(any(Long.class))).thenReturn(true);

    // when
    Company updatedCompany = companyService.updateCompany(inputCompany);

    // then
    assertTrue(updatedCompany.getExternalAdVerificationEnabled());
    assertTrue(updatedCompany.getFraudDetectionJavascriptEnabled());
    ArgumentCaptor<Company> companyArgumentCaptor = ArgumentCaptor.forClass(Company.class);
    verify(companyRepository).save(companyArgumentCaptor.capture());
    Company companyToUpdate = companyArgumentCaptor.getValue();
    assertNull(companyToUpdate.getCpiConversionNoticeUrl());
  }

  @Test
  void shouldUpdateCompanyOfNonSellerTypeWithCpiConversionNoticeUrlSetAndEnabled() {
    // given
    Company inputCompany = createCompany(CompanyType.BUYER);
    String cpiConversionNoticeUrl = "cpi_conversion_notice_url";
    inputCompany.setCpiConversionNoticeUrl(cpiConversionNoticeUrl);
    inputCompany.setCpiTrackingEnabled(true);

    Company dbCompany = createCompany(CompanyType.BUYER);

    dbCompany.setExternalAdVerificationEnabled(true);
    dbCompany.setFraudDetectionJavascriptEnabled(true);

    when(companyRepository.findById(inputCompany.getPid())).thenReturn(Optional.of(dbCompany));
    when(companyRepository.save(inputCompany)).thenReturn(inputCompany);

    List<BuyerMetaDataForCompanyProjection> buyerMetaDataForCompanyProjections = new ArrayList<>();
    BuyerMetaDataForCompanyProjection buyerMetaData = mock(BuyerMetaDataForCompanyProjection.class);
    buyerMetaDataForCompanyProjections.add(buyerMetaData);
    when(companyRepository.findBuyerMetaDataForCompanyProjectionsByCompanyPid(
            inputCompany.getPid()))
        .thenReturn(buyerMetaDataForCompanyProjections);

    when(userContext.doSameOrNexageAffiliation(any(Long.class))).thenReturn(true);

    // when
    Company updatedCompany = companyService.updateCompany(inputCompany);

    // then
    assertTrue(updatedCompany.getExternalAdVerificationEnabled());
    assertTrue(updatedCompany.getFraudDetectionJavascriptEnabled());
    ArgumentCaptor<Company> companyArgumentCaptor = ArgumentCaptor.forClass(Company.class);
    verify(companyRepository).save(companyArgumentCaptor.capture());
    Company companyToUpdate = companyArgumentCaptor.getValue();
    assertEquals(cpiConversionNoticeUrl, companyToUpdate.getCpiConversionNoticeUrl());
  }

  @Test
  void shouldUpdateCompanyOfNonSellerTypeWithExternalAdVerificationEnabledFlag() {
    // given
    Company inputCompany = createCompany(CompanyType.BUYER);
    Company dbCompany = createCompany(CompanyType.BUYER);

    inputCompany.setExternalAdVerificationEnabled(false);
    dbCompany.setExternalAdVerificationEnabled(true);

    when(companyRepository.findById(inputCompany.getPid())).thenReturn(Optional.of(dbCompany));
    when(companyRepository.save(inputCompany)).thenReturn(inputCompany);

    List<BuyerMetaDataForCompanyProjection> buyerMetaDataForCompanyProjections = new ArrayList<>();
    BuyerMetaDataForCompanyProjection buyerMetaData = mock(BuyerMetaDataForCompanyProjection.class);
    buyerMetaDataForCompanyProjections.add(buyerMetaData);
    when(companyRepository.findBuyerMetaDataForCompanyProjectionsByCompanyPid(
            inputCompany.getPid()))
        .thenReturn(buyerMetaDataForCompanyProjections);

    when(userContext.doSameOrNexageAffiliation(any(Long.class))).thenReturn(true);

    // when
    Company updatedCompany = companyService.updateCompany(inputCompany);

    // then
    assertFalse(updatedCompany.getExternalAdVerificationEnabled());
  }

  @Test
  void shouldUpdateSellerWithRevenueShareAndPropagateToSites() {
    // given
    when(userContext.getType()).thenReturn(CompanyType.NEXAGE);
    when(userContext.doSameOrNexageAffiliation(any(Long.class))).thenReturn(true);

    Company dbCompany = createCompany(CompanyType.SELLER);
    SellerAttributes attributes = new SellerAttributes();
    attributes.setRevenueShare(new BigDecimal("0.5"));
    dbCompany.setSellerAttributes(attributes);

    Company inputCompany = createCompany(CompanyType.SELLER);
    inputCompany.setPid(dbCompany.getPid());
    inputCompany.setName(dbCompany.getName());
    SellerAttributes newAttribs = new SellerAttributes();
    newAttribs.setRevenueShare(new BigDecimal("0.6"));
    inputCompany.setSellerAttributes(newAttribs);
    List<Site> sites = TestObjectsFactory.createSiteDTO(1);
    List<Long> siteIDS = sites.stream().map(Site::getPid).collect(Collectors.toList());
    when(siteRepository.findPidsByCompanyPid(inputCompany.getPid())).thenReturn(siteIDS);
    when(companyRepository.findById(inputCompany.getPid())).thenReturn(Optional.of(dbCompany));
    when(companyRepository.save(inputCompany)).thenReturn(inputCompany);

    List<SellerMetaDataForCompanyProjection> sellerMetaDataForCompanyProjections =
        new ArrayList<>();
    SellerMetaDataForCompanyProjection sellerMetaData =
        mock(SellerMetaDataForCompanyProjection.class);
    sellerMetaDataForCompanyProjections.add(sellerMetaData);
    when(companyRepository.findSellerMetaDataForCompanyProjectionsByCompanyPid(anyLong()))
        .thenReturn(sellerMetaDataForCompanyProjections);

    // when
    companyService.updateCompany(inputCompany);

    // then
    verify(siteRepository).findPidsByCompanyPid(inputCompany.getPid());
    verify(sellerSiteService).updateSiteDealTermsToPubDefault(inputCompany.getPid(), siteIDS);
  }

  @Test
  void shouldThrowExceptionWhenUpdatingSellerCompanyWithoutPermission() {
    // given
    when(userContext.getType()).thenReturn(CompanyType.SELLER);
    when(userContext.doSameOrNexageAffiliation(any(Long.class))).thenReturn(true);

    Company sellerCompanyFromDb = createCompany(CompanyType.SELLER);
    SellerAttributes saFromDb = new SellerAttributes();
    saFromDb.setRevenueShare(BigDecimal.valueOf(0.4));
    sellerCompanyFromDb.setSellerAttributes(saFromDb);

    Company sellerCompanyInput = createCompany(CompanyType.SELLER);
    sellerCompanyInput.setPid(sellerCompanyFromDb.getPid());
    SellerAttributes sellerAttributes = new SellerAttributes();
    sellerAttributes.setRevenueShare(BigDecimal.valueOf(0.3));
    sellerCompanyInput.setSellerAttributes(sellerAttributes);

    when(companyRepository.findById(any())).thenReturn(Optional.of(sellerCompanyFromDb));
    when(revenueShareUpdateValidator.isRevenueShareUpdated(any(Company.class), any(Company.class)))
        .thenReturn(true);
    when(userContext.isOcManagerYieldNexage()).thenReturn(false);

    // when & then
    var exception =
        assertThrows(
            GenevaSecurityException.class, () -> companyService.updateCompany(sellerCompanyInput));
    assertEquals(SecurityErrorCodes.SECURITY_NOT_AUTHORIZED, exception.getErrorCode());
  }

  @Test
  void shouldUpdateSellerCompanyWhenPermissionIsGranted() {
    // given
    when(userContext.getType()).thenReturn(CompanyType.SELLER);
    when(userContext.doSameOrNexageAffiliation(any(Long.class))).thenReturn(true);

    Company sellerCompanyFromDb = createCompany(CompanyType.SELLER);
    SellerAttributes saFromDb = new SellerAttributes();
    saFromDb.setRevenueShare(BigDecimal.valueOf(0.4));
    sellerCompanyFromDb.setSellerAttributes(saFromDb);

    Company sellerCompanyInput = createCompany(CompanyType.SELLER);
    sellerCompanyInput.setPid(sellerCompanyFromDb.getPid());
    SellerAttributes sellerAttributes = new SellerAttributes();
    sellerAttributes.setRevenueShare(BigDecimal.valueOf(0.3));
    sellerCompanyInput.setSellerAttributes(sellerAttributes);

    when(companyRepository.findById(any())).thenReturn(Optional.of(sellerCompanyFromDb));
    when(revenueShareUpdateValidator.isRevenueShareUpdated(any(Company.class), any(Company.class)))
        .thenReturn(true);
    when(userContext.isOcManagerYieldNexage()).thenReturn(true);

    when(companyRepository.save(sellerCompanyInput)).thenReturn(sellerCompanyInput);

    List<SellerMetaDataForCompanyProjection> sellerMetaDataForCompanyProjections =
        new ArrayList<>();
    SellerMetaDataForCompanyProjection sellerMetaData =
        mock(SellerMetaDataForCompanyProjection.class);
    sellerMetaDataForCompanyProjections.add(sellerMetaData);
    when(companyRepository.findSellerMetaDataForCompanyProjectionsByCompanyPid(anyLong()))
        .thenReturn(sellerMetaDataForCompanyProjections);

    // when
    Company updated = companyService.updateCompany(sellerCompanyInput);

    // then
    assertEquals(sellerCompanyInput.getPid(), updated.getPid());
  }

  @Test
  void shouldSaveNewAdvertiserWhenUpdatingSellerCompanyWithAdServingEnabled() {
    // given
    Company sellerCompanyFromDb = createCompany(CompanyType.SELLER);
    Company sellerCompanyInput = createCompany(CompanyType.SELLER);
    sellerCompanyInput.setPid(sellerCompanyFromDb.getPid());
    sellerCompanyInput.setAdServingEnabled(true);
    Advertiser advertiser = Advertiser.createHouseAdvertiser(sellerCompanyInput.getPid());

    List<SellerMetaDataForCompanyProjection> sellerMetaDataForCompanyProjections =
        new ArrayList<>();
    SellerMetaDataForCompanyProjection sellerMetaData =
        mock(SellerMetaDataForCompanyProjection.class);
    sellerMetaDataForCompanyProjections.add(sellerMetaData);
    when(companyRepository.findSellerMetaDataForCompanyProjectionsByCompanyPid(anyLong()))
        .thenReturn(sellerMetaDataForCompanyProjections);

    when(userContext.doSameOrNexageAffiliation(any(Long.class))).thenReturn(true);
    when(companyRepository.findById(any())).thenReturn(Optional.of(sellerCompanyFromDb));
    when(companyRepository.save(sellerCompanyInput)).thenReturn(sellerCompanyInput);
    when(advertiserRepository.findAllBySellerIdAndStatusNotDeleted(sellerCompanyInput.getPid()))
        .thenReturn(new ArrayList<>());
    when(advertiserRepository.save(advertiser))
        .thenReturn(Advertiser.createHouseAdvertiser(sellerCompanyInput.getPid()));

    // when
    companyService.updateCompany(sellerCompanyInput);

    // then
    verify(advertiserRepository).findAllBySellerIdAndStatusNotDeleted(sellerCompanyInput.getPid());
    verify(advertiserRepository).save(advertiser);
    verifyNoMoreInteractions(advertiserRepository);
  }

  @Test
  void shouldUpdateSellerAttributesDealRevShareValuesWhenUpdatedWithPermission() {
    // given
    when(userContext.getType()).thenReturn(CompanyType.SELLER);
    when(userContext.doSameOrNexageAffiliation(any(Long.class))).thenReturn(true);

    Company sellerCompanyFromDb = createCompany(CompanyType.SELLER);
    SellerAttributes saFromDb = new SellerAttributes();
    saFromDb.setSspDealRevShare(BigDecimal.valueOf(0.3));
    saFromDb.setJointDealRevShare(BigDecimal.valueOf(0.2));
    saFromDb.setSellerDealRevShare(BigDecimal.valueOf(0.1));
    sellerCompanyFromDb.setSellerAttributes(saFromDb);

    Company sellerCompanyInput = createCompany(CompanyType.SELLER);
    sellerCompanyInput.setPid(sellerCompanyFromDb.getPid());
    SellerAttributes sellerAttributes = new SellerAttributes();
    sellerAttributes.setSspDealRevShare(BigDecimal.valueOf(0.4));
    sellerAttributes.setJointDealRevShare(BigDecimal.valueOf(0.3));
    sellerAttributes.setSellerDealRevShare(BigDecimal.valueOf(0.2));
    sellerCompanyInput.setSellerAttributes(sellerAttributes);

    when(companyRepository.findById(any())).thenReturn(Optional.of(sellerCompanyFromDb));
    when(revenueShareUpdateValidator.isRevenueShareUpdated(any(Company.class), any(Company.class)))
        .thenReturn(false);
    when(revenueShareUpdateValidator.isRevenueShareUpdated(
            any(BigDecimal.class), any(BigDecimal.class), any(), any()))
        .thenReturn(true);
    when(userContext.isOcManagerYieldNexage()).thenReturn(true);

    when(companyRepository.save(sellerCompanyInput)).thenReturn(sellerCompanyInput);

    List<SellerMetaDataForCompanyProjection> sellerMetaDataForCompanyProjections =
        new ArrayList<>();
    SellerMetaDataForCompanyProjection sellerMetaData =
        mock(SellerMetaDataForCompanyProjection.class);
    sellerMetaDataForCompanyProjections.add(sellerMetaData);
    when(companyRepository.findSellerMetaDataForCompanyProjectionsByCompanyPid(anyLong()))
        .thenReturn(sellerMetaDataForCompanyProjections);

    // when
    Company testCompany = companyService.updateCompany(sellerCompanyInput);

    // then
    assertEquals(BigDecimal.valueOf(0.4), testCompany.getSellerAttributes().getSspDealRevShare());
    assertEquals(BigDecimal.valueOf(0.3), testCompany.getSellerAttributes().getJointDealRevShare());
    assertEquals(
        BigDecimal.valueOf(0.2), testCompany.getSellerAttributes().getSellerDealRevShare());
  }

  @Test
  void shouldThrowWhenSellerAttributesSSPDealRevShareUpdatedWithoutPermission() {
    // given
    when(userContext.getType()).thenReturn(CompanyType.SELLER);
    when(userContext.doSameOrNexageAffiliation(any(Long.class))).thenReturn(true);

    Company sellerCompanyFromDb = createCompany(CompanyType.SELLER);
    SellerAttributes saFromDb = new SellerAttributes();
    saFromDb.setSspDealRevShare(BigDecimal.valueOf(0.3));
    sellerCompanyFromDb.setSellerAttributes(saFromDb);

    Company sellerCompanyInput = createCompany(CompanyType.SELLER);
    sellerCompanyInput.setPid(sellerCompanyFromDb.getPid());
    SellerAttributes sellerAttributes = new SellerAttributes();
    sellerAttributes.setSspDealRevShare(BigDecimal.valueOf(0.4));
    sellerCompanyInput.setSellerAttributes(sellerAttributes);

    when(companyRepository.findById(any())).thenReturn(Optional.of(sellerCompanyFromDb));
    when(revenueShareUpdateValidator.isRevenueShareUpdated(any(Company.class), any(Company.class)))
        .thenReturn(false);
    when(revenueShareUpdateValidator.isRevenueShareUpdated(
            any(BigDecimal.class), any(BigDecimal.class), any(), any()))
        .thenReturn(true);
    when(userContext.isOcManagerYieldNexage()).thenReturn(false);

    // when & then
    var exception =
        assertThrows(
            GenevaSecurityException.class, () -> companyService.updateCompany(sellerCompanyInput));
    assertEquals(SecurityErrorCodes.SECURITY_NOT_AUTHORIZED, exception.getErrorCode());
  }

  @Test
  void shouldGetAllCompaniesForNexageUser() {
    // given
    Company nexage = createCompany(CompanyType.NEXAGE);
    User nexageUser = createUser(User.Role.ROLE_USER, nexage);
    loggedUser(nexageUser);

    Company seller = createCompany(CompanyType.SELLER);
    Company buyer = createCompany(CompanyType.BUYER);
    Company seatholder = createCompany(CompanyType.SEATHOLDER);
    when(companyRepository.findByType(CompanyType.SELLER)).thenReturn(singletonList(seller));
    when(companyRepository.findByType(CompanyType.BUYER)).thenReturn(singletonList(buyer));
    when(companyRepository.findByType(CompanyType.SEATHOLDER))
        .thenReturn(singletonList(seatholder));
    when(companyRepository.findByType(CompanyType.NEXAGE)).thenReturn(singletonList(nexage));

    // when
    List<Company> allCompanies = companyService.getAllCompanies();

    // then
    List<Company> expected = asList(seller, buyer, seatholder, nexage);
    expected.sort(Comparator.comparingLong(Company::getPid));
    allCompanies.sort(Comparator.comparingLong(Company::getPid));
    assertEquals(expected, allCompanies);
  }

  @Test
  void shouldGetAllCompaniesByTypeForNotNexageUser() {
    // given
    Company seller = createCompany(CompanyType.SELLER);
    User sellerUser = createUser(User.Role.ROLE_USER, seller);
    loggedUser(sellerUser);
    when(companyRepository.findByPidIn(Set.of(seller.getPid()))).thenReturn(singletonList(seller));

    // when
    List<Company> sellers = companyService.getAllCompanies();

    // then
    assertEquals(sellers.get(0), seller);
    assertEquals(sellers.get(0).getPid(), seller.getPid());
  }

  @Test
  void shouldReturnOnlyNexageCompaniesWhenGettingAllCompaniesForNexageAdmin() {
    // given
    Company nexage1 = createCompany(CompanyType.NEXAGE);
    User nexageUser = createUser(Role.ROLE_ADMIN, nexage1);
    loggedUser(nexageUser);

    List<Company> nexageCompanies = singletonList(nexage1);
    when(userContext.isOcAdminNexage()).thenReturn(true);
    when(companyRepository.findByType(CompanyType.NEXAGE)).thenReturn(nexageCompanies);

    // when
    List<Company> allCompanies = companyService.getAllCompanies();

    // then
    assertEquals(nexageCompanies, allCompanies);
    verify(companyRepository, only()).findByType(CompanyType.NEXAGE);
    verifyNoMoreInteractions(companyRepository);
  }

  @Test
  void shouldFilterSellerSeatUsersWhenDeletingSeatholderCompany() {
    // given
    Company company = createCompany(CompanyType.SEATHOLDER);
    SellerSeat seat = TestObjectsFactory.createSellerSeat(company);
    User regularUser = createUser(Role.ROLE_ADMIN, company);
    User seatUser = createUser(Role.ROLE_ADMIN, company);
    BDRAdvertiser bdrAdvertiser = new BDRAdvertiser();

    company.setSellerSeat(seat);
    company.setSellerSeatPid(seat.getPid());
    seatUser.setSellerSeat(seat);

    long companyPid = company.getPid();

    when(companyRepository.findById(companyPid)).thenReturn(Optional.of(company));
    given(userRepository.findAll(any(Specification.class)))
        .willReturn(List.of(regularUser, seatUser));
    given(bdrInsertionOrderRepository.findAllByAdvertiser_Company_Pid(companyPid))
        .willReturn(List.of());
    given(bdrAdvertiserRepository.findByCompanyPid(companyPid)).willReturn(List.of(bdrAdvertiser));
    given(bdrExchangeCompanyRepository.findByExchangeCompanyPk_Company_Pid(companyPid))
        .willReturn(List.of());

    // when
    companyService.deleteCompany(companyPid);

    // then
    verify(userRepository).delete(regularUser);
    verify(bdrExchangeCompanyRepository).findByExchangeCompanyPk_Company_Pid(companyPid);
    verify(userRepository, never()).delete(seatUser);
    verify(bdrAdvertiserRepository).findByCompanyPid(companyPid);
    verify(bdrAdvertiserRepository).deleteAll(List.of(bdrAdvertiser));
    verifyNoMoreInteractions(bdrAdvertiserRepository);
    verifyNoMoreInteractions(bdrExchangeCompanyRepository);
  }

  @Test
  void shouldDeleteAllBdrExchangeCompanyWhenDeletingCompany() {
    // given
    Company company = createCompany(CompanyType.SEATHOLDER);
    long companyPid = company.getPid();
    BdrExchangeCompany bdrExchangeCompany = new BdrExchangeCompany();

    when(companyRepository.findById(companyPid)).thenReturn(Optional.of(company));
    given(bdrInsertionOrderRepository.findAllByAdvertiser_Company_Pid(companyPid))
        .willReturn(List.of());
    given(bdrExchangeCompanyRepository.findByExchangeCompanyPk_Company_Pid(companyPid))
        .willReturn(List.of(bdrExchangeCompany));

    // when
    companyService.deleteCompany(companyPid);

    // then
    verify(companyRepository).findById(companyPid);
    verify(bdrInsertionOrderRepository).findAllByAdvertiser_Company_Pid(companyPid);
    verify(bdrExchangeCompanyRepository).findByExchangeCompanyPk_Company_Pid(companyPid);
    verify(bdrExchangeCompanyRepository).deleteAll(List.of(bdrExchangeCompany));
    verifyNoMoreInteractions(bdrExchangeCompanyRepository);
  }

  @Test
  void shouldThrowExceptionWhenDeletingSeatholderCompanyWithInsertionOrders() {
    // given
    Company company = createCompany(CompanyType.SEATHOLDER);
    long companyPid = company.getPid();

    BdrInsertionOrder insertionOrder = new BdrInsertionOrder();
    BDRAdvertiser advertiser = new BDRAdvertiser();
    advertiser.setCompany(company);
    insertionOrder.setAdvertiser(advertiser);

    when(companyRepository.findById(companyPid)).thenReturn(Optional.of(company));
    given(bdrInsertionOrderRepository.findAllByAdvertiser_Company_Pid(companyPid))
        .willReturn(List.of(insertionOrder));

    // when
    GenevaAppRuntimeException result =
        assertThrows(
            GenevaAppRuntimeException.class, () -> companyService.deleteCompany(companyPid));

    // then
    assertEquals(ServerErrorCodes.SERVER_SEATHOLDER_DELETE_NOT_ALLOWED, result.getErrorCode());
    verify(bdrInsertionOrderRepository).findAllByAdvertiser_Company_Pid(companyPid);
    verify(companyRepository).findById(companyPid);
  }

  @Test
  void shouldNotFilterSellerSeatUsersWhenDeletingNonSeatholderCompany() {
    // given
    Company company = createCompany(CompanyType.SELLER);
    SellerSeat seat = TestObjectsFactory.createSellerSeat();
    User regularUser = createUser(Role.ROLE_ADMIN, company);
    User seatUser = createUser(Role.ROLE_ADMIN, company);

    seatUser.setSellerSeat(seat);

    long companyPid = company.getPid();

    given(companyRepository.findById(companyPid)).willReturn(Optional.of(company));
    given(userRepository.findAll(any(Specification.class)))
        .willReturn(List.of(regularUser, seatUser));
    given(siteRepository.existsByCompanyPidAndStatusNot(companyPid, Status.DELETED))
        .willReturn(false);
    given(campaignRepository.findBySellerId(companyPid)).willReturn(List.of());
    given(creativeRepository.findAllBySellerId(companyPid)).willReturn(List.of());
    given(advertiserRepository.findAllBySellerId(companyPid)).willReturn(List.of());

    // when
    companyService.deleteCompany(companyPid);

    // then
    verify(userRepository).delete(regularUser);
    verify(userRepository).delete(seatUser);
    verifyNoMoreInteractions(userRepository);
  }

  @Test
  void shouldDeleteCompanyAdvertisersWhenDeletingTheCompanyWhenCompanyTypeIsSeller() {
    // given
    Company company = createCompany(CompanyType.SELLER);
    long companyPid = company.getPid();
    Advertiser advertiser = Advertiser.createHouseAdvertiser(companyPid);

    given(companyRepository.findById(companyPid)).willReturn(Optional.of(company));
    given(userRepository.findAll(any(Specification.class))).willReturn(List.of());
    given(siteRepository.existsByCompanyPidAndStatusNot(companyPid, Status.DELETED))
        .willReturn(false);
    given(campaignRepository.findBySellerId(companyPid)).willReturn(List.of());
    given(creativeRepository.findAllBySellerId(companyPid)).willReturn(List.of());
    given(advertiserRepository.findAllBySellerId(companyPid)).willReturn(List.of(advertiser));

    // when
    companyService.deleteCompany(companyPid);

    // then
    verify(companyRepository).findById(companyPid);
    verify(companyRepository).delete(company);
    verify(userRepository).findAll(any(Specification.class));
    verify(siteRepository).existsByCompanyPidAndStatusNot(companyPid, Status.DELETED);
    verify(advertiserRepository).findAllBySellerId(anyLong());
    verify(advertiserRepository).deleteAll(List.of(advertiser));
    verifyNoMoreInteractions(companyRepository);
    verifyNoMoreInteractions(userRepository);
    verifyNoMoreInteractions(siteRepository);
    verifyNoMoreInteractions(advertiserRepository);
  }

  @Test
  void shouldThrowDeleteNotAllowedExceptionWhenDeleteSellerCompanyWithSites() {
    // given
    Company company = createCompany(CompanyType.SELLER);
    long companyPid = company.getPid();
    given(companyRepository.findById(companyPid)).willReturn(Optional.of(company));
    given(siteRepository.existsByCompanyPidAndStatusNot(companyPid, Status.DELETED))
        .willReturn(true);

    // when & then
    GenevaAppRuntimeException exception =
        assertThrows(
            GenevaAppRuntimeException.class, () -> companyService.deleteCompany(companyPid));
    assertEquals(ServerErrorCodes.SERVER_SELLER_DELETE_NOT_ALLOWED, exception.getErrorCode());
  }

  @Test
  void shouldDeleteCompanyCreativesWhenDeletingTheCompanyWhenCompanyTypeIsSeller() {
    // given
    Company company = createCompany(CompanyType.SELLER);
    long companyPid = company.getPid();
    Creative creative = new Creative();
    creative.setSellerId(companyPid);
    Campaign campaign = new Campaign();
    campaign.setSellerId(companyPid);

    given(companyRepository.findById(companyPid)).willReturn(Optional.of(company));
    given(userRepository.findAll(any(Specification.class))).willReturn(List.of());
    given(siteRepository.existsByCompanyPidAndStatusNot(companyPid, Status.DELETED))
        .willReturn(false);
    given(creativeRepository.findAllBySellerId(companyPid)).willReturn(List.of(creative));

    // when
    companyService.deleteCompany(companyPid);

    // then
    verify(companyRepository).findById(companyPid);
    verify(companyRepository).delete(company);
    verify(userRepository).findAll(any(Specification.class));
    verify(siteRepository).existsByCompanyPidAndStatusNot(companyPid, Status.DELETED);
    verify(creativeRepository).findAllBySellerId(companyPid);
    verify(creativeRepository).deleteAll(List.of(creative));
    verifyNoMoreInteractions(companyRepository);
    verifyNoMoreInteractions(userRepository);
    verifyNoMoreInteractions(siteRepository);
    verifyNoMoreInteractions(creativeRepository);
  }

  @Test
  void shouldDeleteCompanyCampaignsWhenDeletingTheCompanyWhenCompanyTypeIsSeller() {
    // given
    Company company = createCompany(CompanyType.SELLER);
    long companyPid = company.getPid();
    Campaign campaign = new Campaign();
    campaign.setSellerId(companyPid);

    given(companyRepository.findById(companyPid)).willReturn(Optional.of(company));
    given(userRepository.findAll(any(Specification.class))).willReturn(List.of());
    given(siteRepository.existsByCompanyPidAndStatusNot(companyPid, Status.DELETED))
        .willReturn(false);
    given(campaignRepository.findBySellerId(companyPid)).willReturn(List.of(campaign));

    // when
    companyService.deleteCompany(companyPid);

    // then
    verify(companyRepository).findById(companyPid);
    verify(companyRepository).delete(company);
    verify(userRepository).findAll(any(Specification.class));
    verify(siteRepository).existsByCompanyPidAndStatusNot(companyPid, Status.DELETED);
    verify(campaignRepository).findBySellerId(companyPid);
    verify(campaignRepository).deleteAll(List.of(campaign));
    verifyNoMoreInteractions(companyRepository);
    verifyNoMoreInteractions(userRepository);
    verifyNoMoreInteractions(siteRepository);
    verifyNoMoreInteractions(campaignRepository);
  }

  @Test
  void shouldThrowBuyerDeleteNotAllowedExceptionWhenActiveBuyerCompanyPreviouslyMadeBids() {
    // given
    Company company = createCompany(CompanyType.BUYER);
    long companyPid = company.getPid();

    BuyerKeyMetrics buyerKeyMetrics = new BuyerKeyMetrics(1, 1, 1, 1, 1, BigDecimal.valueOf(1));

    given(companyRepository.findById(companyPid)).willReturn(Optional.of(company));
    given(dashboardDao.getBuyerMetrics(any(), any(), eq(companyPid))).willReturn(buyerKeyMetrics);

    // when / then
    GenevaAppRuntimeException exception =
        assertThrows(
            GenevaAppRuntimeException.class, () -> companyService.deleteCompany(companyPid));
    assertEquals(ServerErrorCodes.SERVER_BUYER_DELETE_NOT_ALLOWED, exception.getErrorCode());
  }

  @Test
  void shouldCreateSellerCompanyWithSmartQPSEnabledTrueWithPermissionRole() {
    // given
    Company sellerCompany = TestObjectsFactory.createCompany(CompanyType.NEXAGE);
    SellerAttributes sellerAttributes = new SellerAttributes();
    sellerAttributes.setSmartQPSEnabled(true);
    sellerCompany.setSellerAttributes(sellerAttributes);

    when(companyRepository.save(any())).thenReturn(sellerCompany);
    when(userContext.canEditSmartExchange()).thenReturn(true);

    // when
    Company testCompany = companyService.createCompany(sellerCompany);

    // then
    assertEquals(true, testCompany.getSellerAttributes().getSmartQPSEnabled());
  }

  @Test
  void shouldCreateSellerCompanyWithSmartQPSEnabledFalse() {
    // given
    Company sellerCompany = TestObjectsFactory.createCompany(CompanyType.NEXAGE);
    SellerAttributes sellerAttributes = new SellerAttributes();
    sellerAttributes.setSmartQPSEnabled(false);
    sellerCompany.setSellerAttributes(sellerAttributes);

    when(companyRepository.save(any())).thenReturn(sellerCompany);

    // when
    Company testCompany = companyService.createCompany(sellerCompany);

    // then
    assertEquals(false, testCompany.getSellerAttributes().getSmartQPSEnabled());
  }

  @Test
  void shouldThrowExceptionWhenCreatingSellerCompanyWithSmartQPSEnabledTrueWithoutPermissionRole() {
    // given
    Company sellerCompany = TestObjectsFactory.createCompany(CompanyType.NEXAGE);
    SellerAttributes sellerAttributes = new SellerAttributes();
    sellerAttributes.setSmartQPSEnabled(true);
    sellerCompany.setSellerAttributes(sellerAttributes);

    User user = createUser(Role.ROLE_MANAGER_YIELD, sellerCompany);
    loggedUser(user);

    // when & then
    var exception =
        assertThrows(
            GenevaSecurityException.class, () -> companyService.createCompany(sellerCompany));
    assertEquals(SecurityErrorCodes.SECURITY_NOT_AUTHORIZED, exception.getErrorCode());
  }

  @Test
  void shouldUpdateSellerCompanyWithSmartQPSEnabledFieldWithPermissionRole() {
    // given
    Company companyFromDb = createCompany(CompanyType.NEXAGE);
    SellerAttributes sellerAttributesFromDb = new SellerAttributes();
    sellerAttributesFromDb.setSmartQPSEnabled(true);
    companyFromDb.setSellerAttributes(sellerAttributesFromDb);

    Company inputCompany = createCompany(CompanyType.SELLER);
    inputCompany.setPid(companyFromDb.getPid());
    SellerAttributes sellerAttributes = new SellerAttributes();
    sellerAttributes.setSmartQPSEnabled(false);
    inputCompany.setSellerAttributes(sellerAttributes);

    when(userContext.getType()).thenReturn(CompanyType.SELLER);
    when(userContext.doSameOrNexageAffiliation(any(Long.class))).thenReturn(true);
    when(companyRepository.findById(any())).thenReturn(Optional.of(companyFromDb));
    when(companyRepository.save(inputCompany)).thenReturn(inputCompany);
    when(userContext.canEditSmartExchange()).thenReturn(true);

    List<SellerMetaDataForCompanyProjection> sellerMetaDataForCompanyProjections =
        new ArrayList<>();
    SellerMetaDataForCompanyProjection sellerMetaData =
        mock(SellerMetaDataForCompanyProjection.class);
    sellerMetaDataForCompanyProjections.add(sellerMetaData);
    when(companyRepository.findSellerMetaDataForCompanyProjectionsByCompanyPid(anyLong()))
        .thenReturn(sellerMetaDataForCompanyProjections);

    // when
    Company testCompany = companyService.updateCompany(inputCompany);

    // then
    assertEquals(false, testCompany.getSellerAttributes().getSmartQPSEnabled());
  }

  @Test
  void
      shouldThrowExceptionWhenUpdatingSellerCompanyWithSmartQPSEnabledFieldWithoutPermissionRole() {
    // given
    Company companyFromDb = createCompany(CompanyType.NEXAGE);
    SellerAttributes sellerAttributesFromDb = new SellerAttributes();
    sellerAttributesFromDb.setSmartQPSEnabled(true);
    companyFromDb.setSellerAttributes(sellerAttributesFromDb);

    Company inputCompany = createCompany(CompanyType.SELLER);
    inputCompany.setPid(companyFromDb.getPid());
    SellerAttributes sellerAttributes = new SellerAttributes();
    sellerAttributes.setSmartQPSEnabled(false);
    inputCompany.setSellerAttributes(sellerAttributes);

    User user = createUser(Role.ROLE_MANAGER_YIELD, companyFromDb);
    loggedUser(user);

    when(userContext.getType()).thenReturn(CompanyType.SELLER);
    when(userContext.doSameOrNexageAffiliation(any(Long.class))).thenReturn(true);
    when(companyRepository.findById(any())).thenReturn(Optional.of(companyFromDb));

    // when & then
    var exception =
        assertThrows(
            GenevaSecurityException.class, () -> companyService.updateCompany(inputCompany));
    assertEquals(SecurityErrorCodes.SECURITY_NOT_AUTHORIZED, exception.getErrorCode());
  }

  @Test
  void
      shouldUpdateCompanyWithNoSellerAttributesWithNewSellerAttributesAndSmartQPSEnabledFieldWithPermissionRole() {
    // given
    Company companyFromDb = createCompany(CompanyType.NEXAGE);

    Company inputCompany = createCompany(CompanyType.SELLER);
    inputCompany.setPid(companyFromDb.getPid());
    SellerAttributes sellerAttributes = new SellerAttributes();
    sellerAttributes.setSmartQPSEnabled(false);
    inputCompany.setSellerAttributes(sellerAttributes);

    when(userContext.doSameOrNexageAffiliation(any(Long.class))).thenReturn(true);
    when(companyRepository.findById(any())).thenReturn(Optional.of(companyFromDb));
    when(companyRepository.save(inputCompany)).thenReturn(inputCompany);
    when(sellerAttributesRepository.save(any())).thenReturn(sellerAttributes);

    List<SellerMetaDataForCompanyProjection> sellerMetaDataForCompanyProjections =
        new ArrayList<>();
    SellerMetaDataForCompanyProjection sellerMetaData =
        mock(SellerMetaDataForCompanyProjection.class);
    sellerMetaDataForCompanyProjections.add(sellerMetaData);
    when(companyRepository.findSellerMetaDataForCompanyProjectionsByCompanyPid(anyLong()))
        .thenReturn(sellerMetaDataForCompanyProjections);

    // when
    Company testCompany = companyService.updateCompany(inputCompany);

    // then
    assertEquals(false, testCompany.getSellerAttributes().getSmartQPSEnabled());
  }

  @Test
  void
      shouldThrowExceptionWhenUpdatingCompanyWithNoSellerAttributesWithNewSellerAttributesAndSmartQPSEnabledFieldWithPermissionRole() {
    // given
    Company companyFromDb = createCompany(CompanyType.NEXAGE);
    Company inputCompany = createCompany(CompanyType.SELLER);
    inputCompany.setPid(companyFromDb.getPid());
    SellerAttributes sellerAttributes = new SellerAttributes();
    sellerAttributes.setSmartQPSEnabled(true);
    inputCompany.setSellerAttributes(sellerAttributes);

    User user = createUser(Role.ROLE_MANAGER_YIELD, companyFromDb);
    loggedUser(user);

    when(companyRepository.findById(any())).thenReturn(Optional.of(companyFromDb));

    // when & then
    var exception =
        assertThrows(
            GenevaSecurityException.class, () -> companyService.updateCompany(inputCompany));
    assertEquals(SecurityErrorCodes.SECURITY_NOT_AUTHORIZED, exception.getErrorCode());
  }

  @Test
  void shouldThrowExceptionWhenCreatingCompanyWithDynamicFloorEnabledTrueWithoutPermissionRole() {
    // given
    Company sellerCompany = TestObjectsFactory.createCompany(CompanyType.NEXAGE);
    SellerAttributes sellerAttributes = new SellerAttributes();
    sellerAttributes.setDynamicFloorEnabled(true);
    sellerCompany.setSellerAttributes(sellerAttributes);

    User user = createUser(Role.ROLE_ADMIN, sellerCompany);
    loggedUser(user);

    // when & then
    var exception =
        assertThrows(
            GenevaSecurityException.class, () -> companyService.createCompany(sellerCompany));
    assertEquals(SecurityErrorCodes.SECURITY_NOT_AUTHORIZED, exception.getErrorCode());
  }

  @Test
  void shouldThrowExceptionWhenCreatingCompanyWithDynamicFloorEnabledTrueWithPermissionRole() {
    // given
    Company sellerCompany = TestObjectsFactory.createCompany(CompanyType.NEXAGE);
    SellerAttributes sellerAttributes = new SellerAttributes();
    sellerAttributes.setDynamicFloorEnabled(true);
    sellerCompany.setSellerAttributes(sellerAttributes);

    User user = createUser(Role.ROLE_MANAGER_SMARTEX, sellerCompany);
    loggedUser(user);

    when(companyRepository.save(any())).thenReturn(sellerCompany);
    when(userContext.canEditSmartExchange()).thenReturn(true);

    // when
    Company testCompany = companyService.createCompany(sellerCompany);

    // then
    assertEquals(true, testCompany.getSellerAttributes().getDynamicFloorEnabled());
  }

  @Test
  void shouldUpdateCompanyWithDynamicFloorEnabledFieldWithPermissionRole() {
    // given
    Company companyFromDb = createCompany(CompanyType.NEXAGE);
    SellerAttributes sellerAttributesFromDb = new SellerAttributes();
    sellerAttributesFromDb.setDynamicFloorEnabled(false);
    companyFromDb.setSellerAttributes(sellerAttributesFromDb);

    Company inputCompany = createCompany(CompanyType.SELLER);
    inputCompany.setPid(companyFromDb.getPid());
    SellerAttributes sellerAttributes = new SellerAttributes();
    sellerAttributes.setDynamicFloorEnabled(true);
    inputCompany.setSellerAttributes(sellerAttributes);

    when(userContext.doSameOrNexageAffiliation(any(Long.class))).thenReturn(true);
    when(userContext.canEditSmartExchange()).thenReturn(true);
    when(companyRepository.findById(any())).thenReturn(Optional.of(companyFromDb));
    when(companyRepository.save(inputCompany)).thenReturn(inputCompany);

    List<SellerMetaDataForCompanyProjection> sellerMetaDataForCompanyProjections =
        new ArrayList<>();
    SellerMetaDataForCompanyProjection sellerMetaData =
        mock(SellerMetaDataForCompanyProjection.class);
    sellerMetaDataForCompanyProjections.add(sellerMetaData);
    when(companyRepository.findSellerMetaDataForCompanyProjectionsByCompanyPid(anyLong()))
        .thenReturn(sellerMetaDataForCompanyProjections);

    // when
    Company testCompany = companyService.updateCompany(inputCompany);

    // then
    assertEquals(true, testCompany.getSellerAttributes().getDynamicFloorEnabled());
  }

  @Test
  void shouldThrowExceptionWhenUpdatingCompanyWithDynamicFloorEnabledFieldWithoutPermissionRole() {
    // given
    Company companyFromDb = createCompany(CompanyType.NEXAGE);
    SellerAttributes sellerAttributesFromDb = new SellerAttributes();
    sellerAttributesFromDb.setDynamicFloorEnabled(true);
    companyFromDb.setSellerAttributes(sellerAttributesFromDb);

    Company inputCompany = createCompany(CompanyType.SELLER);
    inputCompany.setPid(companyFromDb.getPid());
    SellerAttributes sellerAttributes = new SellerAttributes();
    sellerAttributes.setDynamicFloorEnabled(false);
    inputCompany.setSellerAttributes(sellerAttributes);

    User user = createUser(Role.ROLE_ADMIN, companyFromDb);
    loggedUser(user);

    when(companyRepository.findById(any())).thenReturn(Optional.of(companyFromDb));

    // when & then
    var exception =
        assertThrows(
            GenevaSecurityException.class, () -> companyService.updateCompany(inputCompany));
    assertEquals(SecurityErrorCodes.SECURITY_NOT_AUTHORIZED, exception.getErrorCode());
  }

  @Test
  void
      shouldThrowExceptionWhenUpdatingCompanyWithNoSellerAttributesWithNewSellerAttributesAndDynamicFloorEnabledFieldWithPermissionRole() {
    // given
    Company companyFromDb = createCompany(CompanyType.NEXAGE);

    Company inputCompany = createCompany(CompanyType.SELLER);
    inputCompany.setPid(companyFromDb.getPid());
    SellerAttributes sellerAttributes = new SellerAttributes();
    sellerAttributes.setDynamicFloorEnabled(true);
    inputCompany.setSellerAttributes(sellerAttributes);

    User user = createUser(Role.ROLE_MANAGER_YIELD, companyFromDb);
    loggedUser(user);

    when(companyRepository.findById(any())).thenReturn(Optional.of(companyFromDb));

    // when & then
    var exception =
        assertThrows(
            GenevaSecurityException.class, () -> companyService.updateCompany(inputCompany));
    assertEquals(SecurityErrorCodes.SECURITY_NOT_AUTHORIZED, exception.getErrorCode());
  }

  @Test
  void shouldCreateCompanyWhenCreatingSellerAttributesWithDealFeeRevenues() {
    // given
    Company sellerCompany = createCompany(CompanyType.SELLER);
    SellerAttributes sellerAttributes = new SellerAttributes();
    sellerAttributes.setSspDealRevShare(new BigDecimal("0.534"));
    sellerAttributes.setJointDealRevShare(new BigDecimal("0.2"));
    sellerAttributes.setSellerDealRevShare(new BigDecimal(1));
    sellerCompany.setSellerAttributes(sellerAttributes);

    when(companyRepository.save(any())).thenReturn(sellerCompany);

    // when
    var createdCompany = companyService.createCompany(sellerCompany);

    // then
    verifyPublish(createdCompany);
  }

  @Test
  void shouldUpdateSellerAttributesWithRawResponseWhenUpdatingCompany() {
    Company sellerCompany = createCompany(CompanyType.SELLER);
    SellerAttributes sellerAttributes = new SellerAttributes();

    sellerAttributes.setRawResponse(true);
    sellerCompany.setSellerAttributes(sellerAttributes);

    when(companyRepository.save(any())).thenAnswer(returnsFirstArg());

    var createdCompany = companyService.createCompany(sellerCompany);

    verifyPublish(createdCompany);
    assertTrue(createdCompany.getSellerAttributes().isRawResponse());
  }

  @Test
  void shouldUpdateHumanSamplingRatesSellerAttributesWhenNonNullAreGiven() {
    // given
    Company sellerCompanyInDb = createCompany(CompanyType.SELLER);
    sellerCompanyInDb.setSellerAttributes(new SellerAttributes());

    Company sellerCompany = new Company();
    sellerCompany.setType(sellerCompanyInDb.getType());
    sellerCompany.setPid(sellerCompanyInDb.getPid());
    sellerCompany.setId(sellerCompanyInDb.getId());
    sellerCompany.setStatus(sellerCompanyInDb.getStatus());
    sellerCompany.setName(sellerCompanyInDb.getName());
    sellerCompany.setWebsite(sellerCompanyInDb.getWebsite());
    SellerAttributes sellerAttributes = new SellerAttributes();
    sellerAttributes.setHumanPrebidSampleRate(10);
    sellerAttributes.setHumanPostbidSampleRate(90);
    sellerCompany.setSellerAttributes(sellerAttributes);

    when(userContext.doSameOrNexageAffiliation(sellerCompany.getPid())).thenReturn(true);
    when(companyRepository.findById(sellerCompanyInDb.getPid()))
        .thenReturn(Optional.of(sellerCompanyInDb));
    when(companyRepository.save(any())).thenAnswer(returnsFirstArg());

    // when
    var updatedCompany = companyService.updateCompany(sellerCompany);

    // then
    assertEquals(
        sellerAttributes.getHumanPrebidSampleRate(),
        updatedCompany.getSellerAttributes().getHumanPrebidSampleRate());
    assertEquals(
        sellerAttributes.getHumanPostbidSampleRate(),
        updatedCompany.getSellerAttributes().getHumanPostbidSampleRate());
  }

  @Test
  void shouldNotUpdateHumanSamplingRatesSellerAttributesWhenNullAreGiven() {
    // given
    Company sellerCompanyInDb = createCompany(CompanyType.SELLER);
    SellerAttributes sellerAttributesInDb = new SellerAttributes();
    sellerAttributesInDb.setHumanPrebidSampleRate(20);
    sellerAttributesInDb.setHumanPostbidSampleRate(80);
    sellerCompanyInDb.setSellerAttributes(sellerAttributesInDb);

    Company sellerCompany = new Company();
    sellerCompany.setType(sellerCompanyInDb.getType());
    sellerCompany.setName(sellerCompanyInDb.getName());
    sellerCompany.setWebsite(sellerCompanyInDb.getWebsite());
    sellerCompany.setPid(sellerCompanyInDb.getPid());
    sellerCompany.setId(sellerCompanyInDb.getId());
    sellerCompany.setStatus(sellerCompanyInDb.getStatus());
    SellerAttributes sellerAttributes = new SellerAttributes();
    sellerAttributes.setHumanPrebidSampleRate(null);
    sellerAttributes.setHumanPostbidSampleRate(null);
    sellerCompany.setSellerAttributes(sellerAttributes);

    when(userContext.doSameOrNexageAffiliation(any(Long.class))).thenReturn(true);
    when(companyRepository.findById(sellerCompanyInDb.getPid()))
        .thenReturn(Optional.of(sellerCompanyInDb));
    when(companyRepository.save(any())).thenAnswer(returnsFirstArg());

    // when
    var updatedCompany = companyService.updateCompany(sellerCompany);

    // then
    assertEquals(
        sellerAttributesInDb.getHumanPrebidSampleRate(),
        updatedCompany.getSellerAttributes().getHumanPrebidSampleRate());
    assertEquals(
        sellerAttributesInDb.getHumanPostbidSampleRate(),
        updatedCompany.getSellerAttributes().getHumanPostbidSampleRate());
  }

  @Test
  void shouldAddContact() {
    // given
    Company company = new Company();
    User user = TestObjectsFactory.createUser(Role.ROLE_ADMIN, company);
    company.setContactUserPid(user.getPid());
    given(userRepository.findById(user.getPid())).willReturn(Optional.of(user));

    // when
    companyService.addContact(company, user.getPid());

    // then
    assertEquals(user, company.getContact());
  }

  @Test
  void shouldThrowGenevaValidationExceptionWhenAddContactWithNotExistingUser() {
    // given
    long userPid = 1L;
    Company company = new Company();
    company.setContactUserPid(userPid);
    given(userRepository.findById(userPid)).willReturn(Optional.empty());

    // when
    GenevaValidationException exception =
        assertThrows(
            GenevaValidationException.class, () -> companyService.addContact(company, userPid));

    // then
    assertEquals(ServerErrorCodes.SERVER_USER_NOT_FOUND, exception.getErrorCode());
  }

  @Test
  void shouldReturnCorrectNumberOfAlterReserveUpdatesWhenPFOEnabled() {
    // given
    var profileOff = new RTBProfile();
    var profileOnlyIfHigher = new RTBProfile();
    profileOff.setAlterReserve(AlterReserve.OFF);
    profileOnlyIfHigher.setAlterReserve(AlterReserve.ONLY_IF_HIGHER);

    var profiles = List.of(profileOff, profileOnlyIfHigher, profileOnlyIfHigher);

    given(rtbProfileRepository.findBySitePidIn(anySet())).willReturn(profiles);
    given(rtbProfileRepository.saveAll(anyList())).willReturn(List.of(new RTBProfile()));

    // when
    var updateCount = companyService.togglePfo(1L, true);

    // then
    assertEquals(1, updateCount);
  }

  @Test
  void shouldReturnCorrectNumberOfAlterReserveUpdatesWhenPFONotEnabled() {
    // given
    var profileOff = new RTBProfile();
    var profileOnlyIfHigher = new RTBProfile();
    profileOff.setAlterReserve(AlterReserve.OFF);
    profileOnlyIfHigher.setAlterReserve(AlterReserve.ONLY_IF_HIGHER);

    var profiles = List.of(profileOff, profileOnlyIfHigher, profileOnlyIfHigher);

    given(rtbProfileRepository.findBySitePidIn(anySet())).willReturn(profiles);
    given(rtbProfileRepository.saveAll(anyList()))
        .willReturn(List.of(new RTBProfile(), new RTBProfile()));

    // when
    var updateCount = companyService.togglePfo(1L, false);

    // then
    assertEquals(2, updateCount);
  }

  private void verifyPublish(Company company) {
    ArgumentCaptor<SyncEvent<Company>> argumentCaptor = ArgumentCaptor.forClass(SyncEvent.class);
    verify(companySyncProducer).publishEvent(argumentCaptor.capture());

    assertEquals(SyncEvent.Status.CREATE, argumentCaptor.getValue().getStatus());

    var publishedCompany = argumentCaptor.getValue().getData();
    assertEquals(company.getPid(), publishedCompany.getPid());
    assertEquals(company.getName(), publishedCompany.getName());
  }

  private void loggedUser(User user) {
    SpringUserDetails loggedUserDetails = mock(SpringUserDetails.class);
    lenient().when(loggedUserDetails.getPid()).thenReturn(user.getPid());
    lenient().when(loggedUserDetails.getUsername()).thenReturn(user.getUsername());
    lenient().when(loggedUserDetails.getPassword()).thenReturn(user.getPassword());
    lenient().when(loggedUserDetails.getRole()).thenReturn(user.getRole());
    lenient()
        .when(loggedUserDetails.getCompanyPids())
        .thenReturn(user.getCompanies().stream().map(Company::getPid).collect(toSet()));
    if (loggedUserDetails.getCompanyPids().size() == 1) {
      lenient().when(loggedUserDetails.getType()).thenReturn(user.getCompanyType());
    } else {
      lenient().when(loggedUserDetails.getType()).thenReturn(user.getCompanyType());
    }
    lenient().when(loggedUserDetails.getRole()).thenReturn(user.getRole());
    lenient().when(loggedUserDetails.isEnabled()).thenReturn(user.isEnabled());
    Collection<GrantedAuthority> auth = user.getAuthorities();
    lenient().doReturn(auth).when(loggedUserDetails).getAuthorities();
    var entitlements = EntitlementsTestUtil.buildOneCentralEntitlements(user);
    lenient().when(loggedUserDetails.getEntitlements()).thenReturn(entitlements);
    reset(userContext);
    lenient().when(userContext.getCurrentUser()).thenReturn(loggedUserDetails);
    lenient().when(userContext.getCompanyPids()).thenCallRealMethod();
    lenient().when(userContext.isNexageUser()).thenCallRealMethod();
    lenient().when(userContext.hasEntitlements(anyString(), anyString())).thenCallRealMethod();
    lenient()
        .when(userContext.hasEntitlements(anyString(), anyString(), anyString()))
        .thenCallRealMethod();
    lenient()
        .when(userContext.hasEntitlements(anyString(), anyString(), anyString(), anyString()))
        .thenCallRealMethod();
    lenient().when(userContext.isOcUserNexage()).thenCallRealMethod();
  }

  @Test
  void shouldUpdateCompanyWithSeatHolderMetadata() {
    // given
    lenient().when(userContext.getType()).thenReturn(CompanyType.SELLER);
    when(userContext.doSameOrNexageAffiliation(any(Long.class))).thenReturn(true);
    Company inputCompany = createCompany(CompanyType.SEATHOLDER);
    Company dbCompany = createCompany(CompanyType.SEATHOLDER);

    dbCompany.setExternalAdVerificationEnabled(true);
    dbCompany.setFraudDetectionJavascriptEnabled(true);

    inputCompany.setHasHeaderBiddingSites(null);
    inputCompany.setAdsourceNames(null);
    inputCompany.setExternalDataProviderNames(null);
    inputCompany.setActiveIOs(1);
    inputCompany.setHasHeaderBiddingSites(null);

    when(companyRepository.findById(inputCompany.getPid())).thenReturn(Optional.of(dbCompany));
    when(companyRepository.save(inputCompany)).thenReturn(inputCompany);
    when(companyRepository.findSeatHolderMetadataByCompanyPid(inputCompany.getPid()))
        .thenReturn(mock(SeatHolderMetadataDTO.class));
    when(companyRepository.findSeatHolderMetadataByCompanyPid(inputCompany.getPid()).getActiveIOs())
        .thenReturn(ACTIVE_IOS);

    // when
    Company updatedCompany = companyService.updateCompany(inputCompany);

    // then
    assertEquals(CompanyType.SEATHOLDER, updatedCompany.getType());
    assertEquals(ACTIVE_IOS, inputCompany.getActiveIOs());
    assertNull(inputCompany.getHasHeaderBiddingSites());
    assertNull(inputCompany.getAdsourceNames());
    assertNull(inputCompany.getExternalDataProviderNames());
    assertNull(inputCompany.getHasHeaderBiddingSites());
  }

  @Test
  void shouldRemoveSellerSeatFromCompanyWithSellerSeatNone() {
    // given
    Company sellerCompany = createCompany(CompanyType.SELLER);
    SellerAttributes sellerAttributes = setSellerAttributes();
    sellerCompany.setSellerAttributes(sellerAttributes);
    sellerCompany.setRegionId(2L);
    long sellerSeatPid = 123L;
    when(companyRepository.save(any())).thenReturn(sellerCompany);

    SellerSeat sellerSeat = setSellerSeat(sellerSeatPid);
    sellerCompany.setSellerSeat(sellerSeat);

    // when
    Company createdCompany = companyService.createCompany(sellerCompany);

    // then
    assertNull(createdCompany.getSellerSeat());
    assertNull(createdCompany.getSellerSeatPid());
  }

  @Test
  void shouldThrowExceptionWhenUpdatingCompanyWithSellerSeatNone() {
    // given
    Company sellerCompany = createCompany(CompanyType.SELLER);
    SellerAttributes sellerAttributes = setSellerAttributes();
    sellerCompany.setSellerAttributes(sellerAttributes);
    sellerCompany.setRegionId(2L);
    long sellerSeatPid = 123L;
    sellerCompany.setSellerSeatPid(sellerSeatPid);

    SellerSeat sellerSeat = setSellerSeat(sellerSeatPid);
    // seller seat is disabled
    sellerSeat.disable();
    when(sellerSeatRepository.findById(sellerSeatPid)).thenReturn(Optional.of(sellerSeat));

    // when
    GenevaValidationException exception =
        assertThrows(
            GenevaValidationException.class, () -> companyService.createCompany(sellerCompany));

    // then
    assertEquals(ServerErrorCodes.SERVER_SELLER_SEAT_NOT_ENABLED, exception.getErrorCode());
  }

  @Test
  void shouldUpdateUserCompanyRelationForSellerSeatWhenSellerSeatIsNone() {
    // given
    when(userContext.doSameOrNexageAffiliation(any(Long.class))).thenReturn(true);
    when(userContext.canEditSmartExchange()).thenReturn(true);

    Company companyFromDb = createCompany(CompanyType.NEXAGE);
    SellerAttributes sellerAttributesFromDb = new SellerAttributes();
    sellerAttributesFromDb.setDynamicFloorEnabled(true);
    sellerAttributesFromDb.setHbThrottleEnabled(true);
    sellerAttributesFromDb.setHbThrottlePercentage(10);
    companyFromDb.setSellerAttributes(sellerAttributesFromDb);

    Company sellerCompany = createCompany(CompanyType.SELLER);
    sellerCompany.setPid(companyFromDb.getPid());
    SellerAttributes sellerAttributes = setSellerAttributes();
    sellerAttributesFromDb.setDynamicFloorEnabled(true);
    sellerAttributes.setHbThrottleEnabled(true);
    sellerAttributes.setHbThrottlePercentage(10);
    sellerCompany.setSellerAttributes(sellerAttributes);

    long sellerSeatPid = 123L;

    when(companyRepository.findById(any())).thenReturn(Optional.of(companyFromDb));
    when(companyRepository.save(any())).thenReturn(sellerCompany);

    SellerSeat sellerSeat = setSellerSeat(sellerSeatPid);
    sellerCompany.setSellerSeat(sellerSeat);
    when(sellerSeatRepository.findById(sellerSeatPid)).thenReturn(Optional.of(sellerSeat));
    when(sellerSeatRepository.findById(sellerCompany.getSellerSeat().getPid()))
        .thenReturn(Optional.of(sellerSeat));

    List<SellerMetaDataForCompanyProjection> sellerMetaDataForCompanyProjections =
        new ArrayList<>();
    SellerMetaDataForCompanyProjection sellerMetaData =
        mock(SellerMetaDataForCompanyProjection.class);
    sellerMetaDataForCompanyProjections.add(sellerMetaData);
    when(companyRepository.findSellerMetaDataForCompanyProjectionsByCompanyPid(anyLong()))
        .thenReturn(sellerMetaDataForCompanyProjections);

    User user = createUser();
    List<User> usersFromDb = List.of(user);
    when(userRepository.findAllBySellerSeat_Pid(sellerSeatPid)).thenReturn(usersFromDb);

    // when
    companyService.updateCompany(sellerCompany);

    // then
    usersFromDb.forEach(u -> assertEquals(user.getCompanies().size(), 1));
  }

  @Test
  void
      shouldCreateCompanySellerAttributesWithDefaultHumanSampleRatesObtainedFromConfigIfNullAreGiven() {
    // given
    int defaultPrebidRate = 20;
    int defaultPostbidRate = 80;
    given(companyRepository.save(any())).willAnswer(returnsFirstArg());
    Company company = createCompany(CompanyType.SELLER);
    company.setSellerAttributes(new SellerAttributes());

    // when
    Company createdCompany = companyService.createCompany(company);

    // then
    assertEquals(
        DEFAULT_HUMAN_PREBID_SAMPLE_RATE,
        createdCompany.getSellerAttributes().getHumanPrebidSampleRate());
    assertEquals(
        DEFAULT_HUMAN_POSTBID_SAMPLE_RATE,
        createdCompany.getSellerAttributes().getHumanPostbidSampleRate());
  }

  @Test
  void shouldCreateCompanySellerAttributesWithGivenHumanSampleRates() {
    // given
    int prebidRate = 10;
    int postbidRate = 90;
    given(companyRepository.save(any())).willAnswer(returnsFirstArg());
    Company company = createCompany(CompanyType.SELLER);
    SellerAttributes sellerAttributes = new SellerAttributes();
    sellerAttributes.setHumanPrebidSampleRate(prebidRate);
    sellerAttributes.setHumanPostbidSampleRate(postbidRate);
    company.setSellerAttributes(sellerAttributes);

    // when
    Company createdCompany = companyService.createCompany(company);

    // then
    assertEquals(prebidRate, createdCompany.getSellerAttributes().getHumanPrebidSampleRate());
    assertEquals(postbidRate, createdCompany.getSellerAttributes().getHumanPostbidSampleRate());
  }

  @Test
  void
      shouldSetDefaultHumanSampleRatesWhenUpdatingSellerWithNoSellerAttributesAndNullSampleRatesAreGiven() {
    // given
    int defaultPrebidRate = 20;
    int defaultPostbidRate = 80;
    long sellerPid = 1234L;
    Company companyInDb = createCompany(CompanyType.SELLER);
    companyInDb.setPid(sellerPid);
    given(userContext.doSameOrNexageAffiliation(any(Long.class))).willReturn(true);
    given(companyRepository.save(any())).willAnswer(returnsFirstArg());
    given(companyRepository.findById(sellerPid)).willReturn(Optional.of(companyInDb));
    Company company = new Company();
    company.setPid(companyInDb.getPid());
    company.setType(companyInDb.getType());
    company.setName(companyInDb.getName());
    company.setStatus(companyInDb.getStatus());
    company.setWebsite(companyInDb.getWebsite());
    company.setSellerAttributes(new SellerAttributes());

    // when
    Company updatedCompany = companyService.updateCompany(company);

    // then
    assertEquals(
        DEFAULT_HUMAN_PREBID_SAMPLE_RATE,
        updatedCompany.getSellerAttributes().getHumanPrebidSampleRate());
    assertEquals(
        DEFAULT_HUMAN_POSTBID_SAMPLE_RATE,
        updatedCompany.getSellerAttributes().getHumanPostbidSampleRate());
  }

  @Test
  void shouldThrowUnauthorizedOnGetCompanyWithoutPermission() {
    when(userContext.doSameOrNexageAffiliation(anyLong())).thenReturn(false);
    var exception =
        assertThrows(GenevaSecurityException.class, () -> companyService.getCompany(1L));
    assertEquals(SecurityErrorCodes.SECURITY_NOT_AUTHORIZED, exception.getErrorCode());
  }

  void shouldCreateCompanyWithCustomDealFloorEnabledDefaultValueFalse() {
    // given
    given(companyRepository.save(any())).willAnswer(returnsFirstArg());
    Company company = createCompany(CompanyType.SELLER);
    SellerAttributes sellerAttributes = new SellerAttributes();
    company.setSellerAttributes(sellerAttributes);

    // when
    Company createdCompany = companyService.createCompany(company);

    // then
    assertEquals(false, createdCompany.getSellerAttributes().isCustomDealFloorEnabled());
  }

  @Test
  void shouldCreateCompanyWithGivenCustomDealFloorEnabledValue() {
    // given
    given(companyRepository.save(any())).willAnswer(returnsFirstArg());
    Company company = createCompany(CompanyType.SELLER);
    SellerAttributes sellerAttributes = new SellerAttributes();
    sellerAttributes.setCustomDealFloorEnabled(true);
    company.setSellerAttributes(sellerAttributes);

    // when
    Company createdCompany = companyService.createCompany(company);

    // then
    assertEquals(
        sellerAttributes.isCustomDealFloorEnabled(),
        createdCompany.getSellerAttributes().isCustomDealFloorEnabled());
  }

  @Test
  void
      shouldSetDefaultCustomDealFloorEnabledFlagWhenUpdatingSellerWithNoSellerAttributesAndNoCustomDealFloorEnabledFlagGiven() {
    // given
    long sellerPid = 1234L;
    Company companyInDb = createCompany(CompanyType.SELLER);
    companyInDb.setPid(sellerPid);
    given(userContext.doSameOrNexageAffiliation(any(Long.class))).willReturn(true);
    given(companyRepository.save(any())).willAnswer(returnsFirstArg());
    given(companyRepository.findById(sellerPid)).willReturn(Optional.of(companyInDb));
    Company company = new Company();
    company.setPid(companyInDb.getPid());
    company.setType(companyInDb.getType());
    company.setName(companyInDb.getName());
    company.setStatus(companyInDb.getStatus());
    company.setWebsite(companyInDb.getWebsite());
    company.setSellerAttributes(new SellerAttributes());

    // when
    Company updatedCompany = companyService.updateCompany(company);

    // then
    assertEquals(false, updatedCompany.getSellerAttributes().isCustomDealFloorEnabled());
  }

  @Test
  void shouldGetAllCompaniesByTypeAndNameContainingAString() {
    Company company = createCompanyBuyerType();

    given(companyRepository.findAll(any(Specification.class))).willReturn(List.of(company));

    List<Company> result =
        companyService.getAllCompaniesByType(CompanyType.BUYER, Sets.newHashSet("name"), "foo");

    verify(companyRepository).findAll(any(Specification.class));

    assertEquals(1, result.size());
    assertEquals(123L, result.get(0).getPid());
    assertEquals("foo", result.get(0).getName());
    assertEquals(CompanyType.BUYER, result.get(0).getType());
  }

  @Test
  void shouldThrowWhenSellerAttributesJointDealRevShareUpdatedWithoutPermission() {
    // given
    when(userContext.getType()).thenReturn(CompanyType.SELLER);
    when(userContext.doSameOrNexageAffiliation(any(Long.class))).thenReturn(true);

    Company sellerCompanyFromDb = createCompany(CompanyType.SELLER);
    SellerAttributes saFromDb = new SellerAttributes();
    saFromDb.setJointDealRevShare(BigDecimal.valueOf(0.3));
    sellerCompanyFromDb.setSellerAttributes(saFromDb);

    Company sellerCompanyInput = createCompany(CompanyType.SELLER);
    sellerCompanyInput.setPid(sellerCompanyFromDb.getPid());
    SellerAttributes sellerAttributes = new SellerAttributes();
    sellerAttributes.setJointDealRevShare(BigDecimal.valueOf(0.4));
    sellerCompanyInput.setSellerAttributes(sellerAttributes);

    when(companyRepository.findById(any())).thenReturn(Optional.of(sellerCompanyFromDb));
    when(revenueShareUpdateValidator.isRevenueShareUpdated(any(Company.class), any(Company.class)))
        .thenReturn(false);
    when(revenueShareUpdateValidator.isRevenueShareUpdated(any(), any(), any(), any()))
        .thenReturn(true, true);
    when(userContext.isOcManagerYieldNexage()).thenReturn(true, false);

    // when & then
    var exception =
        assertThrows(
            GenevaSecurityException.class, () -> companyService.updateCompany(sellerCompanyInput));
    assertEquals(SecurityErrorCodes.SECURITY_NOT_AUTHORIZED, exception.getErrorCode());
  }

  @Test
  void shouldThrowWhenSellerAttributesSellerDealRevShareUpdatedWithoutPermission() {
    // given
    when(userContext.getType()).thenReturn(CompanyType.SELLER);
    when(userContext.doSameOrNexageAffiliation(any(Long.class))).thenReturn(true);

    Company sellerCompanyFromDb = createCompany(CompanyType.SELLER);
    SellerAttributes saFromDb = new SellerAttributes();
    saFromDb.setSellerDealRevShare(BigDecimal.valueOf(0.3));
    sellerCompanyFromDb.setSellerAttributes(saFromDb);

    Company sellerCompanyInput = createCompany(CompanyType.SELLER);
    sellerCompanyInput.setPid(sellerCompanyFromDb.getPid());
    SellerAttributes sellerAttributes = new SellerAttributes();
    sellerAttributes.setSellerDealRevShare(BigDecimal.valueOf(0.4));
    sellerCompanyInput.setSellerAttributes(sellerAttributes);

    when(companyRepository.findById(any())).thenReturn(Optional.of(sellerCompanyFromDb));
    when(revenueShareUpdateValidator.isRevenueShareUpdated(any(Company.class), any(Company.class)))
        .thenReturn(false);
    when(revenueShareUpdateValidator.isRevenueShareUpdated(any(), any(), any(), any()))
        .thenReturn(true, true, true);
    when(userContext.isOcManagerYieldNexage()).thenReturn(true, true, false);

    // when & then
    var exception =
        assertThrows(
            GenevaSecurityException.class, () -> companyService.updateCompany(sellerCompanyInput));
    assertEquals(SecurityErrorCodes.SECURITY_NOT_AUTHORIZED, exception.getErrorCode());
  }

  private Company createCompanyBuyerType() {
    Company company = new Company();
    company.setPid(123L);
    company.setName("foo");
    company.setType(CompanyType.BUYER);
    return company;
  }

  private SellerSeat setSellerSeat(long sellerSeatPid) {
    SellerSeat sellerSeat = new SellerSeat();
    sellerSeat.setPid(sellerSeatPid);
    sellerSeat.enable();
    return sellerSeat;
  }

  private SellerAttributes setSellerAttributes() {
    SellerAttributes sellerAttributes = new SellerAttributes();
    sellerAttributes.setHbThrottleEnabled(true);
    sellerAttributes.setHbThrottlePercentage(15);
    return sellerAttributes;
  }
}
