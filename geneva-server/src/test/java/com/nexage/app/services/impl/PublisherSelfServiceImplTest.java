package com.nexage.app.services.impl;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import com.nexage.admin.core.dto.AdSourceSummaryDTO;
import com.nexage.admin.core.enums.DapPlayerType;
import com.nexage.admin.core.enums.PlacementCategory;
import com.nexage.admin.core.enums.PlacementVideoLinearity;
import com.nexage.admin.core.enums.PlacementVideoSsai;
import com.nexage.admin.core.enums.PlacementVideoStreamType;
import com.nexage.admin.core.enums.Status;
import com.nexage.admin.core.enums.TierType;
import com.nexage.admin.core.enums.TrafficType;
import com.nexage.admin.core.enums.VideoLinearity;
import com.nexage.admin.core.enums.VideoSupport;
import com.nexage.admin.core.enums.site.Type;
import com.nexage.admin.core.model.AdSource;
import com.nexage.admin.core.model.Company;
import com.nexage.admin.core.model.Position;
import com.nexage.admin.core.model.RTBProfile;
import com.nexage.admin.core.model.SellerAttributes;
import com.nexage.admin.core.model.Site;
import com.nexage.admin.core.model.Tag;
import com.nexage.admin.core.model.Tier;
import com.nexage.admin.core.model.User;
import com.nexage.admin.core.repository.AdSourceRepository;
import com.nexage.admin.core.repository.CompanyRepository;
import com.nexage.admin.core.repository.DealTermViewRepository;
import com.nexage.admin.core.repository.PlacementVideoPlaylistRepository;
import com.nexage.admin.core.repository.PlacementVideoRepository;
import com.nexage.admin.core.repository.PositionRepository;
import com.nexage.admin.core.repository.SiteRepository;
import com.nexage.admin.core.repository.TagRepository;
import com.nexage.admin.core.repository.TagViewRepository;
import com.nexage.admin.core.sparta.jpa.model.DealTermView;
import com.nexage.admin.core.sparta.jpa.model.PlacementVideo;
import com.nexage.admin.core.sparta.jpa.model.SellerAdSource;
import com.nexage.admin.core.sparta.jpa.model.SiteDealTerm;
import com.nexage.admin.core.sparta.jpa.model.TagPosition;
import com.nexage.admin.core.sparta.jpa.model.TagView;
import com.nexage.admin.core.validator.CreateGroup;
import com.nexage.admin.core.validator.UpdateGroup;
import com.nexage.app.dto.SiteUpdateInfoDTO;
import com.nexage.app.dto.publisher.PublisherAdSourceDefaultsDTO;
import com.nexage.app.dto.publisher.PublisherBuyerDTO;
import com.nexage.app.dto.publisher.PublisherDefaultRTBProfileDTO;
import com.nexage.app.dto.publisher.PublisherPositionDTO;
import com.nexage.app.dto.publisher.PublisherSiteDTO;
import com.nexage.app.dto.publisher.PublisherSiteDTO.SiteType;
import com.nexage.app.dto.publisher.PublisherSiteDealTermDTO;
import com.nexage.app.dto.publisher.PublisherTagDTO;
import com.nexage.app.dto.publisher.PublisherTierDTO;
import com.nexage.app.dto.seller.PlacementDoohDTO;
import com.nexage.app.dto.seller.PlacementVideoCompanionDTO;
import com.nexage.app.dto.seller.PlacementVideoDTO;
import com.nexage.app.error.EntityConstraintViolationException;
import com.nexage.app.error.ServerErrorCodes;
import com.nexage.app.security.LoginUserContext;
import com.nexage.app.services.BeanValidationService;
import com.nexage.app.services.BuyerService;
import com.nexage.app.services.CompanyService;
import com.nexage.app.services.HbPartnerSiteService;
import com.nexage.app.services.RTBProfileService;
import com.nexage.app.services.RtbProfileLibraryService;
import com.nexage.app.services.RuleDSPService;
import com.nexage.app.services.SellerPositionService;
import com.nexage.app.services.SellerService;
import com.nexage.app.services.SellerSiteService;
import com.nexage.app.services.SellerTagService;
import com.nexage.app.services.impl.limit.PositionLimitChecker;
import com.nexage.app.services.impl.limit.SiteLimitChecker;
import com.nexage.app.services.impl.limit.TagLimitChecker;
import com.nexage.app.services.validation.RevenueShareUpdateValidator;
import com.nexage.app.util.PositionTrafficTypeValidator;
import com.nexage.app.util.PositionValidator;
import com.nexage.app.util.RTBProfileUtil;
import com.nexage.app.util.assemblers.PublisherAdSourceDefaultsAssembler;
import com.nexage.app.util.assemblers.PublisherBuyerAssembler;
import com.nexage.app.util.assemblers.PublisherPositionAssembler;
import com.nexage.app.util.assemblers.PublisherSiteAssembler;
import com.nexage.app.util.assemblers.PublisherTagAssembler;
import com.nexage.app.util.assemblers.PublisherTierAssembler;
import com.nexage.app.util.assemblers.context.PublisherPositionContext;
import com.nexage.app.util.assemblers.context.PublisherTagContext;
import com.nexage.app.util.assemblers.context.PublisherTierContext;
import com.nexage.app.web.support.TestObjectsFactory;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import com.ssp.geneva.common.security.error.GenevaSecurityException;
import com.ssp.geneva.common.security.error.SecurityErrorCodes;
import com.ssp.geneva.common.security.model.SpringUserDetails;
import com.ssp.geneva.server.report.performance.pss.facade.BiddersPerformanceFacadeImpl;
import com.ssp.geneva.server.report.performance.pss.model.BiddersPerformanceForPubSelfServe;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import javax.validation.groups.Default;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.commons.util.StringUtils;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.BeanUtils;
import org.springframework.test.util.ReflectionTestUtils;

@Log4j2
@ExtendWith(MockitoExtension.class)
class PublisherSelfServiceImplTest {

  @Mock private HbPartnerSiteService hbPartnerSiteService;
  @Mock private SellerService sellerService;
  @Mock private SellerSiteService sellerSiteService;
  @Mock private SellerPositionService sellerPositionService;
  @Mock private SellerTagService sellerTagService;
  @Mock private AdSourceRepository adSourceRepository;
  @Mock private PublisherSiteAssembler publisherSiteAssembler;
  @Mock private RevenueShareUpdateValidator revenueShareUpdateValidator;
  @Mock private PublisherPositionAssembler publisherPositionAssembler;
  @Mock private PublisherTierAssembler publisherTierAssembler;
  @Mock private PublisherTagAssembler publisherTagAssembler;
  @Mock private LoginUserContext userContext;
  @Mock private RTBProfileService rtbProfileService;
  @Mock private RtbProfileLibraryService rtbProfileLibraryService;
  @Mock private RuleDSPService ruleDSPService;
  @Mock private CompanyService companyService;
  @Mock private PositionValidator positionValidator;
  @Mock private PositionTrafficTypeValidator positionTrafficTypeValidator;
  @Mock private PlacementVideoServiceImpl placementVideoService;

  @Mock private PositionRepository positionRepository;

  @Mock private PlacementVideoRepository placementVideoRepository;
  @Mock private PlacementVideoPlaylistRepository placementVideoPlaylistRepository;

  @Mock private RTBProfileUtil rtbProfileUtil;

  @Mock private TagViewRepository tagViewRepository;
  @Mock private TagRepository tagRepository;
  @Mock private DealTermViewRepository dealTermViewRepository;
  @Mock private CompanyRepository companyRepository;
  @Mock private SiteRepository siteRepository;
  @Mock BeanValidationService beanValidationService;
  @Mock private BuyerService buyerService;
  @Mock private PublisherAdSourceDefaultsAssembler publisherAdSourceDefaultsAssembler;
  @Mock private PublisherBuyerAssembler publisherBuyerAssembler;
  @Mock private TagLimitChecker tagLimitChecker;
  @Mock private PositionLimitChecker positionLimitChecker;
  @Mock private SiteLimitChecker siteLimitChecker;
  @Mock private BiddersPerformanceFacadeImpl biddersPerformanceFacade;

  @InjectMocks private PublisherSelfServiceImpl publisherSelfService;

  private static final Long REGULAR_BUYER = 1000L;
  private static final Long BID_ENABLED_BUYER = 1001L;
  private static final Long DM_ENABLED_BUYER = 1002L;
  private static final Long BID_DM_ENABLED_BUYER = 1003L;

  private static final Long REGULAR_BUYER_TAG = 9889737L;
  private static final Long BID_ENABLED_BUYER_TAG = 9889729L;
  private static final Long DM_ENABLED_BUYER_TAG = 9889730L;
  private static final Long BID_DM_ENABLED_BUYER_TAG = 9889731L;

  private static final Date START = new Date(0L);
  private static final Date END = new Date(1L);
  private static final long PUBLISHER_PID = 1L;

  List<Tag> tagList =
      List.of(
          getTag(REGULAR_BUYER_TAG, REGULAR_BUYER),
          getTag(BID_ENABLED_BUYER_TAG, BID_ENABLED_BUYER),
          getTag(DM_ENABLED_BUYER_TAG, DM_ENABLED_BUYER),
          getTag(BID_DM_ENABLED_BUYER_TAG, BID_DM_ENABLED_BUYER));

  private final Random random = ThreadLocalRandom.current();

  @BeforeEach
  void setUp() {
    lenient().when(adSourceRepository.findNonDeletedByPidIn(anyList())).thenReturn(getAdSources());
    lenient()
        .when(adSourceRepository.findById(REGULAR_BUYER))
        .thenReturn(Optional.of(getAdSources(REGULAR_BUYER)));
    lenient()
        .when(adSourceRepository.findById(DM_ENABLED_BUYER))
        .thenReturn(Optional.of(getAdSources(DM_ENABLED_BUYER)));
    lenient()
        .when(adSourceRepository.findById(BID_DM_ENABLED_BUYER))
        .thenReturn(Optional.of(getAdSources(BID_DM_ENABLED_BUYER)));

    lenient().when(userContext.isNexageUser()).thenReturn(false);
    lenient().when(positionRepository.findById(anyLong())).thenReturn(Optional.of(new Position()));
  }

  @Test
  void shouldFailToCreateSiteWhenNotAuthorized() {
    doNothing().when(siteLimitChecker).checkLimitsSite(anyLong());
    when(revenueShareUpdateValidator.isRevenueShareUpdate(
            any(SellerAttributes.class), any(PublisherSiteDealTermDTO.class)))
        .thenReturn(true);
    when(userContext.isOcManagerYieldNexage()).thenReturn(false);

    Company company = new Company();
    company.setSellerAttributes(new SellerAttributes());
    when(companyService.getCompany(anyLong())).thenReturn(company);

    PublisherSiteDTO publisherSiteDTO = new PublisherSiteDTO();
    publisherSiteDTO.setCurrentDealTerm(new PublisherSiteDealTermDTO());
    var exception =
        assertThrows(
            GenevaSecurityException.class,
            () -> publisherSelfService.createSite(1L, publisherSiteDTO, false));
    assertEquals(SecurityErrorCodes.SECURITY_NOT_AUTHORIZED, exception.getErrorCode());
  }

  @Test
  void shouldCreateSiteSucceedsWhenUserHasYieldManagerRole() {
    when(publisherSiteAssembler.apply(anyLong(), any(), any())).thenReturn(new Site());
    doNothing().when(siteLimitChecker).checkLimitsSite(anyLong());
    when(revenueShareUpdateValidator.isRevenueShareUpdate(
            any(SellerAttributes.class), any(PublisherSiteDealTermDTO.class)))
        .thenReturn(true);
    when(userContext.isOcManagerYieldNexage()).thenReturn(true);

    Company company = new Company();
    company.setSellerAttributes(new SellerAttributes());
    when(companyService.getCompany(anyLong())).thenReturn(company);

    PublisherSiteDTO publisherSiteDTO = new PublisherSiteDTO();
    publisherSiteDTO.setCurrentDealTerm(new PublisherSiteDealTermDTO());
    when(publisherSiteAssembler.make(any(), anyBoolean())).thenReturn(publisherSiteDTO);

    assertNotNull(publisherSelfService.createSite(1L, publisherSiteDTO, false));
  }

  @Test
  void shouldThrowUnauthorizedWhenSiteDcnHasReadOnlyOnCreateSite() {
    when(publisherSiteAssembler.apply(anyLong(), any(), any())).thenReturn(new Site());
    doNothing().when(siteLimitChecker).checkLimitsSite(anyLong());
    when(revenueShareUpdateValidator.isRevenueShareUpdate(
            any(SellerAttributes.class), any(PublisherSiteDealTermDTO.class)))
        .thenReturn(true);
    when(userContext.isOcManagerYieldNexage()).thenReturn(true);
    when(userContext.isNexageAdminOrManager()).thenReturn(false);
    var details = mock(SpringUserDetails.class);
    given(details.getRole()).willReturn(User.Role.ROLE_DUMMY);
    given(userContext.getCurrentUser()).willReturn(details);

    Company company = new Company();
    company.setSellerAttributes(new SellerAttributes());
    when(companyService.getCompany(anyLong())).thenReturn(company);

    PublisherSiteDTO publisherSiteDTO = new PublisherSiteDTO();
    publisherSiteDTO.setCurrentDealTerm(new PublisherSiteDealTermDTO());
    publisherSiteDTO.setDcn("test.com");

    var exception =
        assertThrows(
            GenevaSecurityException.class,
            () -> publisherSelfService.createSite(1L, publisherSiteDTO, false));
    assertEquals(ServerErrorCodes.SERVER_SITE_DCN_READONLY, exception.getErrorCode());
  }

  @Test
  void shouldThrowUnauthorizedOnCloneTagWithoutSiteAccess() {
    // when
    when(userContext.canAccessSite(anyLong())).thenReturn(false);

    // then
    var exception =
        assertThrows(
            GenevaSecurityException.class,
            () -> publisherSelfService.cloneTag(1L, 1L, 1L, 1L, null, 1L, 1L));
    assertEquals(SecurityErrorCodes.SECURITY_NOT_AUTHORIZED, exception.getErrorCode());
  }

  @Test
  void shouldThrowNotFoundErrorIfTagDoesNotExist() {
    // when
    when(adSourceRepository.findById(anyLong())).thenReturn(Optional.empty());

    // then
    var exception =
        assertThrows(
            GenevaValidationException.class,
            () -> publisherSelfService.validateTag(1L, 1L, "1", "primary", "1", "secondary"));
    assertEquals(ServerErrorCodes.SERVER_TAG_NOT_FOUND, exception.getErrorCode());
  }

  @Test
  void shouldThrowNotFoundErrorIfPrimaryIdRequiredButDoesNotExist() {
    // when
    AdSource adSource = new AdSource();
    adSource.setPrimaryIdRequired(true);
    when(adSourceRepository.findById(anyLong())).thenReturn(Optional.of(adSource));

    // then
    var exception =
        assertThrows(
            GenevaValidationException.class,
            () -> publisherSelfService.validateTag(1L, 1L, null, "primary", "1", "secondary"));
    assertEquals(ServerErrorCodes.SERVER_TAG_NOT_FOUND, exception.getErrorCode());
  }

  @Test
  void shouldFailToUpdateSiteWhenNotAuthorized() {
    when(revenueShareUpdateValidator.isRevenueShareUpdate(
            any(SiteDealTerm.class), any(PublisherSiteDealTermDTO.class)))
        .thenReturn(true);
    when(userContext.isOcManagerYieldNexage()).thenReturn(false);

    Site siteDTO = new Site();
    siteDTO.setCurrentDealTerm(new SiteDealTerm());
    when(sellerSiteService.getSite(anyLong())).thenReturn(siteDTO);

    PublisherSiteDTO publisherSiteDTO = new PublisherSiteDTO();
    publisherSiteDTO.setPid(1L);
    publisherSiteDTO.setCurrentDealTerm(new PublisherSiteDealTermDTO());

    var exception =
        assertThrows(
            GenevaSecurityException.class,
            () -> publisherSelfService.updateSite(1L, publisherSiteDTO, false));
    assertEquals(SecurityErrorCodes.SECURITY_NOT_AUTHORIZED, exception.getErrorCode());
  }

  @Test
  void shouldUpdateSiteWhenUserHasYieldManagerRole() {

    when(revenueShareUpdateValidator.isRevenueShareUpdate(
            any(SiteDealTerm.class), any(PublisherSiteDealTermDTO.class)))
        .thenReturn(true);
    when(userContext.isOcManagerYieldNexage()).thenReturn(true);

    Site siteDTO = new Site();
    siteDTO.setVersion(1);
    siteDTO.setCurrentDealTerm(new SiteDealTerm());
    when(sellerSiteService.getSite(anyLong())).thenReturn(siteDTO);
    when(publisherSiteAssembler.apply(anyLong(), any(), any())).thenReturn(new Site());

    PublisherSiteDTO publisherSiteDTO = new PublisherSiteDTO();
    publisherSiteDTO.setPid(1L);
    publisherSiteDTO.setVersion(1);
    publisherSiteDTO.setCurrentDealTerm(new PublisherSiteDealTermDTO());
    when(publisherSiteAssembler.make(any(), anyBoolean())).thenReturn(publisherSiteDTO);

    assertNotNull(publisherSelfService.updateSite(1L, publisherSiteDTO, false));
  }

  @Test
  void shouldGetTier() {
    Site inSite = getSite();
    inSite.setCompanyPid(1L);
    inSite.getPositions().stream()
        .findFirst()
        .ifPresent(
            p ->
                p.setTiers(
                    List.of(
                        getTier(
                            1,
                            0,
                            TierType.SUPER_AUCTION,
                            List.of(getTag(REGULAR_BUYER_TAG, REGULAR_BUYER, 1L, 1L, null))))));
    when(sellerSiteService.getValidatedSiteForPublisher(anyLong(), anyLong())).thenReturn(inSite);
    when(publisherTierAssembler.make(any(PublisherTierContext.class), any(Tier.class)))
        .thenReturn(
            getPublisherTier(
                1L, 0, TierType.SUPER_AUCTION, List.of(getPublisherTag(1L, 1L, 1L, 0))));
    when(userContext.isPublisherSelfServeEnabled(1L)).thenReturn(true);
    PublisherTierDTO publisherTier = publisherSelfService.getTier(PUBLISHER_PID, 1L, 1L, 1L);
    assertEquals(TierType.SUPER_AUCTION, publisherTier.getTierType());
  }

  @Test
  void shouldCreateWaterFallTier() { // waterfall tier with different tags
    for (Tag tag : tagList) {
      Site inSite = getSite();
      PublisherTagDTO pubTag = getPublisherTag(tag.getPid(), tag.getBuyerPid(), 1L, 0);
      Tier tier = getTier(1001L, 0, TierType.WATERFALL, null);

      PublisherTierDTO input = getPublisherTier(null, 0, TierType.WATERFALL, List.of(pubTag));
      PublisherTierDTO output = getPublisherTier(1000L, 0, TierType.WATERFALL, List.of(pubTag));

      when(sellerSiteService.getSite(anyLong())).thenReturn(inSite);
      tier.setTags(List.of(tag));
      Site updateSite = getSite();
      updateSite.getPositions().stream()
          .filter(p -> p.getTrafficType().equals(TrafficType.SMART_YIELD))
          .findFirst()
          .ifPresent(p -> p.setTiers(List.of(tier)));

      when(sellerPositionService.updatePosition(any(Position.class))).thenReturn(updateSite);
      when(publisherTierAssembler.make(any(PublisherTierContext.class), any(Tier.class)))
          .thenReturn(output);
      when(publisherTierAssembler.apply(
              any(PublisherTierContext.class), any(Tier.class), any(PublisherTierDTO.class)))
          .thenReturn(tier);
      try {
        publisherSelfService.createTier(1L, 1L, 1L, input);
      } catch (GenevaSecurityException gse) {
        assertEquals(SecurityErrorCodes.SECURITY_NOT_AUTHORIZED, gse.getErrorCode());
      } catch (Exception e) {
        fail("Unexpected Exception: " + e.getMessage());
      }

      PublisherTierDTO publisherTier = publisherSelfService.createTier(1L, 1L, 1L, input);
      assertEquals(1000L, publisherTier.getPid().longValue());
      assertEquals(0, publisherTier.getLevel().intValue());
      assertEquals(1, publisherTier.getTags().size());
      assertEquals(tag.getPid(), publisherTier.getTags().get(0).getPid());
      assertEquals(1, publisherTier.getTags().get(0).getPosition().getPid().longValue());
      assertEquals(TierType.WATERFALL, publisherTier.getTierType());

      // non SY
      updateSite = getSite();
      pubTag = getPublisherTag(tag.getPid(), tag.getBuyerPid(), 2L, 0);

      input = getPublisherTier(null, 0, TierType.WATERFALL, List.of(pubTag));
      output = getPublisherTier(1000L, 0, TierType.WATERFALL, List.of(pubTag));

      when(sellerPositionService.updatePosition(any(Position.class))).thenReturn(updateSite);
      when(publisherTierAssembler.make(any(PublisherTierContext.class), any(Tier.class)))
          .thenReturn(output);
      when(publisherTierAssembler.apply(
              any(PublisherTierContext.class), any(Tier.class), any(PublisherTierDTO.class)))
          .thenReturn(tier);

      publisherTier = publisherSelfService.createTier(1L, 1L, 2L, input);
      assertEquals(1000L, publisherTier.getPid().longValue());
      assertEquals(0, publisherTier.getLevel().intValue());
      assertEquals(1, publisherTier.getTags().size());
      assertEquals(tag.getPid(), publisherTier.getTags().get(0).getPid());
      assertEquals(2, publisherTier.getTags().get(0).getPosition().getPid().longValue());
      assertEquals(TierType.WATERFALL, publisherTier.getTierType());
    }
  }

  @Test
  void shouldCreateDecisionMakerTier() {
    for (Tag tag : tagList) {
      Site inSite = getSite();
      PublisherTagDTO pubTag = getPublisherTag(tag.getPid(), tag.getBuyerPid(), 1L, 0);
      Tier tier = getTier(1001L, 0, TierType.SY_DECISION_MAKER, null);

      PublisherTierDTO input =
          getPublisherTier(null, 0, TierType.SY_DECISION_MAKER, List.of(pubTag));
      PublisherTierDTO output =
          getPublisherTier(1000L, 0, TierType.SY_DECISION_MAKER, List.of(pubTag));

      when(sellerSiteService.getSite(anyLong())).thenReturn(inSite);
      tier.setTags(List.of(tag));
      Site updateSite = getSite();
      updateSite.getPositions().stream()
          .filter(p -> p.getTrafficType().equals(TrafficType.SMART_YIELD))
          .findFirst()
          .ifPresent(p -> p.setTiers(List.of(tier)));

      when(sellerPositionService.updatePosition(any(Position.class))).thenReturn(updateSite);
      when(publisherTierAssembler.make(any(PublisherTierContext.class), any(Tier.class)))
          .thenReturn(output);
      when(publisherTierAssembler.apply(
              any(PublisherTierContext.class), any(Tier.class), any(PublisherTierDTO.class)))
          .thenReturn(tier);
      lenient()
          .when(adSourceRepository.findNonDeletedByPidIn(anyList()))
          .thenReturn(getAdSources());

      try {
        publisherSelfService.createTier(1L, 1L, 1L, input);
      } catch (GenevaSecurityException gse) {
        assertEquals(SecurityErrorCodes.SECURITY_NOT_AUTHORIZED, gse.getErrorCode());
      } catch (Exception e) {
        fail("Unexpected Exception: " + e.getMessage());
      }

      if (List.of(DM_ENABLED_BUYER, BID_DM_ENABLED_BUYER).contains(tag.getBuyerPid())) {
        PublisherTierDTO publisherTier = publisherSelfService.createTier(1L, 1L, 1L, input);
        assertEquals(1000L, publisherTier.getPid().longValue());
        assertEquals(0, publisherTier.getLevel().intValue());
        assertEquals(1, publisherTier.getTags().size());
        assertEquals(tag.getPid(), publisherTier.getTags().get(0).getPid());
        assertEquals(TierType.SY_DECISION_MAKER, publisherTier.getTierType());

        doThrow(
                new GenevaValidationException(
                    ServerErrorCodes.SERVER_TIER_TYPE_NOT_VALID_FOR_POSITION))
            .when(positionTrafficTypeValidator)
            .validatePositionTrafficType(any(), any(), eq(input), eq(false));
        // non SY placement
        var exception =
            assertThrows(
                GenevaValidationException.class,
                () -> publisherSelfService.createTier(1L, 1L, 2L, input));
        assertEquals(
            ServerErrorCodes.SERVER_TIER_TYPE_NOT_VALID_FOR_POSITION, exception.getErrorCode());
      } else {
        doThrow(
                new GenevaValidationException(
                    ServerErrorCodes.SERVER_TAG_ADSOURCE_NOT_DECISION_MAKER_ENABLED))
            .when(positionTrafficTypeValidator)
            .validatePositionTrafficType(any(), any(), eq(input), eq(false));
        var exception =
            assertThrows(
                GenevaValidationException.class,
                () -> publisherSelfService.createTier(1L, 1L, 1L, input));
        assertEquals(
            ServerErrorCodes.SERVER_TAG_ADSOURCE_NOT_DECISION_MAKER_ENABLED,
            exception.getErrorCode());
      }
    }
  }

  @Test
  void shouldCreateSuperAuctionTier() {
    Tier dmTier = getTier(1001L, 0, TierType.SY_DECISION_MAKER, null);
    dmTier.addTag(getTag(10009L, DM_ENABLED_BUYER));

    for (Tag tag : tagList) {
      Site inSite = getSite();
      PublisherTagDTO pubTag = getPublisherTag(tag.getPid(), tag.getBuyerPid(), 1L, 0);
      Tier tier = getTier(1001L, 0, TierType.SUPER_AUCTION, null);

      PublisherTierDTO output = getPublisherTier(1000L, 0, TierType.SUPER_AUCTION, List.of(pubTag));

      when(sellerSiteService.getSite(anyLong())).thenReturn(inSite);
      tier.setTags(List.of(tag));
      Site updateSite = getSite();
      updateSite.getPositions().stream()
          .filter(p -> p.getTrafficType().equals(TrafficType.SMART_YIELD))
          .findFirst()
          .ifPresent(p -> p.setTiers(List.of(tier)));

      when(publisherTierAssembler.make(any(PublisherTierContext.class), any(Tier.class)))
          .thenReturn(output);
      when(publisherTierAssembler.apply(
              any(PublisherTierContext.class), any(Tier.class), any(PublisherTierDTO.class)))
          .thenReturn(tier);
      lenient()
          .when(adSourceRepository.findNonDeletedByPidIn(anyList()))
          .thenReturn(getAdSources());

      final PublisherTierDTO input =
          getPublisherTier(null, 0, TierType.SUPER_AUCTION, List.of(pubTag));
      try {
        publisherSelfService.createTier(1L, 1L, 1L, input);
      } catch (GenevaSecurityException gse) {
        assertEquals(SecurityErrorCodes.SECURITY_NOT_AUTHORIZED, gse.getErrorCode());
      } catch (Exception e) {
        fail("Unexpected Exception: " + e.getMessage());
      }

      // without DM tier
      doThrow(
              new GenevaValidationException(
                  ServerErrorCodes.SERVER_TIER_DECISION_MAKER_DOES_NOT_EXIST))
          .when(positionTrafficTypeValidator)
          .validatePositionTrafficType(any(), any(), eq(input), eq(false));
      // non SY placement
      var GenevaValidationException =
          assertThrows(
              GenevaValidationException.class,
              () -> publisherSelfService.createTier(1L, 1L, 1L, input));
      assertNotNull(GenevaValidationException);
      assertEquals(
          ServerErrorCodes.SERVER_TIER_DECISION_MAKER_DOES_NOT_EXIST,
          GenevaValidationException.getErrorCode());

      // with dm tier
      Site dmSite = getSite();
      dmSite.getPositions().stream()
          .filter(p -> p.getTrafficType().equals(TrafficType.SMART_YIELD))
          .findFirst()
          .ifPresent(p -> p.setTiers(List.of(dmTier)));
      when(sellerSiteService.getSite(anyLong())).thenReturn(dmSite);
      Tier tier1 = getTier(1001L, 1, TierType.SUPER_AUCTION, null);
      updateSite = getSite();
      updateSite.getPositions().stream()
          .filter(p -> p.getTrafficType().equals(TrafficType.SMART_YIELD))
          .findFirst()
          .ifPresent(p -> p.setTiers(List.of(dmTier, tier1)));
      when(sellerPositionService.updatePosition(any(Position.class))).thenReturn(updateSite);

      var anotherInput = getPublisherTier(null, 1, TierType.SUPER_AUCTION, List.of(pubTag));
      if (List.of(BID_ENABLED_BUYER, BID_DM_ENABLED_BUYER).contains(tag.getBuyerPid())) {
        PublisherTierDTO publisherTier = publisherSelfService.createTier(1L, 1L, 1L, anotherInput);
        assertEquals(1000L, publisherTier.getPid().longValue());
        assertEquals(0, publisherTier.getLevel().intValue());
        assertEquals(1, publisherTier.getTags().size());
        assertEquals(tag.getPid(), publisherTier.getTags().get(0).getPid());
        assertEquals(TierType.SUPER_AUCTION, publisherTier.getTierType());

        reset(positionTrafficTypeValidator);
        doThrow(
                new GenevaValidationException(
                    ServerErrorCodes.SERVER_TIER_TYPE_NOT_VALID_FOR_POSITION))
            .when(positionTrafficTypeValidator)
            .validatePositionTrafficType(any(), any(), eq(anotherInput), eq(false));

        var exception =
            assertThrows(
                GenevaValidationException.class,
                () -> publisherSelfService.createTier(1L, 1L, 2L, anotherInput));
        assertNotNull(exception);
        assertEquals(
            ServerErrorCodes.SERVER_TIER_TYPE_NOT_VALID_FOR_POSITION, exception.getErrorCode());
      } else {
        // without DM tier
        reset(positionTrafficTypeValidator);
        doThrow(new GenevaValidationException(ServerErrorCodes.SERVER_TAG_ADSOURCE_NOT_BID_ENABLED))
            .when(positionTrafficTypeValidator)
            .validatePositionTrafficType(any(), any(), eq(anotherInput), eq(false));
        // non SY placement
        var exception =
            assertThrows(
                GenevaValidationException.class,
                () -> publisherSelfService.createTier(1L, 1L, 1L, anotherInput));
        assertNotNull(exception);
        assertEquals(
            ServerErrorCodes.SERVER_TAG_ADSOURCE_NOT_BID_ENABLED, exception.getErrorCode());
      }
    }
  }

  @Test
  void shouldUpdateTier() {
    Site inSite = getSite();
    Tier tier1 = getTier(1001L, 0, TierType.SY_DECISION_MAKER, null);
    tier1.setTags(List.of(getTag(BID_DM_ENABLED_BUYER_TAG, BID_DM_ENABLED_BUYER)));
    Tier tier2 = getTier(1002L, 1, TierType.SUPER_AUCTION, null);
    tier2.setTags(List.of(getTag(BID_ENABLED_BUYER_TAG, BID_ENABLED_BUYER)));

    inSite.getPositions().stream()
        .filter(p -> p.getTrafficType().equals(TrafficType.SMART_YIELD))
        .findFirst()
        .ifPresent(p -> p.setTiers(List.of(tier1, tier2)));
    when(sellerSiteService.getSite(anyLong())).thenReturn(inSite);

    Site updateSite = getSite();
    tier2.setTags(List.of(getTag(BID_DM_ENABLED_BUYER_TAG, BID_DM_ENABLED_BUYER)));
    updateSite.getPositions().stream()
        .filter(p -> p.getTrafficType().equals(TrafficType.SMART_YIELD))
        .findFirst()
        .ifPresent(p -> p.setTiers(List.of(tier1, tier2)));
    when(sellerPositionService.updatePosition(any(Position.class))).thenReturn(updateSite);

    PublisherTagDTO pubTag1 =
        getPublisherTag(BID_DM_ENABLED_BUYER_TAG, BID_DM_ENABLED_BUYER, 1L, 0);
    PublisherTagDTO pubTag2 = getPublisherTag(BID_ENABLED_BUYER_TAG, BID_ENABLED_BUYER, 1L, 0);
    PublisherTagDTO pubTag3 = getPublisherTag(DM_ENABLED_BUYER_TAG, DM_ENABLED_BUYER, 1L, 0);
    PublisherTagDTO pubTag4 = getPublisherTag(REGULAR_BUYER_TAG, REGULAR_BUYER, 1L, 0);

    PublisherTierDTO input =
        getPublisherTier(1001L, 0, TierType.SY_DECISION_MAKER, List.of(pubTag1, pubTag3));
    try {
      publisherSelfService.updateTier(1L, 1L, 1L, input);
    } catch (GenevaSecurityException gse) {
      assertEquals(SecurityErrorCodes.SECURITY_NOT_AUTHORIZED, gse.getErrorCode());
    } catch (Exception e) {
      fail("Unexpected Exception: " + e.getMessage());
    }

    reset(positionTrafficTypeValidator);
    doThrow(
            new GenevaValidationException(
                ServerErrorCodes.SERVER_DECISION_MAKER_TIER_SHOULD_CONTAIN_ONE_TAG))
        .when(positionTrafficTypeValidator)
        .validatePositionTrafficType(any(), any(), eq(input), eq(false));

    try { // DM tier more than one tag
      publisherSelfService.updateTier(1L, 1L, 1L, input);
      fail("Expected exception not thrown");
    } catch (GenevaValidationException e) {
      assertEquals(
          ServerErrorCodes.SERVER_DECISION_MAKER_TIER_SHOULD_CONTAIN_ONE_TAG, e.getErrorCode());
    } catch (Exception ee) {
      fail("Unexpected Exception: " + ee.getMessage());
    }

    input = getPublisherTier(1001L, 0, TierType.SY_DECISION_MAKER, List.of(pubTag4));

    reset(positionTrafficTypeValidator);
    doThrow(
            new GenevaValidationException(
                ServerErrorCodes.SERVER_TAG_ADSOURCE_NOT_DECISION_MAKER_ENABLED))
        .when(positionTrafficTypeValidator)
        .validatePositionTrafficType(any(), any(), eq(input), eq(false));

    try { // DM tier with tag DM nt enabled
      publisherSelfService.updateTier(1L, 1L, 1L, input);
      fail("Expected exception not thrown");
    } catch (GenevaValidationException e) {
      assertEquals(
          ServerErrorCodes.SERVER_TAG_ADSOURCE_NOT_DECISION_MAKER_ENABLED, e.getErrorCode());
    } catch (Exception ee) {
      fail("Unexpected Exception: " + ee.getMessage());
    }

    input = getPublisherTier(1001L, 0, TierType.SY_DECISION_MAKER, List.of(pubTag3));
    PublisherTierDTO output =
        getPublisherTier(1001L, 0, TierType.SY_DECISION_MAKER, List.of(pubTag3));
    when(publisherTierAssembler.make(any(PublisherTierContext.class), any(Tier.class)))
        .thenReturn(output);
    when(publisherTierAssembler.apply(
            any(PublisherTierContext.class), any(Tier.class), any(PublisherTierDTO.class)))
        .thenReturn(tier1);

    PublisherTierDTO publisherTier = publisherSelfService.updateTier(1L, 1L, 1L, input);
    assertEquals(1001L, publisherTier.getPid().longValue());
    assertEquals(0, publisherTier.getLevel().intValue());
    assertEquals(1, publisherTier.getTags().size());
    assertEquals(
        DM_ENABLED_BUYER_TAG.longValue(), publisherTier.getTags().get(0).getPid().longValue());
    assertEquals(TierType.SY_DECISION_MAKER, publisherTier.getTierType());

    input = getPublisherTier(1002L, 1, TierType.SUPER_AUCTION, List.of(pubTag4));

    reset(positionTrafficTypeValidator);
    doThrow(new GenevaValidationException(ServerErrorCodes.SERVER_TAG_ADSOURCE_NOT_BID_ENABLED))
        .when(positionTrafficTypeValidator)
        .validatePositionTrafficType(any(), any(), eq(input), eq(false));

    try { // SA tier with tag bit not enabled
      publisherSelfService.updateTier(1L, 1L, 1L, input);
      fail("Expected exception not thrown");
    } catch (GenevaValidationException e) {
      assertEquals(ServerErrorCodes.SERVER_TAG_ADSOURCE_NOT_BID_ENABLED, e.getErrorCode());
    } catch (Exception ee) {
      fail("Unexpected Exception: " + ee.getMessage());
    }

    input = getPublisherTier(1002L, 1, TierType.SUPER_AUCTION, List.of(pubTag2, pubTag1));
    output = getPublisherTier(1002L, 1, TierType.SUPER_AUCTION, List.of(pubTag2, pubTag1));
    when(publisherTierAssembler.make(any(PublisherTierContext.class), any(Tier.class)))
        .thenReturn(output);
    when(publisherTierAssembler.apply(
            any(PublisherTierContext.class), any(Tier.class), any(PublisherTierDTO.class)))
        .thenReturn(tier1);

    publisherTier = publisherSelfService.updateTier(1L, 1L, 1L, input);
    assertEquals(1002L, publisherTier.getPid().longValue());
    assertEquals(1, publisherTier.getLevel().intValue());
    assertEquals(2, publisherTier.getTags().size());
    assertEquals(TierType.SUPER_AUCTION, publisherTier.getTierType());

    // tier type update
    input = getPublisherTier(1002L, 1, TierType.WATERFALL, List.of(pubTag2, pubTag1));

    reset(positionTrafficTypeValidator);
    try {
      publisherSelfService.updateTier(1L, 1L, 1L, input);
      fail("Expected exception not thrown");
    } catch (GenevaValidationException e) {
      assertEquals(ServerErrorCodes.SERVER_TIER_TYPE_UPDATE_NOT_SUPPORTED, e.getErrorCode());
    } catch (Exception ee) {
      fail("Unexpected Exception: " + ee.getMessage());
    }
  }

  @Test
  void shouldUpdatePosition() {
    Site inSite = getSite();
    inSite.setCompanyPid(1L);
    Tier tier1 = getTier(1001L, 0, TierType.SY_DECISION_MAKER, null);
    tier1.setTags(List.of(getTag(BID_DM_ENABLED_BUYER_TAG, BID_DM_ENABLED_BUYER)));
    Tier tier2 = getTier(1002L, 1, TierType.SUPER_AUCTION, null);
    tier2.setTags(List.of(getTag(BID_ENABLED_BUYER_TAG, BID_ENABLED_BUYER)));
    Tier tier3 = getTier(1003L, 2, TierType.WATERFALL, null);
    tier3.setTags(List.of(getTag(REGULAR_BUYER_TAG, REGULAR_BUYER)));

    inSite.getPositions().stream()
        .filter(p -> p.getTrafficType().equals(TrafficType.SMART_YIELD))
        .findFirst()
        .ifPresent(p -> p.setTiers(List.of(tier1, tier2, tier3)));
    when(sellerSiteService.getSite(anyLong())).thenReturn(inSite);
    when(sellerSiteService.getValidatedSiteForPublisher(anyLong(), anyLong())).thenReturn(inSite);

    Site updateSite = getSite();
    updateSite.setCompanyPid(1L);
    tier2.setTags(List.of(getTag(BID_DM_ENABLED_BUYER_TAG, BID_DM_ENABLED_BUYER)));
    updateSite.getPositions().stream()
        .filter(p -> p.getTrafficType().equals(TrafficType.SMART_YIELD))
        .findFirst()
        .ifPresent(p -> p.setTiers(List.of(tier1, tier2, tier3)));
    when(sellerPositionService.updatePosition(any(Position.class)))
        .thenReturn(updateSite); // redundant

    PublisherTagDTO pubTag1 =
        getPublisherTag(BID_DM_ENABLED_BUYER_TAG, BID_DM_ENABLED_BUYER, 1L, 0);
    PublisherTagDTO pubTag2 = getPublisherTag(BID_ENABLED_BUYER_TAG, BID_ENABLED_BUYER, 1L, 0);
    PublisherTagDTO pubTag3 = getPublisherTag(DM_ENABLED_BUYER_TAG, DM_ENABLED_BUYER, 1L, 0);
    PublisherTagDTO pubTag4 = getPublisherTag(REGULAR_BUYER_TAG, REGULAR_BUYER, 1L, 0);

    // DM at level 0 with two tags
    PublisherTierDTO pubtier1 =
        getPublisherTier(1001L, 0, TierType.SY_DECISION_MAKER, List.of(pubTag1, pubTag3));
    PublisherTierDTO pubtier2 =
        getPublisherTier(1002L, 1, TierType.SUPER_AUCTION, List.of(pubTag2));
    PublisherTierDTO pubtier3 = getPublisherTier(1003L, 2, TierType.WATERFALL, List.of(pubTag3));
    final PublisherPositionDTO input =
        getPublisherPosition(
            1L, TrafficType.SMART_YIELD, List.of(pubtier1, pubtier2, pubtier3), null);

    Position position = getPosition(1L, TrafficType.SMART_YIELD);
    position.setTiers(List.of(tier1, tier2, tier3));
    when(userContext.isPublisherSelfServeEnabled(1L)).thenReturn(true);
    PlacementVideoDTO placementVideoDTO = TestObjectsFactory.createDefaultPlacementVideoDTO();
    when(placementVideoService.getPlacementVideo(anyLong())).thenReturn(placementVideoDTO);

    reset(positionTrafficTypeValidator);
    doThrow(
            new GenevaValidationException(
                ServerErrorCodes.SERVER_DECISION_MAKER_TIER_SHOULD_CONTAIN_ONE_TAG))
        .when(positionTrafficTypeValidator)
        .validatePositionTiers(any(), any(), any());
    var exception =
        assertThrows(
            GenevaValidationException.class,
            () -> publisherSelfService.updatePosition(PUBLISHER_PID, 1L, input, false));
    assertNotNull(exception);
    assertEquals(
        ServerErrorCodes.SERVER_DECISION_MAKER_TIER_SHOULD_CONTAIN_ONE_TAG,
        exception.getErrorCode());

    // DM at with tag not DM enabled
    pubtier1 =
        getPublisherTier(1001L, 0, TierType.SY_DECISION_MAKER, Collections.singletonList(pubTag2));
    pubtier2 =
        getPublisherTier(1002L, 1, TierType.SUPER_AUCTION, Collections.singletonList(pubTag2));
    pubtier3 = getPublisherTier(1003L, 2, TierType.WATERFALL, Collections.singletonList(pubTag3));
    var input2 =
        getPublisherPosition(
            1L, TrafficType.SMART_YIELD, List.of(pubtier1, pubtier2, pubtier3), null);

    position = getPosition(1L, TrafficType.SMART_YIELD);
    position.setTiers(List.of(tier1, tier2, tier3));

    doThrow(
            new GenevaValidationException(
                ServerErrorCodes.SERVER_TAG_ADSOURCE_NOT_DECISION_MAKER_ENABLED))
        .when(positionTrafficTypeValidator)
        .validatePositionTiers(any(), any(), any());

    exception =
        assertThrows(
            GenevaValidationException.class,
            () -> publisherSelfService.updatePosition(PUBLISHER_PID, 1L, input2, false));
    assertNotNull(exception);
    assertEquals(
        ServerErrorCodes.SERVER_TAG_ADSOURCE_NOT_DECISION_MAKER_ENABLED, exception.getErrorCode());

    // SA at with tag not Bid enabled
    pubtier1 =
        getPublisherTier(1001L, 0, TierType.SY_DECISION_MAKER, Collections.singletonList(pubTag1));
    pubtier2 =
        getPublisherTier(1002L, 1, TierType.SUPER_AUCTION, Collections.singletonList(pubTag3));
    pubtier3 = getPublisherTier(1003L, 2, TierType.WATERFALL, Collections.singletonList(pubTag3));
    var input3 =
        getPublisherPosition(
            1L, TrafficType.SMART_YIELD, List.of(pubtier1, pubtier2, pubtier3), null);

    position = getPosition(1L, TrafficType.SMART_YIELD);
    position.setTiers(List.of(tier1, tier2, tier3));

    doThrow(new GenevaValidationException(ServerErrorCodes.SERVER_TAG_ADSOURCE_NOT_BID_ENABLED))
        .when(positionTrafficTypeValidator)
        .validatePositionTiers(any(), any(), any());

    exception =
        assertThrows(
            GenevaValidationException.class,
            () -> publisherSelfService.updatePosition(PUBLISHER_PID, 1L, input3, false));
    assertNotNull(exception);
    assertEquals(ServerErrorCodes.SERVER_TAG_ADSOURCE_NOT_BID_ENABLED, exception.getErrorCode());

    // SA at with tag  Bid enabled
    pubtier1 =
        getPublisherTier(1001L, 0, TierType.SY_DECISION_MAKER, Collections.singletonList(pubTag1));
    pubtier2 = getPublisherTier(1002L, 1, TierType.SUPER_AUCTION, List.of(pubTag2, pubTag1));
    pubtier3 = getPublisherTier(1003L, 2, TierType.WATERFALL, Collections.singletonList(pubTag3));
    PublisherTierDTO pubtier4 =
        getPublisherTier(1004L, 3, TierType.WATERFALL, Collections.singletonList(pubTag4));

    var input4 =
        getPublisherPosition(
            1L, TrafficType.SMART_YIELD, List.of(pubtier1, pubtier2, pubtier3, pubtier4), null);

    position = getPosition(1L, TrafficType.SMART_YIELD);
    position.setTiers(List.of(tier1, tier2, tier3));
    doNothing().when(positionTrafficTypeValidator).validatePositionTiers(any(), any(), any());
    when(publisherPositionAssembler.make(any(PublisherPositionContext.class), any(Position.class)))
        .thenReturn(input);
    when(publisherPositionAssembler.apply(
            any(PublisherPositionContext.class),
            any(Position.class),
            any(PublisherPositionDTO.class)))
        .thenReturn(position);
    PublisherPositionDTO publisherPosition =
        publisherSelfService.updatePosition(PUBLISHER_PID, 1L, input4, false);
    assertEquals(1L, publisherPosition.getPid().longValue());
    assertEquals(3, publisherPosition.getTiers().size());
  }

  @Test
  void shouldGetDecisionMaker() {
    Site inSite = getSite();
    inSite.getPositions().stream()
        .findFirst()
        .ifPresent(
            p ->
                p.setTiers(
                    Collections.singletonList(getTier(1, 0, TierType.SY_DECISION_MAKER, null))));
    PublisherTagDTO publisherTag = mock(PublisherTagDTO.class);
    given(publisherTag.getPid()).willReturn(999L);
    when(sellerSiteService.getSite(anyLong())).thenReturn(inSite);
    when(publisherTagAssembler.make(any(PublisherTagContext.class), nullable(Tag.class)))
        .thenReturn(publisherTag);

    PublisherTagDTO decisionMaker = publisherSelfService.getDecisionMaker(1L, 1L, 1L);
    assertEquals(999L, decisionMaker.getPid().longValue());

    try {
      publisherSelfService.getDecisionMaker(1L, 1L, 2L);
      fail("Expected exception not thrown");
    } catch (GenevaValidationException e) {
      assertEquals("SERVER_TIER_TYPE_NOT_VALID_FOR_POSITION", e.getErrorCode().toString());
      assertEquals(1405, e.getErrorCode().getCode());
    } catch (Exception ee) {
      fail("Unexpected Exception: " + ee.getMessage());
    }
  }

  @Test
  void shouldCreateDecisionMaker() {
    Site inSite = getSite();
    inSite.setCompanyPid(1L);
    inSite.getPositions().stream()
        .findFirst()
        .ifPresent(p -> p.setTiers(List.of(getTier(1, 0, TierType.SY_DECISION_MAKER, null))));
    when(sellerSiteService.getSite(anyLong())).thenReturn(inSite);
    when(sellerSiteService.getValidatedSiteForPublisher(anyLong(), anyLong())).thenReturn(inSite);
    when(userContext.isPublisherSelfServeEnabled(1L)).thenReturn(true);

    PublisherTagDTO input = getPublisherTag(111L, DM_ENABLED_BUYER, 1L, 0);

    // DM exists
    try {
      publisherSelfService.createDecisionMaker(1L, 1L, 1L, input);
      fail("Expected exception not thrown");
    } catch (GenevaValidationException e) {
      assertEquals(ServerErrorCodes.SERVER_TIER_DECISION_MAKER_ALREADY_EXISTS, e.getErrorCode());
    } catch (Exception ee) {
      fail("Unexpected Exception: " + ee.getMessage());
    }

    inSite = getSite();
    inSite.setCompanyPid(1L);
    inSite.getPositions().stream().findFirst().ifPresent(p -> p.setTiers(Collections.emptyList()));
    when(sellerSiteService.getSite(anyLong())).thenReturn(inSite);

    // pubDm == null
    try {
      publisherSelfService.createDecisionMaker(1L, 1L, 1L, null);
      fail("Expected exception not thrown");
    } catch (GenevaValidationException e) {
      assertEquals(ServerErrorCodes.SERVER_INVALID_INPUT, e.getErrorCode());
    } catch (Exception ee) {
      fail("Unexpected Exception: " + ee.getMessage());
    }

    // buyer == null
    input = getPublisherTag(111L, null, 1L, 0);
    try {
      publisherSelfService.createDecisionMaker(1L, 1L, 1L, input);
      fail("Expected exception not thrown");
    } catch (GenevaValidationException e) {
      assertEquals(ServerErrorCodes.SERVER_INVALID_INPUT, e.getErrorCode());
    } catch (Exception ee) {
      fail("Unexpected Exception: " + ee.getMessage());
    }

    // buyer dm not enabled
    input = getPublisherTag(111L, REGULAR_BUYER, 1L, 0);
    try {
      publisherSelfService.createDecisionMaker(1L, 1L, 1L, input);
      fail("Expected exception not thrown");
    } catch (GenevaValidationException e) {
      assertEquals(
          ServerErrorCodes.SERVER_TAG_ADSOURCE_NOT_DECISION_MAKER_ENABLED, e.getErrorCode());
    } catch (Exception ee) {
      fail("Unexpected Exception: " + ee.getMessage());
    }

    when(publisherTagAssembler.apply(
            any(PublisherTagContext.class), any(Tag.class), any(PublisherTagDTO.class)))
        .thenReturn(getTag(DM_ENABLED_BUYER_TAG, DM_ENABLED_BUYER, 1L, 1L, null));
    when(publisherTagAssembler.make(any(PublisherTagContext.class), any(Tag.class)))
        .thenReturn(getPublisherTag(DM_ENABLED_BUYER_TAG, DM_ENABLED_BUYER, 1L, 0));
    when(publisherTierAssembler.apply(
            any(PublisherTierContext.class), any(Tier.class), any(PublisherTierDTO.class)))
        .thenReturn(getTier(1L, 0, TierType.SY_DECISION_MAKER, null));
    when(publisherTierAssembler.make(any(PublisherTierContext.class), any(Tier.class)))
        .thenReturn(
            getPublisherTier(
                1L,
                0,
                TierType.SY_DECISION_MAKER,
                List.of(getPublisherTag(DM_ENABLED_BUYER_TAG, DM_ENABLED_BUYER, 1L, 0))));
    Site updateSite = getSite();
    updateSite.setCompanyPid(1L);
    updateSite.setTags(
        new HashSet<>(List.of(getTag(DM_ENABLED_BUYER_TAG, DM_ENABLED_BUYER, 1L, 1L, null))));
    when(sellerSiteService.getSite(anyLong())).thenReturn(updateSite);
    when(publisherPositionAssembler.make(any(PublisherPositionContext.class), any(Position.class)))
        .thenReturn(
            getPublisherPosition(
                1L,
                TrafficType.SMART_YIELD,
                List.of(
                    getPublisherTier(
                        1L,
                        0,
                        TierType.SY_DECISION_MAKER,
                        List.of(getPublisherTag(DM_ENABLED_BUYER_TAG, DM_ENABLED_BUYER, 1L, 0))),
                    getPublisherTier(
                        2L,
                        0,
                        TierType.WATERFALL,
                        List.of(getPublisherTag(DM_ENABLED_BUYER_TAG, DM_ENABLED_BUYER, 1L, 0)))),
                getPublisherTag(DM_ENABLED_BUYER_TAG, DM_ENABLED_BUYER, 1L, 0)));
    when(publisherPositionAssembler.apply(any(), any(), any())).thenReturn(new Position());

    doNothing().when(tagLimitChecker).checkLimitsTagsInPosition(1L, 1L, 1L);

    PlacementVideoDTO placementVideoDTO = TestObjectsFactory.createDefaultPlacementVideoDTO();
    when(placementVideoService.getPlacementVideo(anyLong())).thenReturn(placementVideoDTO);

    input = getPublisherTag(DM_ENABLED_BUYER_TAG, DM_ENABLED_BUYER, 1L, 0);
    PublisherTagDTO decisionMaker = publisherSelfService.createDecisionMaker(1L, 1L, 1L, input);
    assertEquals(DM_ENABLED_BUYER, decisionMaker.getBuyer().getPid());
  }

  @Test
  void shouldUpdateDecisionMaker() {
    Site inSite = getSite();
    inSite.getPositions().stream().findFirst().ifPresent(p -> p.setTiers(Collections.emptyList()));
    when(sellerSiteService.getSite(anyLong())).thenReturn(inSite);

    // DM not exist
    PublisherTagDTO input = getPublisherTag(111L, DM_ENABLED_BUYER, 1L, 0);
    try {
      publisherSelfService.updateDecisionMaker(1L, 1L, 1L, input);
      fail("Expected exception not thrown");
    } catch (GenevaValidationException e) {
      assertEquals(ServerErrorCodes.SERVER_TIER_DECISION_MAKER_DOES_NOT_EXIST, e.getErrorCode());
    } catch (Exception ee) {
      fail("Unexpected Exception: " + ee.getMessage());
    }

    inSite = getSite();
    Tier dmTier =
        getTier(
            1,
            0,
            TierType.SY_DECISION_MAKER,
            List.of(getTag(DM_ENABLED_BUYER_TAG, DM_ENABLED_BUYER, 1L, 1L, null)));
    inSite.getPositions().stream()
        .findFirst()
        .ifPresent(p -> p.setTiers(Collections.singletonList(dmTier)));
    inSite.setTags(
        new HashSet<>(
            Collections.singletonList(
                getTag(DM_ENABLED_BUYER_TAG, DM_ENABLED_BUYER, 1L, 1L, null))));
    when(sellerSiteService.getSite(anyLong())).thenReturn(inSite);

    // PubDm null
    try {
      publisherSelfService.updateDecisionMaker(1L, 1L, 1L, null);
      fail("Expected exception not thrown");
    } catch (GenevaValidationException e) {
      assertEquals(ServerErrorCodes.SERVER_INVALID_INPUT, e.getErrorCode());
    } catch (Exception ee) {
      fail("Unexpected Exception: " + ee.getMessage());
    }

    // buyer null
    input = getPublisherTag(111L, null, 1L, 0);
    try {
      publisherSelfService.updateDecisionMaker(1L, 1L, 1L, input);
      fail("Expected exception not thrown");
    } catch (GenevaValidationException e) {
      assertEquals(ServerErrorCodes.SERVER_INVALID_INPUT, e.getErrorCode());
    } catch (Exception ee) {
      fail("Unexpected Exception: " + ee.getMessage());
    }

    // tag dm not enabled
    input = getPublisherTag(111L, REGULAR_BUYER, 1L, 0);
    try {
      publisherSelfService.updateDecisionMaker(1L, 1L, 1L, input);
      fail("Expected exception not thrown");
    } catch (GenevaValidationException e) {
      assertEquals(
          ServerErrorCodes.SERVER_TAG_ADSOURCE_NOT_DECISION_MAKER_ENABLED, e.getErrorCode());
    } catch (Exception ee) {
      fail("Unexpected Exception: " + ee.getMessage());
    }

    when(publisherTagAssembler.apply(
            any(PublisherTagContext.class), any(Tag.class), any(PublisherTagDTO.class)))
        .thenReturn(getTag(BID_DM_ENABLED_BUYER_TAG, BID_DM_ENABLED_BUYER, 1L, 1L, null));
    when(publisherTagAssembler.make(any(PublisherTagContext.class), any(Tag.class)))
        .thenReturn(getPublisherTag(BID_DM_ENABLED_BUYER_TAG, BID_DM_ENABLED_BUYER, 1L, 0));
    when(publisherTierAssembler.apply(
            any(PublisherTierContext.class), any(Tier.class), any(PublisherTierDTO.class)))
        .thenReturn(
            getTier(
                1L,
                0,
                TierType.SY_DECISION_MAKER,
                Collections.singletonList(
                    getTag(BID_DM_ENABLED_BUYER_TAG, BID_DM_ENABLED_BUYER, 1L, 1L, null))));
    when(publisherTierAssembler.make(any(PublisherTierContext.class), any(Tier.class)))
        .thenReturn(
            getPublisherTier(
                1L,
                0,
                TierType.SY_DECISION_MAKER,
                Collections.singletonList(
                    getPublisherTag(BID_DM_ENABLED_BUYER_TAG, BID_DM_ENABLED_BUYER, 1L, 0))));
    when(publisherPositionAssembler.make(any(PublisherPositionContext.class), any(Position.class)))
        .thenReturn(
            getPublisherPosition(
                1L,
                TrafficType.SMART_YIELD,
                Collections.singletonList(
                    getPublisherTier(
                        1L,
                        0,
                        TierType.SY_DECISION_MAKER,
                        Collections.singletonList(
                            getPublisherTag(
                                BID_DM_ENABLED_BUYER_TAG, BID_DM_ENABLED_BUYER, 1L, 0)))),
                getPublisherTag(BID_DM_ENABLED_BUYER_TAG, BID_DM_ENABLED_BUYER, 1L, 0)));
    inSite = getSite();
    Tier dmTier2 =
        getTier(
            1,
            0,
            TierType.SY_DECISION_MAKER,
            Collections.singletonList(
                getTag(BID_DM_ENABLED_BUYER_TAG, BID_DM_ENABLED_BUYER, 1L, 1L, null)));
    inSite.getPositions().stream()
        .findFirst()
        .ifPresent(p -> p.setTiers(Collections.singletonList(dmTier2)));
    inSite.setTags(
        new HashSet<>(
            Collections.singletonList(
                getTag(BID_DM_ENABLED_BUYER_TAG, BID_DM_ENABLED_BUYER, 1L, 1L, null))));
    when(sellerSiteService.getSite(anyLong())).thenReturn(inSite);

    doNothing().when(tagLimitChecker).checkLimitsTagsInPosition(1L, 1L, 1L);

    input = getPublisherTag(BID_DM_ENABLED_BUYER_TAG, BID_DM_ENABLED_BUYER, 1L, 0);
    PublisherTagDTO decisionMaker = publisherSelfService.updateDecisionMaker(1L, 1L, 1L, input);
    assertEquals(BID_DM_ENABLED_BUYER, decisionMaker.getBuyer().getPid());
  }

  @Test
  void shouldUpdateSmartYieldDemandSourceTags() {

    Site siteDTO = getSite();
    siteDTO.setTags(
        new HashSet<>(
            List.of(
                getTag(BID_DM_ENABLED_BUYER_TAG, BID_DM_ENABLED_BUYER, 1L, 1L, 1),
                getTag(2221L, 11111L, 1, 1, 1),
                getTag(2222L, 11112L, 1, 1, 1),
                getTag(2223L, 11113L, 1, 1, 1))));
    when(sellerSiteService.getSite(anyLong())).thenReturn(siteDTO);
    when(publisherPositionAssembler.make(any(PublisherPositionContext.class), any(Position.class)))
        .thenReturn(
            getPublisherPosition(
                1L,
                TrafficType.SMART_YIELD,
                Collections.singletonList(
                    getPublisherTier(
                        1L,
                        0,
                        TierType.SY_DECISION_MAKER,
                        Collections.singletonList(
                            getPublisherTag(
                                BID_DM_ENABLED_BUYER_TAG, BID_DM_ENABLED_BUYER, 1L, 0)))),
                getPublisherTag(BID_DM_ENABLED_BUYER_TAG, BID_DM_ENABLED_BUYER, 1L, 0)));
    when(publisherTagAssembler.apply(
            any(PublisherTagContext.class), any(Tag.class), any(PublisherTagDTO.class)))
        .thenReturn(
            getTag(2221L, 11111L, 1L, 1L, null),
            getTag(2222L, 11112L, 1L, 1L, null),
            getTag(2223L, 11113L, 1L, 1L, null));
    when(publisherTagAssembler.make(any(PublisherTagContext.class), any(Tag.class)))
        .thenReturn(
            getPublisherTag(2221L, 11111L, 1L, 0),
            getPublisherTag(2222L, 11112L, 1L, 0),
            getPublisherTag(2223L, 11113L, 1L, 0));

    // invalid user role
    List<PublisherTagDTO> pubTags =
        List.of(
            getPublisherTag(null, 11111L, 1L, null),
            getPublisherTag(null, 11112L, 1L, null),
            getPublisherTag(null, 11113L, 1L, null));
    doNothing().when(tagLimitChecker).checkLimitsTagsInPosition(1L, 1L, 1L);

    try {
      publisherSelfService.generateSmartYieldDemandSourceTags(1, 1, 1, pubTags);
    } catch (GenevaSecurityException gse) {
      assertEquals(SecurityErrorCodes.SECURITY_NOT_AUTHORIZED, gse.getErrorCode());
    } catch (Exception e) {
      fail("Unexpected Exception: " + e.getMessage());
    }

    // buyer null
    pubTags =
        List.of(
            getPublisherTag(null, 11111L, 1L, null),
            getPublisherTag(null, 11112L, 1L, null),
            getPublisherTag(null, 11113L, 1L, null),
            PublisherTagDTO.newBuilder()
                .withSite(PublisherSiteDTO.newBuilder().withPid(1L).build())
                .withPosition(PublisherPositionDTO.builder().withPid(1L).build())
                .build());
    verifyBadRequestErrorMessage(pubTags, ServerErrorCodes.SERVER_INVALID_INPUT);

    // buyerid null
    pubTags =
        List.of(
            getPublisherTag(null, 11111L, 1L, null),
            getPublisherTag(null, 11112L, 1L, null),
            getPublisherTag(null, 11113L, 1L, null),
            getPublisherTag(null, null, 1L, null));
    verifyBadRequestErrorMessage(pubTags, ServerErrorCodes.SERVER_INVALID_INPUT);

    // payload site provided but differs to request value
    pubTags =
        List.of(
            getPublisherTag(null, 11111L, 1L, null),
            getPublisherTag(null, 11112L, 1L, null),
            getPublisherTag(null, 11113L, 1L, null),
            PublisherTagDTO.newBuilder()
                .withPid(null)
                .withBuyer(PublisherBuyerDTO.newBuilder().withPid(11114L).build())
                .withSite(PublisherSiteDTO.newBuilder().withPid(2L).build())
                .withPosition(PublisherPositionDTO.builder().withPid(1L).build())
                .build());
    verifyBadRequestErrorMessage(pubTags, ServerErrorCodes.SERVER_INVALID_INPUT);

    // payload position provided but differs to request value
    pubTags =
        List.of(
            getPublisherTag(null, 11111L, 1L, null),
            getPublisherTag(null, 11112L, 1L, null),
            getPublisherTag(null, 11113L, 1L, null),
            getPublisherTag(null, 11114L, 2L, null));
    verifyBadRequestErrorMessage(pubTags, ServerErrorCodes.SERVER_INVALID_INPUT);

    // update with an invalid tag position provided but differs to request value
    pubTags =
        List.of(
            getPublisherTag(null, 11111L, 1L, null),
            getPublisherTag(null, 11112L, 1L, null),
            getPublisherTag(null, 11113L, 1L, null),
            getPublisherTag(33333L, 11114L, 1L, null));
    verifyBadRequestErrorMessage(pubTags, ServerErrorCodes.SERVER_TAG_NOT_FOUND);

    pubTags =
        List.of(
            getPublisherTag(null, 11111L, 1L, null),
            getPublisherTag(null, 11112L, 1L, null),
            getPublisherTag(null, 11113L, 1L, null),
            getPublisherTag(BID_DM_ENABLED_BUYER_TAG, BID_DM_ENABLED_BUYER, 1L, 1));
    List<PublisherTagDTO> createdPublisherTags =
        publisherSelfService.generateSmartYieldDemandSourceTags(1, 1, 1, pubTags);
    assertEquals(4, createdPublisherTags.size());
  }

  @Test
  void shouldThrowWhenGettingTiersButSelfServiceIsNotEnabledForSitesCompany() {
    long companyPid = 1L;
    long sitePid = 2L;
    Site site = getSite();
    site.setCompanyPid(companyPid);
    when(userContext.isPublisherSelfServeEnabled(companyPid)).thenReturn(false);
    when(sellerSiteService.getValidatedSiteForPublisher(sitePid, companyPid)).thenReturn(site);
    var exception =
        assertThrows(
            GenevaSecurityException.class,
            () -> publisherSelfService.getTiers(PUBLISHER_PID, sitePid, 3L));

    assertEquals(SecurityErrorCodes.SECURITY_NOT_AUTHORIZED, exception.getErrorCode());
  }

  @Test
  void shouldFailToGetTagsWhenSelfServiceIsNotEnabledForSitesCompany() {
    long companyPid = 1L;
    long sitePid = 2L;
    Site site = getSite();
    site.setCompanyPid(companyPid);
    when(userContext.isPublisherSelfServeEnabled(companyPid)).thenReturn(false);
    when(sellerSiteService.getValidatedSiteForPublisher(sitePid, companyPid)).thenReturn(site);
    var exception =
        assertThrows(
            GenevaSecurityException.class,
            () -> publisherSelfService.getTags(PUBLISHER_PID, sitePid, 3L));
    assertEquals(SecurityErrorCodes.SECURITY_NOT_AUTHORIZED, exception.getErrorCode());
  }

  @Test
  void shouldGetTags() {
    long companyPid = 1L;
    long sitePid = 2L;
    long positionPid = 3L;
    Site site = getSite();
    site.setCompanyPid(companyPid);
    when(userContext.isPublisherSelfServeEnabled(companyPid)).thenReturn(true);
    when(sellerSiteService.getValidatedSiteForPublisher(sitePid, companyPid)).thenReturn(site);

    var tag = getSimpleTag();
    tag.setSitePid(sitePid);
    var tagPosition = new TagPosition();
    tagPosition.setPid(positionPid);
    tag.setPosition(tagPosition);
    when(tagViewRepository.findBySitePidAndPositionPidAndStatusNotIn(
            sitePid, positionPid, List.of(Status.DELETED)))
        .thenReturn(List.of(tag));

    var dealTerm = new DealTermView();
    dealTerm.setSitePid(sitePid);
    dealTerm.setTagPid(1L);
    dealTerm.setNexageRevenueShare(new BigDecimal("0.1"));
    dealTerm.setRtbFee(new BigDecimal("0.2"));
    when(dealTermViewRepository.findBySitePidAndTagPidInOrderByEffectiveDateDesc(
            sitePid, List.of(tag.getPid())))
        .thenReturn(List.of(dealTerm));
    var tagDTO = mock(PublisherTagDTO.class);

    var pubDealTerm =
        PublisherSiteDealTermDTO.newBuilder()
            .withNexageRevenueShare(dealTerm.getNexageRevenueShare())
            .withRtbFee(dealTerm.getRtbFee())
            .build();
    when(tagDTO.getCurrentDealTerm()).thenReturn(pubDealTerm);
    when(publisherTagAssembler.make(any(PublisherTagContext.class), any(Tag.class)))
        .thenReturn(tagDTO);

    var out = publisherSelfService.getTags(PUBLISHER_PID, sitePid, positionPid);
    assertEquals(1, out.size());
    var outTag = out.get(0);
    assertNotNull(outTag);
    var outDeal = outTag.getCurrentDealTerm();
    assertNotNull(outDeal);
    assertEquals(dealTerm.getNexageRevenueShare(), outDeal.getNexageRevenueShare());
    assertEquals(dealTerm.getRtbFee(), outDeal.getRtbFee());
  }

  @Test
  void shouldSingleTag() {
    long companyPid = 1L;
    long sitePid = 2L;
    long positionPid = 3L;
    Site site = getSite();
    site.setCompanyPid(companyPid);
    when(userContext.isPublisherSelfServeEnabled(companyPid)).thenReturn(true);
    when(sellerSiteService.getValidatedSiteForPublisher(sitePid, companyPid)).thenReturn(site);

    var tag = getSimpleTag();
    tag.setSitePid(sitePid);
    var tagPosition = new TagPosition();
    tagPosition.setPid(positionPid);
    tag.setPosition(tagPosition);
    when(tagViewRepository.findByPidAndSitePidAndPositionPidAndStatusNotIn(
            tag.getPid(), sitePid, positionPid, List.of(Status.DELETED)))
        .thenReturn(Optional.of(tag));
    var tagDTO = mock(PublisherTagDTO.class);
    when(tagDTO.getPid()).thenReturn(tag.getPid());
    when(publisherTagAssembler.make(any(PublisherTagContext.class), any(Tag.class)))
        .thenReturn(tagDTO);

    var out = publisherSelfService.getTag(PUBLISHER_PID, sitePid, positionPid, tag.getPid());
    assertNotNull(out);
    assertEquals(tag.getPid(), out.getPid());
  }

  private TagView getSimpleTag() {
    var simpleTag = new TagView();
    simpleTag.setPid(1L);
    simpleTag.setStatus(Status.ACTIVE);
    simpleTag.setBuyerPid(2L);
    simpleTag.setIdentifier("1");

    return simpleTag;
  }

  @Test
  void shouldFailToGetPositionsWhenSelfServiceIsNotEnabledForSitesCompany() {
    long companyPid = 1L;
    long sitePid = 2L;
    Site site = getSite();
    site.setCompanyPid(companyPid);
    when(userContext.isPublisherSelfServeEnabled(companyPid)).thenReturn(false);
    when(sellerSiteService.getValidatedSiteForPublisher(sitePid, companyPid)).thenReturn(site);
    var exception =
        assertThrows(
            GenevaSecurityException.class,
            () -> publisherSelfService.getPositions(PUBLISHER_PID, sitePid, false));

    assertEquals(SecurityErrorCodes.SECURITY_NOT_AUTHORIZED, exception.getErrorCode());
  }

  @Test
  void shouldCreatePositionAndUseGeneratedIdWhenNameFieldIsNull() {
    Site site = getSite();
    when(sellerSiteService.getSite(anyLong())).thenReturn(site);
    when(userContext.isPublisherSelfServeEnabled(anyLong())).thenReturn(true);
    when(publisherPositionAssembler.apply(
            any(PublisherPositionContext.class),
            any(Position.class),
            any(PublisherPositionDTO.class)))
        .thenReturn(new Position());
    when(publisherPositionAssembler.make(any(PublisherPositionContext.class), any(Position.class)))
        .then(
            invocation -> {
              Position position = invocation.getArgument(1);
              PublisherPositionDTO publisherPosition = new PublisherPositionDTO();
              publisherPosition.setName(position.getName());
              return publisherPosition;
            });
    when(sellerPositionService.createPosition(anyLong(), any(Position.class)))
        .thenAnswer(
            invocation -> {
              Position position = invocation.getArgument(1);
              site.addPosition(position);
              return null;
            });
    PublisherPositionDTO publisherPosition =
        getPublisherPosition(1L, TrafficType.MEDIATION, ImmutableList.of(), null);
    publisherPosition.setName(null);
    PlacementVideoDTO placementVideoDTO = TestObjectsFactory.createDefaultPlacementVideoDTO();
    placementVideoDTO.setLinearity(PlacementVideoLinearity.LINEAR);
    placementVideoDTO.setLongform(true);
    when(placementVideoService.populateVideoData(any(), any())).thenReturn(placementVideoDTO);
    publisherPosition.setPlacementVideo(placementVideoDTO);
    PublisherPositionDTO createdPosition =
        publisherSelfService.createPosition(site.getPid(), publisherPosition, false);
    assertEquals(32, createdPosition.getName().length());
  }

  @Test
  void shouldThrowExceptionWhenCreatingPositionAndDoohPositionValidationFails() {
    long sitePid = 72L;
    Site site = getSite(sitePid);
    site.setType(Type.DESKTOP);

    when(userContext.isPublisherSelfServeEnabled(anyLong())).thenReturn(true);
    when(sellerSiteService.getSite(anyLong())).thenReturn(site);

    PublisherPositionDTO publisherPosition =
        getPublisherPosition(72L, TrafficType.MEDIATION, Collections.emptyList(), null);
    publisherPosition.setDooh(new PlacementDoohDTO());
    doThrow(EntityConstraintViolationException.class)
        .when(beanValidationService)
        .validate(publisherPosition, CreateGroup.class, Default.class);
    assertThrows(
        EntityConstraintViolationException.class,
        () -> publisherSelfService.createPosition(sitePid, publisherPosition, true));
  }

  @Test
  void shouldThrowExceptionWhenCreatingPositionWithNoSiteAndDoohPositionFailsValidation() {
    long sitePid = 72L;
    Site site = getSite(sitePid);
    site.setType(Type.DESKTOP);

    when(userContext.isPublisherSelfServeEnabled(anyLong())).thenReturn(true);
    when(sellerSiteService.getSite(anyLong())).thenReturn(site);

    PublisherPositionDTO publisherPosition =
        getPublisherPosition(72L, TrafficType.MEDIATION, Collections.emptyList(), null);
    publisherPosition.setSite(null);
    publisherPosition.setDooh(new PlacementDoohDTO());
    doThrow(EntityConstraintViolationException.class)
        .when(beanValidationService)
        .validate(publisherPosition, CreateGroup.class, Default.class);
    assertThrows(
        EntityConstraintViolationException.class,
        () -> publisherSelfService.createPosition(sitePid, publisherPosition, true));
  }

  @Test
  void shouldThrowExceptionWhenUpdatingPositionForDoohSiteAndDoohObjectIsNull() {
    long sitePid = 72L;
    Site site = getSite(sitePid);
    site.setType(Type.DOOH);

    when(userContext.isPublisherSelfServeEnabled(anyLong())).thenReturn(true);
    when(sellerSiteService.getValidatedSiteForPublisher(anyLong(), anyLong())).thenReturn(site);

    PublisherPositionDTO publisherPosition =
        getPublisherPosition(72L, TrafficType.MEDIATION, Collections.emptyList(), null);
    publisherPosition.setSite(null);
    publisherPosition.setDooh(null);
    doThrow(EntityConstraintViolationException.class)
        .when(beanValidationService)
        .validate(publisherPosition, UpdateGroup.class);
    assertThrows(
        EntityConstraintViolationException.class,
        () -> publisherSelfService.updatePosition(PUBLISHER_PID, sitePid, publisherPosition, true));
  }

  @Test
  void shouldFetchOnePosition() {
    Site site = getSite();
    when(sellerSiteService.getValidatedSiteForPublisher(anyLong(), anyLong())).thenReturn(site);
    when(userContext.isPublisherSelfServeEnabled(anyLong())).thenReturn(true);
    when(publisherPositionAssembler.make(any(PublisherPositionContext.class), any(Position.class)))
        .then(
            invocation -> {
              PublisherPositionDTO publisherPosition = new PublisherPositionDTO();
              publisherPosition.setPid(1L);
              publisherPosition.setName("testPosition");
              return publisherPosition;
            });

    PublisherPositionDTO actualPublisherPositionDTO =
        publisherSelfService.getPosition(PUBLISHER_PID, site.getPid(), 1L, false);
    assertNull(actualPublisherPositionDTO.getPlacementVideo());

    // Test getPosition with Video
    PublisherPositionDTO publisherPositionWithVideo =
        createPublisherPosition(VideoSupport.VIDEO_AND_BANNER);
    publisherPositionWithVideo.setPlacementCategory(PlacementCategory.INSTREAM_VIDEO);
    publisherPositionWithVideo.setLongform(true);

    PlacementVideoDTO placementVideoDTO = TestObjectsFactory.createDefaultPlacementVideoDTO();
    placementVideoDTO.setDapPlayerType(DapPlayerType.O2);
    when(placementVideoService.getPlacementVideo(anyLong())).thenReturn(placementVideoDTO);
    when(publisherPositionAssembler.make(any(PublisherPositionContext.class), any(Position.class)))
        .thenReturn(publisherPositionWithVideo);

    PublisherPositionDTO PublisherPositionDTOWithVideo =
        publisherSelfService.getPosition(PUBLISHER_PID, site.getPid(), 1L, false);
    assertNotNull(PublisherPositionDTOWithVideo.getPlacementVideo());

    // Test getPosition with Video position but empty video object
    PublisherPositionDTO publisherPositionWithoutVideo =
        createPublisherPosition(VideoSupport.VIDEO_AND_BANNER);

    when(placementVideoService.getPlacementVideo(anyLong())).thenReturn(null);
    when(publisherPositionAssembler.make(any(PublisherPositionContext.class), any(Position.class)))
        .thenReturn(publisherPositionWithoutVideo);

    PublisherPositionDTO PublisherPositionDTOWithoutVideo =
        publisherSelfService.getPosition(PUBLISHER_PID, site.getPid(), 1L, false);
    assertNull(PublisherPositionDTOWithoutVideo.getPlacementVideo());

    // Test getPosition without Video
    PublisherPositionDTO publisherPosition = createPublisherPosition(VideoSupport.BANNER);

    when(publisherPositionAssembler.make(any(PublisherPositionContext.class), any(Position.class)))
        .thenReturn(publisherPosition);

    PublisherPositionDTO PublisherPositionDTO =
        publisherSelfService.getPosition(PUBLISHER_PID, site.getPid(), 1L, false);
    assertNull(PublisherPositionDTO.getPlacementVideo());

    // Test getPosition with Video and companion
    PublisherPositionDTO publisherPositionWithVideoAndCompanion =
        createPublisherPosition(VideoSupport.VIDEO_AND_BANNER);
    publisherPositionWithVideoAndCompanion.setPlacementCategory(PlacementCategory.INSTREAM_VIDEO);
    publisherPositionWithVideoAndCompanion.setLongform(true);

    PlacementVideoDTO placementVideoDTOWithCompanion =
        TestObjectsFactory.createDefaultPlacementVideoDTO();
    PlacementVideoCompanionDTO placementVideoCompanionDTO =
        TestObjectsFactory.createDefaultPlacementVideoCompanionDTO();
    placementVideoDTOWithCompanion.addCompanion(placementVideoCompanionDTO);

    placementVideoDTOWithCompanion.setDapPlayerType(DapPlayerType.O2);
    when(placementVideoService.getPlacementVideo(anyLong()))
        .thenReturn(placementVideoDTOWithCompanion);
    when(publisherPositionAssembler.make(any(PublisherPositionContext.class), any(Position.class)))
        .thenReturn(publisherPositionWithVideoAndCompanion);

    PublisherPositionDTO PublisherPositionDTOWithVideoAndCompanion =
        publisherSelfService.getPosition(PUBLISHER_PID, site.getPid(), 1L, false);

    assertAll(
        "Test getPosition with Video and companion",
        () -> assertNotNull(PublisherPositionDTOWithVideoAndCompanion.getPlacementVideo()),
        () ->
            assertNotNull(
                PublisherPositionDTOWithVideoAndCompanion.getPlacementVideo().getCompanions()),
        () ->
            assertEquals(
                placementVideoDTOWithCompanion,
                PublisherPositionDTOWithVideoAndCompanion.getPlacementVideo()),
        () ->
            assertEquals(
                placementVideoDTOWithCompanion.getCompanions(),
                PublisherPositionDTOWithVideoAndCompanion.getPlacementVideo().getCompanions()));

    // Test getPosition with Video and Multiple companion
    PublisherPositionDTO publisherPositionWithVideoAndMultipleCompanion =
        createPublisherPosition(VideoSupport.VIDEO_AND_BANNER);
    publisherPositionWithVideoAndMultipleCompanion.setPlacementCategory(
        PlacementCategory.INSTREAM_VIDEO);
    publisherPositionWithVideoAndMultipleCompanion.setLongform(true);

    PlacementVideoDTO placementVideoDTOWithMultipleCompanion =
        TestObjectsFactory.createDefaultPlacementVideoDTO();
    PlacementVideoCompanionDTO placementVideoCompanionDTO2 = new PlacementVideoCompanionDTO();
    placementVideoCompanionDTO2.setHeight(250);
    placementVideoCompanionDTO2.setWidth(350);
    placementVideoDTOWithMultipleCompanion.addCompanion(placementVideoCompanionDTO);
    placementVideoDTOWithMultipleCompanion.addCompanion(placementVideoCompanionDTO2);

    placementVideoDTOWithMultipleCompanion.setDapPlayerType(DapPlayerType.O2);
    when(placementVideoService.getPlacementVideo(anyLong()))
        .thenReturn(placementVideoDTOWithMultipleCompanion);
    when(publisherPositionAssembler.make(any(PublisherPositionContext.class), any(Position.class)))
        .thenReturn(publisherPositionWithVideoAndMultipleCompanion);

    PublisherPositionDTO PublisherPositionDTOWithVideoAndMultipleCompanion =
        publisherSelfService.getPosition(PUBLISHER_PID, site.getPid(), 1L, false);

    assertAll(
        "Test getPosition with Video and Multiple companion",
        () -> assertNotNull(PublisherPositionDTOWithVideoAndMultipleCompanion.getPlacementVideo()),
        () ->
            assertNotNull(
                PublisherPositionDTOWithVideoAndMultipleCompanion.getPlacementVideo()
                    .getCompanions()),
        () ->
            assertEquals(
                placementVideoDTOWithMultipleCompanion,
                PublisherPositionDTOWithVideoAndMultipleCompanion.getPlacementVideo()),
        () ->
            assertEquals(
                placementVideoDTOWithMultipleCompanion.getCompanions().size(),
                PublisherPositionDTOWithVideoAndMultipleCompanion.getPlacementVideo()
                    .getCompanions()
                    .size()),
        () ->
            assertEquals(
                placementVideoDTOWithMultipleCompanion.getCompanions(),
                PublisherPositionDTOWithVideoAndMultipleCompanion.getPlacementVideo()
                    .getCompanions()));
  }

  @Test
  void shouldSetLongformBasedOnPlacementVideoDTO() {
    Site site = getSite();
    when(sellerSiteService.getValidatedSiteForPublisher(anyLong(), anyLong())).thenReturn(site);
    when(userContext.isPublisherSelfServeEnabled(anyLong())).thenReturn(true);
    when(publisherPositionAssembler.make(any(PublisherPositionContext.class), any(Position.class)))
        .then(
            invocation -> {
              PublisherPositionDTO publisherPosition = new PublisherPositionDTO();
              publisherPosition.setPid(1L);
              publisherPosition.setName("testPosition");
              publisherPosition.setLongform(true);
              return publisherPosition;
            });

    Position position = new Position();

    PlacementVideoDTO placementVideoDTO = TestObjectsFactory.createDefaultPlacementVideoDTO();
    placementVideoDTO.setLinearity(PlacementVideoLinearity.LINEAR);
    placementVideoDTO.setLongform(true);

    when(placementVideoService.getPlacementVideo(anyLong())).thenReturn(placementVideoDTO);

    PublisherPositionDTO actualPublisherPositionDTO =
        publisherSelfService.getPosition(PUBLISHER_PID, site.getPid(), 1L, false);

    // Assert
    assertNotNull(actualPublisherPositionDTO);
    assertTrue(actualPublisherPositionDTO.isLongform());
  }

  @Test
  void shouldFetchOnePositionWithDifferentPlayerTypes() {
    Site site = getSite();
    when(sellerSiteService.getValidatedSiteForPublisher(anyLong(), anyLong())).thenReturn(site);
    when(userContext.isPublisherSelfServeEnabled(anyLong())).thenReturn(true);
    when(publisherPositionAssembler.make(any(PublisherPositionContext.class), any(Position.class)))
        .then(
            invocation -> {
              PublisherPositionDTO publisherPosition = new PublisherPositionDTO();
              publisherPosition.setPid(1L);
              publisherPosition.setName("testPosition");
              return publisherPosition;
            });

    PublisherPositionDTO actualPublisherPositionDTO =
        publisherSelfService.getPosition(PUBLISHER_PID, site.getPid(), 1L, false);
    assertNull(actualPublisherPositionDTO.getPlacementVideo());

    // Test getPosition with Video
    PublisherPositionDTO publisherPositionWithVideo =
        createPublisherPosition(VideoSupport.VIDEO_AND_BANNER);

    PlacementVideoDTO placementVideoDTO = TestObjectsFactory.createDefaultPlacementVideoDTO();
    placementVideoDTO.setDapPlayerType(DapPlayerType.O2);
    when(placementVideoService.getPlacementVideo(anyLong())).thenReturn(placementVideoDTO);
    when(publisherPositionAssembler.make(any(PublisherPositionContext.class), any(Position.class)))
        .thenReturn(publisherPositionWithVideo);

    PublisherPositionDTO PublisherPositionDTOWithVideo =
        publisherSelfService.getPosition(PUBLISHER_PID, site.getPid(), 1L, false);
    assertNull(PublisherPositionDTOWithVideo.getPlacementVideo());

    PublisherPositionDTO publisherPositionWithVideoYVAP =
        createPublisherPosition(VideoSupport.VIDEO_AND_BANNER);

    PlacementVideoDTO placementVideoDTOYVAP = TestObjectsFactory.createDefaultPlacementVideoDTO();
    placementVideoDTOYVAP.setDapPlayerType(DapPlayerType.YVAP);
    when(placementVideoService.getPlacementVideo(anyLong())).thenReturn(placementVideoDTOYVAP);
    when(publisherPositionAssembler.make(any(PublisherPositionContext.class), any(Position.class)))
        .thenReturn(publisherPositionWithVideoYVAP);

    PublisherPositionDTO PublisherPositionDTOWithVideoYVAP =
        publisherSelfService.getPosition(PUBLISHER_PID, site.getPid(), 1L, false);
    assertNull(PublisherPositionDTOWithVideoYVAP.getPlacementVideo());

    PublisherPositionDTO publisherPositionWithVideoNullPlayerType =
        createPublisherPosition(VideoSupport.VIDEO_AND_BANNER);

    PlacementVideoDTO placementVideoDTONullPlayerType =
        TestObjectsFactory.createDefaultPlacementVideoDTO();
    when(placementVideoService.getPlacementVideo(anyLong()))
        .thenReturn(placementVideoDTONullPlayerType);
    when(publisherPositionAssembler.make(any(PublisherPositionContext.class), any(Position.class)))
        .thenReturn(publisherPositionWithVideoYVAP);

    PublisherPositionDTO PublisherPositionDTOWithVideoNullPlayerType =
        publisherSelfService.getPosition(PUBLISHER_PID, site.getPid(), 1L, false);
    assertNull(PublisherPositionDTOWithVideoNullPlayerType.getPlacementVideo());
  }

  @Test
  void shouldFetchAllPositions() {
    Site site = getSite();
    when(sellerSiteService.getValidatedSiteForPublisher(anyLong(), anyLong())).thenReturn(site);
    when(userContext.isPublisherSelfServeEnabled(anyLong())).thenReturn(true);
    when(publisherPositionAssembler.make(any(PublisherPositionContext.class), any(Position.class)))
        .then(
            invocation -> {
              PublisherPositionDTO publisherPosition = new PublisherPositionDTO();
              publisherPosition.setPid(1L);
              publisherPosition.setName("testPosition");
              return publisherPosition;
            });

    List<PublisherPositionDTO> actualPublisherPositionDTOS =
        publisherSelfService.getPositions(PUBLISHER_PID, site.getPid(), false);
    assertNull(actualPublisherPositionDTOS.get(0).getPlacementVideo());

    // Test getPositions with Video
    PublisherPositionDTO publisherPositionWithVideo =
        createPublisherPosition(VideoSupport.VIDEO_AND_BANNER);
    publisherPositionWithVideo.setPlacementCategory(PlacementCategory.INSTREAM_VIDEO);
    publisherPositionWithVideo.setLongform(true);

    PlacementVideoDTO placementVideoDTO = TestObjectsFactory.createDefaultPlacementVideoDTO();
    placementVideoDTO.setDapPlayerType(DapPlayerType.O2);
    when(placementVideoService.getPlacementVideo(anyLong())).thenReturn(placementVideoDTO);
    when(publisherPositionAssembler.make(any(PublisherPositionContext.class), any(Position.class)))
        .thenReturn(publisherPositionWithVideo);

    List<PublisherPositionDTO> PublisherPositionDTOSWithVideo =
        publisherSelfService.getPositions(PUBLISHER_PID, site.getPid(), false);
    assertNotNull(PublisherPositionDTOSWithVideo.get(0).getPlacementVideo());

    // Test getPositions without Video
    PublisherPositionDTO publisherPosition = createPublisherPosition(VideoSupport.BANNER);

    when(publisherPositionAssembler.make(any(PublisherPositionContext.class), any(Position.class)))
        .thenReturn(publisherPosition);

    List<PublisherPositionDTO> PublisherPositionDTOS =
        publisherSelfService.getPositions(PUBLISHER_PID, site.getPid(), false);
    assertNull(PublisherPositionDTOS.get(0).getPlacementVideo());

    // Test getPositions with Video and companion
    PublisherPositionDTO publisherPositionWithVideoAndCompanion =
        createPublisherPosition(VideoSupport.VIDEO_AND_BANNER);
    publisherPositionWithVideoAndCompanion.setPlacementCategory(PlacementCategory.INSTREAM_VIDEO);
    publisherPositionWithVideoAndCompanion.setLongform(true);

    PlacementVideoDTO placementVideoDTOWithCompanion =
        TestObjectsFactory.createDefaultPlacementVideoDTO();
    PlacementVideoCompanionDTO placementVideoCompanionDTO =
        TestObjectsFactory.createDefaultPlacementVideoCompanionDTO();
    placementVideoDTOWithCompanion.addCompanion(placementVideoCompanionDTO);

    placementVideoDTOWithCompanion.setDapPlayerType(DapPlayerType.O2);
    when(placementVideoService.getPlacementVideo(anyLong()))
        .thenReturn(placementVideoDTOWithCompanion);
    when(publisherPositionAssembler.make(any(PublisherPositionContext.class), any(Position.class)))
        .thenReturn(publisherPositionWithVideoAndCompanion);

    List<PublisherPositionDTO> PublisherPositionDTOWithVideoAndCompanion =
        publisherSelfService.getPositions(PUBLISHER_PID, site.getPid(), false);

    assertAll(
        "Test getPositions with Video and companion",
        () -> assertNotNull(PublisherPositionDTOWithVideoAndCompanion.get(0).getPlacementVideo()),
        () ->
            assertNotNull(
                PublisherPositionDTOWithVideoAndCompanion.get(0)
                    .getPlacementVideo()
                    .getCompanions()),
        () ->
            assertEquals(
                placementVideoDTOWithCompanion,
                PublisherPositionDTOWithVideoAndCompanion.get(0).getPlacementVideo()),
        () ->
            assertEquals(
                placementVideoDTOWithCompanion.getCompanions(),
                PublisherPositionDTOWithVideoAndCompanion.get(0)
                    .getPlacementVideo()
                    .getCompanions()));

    // Test getPositions with Video and Multiple companion
    PublisherPositionDTO publisherPositionWithVideoAndMultipleCompanion =
        createPublisherPosition(VideoSupport.VIDEO_AND_BANNER);
    publisherPositionWithVideoAndMultipleCompanion.setPlacementCategory(
        PlacementCategory.INSTREAM_VIDEO);
    publisherPositionWithVideoAndMultipleCompanion.setLongform(true);

    PlacementVideoDTO placementVideoDTOWithMultipleCompanion =
        TestObjectsFactory.createDefaultPlacementVideoDTO();
    PlacementVideoCompanionDTO placementVideoCompanionDTO2 = new PlacementVideoCompanionDTO();
    placementVideoCompanionDTO2.setHeight(250);
    placementVideoCompanionDTO2.setWidth(350);
    placementVideoDTOWithMultipleCompanion.addCompanion(placementVideoCompanionDTO);
    placementVideoDTOWithMultipleCompanion.addCompanion(placementVideoCompanionDTO2);

    placementVideoDTOWithMultipleCompanion.setDapPlayerType(DapPlayerType.O2);
    when(placementVideoService.getPlacementVideo(anyLong()))
        .thenReturn(placementVideoDTOWithMultipleCompanion);
    when(publisherPositionAssembler.make(any(PublisherPositionContext.class), any(Position.class)))
        .thenReturn(publisherPositionWithVideoAndMultipleCompanion);

    List<PublisherPositionDTO> PublisherPositionDTOWithVideoAndMultipleCompanion =
        publisherSelfService.getPositions(PUBLISHER_PID, site.getPid(), false);

    assertAll(
        "Test getPositions with Video and Multiple companion",
        () ->
            assertNotNull(
                PublisherPositionDTOWithVideoAndMultipleCompanion.get(0).getPlacementVideo()),
        () ->
            assertNotNull(
                PublisherPositionDTOWithVideoAndMultipleCompanion.get(0)
                    .getPlacementVideo()
                    .getCompanions()),
        () ->
            assertEquals(
                placementVideoDTOWithMultipleCompanion,
                PublisherPositionDTOWithVideoAndMultipleCompanion.get(0).getPlacementVideo()),
        () ->
            assertEquals(
                placementVideoDTOWithMultipleCompanion.getCompanions().size(),
                PublisherPositionDTOWithVideoAndMultipleCompanion.get(0)
                    .getPlacementVideo()
                    .getCompanions()
                    .size()),
        () ->
            assertEquals(
                placementVideoDTOWithMultipleCompanion.getCompanions(),
                PublisherPositionDTOWithVideoAndMultipleCompanion.get(0)
                    .getPlacementVideo()
                    .getCompanions()));
  }

  @Test
  void shouldThrowGenevaValidationExceptionWhenYvapInfoMediaTypeIsNotMp4() {
    Site site = getSite();
    when(sellerSiteService.getSite(anyLong())).thenReturn(site);
    when(userContext.isPublisherSelfServeEnabled(anyLong())).thenReturn(true);

    PlacementVideoDTO placementVideoDTO =
        TestObjectsFactory.createDefaultPlacementVideoDTOWithPlaylistInfo();
    placementVideoDTO.setLinearity(PlacementVideoLinearity.LINEAR);
    placementVideoDTO.getPlaylistInfo().get(0).setFallbackURL("someurl.mp3");

    PublisherPositionDTO publisherPosition =
        getPublisherPosition(1L, TrafficType.MEDIATION, ImmutableList.of(), null);
    publisherPosition.setName(null);
    publisherPosition.setVideoSupport(VideoSupport.VIDEO_AND_BANNER);
    publisherPosition.setVideoLinearity(VideoLinearity.LINEAR);
    publisherPosition.setPlacementVideo(placementVideoDTO);
    when(publisherPositionAssembler.apply(
            any(PublisherPositionContext.class),
            any(Position.class),
            any(PublisherPositionDTO.class)))
        .thenReturn(new Position());
    when(placementVideoService.populateVideoData(any(), any())).thenReturn(placementVideoDTO);

    assertThrows(
        GenevaValidationException.class,
        () -> publisherSelfService.createPosition(site.getPid(), publisherPosition, false));
  }

  @Test
  void shouldCreatePositionWithOldDTOAndPlacementVideoDTO() {
    Site site = getSite();
    when(sellerSiteService.getSite(anyLong())).thenReturn(site);
    when(userContext.isPublisherSelfServeEnabled(anyLong())).thenReturn(true);
    when(publisherPositionAssembler.apply(
            any(PublisherPositionContext.class),
            any(Position.class),
            any(PublisherPositionDTO.class)))
        .thenReturn(new Position());
    when(publisherPositionAssembler.make(any(PublisherPositionContext.class), any(Position.class)))
        .then(
            invocation -> {
              Position position = invocation.getArgument(1);
              PublisherPositionDTO publisherPosition = new PublisherPositionDTO();
              publisherPosition.setPlacementCategory(PlacementCategory.INSTREAM_VIDEO);
              publisherPosition.setLongform(true);
              publisherPosition.setName(position.getName());
              return publisherPosition;
            });
    when(sellerPositionService.createPosition(anyLong(), any(Position.class)))
        .thenAnswer(
            invocation -> {
              Position position = invocation.getArgument(1);
              site.addPosition(position);
              return null;
            });
    when(placementVideoService.save(any(), any()))
        .thenAnswer(invocation -> invocation.getArgument(0));

    PublisherPositionDTO publisherPosition =
        getPublisherPosition(1L, TrafficType.MEDIATION, ImmutableList.of(), null);
    publisherPosition.setName(null);
    publisherPosition.setVideoSupport(VideoSupport.VIDEO_AND_BANNER);
    publisherPosition.setVideoLinearity(VideoLinearity.LINEAR);
    publisherPosition.setPlacementCategory(PlacementCategory.INSTREAM_VIDEO);
    publisherPosition.setLongform(true);

    PlacementVideoDTO placementVideoDTO = TestObjectsFactory.createDefaultPlacementVideoDTO();
    placementVideoDTO.setLinearity(PlacementVideoLinearity.LINEAR);
    placementVideoDTO.setLongform(true);
    when(placementVideoService.populateVideoData(any(), any())).thenReturn(placementVideoDTO);
    publisherPosition.setPlacementVideo(placementVideoDTO);

    PublisherPositionDTO createdPosition =
        publisherSelfService.createPosition(site.getPid(), publisherPosition, false);
    assertEquals(32, createdPosition.getName().length());
    assertNotNull(createdPosition.getPlacementVideo());

    publisherPosition.setVideoSupport(VideoSupport.VIDEO);
    createdPosition = publisherSelfService.createPosition(site.getPid(), publisherPosition, false);
    assertNotNull(createdPosition.getPlacementVideo());

    publisherPosition.setVideoSupport(VideoSupport.BANNER);
    createdPosition = publisherSelfService.createPosition(site.getPid(), publisherPosition, false);
    assertNotNull(createdPosition.getPlacementVideo());
    assertTrue(createdPosition.getPlacementVideo().isLongform());
  }

  @Test
  void shouldCreatePositionWithInstreamVideoAndValidPlacementVideoDTO() {
    Site site = getSite();
    when(sellerSiteService.getSite(anyLong())).thenReturn(site);

    when(publisherPositionAssembler.apply(
            any(PublisherPositionContext.class),
            any(Position.class),
            any(PublisherPositionDTO.class)))
        .thenReturn(new Position());
    when(publisherPositionAssembler.make(any(PublisherPositionContext.class), any(Position.class)))
        .then(
            invocation -> {
              Position position = invocation.getArgument(1);
              PublisherPositionDTO publisherPosition = new PublisherPositionDTO();
              publisherPosition.setName(position.getName());
              return publisherPosition;
            });
    when(sellerPositionService.createPosition(anyLong(), any(Position.class)))
        .thenAnswer(
            invocation -> {
              Position position = invocation.getArgument(1);
              site.addPosition(position);
              return null;
            });
    when(placementVideoService.save(any(), any()))
        .thenAnswer(invocation -> invocation.getArgument(0));

    when(userContext.isPublisherSelfServeEnabled(anyLong())).thenReturn(true);

    PlacementVideoDTO placementVideoDTO = TestObjectsFactory.createDefaultPlacementVideoDTO();

    PublisherPositionDTO publisherPosition =
        getPublisherPosition(1L, TrafficType.MEDIATION, ImmutableList.of(), null);
    publisherPosition.setPlacementCategory(PlacementCategory.INSTREAM_VIDEO);
    publisherPosition.setPlacementVideo(placementVideoDTO);
    publisherPosition.setLongform(true);

    when(placementVideoService.populateVideoData(any(), any())).thenReturn(placementVideoDTO);

    PublisherPositionDTO createdPosition =
        publisherSelfService.createPosition(site.getPid(), publisherPosition, false);

    assertNotNull(createdPosition.getPlacementVideo());
    assertEquals(PlacementCategory.INSTREAM_VIDEO, publisherPosition.getPlacementCategory());
    assertEquals(300, createdPosition.getPlacementVideo().getPlayerHeight());
    assertEquals(300, createdPosition.getPlacementVideo().getPlayerWidth());
    assertFalse(createdPosition.getPlacementVideo().isMultiImpressionBid());
    assertFalse(createdPosition.getPlacementVideo().isCompetitiveSeparation());
    assertNotNull(createdPosition.getPlacementVideo().getSsai());
    assertNotNull(createdPosition.getPlacementVideo().getStreamType());
  }

  @Test
  void shouldCreatePositionWithInstreamVideoLongFormTrueAndValidPlacementVideoDTO() {
    Site site = getSite();
    when(sellerSiteService.getSite(anyLong())).thenReturn(site);

    when(publisherPositionAssembler.apply(
            any(PublisherPositionContext.class),
            any(Position.class),
            any(PublisherPositionDTO.class)))
        .thenReturn(new Position());
    when(publisherPositionAssembler.make(any(PublisherPositionContext.class), any(Position.class)))
        .then(
            invocation -> {
              Position position = invocation.getArgument(1);
              PublisherPositionDTO publisherPosition = new PublisherPositionDTO();
              publisherPosition.setName(position.getName());
              return publisherPosition;
            });
    when(sellerPositionService.createPosition(anyLong(), any(Position.class)))
        .thenAnswer(
            invocation -> {
              Position position = invocation.getArgument(1);
              site.addPosition(position);
              return null;
            });
    when(placementVideoService.save(any(), any()))
        .thenAnswer(invocation -> invocation.getArgument(0));

    when(userContext.isPublisherSelfServeEnabled(anyLong())).thenReturn(true);

    PlacementVideoDTO placementVideoDTO = TestObjectsFactory.createDefaultPlacementVideoDTO();

    PublisherPositionDTO publisherPosition =
        getPublisherPosition(1L, TrafficType.MEDIATION, ImmutableList.of(), null);
    publisherPosition.setPlacementCategory(PlacementCategory.INSTREAM_VIDEO);
    publisherPosition.setLongform(true);
    publisherPosition.setPlacementVideo(placementVideoDTO);

    when(placementVideoService.populateVideoData(any(), any())).thenReturn(placementVideoDTO);

    PublisherPositionDTO createdPosition =
        publisherSelfService.createPosition(site.getPid(), publisherPosition, false);

    assertNotNull(createdPosition.getPlacementVideo());
    assertEquals(PlacementCategory.INSTREAM_VIDEO, publisherPosition.getPlacementCategory());
    assertEquals(300, createdPosition.getPlacementVideo().getPlayerHeight());
    assertEquals(300, createdPosition.getPlacementVideo().getPlayerWidth());
    assertFalse(createdPosition.getPlacementVideo().isMultiImpressionBid());
    assertFalse(createdPosition.getPlacementVideo().isCompetitiveSeparation());
    assertEquals(PlacementVideoSsai.ALL_SERVER_SIDE, createdPosition.getPlacementVideo().getSsai());
    assertEquals(PlacementVideoStreamType.VOD, createdPosition.getPlacementVideo().getStreamType());
  }

  @Test
  void shouldHandleVideoPlacementOrInstreamVideoCategory() {
    Site site = getSite();
    when(sellerSiteService.getSite(anyLong())).thenReturn(site);
    when(userContext.isPublisherSelfServeEnabled(anyLong())).thenReturn(true);

    when(publisherPositionAssembler.apply(
            any(PublisherPositionContext.class),
            any(Position.class),
            any(PublisherPositionDTO.class)))
        .thenReturn(new Position());

    when(publisherPositionAssembler.make(any(PublisherPositionContext.class), any(Position.class)))
        .then(
            invocation -> {
              Position position = invocation.getArgument(1);
              PublisherPositionDTO publisherPosition = new PublisherPositionDTO();
              publisherPosition.setName(position.getName());
              return publisherPosition;
            });

    when(sellerPositionService.createPosition(anyLong(), any(Position.class)))
        .thenAnswer(
            invocation -> {
              Position position = invocation.getArgument(1);
              site.addPosition(position);
              return null;
            });
    when(placementVideoService.save(any(), any()))
        .thenAnswer(invocation -> invocation.getArgument(0));

    when(placementVideoService.populateVideoData(any(), any()))
        .thenReturn(TestObjectsFactory.createDefaultPlacementVideoDTO());
    PublisherPositionDTO publisherPosition =
        getPublisherPosition(1L, TrafficType.MEDIATION, ImmutableList.of(), null);

    publisherPosition.setPlacementCategory(PlacementCategory.INSTREAM_VIDEO);
    publisherPosition.setLongform(true);

    PublisherPositionDTO createdPosition =
        publisherSelfService.createPosition(site.getPid(), publisherPosition, false);

    assertNotNull(createdPosition);
    assertNotNull(createdPosition.getPlacementVideo());

    publisherPosition.setPlacementCategory(PlacementCategory.INSTREAM_VIDEO);
    publisherPosition.setVideoSupport(null); // Ensure only the category triggers the logic

    PlacementVideoDTO placementVideoDTO = TestObjectsFactory.createDefaultPlacementVideoDTO();
    when(placementVideoService.populateVideoData(any(), any())).thenReturn(placementVideoDTO);

    createdPosition = publisherSelfService.createPosition(site.getPid(), publisherPosition, false);

    // PlacementVideoDTO is updated correctly for INSTREAM_VIDEO
    assertNotNull(createdPosition.getPlacementVideo());
    assertEquals(300, createdPosition.getPlacementVideo().getPlayerHeight());
    assertEquals(300, createdPosition.getPlacementVideo().getPlayerWidth());
    assertFalse(createdPosition.getPlacementVideo().isMultiImpressionBid());
    assertFalse(createdPosition.getPlacementVideo().isCompetitiveSeparation());
    assertNotNull(createdPosition.getPlacementVideo().getSsai());
    assertNotNull(createdPosition.getPlacementVideo().getStreamType());
  }

  @Test
  void shouldCreatePositionWithNewDTOAndPlacementVideoDTO() {
    Site site = getSite();
    when(sellerSiteService.getSite(anyLong())).thenReturn(site);
    when(userContext.isPublisherSelfServeEnabled(anyLong())).thenReturn(true);
    when(publisherPositionAssembler.apply(
            any(PublisherPositionContext.class),
            any(Position.class),
            any(PublisherPositionDTO.class)))
        .thenAnswer(
            invocation -> {
              Position position = new Position();
              PublisherPositionDTO publisherPositionDTO = invocation.getArgument(2);
              position.setVideoLinearity(publisherPositionDTO.getVideoLinearity());
              return position;
            });
    when(publisherPositionAssembler.make(any(PublisherPositionContext.class), any(Position.class)))
        .then(
            invocation -> {
              Position position = invocation.getArgument(1);
              PublisherPositionDTO publisherPosition = new PublisherPositionDTO();
              publisherPosition.setName(position.getName());
              publisherPosition.setVideoLinearity(position.getVideoLinearity());
              return publisherPosition;
            });
    when(sellerPositionService.createPosition(anyLong(), any(Position.class)))
        .thenAnswer(
            invocation -> {
              Position position = invocation.getArgument(1);
              site.addPosition(position);
              return null;
            });
    when(placementVideoService.save(any(), any()))
        .thenAnswer(invocation -> invocation.getArgument(0));

    PublisherPositionDTO publisherPosition =
        getPublisherPosition(1L, TrafficType.MEDIATION, ImmutableList.of(), null);
    publisherPosition.setName(null);
    publisherPosition.setHbPartnerAttributes(null);
    publisherPosition.setVideoSupport(VideoSupport.VIDEO_AND_BANNER);
    publisherPosition.setVideoLinearity(VideoLinearity.LINEAR);
    publisherPosition.setLongform(true);
    publisherPosition.setPlacementCategory(PlacementCategory.INSTREAM_VIDEO);
    PlacementVideoDTO placementVideoDTO = TestObjectsFactory.createDefaultPlacementVideoDTO();
    placementVideoDTO.setLongform(true);
    placementVideoDTO.setStreamType(PlacementVideoStreamType.VOD);
    placementVideoDTO.setPlayerBrand("test_player");
    placementVideoDTO.setSsai(PlacementVideoSsai.UNKNOWN);
    placementVideoDTO.setMultiImpressionBid(true);
    placementVideoDTO.setCompetitiveSeparation(false);
    publisherPosition.setPlacementVideo(placementVideoDTO);
    when(placementVideoService.populateVideoData(any(), any())).thenReturn(placementVideoDTO);
    PublisherPositionDTO createdPosition =
        publisherSelfService.createPosition(site.getPid(), publisherPosition, false);
    assertEquals(32, createdPosition.getName().length());
    assertNotNull(createdPosition.getPlacementVideo());
    assertEquals(VideoLinearity.LINEAR, createdPosition.getVideoLinearity());

    // companions
    PlacementVideoCompanionDTO placementVideoCompanionDTO =
        TestObjectsFactory.createDefaultPlacementVideoCompanionDTO();
    placementVideoDTO.addCompanion(placementVideoCompanionDTO);
    publisherPosition.setPlacementVideo(placementVideoDTO);
    createdPosition = publisherSelfService.createPosition(site.getPid(), publisherPosition, false);
    PlacementVideoDTO savedPlacementVideoDTO = createdPosition.getPlacementVideo();
    assertNotNull(savedPlacementVideoDTO);
    assertNotNull(savedPlacementVideoDTO.getCompanions());
    assertEquals(1, savedPlacementVideoDTO.getCompanions().size());

    // banner
    publisherPosition.setVideoSupport(VideoSupport.BANNER);
    createdPosition = publisherSelfService.createPosition(site.getPid(), publisherPosition, false);
    assertNotNull(createdPosition.getPlacementVideo());

    // longform fields
    assertTrue(savedPlacementVideoDTO.isLongform());
    assertEquals(PlacementVideoStreamType.VOD, savedPlacementVideoDTO.getStreamType());
    assertEquals("test_player", savedPlacementVideoDTO.getPlayerBrand());
    assertEquals(PlacementVideoSsai.ALL_SERVER_SIDE, savedPlacementVideoDTO.getSsai());
    assertFalse(savedPlacementVideoDTO.isMultiImpressionBid());
    assertFalse(savedPlacementVideoDTO.isCompetitiveSeparation());
  }

  @Test
  void shouldUpdatePositionWithInstreamVideoPlacementCategory() {
    Site site = getSite();
    Long PID = 3L;
    Position position = getPosition(PID, TrafficType.MEDIATION);
    position.setVideoSupport(VideoSupport.VIDEO_AND_BANNER);
    position.setLongform(true);
    site.addPosition(position);

    PlacementVideoDTO placementVideoDTO = new PlacementVideoDTO();
    placementVideoDTO.setPid(PID);
    placementVideoDTO.setVersion(1);
    placementVideoDTO.setLongform(true);
    placementVideoDTO.setSsai(PlacementVideoSsai.ALL_SERVER_SIDE);
    placementVideoDTO.setStreamType(PlacementVideoStreamType.VOD);

    PublisherPositionDTO input =
        getPublisherPosition(PID, TrafficType.MEDIATION, ImmutableList.of(), null);
    input.setLongform(true);
    input.setPlacementVideo(placementVideoDTO);
    input.setPlacementCategory(PlacementCategory.INSTREAM_VIDEO);
    input.setVideoSupport(VideoSupport.VIDEO_AND_BANNER);

    when(sellerSiteService.getSite(anyLong())).thenReturn(site);
    when(sellerSiteService.getValidatedSiteForPublisher(anyLong(), anyLong())).thenReturn(site);
    when(userContext.isPublisherSelfServeEnabled(anyLong())).thenReturn(true);
    when(publisherPositionAssembler.make(any(PublisherPositionContext.class), any(Position.class)))
        .thenReturn(input);
    when(publisherPositionAssembler.apply(
            any(PublisherPositionContext.class),
            any(Position.class),
            any(PublisherPositionDTO.class)))
        .thenReturn(position);
    when(placementVideoService.getPlacementVideo(anyLong())).thenReturn(placementVideoDTO);
    when(placementVideoService.populateVideoData(
            any(PlacementVideoDTO.class), any(PublisherPositionDTO.class)))
        .thenReturn(placementVideoDTO);
    when(placementVideoService.update(any(PlacementVideoDTO.class), anyLong(), anyBoolean()))
        .thenReturn(placementVideoDTO);

    PublisherPositionDTO publisherPosition =
        publisherSelfService.updatePosition(PUBLISHER_PID, 1L, input, false);

    assertNotNull(publisherPosition);
    assertEquals(PID, publisherPosition.getPid());
    assertNotNull(publisherPosition.getPlacementVideo());
    assertEquals(PID, publisherPosition.getPlacementVideo().getPid());
    assertTrue(publisherPosition.isLongform());
    assertEquals(
        PlacementVideoSsai.ALL_SERVER_SIDE, publisherPosition.getPlacementVideo().getSsai());
    assertEquals(
        PlacementVideoStreamType.VOD, publisherPosition.getPlacementVideo().getStreamType());
  }

  @Test
  void shouldUpdatePositionWithInstreamVideoLongFormFalsePlacementCategory() {
    Site site = getSite();
    Long PID = 3L;
    Position position = getPosition(PID, TrafficType.MEDIATION);
    position.setVideoSupport(VideoSupport.VIDEO_AND_BANNER);
    position.setLongform(false);
    site.addPosition(position);

    PlacementVideoDTO placementVideoDTO = new PlacementVideoDTO();
    placementVideoDTO.setPid(PID);
    placementVideoDTO.setVersion(1);
    placementVideoDTO.setLongform(false);
    placementVideoDTO.setSsai(null);
    placementVideoDTO.setStreamType(null);

    PublisherPositionDTO input =
        getPublisherPosition(PID, TrafficType.MEDIATION, ImmutableList.of(), null);
    input.setLongform(false);
    input.setPlacementVideo(placementVideoDTO);
    input.setPlacementCategory(PlacementCategory.INSTREAM_VIDEO);
    input.setVideoSupport(VideoSupport.VIDEO_AND_BANNER);

    when(sellerSiteService.getSite(anyLong())).thenReturn(site);
    when(sellerSiteService.getValidatedSiteForPublisher(anyLong(), anyLong())).thenReturn(site);
    when(userContext.isPublisherSelfServeEnabled(anyLong())).thenReturn(true);
    when(publisherPositionAssembler.make(any(PublisherPositionContext.class), any(Position.class)))
        .thenReturn(input);
    when(publisherPositionAssembler.apply(
            any(PublisherPositionContext.class),
            any(Position.class),
            any(PublisherPositionDTO.class)))
        .thenReturn(position);
    when(placementVideoService.getPlacementVideo(anyLong())).thenReturn(placementVideoDTO);

    PublisherPositionDTO publisherPosition =
        publisherSelfService.updatePosition(PUBLISHER_PID, 1L, input, false);

    assertNotNull(publisherPosition);
    assertEquals(PID, publisherPosition.getPid());
    assertNotNull(publisherPosition.getPlacementVideo());
    assertEquals(PID, publisherPosition.getPlacementVideo().getPid());
    assertFalse(publisherPosition.isLongform());
    assertNull(publisherPosition.getPlacementVideo().getSsai());
    assertNull(publisherPosition.getPlacementVideo().getStreamType());
  }

  @Test
  void shouldUpdatePositionWithPlacementVideo() {
    Site site = getSite();
    Long PID = 3L;
    when(sellerSiteService.getSite(anyLong())).thenReturn(site);
    when(sellerSiteService.getValidatedSiteForPublisher(anyLong(), anyLong())).thenReturn(site);
    when(userContext.isPublisherSelfServeEnabled(anyLong())).thenReturn(true);

    var input = getPublisherPosition(PID, TrafficType.MEDIATION, ImmutableList.of(), null);

    Position position = getPosition(PID, TrafficType.MEDIATION);
    position.setVideoSupport(VideoSupport.VIDEO_AND_BANNER);
    site.addPosition(position);
    PlacementVideoDTO placementVideoDTO = TestObjectsFactory.createDefaultPlacementVideoDTO();
    placementVideoDTO.setPid(PID);
    placementVideoDTO.setVersion(1);
    input.setPlacementVideo(placementVideoDTO);
    input.setHbPartnerAttributes(null);
    input.setVideoSupport(VideoSupport.VIDEO_AND_BANNER);
    input.setVideoLinearity(VideoLinearity.NON_LINEAR);
    input.setPlacementCategory(PlacementCategory.INSTREAM_VIDEO);
    input.setLongform(true);
    when(placementVideoService.populateVideoData(any(), any())).thenReturn(placementVideoDTO);
    when(placementVideoService.update(any(), any(), anyBoolean())).thenReturn(placementVideoDTO);
    when(publisherPositionAssembler.make(any(PublisherPositionContext.class), any(Position.class)))
        .thenReturn(input);
    when(publisherPositionAssembler.apply(
            any(PublisherPositionContext.class),
            any(Position.class),
            any(PublisherPositionDTO.class)))
        .thenReturn(position);
    PlacementVideo placementVideo = TestObjectsFactory.createDefaultPlacementVideo();
    placementVideo.setPid(PID);
    PublisherPositionDTO publisherPosition =
        publisherSelfService.updatePosition(PUBLISHER_PID, 1L, input, false);
    assertEquals(3L, publisherPosition.getPid().longValue());
    assertNotNull(publisherPosition.getPlacementVideo());

    placementVideoDTO.setPid(null);
    publisherPosition = publisherSelfService.updatePosition(PUBLISHER_PID, 1L, input, false);
    assertEquals(3L, publisherPosition.getPid().longValue());
    assertNotNull(publisherPosition.getPlacementVideo());

    input.setPlacementVideo(null);
    when(publisherPositionAssembler.make(any(PublisherPositionContext.class), any(Position.class)))
        .thenReturn(input);
    publisherPosition = publisherSelfService.updatePosition(PUBLISHER_PID, 1L, input, false);
    assertEquals(3L, publisherPosition.getPid().longValue());
    assertNotNull(publisherPosition.getPlacementVideo());

    input.setVideoSupport(VideoSupport.BANNER);
    input.setPlacementVideo(null);
    when(publisherPositionAssembler.make(any(PublisherPositionContext.class), any(Position.class)))
        .thenReturn(input);
    publisherPosition = publisherSelfService.updatePosition(PUBLISHER_PID, 1L, input, false);
    assertEquals(3L, publisherPosition.getPid().longValue());
    assertNotNull(publisherPosition.getPlacementVideo());

    input.setVideoSupport(VideoSupport.VIDEO_AND_BANNER);
    input.setPlacementVideo(null);
    publisherPosition = publisherSelfService.updatePosition(PUBLISHER_PID, 1L, input, false);
    assertEquals(3L, publisherPosition.getPid().longValue());
    assertNotNull(publisherPosition.getPlacementVideo());

    input.setVideoSupport(VideoSupport.BANNER);
    input.setPlacementVideo(null);
    when(publisherPositionAssembler.make(any(PublisherPositionContext.class), any(Position.class)))
        .thenReturn(input);
    publisherPosition = publisherSelfService.updatePosition(PUBLISHER_PID, 1L, input, false);
    assertEquals(3L, publisherPosition.getPid().longValue());
    assertNotNull(publisherPosition.getPlacementVideo());

    input.setVideoSupport(VideoSupport.BANNER);
    position.setVideoSupport(null);
    input.setPlacementVideo(null);
    when(publisherPositionAssembler.make(any(PublisherPositionContext.class), any(Position.class)))
        .thenReturn(input);
    publisherPosition = publisherSelfService.updatePosition(PUBLISHER_PID, 1L, input, false);
    assertEquals(3L, publisherPosition.getPid().longValue());
    assertNotNull(publisherPosition.getPlacementVideo());
  }

  @Test
  void shouldUpdatePositionVideoToNativeWithPlacementVideo() {
    Site site = getSite();
    Long PID = 3L;
    when(sellerSiteService.getSite(anyLong())).thenReturn(site);
    when(sellerSiteService.getValidatedSiteForPublisher(anyLong(), anyLong())).thenReturn(site);
    when(userContext.isPublisherSelfServeEnabled(anyLong())).thenReturn(true);
    var input = getPublisherPosition(PID, TrafficType.MEDIATION, ImmutableList.of(), null);

    Position position = getPosition(PID, TrafficType.MEDIATION);
    position.setVideoSupport(VideoSupport.VIDEO);
    site.addPosition(position);
    PlacementVideoDTO placementVideoDTO = TestObjectsFactory.createDefaultPlacementVideoDTO();
    placementVideoDTO.setPid(PID);
    placementVideoDTO.setVersion(1);
    input.setPlacementVideo(placementVideoDTO);
    input.setHbPartnerAttributes(null);
    input.setVideoSupport(VideoSupport.VIDEO);
    input.setVideoLinearity(VideoLinearity.NON_LINEAR);
    when(placementVideoService.update(any(), any(), anyBoolean())).thenReturn(placementVideoDTO);

    when(publisherPositionAssembler.make(any(PublisherPositionContext.class), any(Position.class)))
        .thenReturn(input);
    when(publisherPositionAssembler.apply(
            any(PublisherPositionContext.class),
            any(Position.class),
            any(PublisherPositionDTO.class)))
        .thenReturn(position);
    PlacementVideo placementVideo = TestObjectsFactory.createDefaultPlacementVideo();
    placementVideo.setPid(PID);
    PublisherPositionDTO publisherPosition =
        publisherSelfService.updatePosition(PUBLISHER_PID, 1L, input, false);
    assertEquals(3L, publisherPosition.getPid().longValue());
    assertNotNull(publisherPosition.getPlacementVideo());

    input.setVideoSupport(VideoSupport.NATIVE);
    input.setPlacementVideo(null);
    when(publisherPositionAssembler.make(any(PublisherPositionContext.class), any(Position.class)))
        .thenReturn(input);
    publisherPosition = publisherSelfService.updatePosition(PUBLISHER_PID, 1L, input, false);
    assertEquals(3L, publisherPosition.getPid().longValue());
    assertNull(publisherPosition.getPlacementVideo());

    position.setVideoSupport(VideoSupport.VIDEO_AND_BANNER);
    input.setVideoSupport(VideoSupport.NATIVE);
    input.setPlacementVideo(null);
    when(publisherPositionAssembler.make(any(PublisherPositionContext.class), any(Position.class)))
        .thenReturn(input);
    publisherPosition = publisherSelfService.updatePosition(PUBLISHER_PID, 1L, input, false);
    assertEquals(3L, publisherPosition.getPid().longValue());
    assertNull(publisherPosition.getPlacementVideo());
  }

  @Test
  void shouldUpdatePositionBannerToVideoWithPlacementVideo() {
    Site site = getSite();
    Long PID = 3L;
    when(sellerSiteService.getSite(anyLong())).thenReturn(site);
    when(sellerSiteService.getValidatedSiteForPublisher(anyLong(), anyLong())).thenReturn(site);
    when(userContext.isPublisherSelfServeEnabled(anyLong())).thenReturn(true);
    var input = getPublisherPosition(PID, TrafficType.MEDIATION, ImmutableList.of(), null);

    Position position = getPosition(PID, TrafficType.MEDIATION);
    position.setVideoSupport(VideoSupport.BANNER);
    site.addPosition(position);
    PlacementVideoDTO placementVideoDTO = TestObjectsFactory.createDefaultPlacementVideoDTO();
    placementVideoDTO.setPid(PID);
    placementVideoDTO.setVersion(1);
    // input.setPlacementVideo(placementVideoDTO);
    input.setHbPartnerAttributes(null);
    input.setVideoSupport(VideoSupport.VIDEO);
    input.setVideoLinearity(VideoLinearity.NON_LINEAR);
    input.setVideoLinearity(VideoLinearity.LINEAR);

    when(publisherPositionAssembler.make(any(PublisherPositionContext.class), any(Position.class)))
        .thenReturn(input);
    when(publisherPositionAssembler.apply(
            any(PublisherPositionContext.class),
            any(Position.class),
            any(PublisherPositionDTO.class)))
        .thenReturn(position);
    PlacementVideo placementVideo = TestObjectsFactory.createDefaultPlacementVideo();
    placementVideo.setPid(PID);
    PublisherPositionDTO publisherPosition =
        publisherSelfService.updatePosition(PUBLISHER_PID, 1L, input, false);
    assertEquals(3L, publisherPosition.getPid().longValue());
    assertNull(publisherPosition.getPlacementVideo());

    input.setVideoSupport(VideoSupport.VIDEO_AND_BANNER);
    publisherPosition = publisherSelfService.updatePosition(PUBLISHER_PID, 1L, input, false);
    assertEquals(3L, publisherPosition.getPid().longValue());
    assertNull(publisherPosition.getPlacementVideo());

    position.setVideoSupport(null);
    input.setVideoSupport(VideoSupport.VIDEO_AND_BANNER);
    publisherPosition = publisherSelfService.updatePosition(PUBLISHER_PID, 1L, input, false);
    assertEquals(3L, publisherPosition.getPid().longValue());
    assertNull(publisherPosition.getPlacementVideo());

    position.setVideoSupport(VideoSupport.BANNER);
    input.setVideoSupport(null);
    input.setPlacementVideo(null);
    publisherPosition = publisherSelfService.updatePosition(PUBLISHER_PID, 1L, input, false);
    assertEquals(3L, publisherPosition.getPid().longValue());
    assertNull(publisherPosition.getPlacementVideo());
  }

  @Test
  void shouldUpdatePositionNativeToVideoAndBannerWithPlacementVideo() {
    Site site = getSite();
    Long PID = 3L;
    when(sellerSiteService.getSite(anyLong())).thenReturn(site);
    when(sellerSiteService.getValidatedSiteForPublisher(anyLong(), anyLong())).thenReturn(site);
    when(userContext.isPublisherSelfServeEnabled(anyLong())).thenReturn(true);
    var input = getPublisherPosition(PID, TrafficType.MEDIATION, ImmutableList.of(), null);

    Position position = getPosition(PID, TrafficType.MEDIATION);
    position.setVideoSupport(VideoSupport.NATIVE);
    site.addPosition(position);
    PlacementVideoDTO placementVideoDTO = TestObjectsFactory.createDefaultPlacementVideoDTO();
    placementVideoDTO.setPid(PID);
    placementVideoDTO.setVersion(1);
    input.setPlacementVideo(placementVideoDTO);
    input.setHbPartnerAttributes(null);
    input.setVideoSupport(VideoSupport.VIDEO_AND_BANNER);
    input.setVideoLinearity(VideoLinearity.NON_LINEAR);
    when(placementVideoService.update(any(), any(), anyBoolean())).thenReturn(placementVideoDTO);

    when(publisherPositionAssembler.make(any(PublisherPositionContext.class), any(Position.class)))
        .thenReturn(input);
    when(publisherPositionAssembler.apply(
            any(PublisherPositionContext.class),
            any(Position.class),
            any(PublisherPositionDTO.class)))
        .thenReturn(position);
    PlacementVideo placementVideo = TestObjectsFactory.createDefaultPlacementVideo();
    placementVideo.setPid(PID);
    PublisherPositionDTO publisherPosition =
        publisherSelfService.updatePosition(PUBLISHER_PID, 1L, input, false);
    assertEquals(3L, publisherPosition.getPid().longValue());
    assertNotNull(publisherPosition.getPlacementVideo());

    input.setVideoSupport(VideoSupport.VIDEO);
    publisherPosition = publisherSelfService.updatePosition(PUBLISHER_PID, 1L, input, false);
    assertEquals(3L, publisherPosition.getPid().longValue());
    assertNotNull(publisherPosition.getPlacementVideo());
  }

  @Test
  void shouldGetDetailedPositionWithVideoTagInPositionAndPlacementVideoWithCompanionData() {
    Site site = getSite();
    long position_id = 1L;
    Integer actualHeight = 320;
    Integer actualWidth = 240;

    when(sellerSiteService.getSite(anyLong())).thenReturn(site);
    when(publisherPositionAssembler.make(any(PublisherPositionContext.class), any(Position.class)))
        .thenAnswer(
            invocation -> {
              Position position = getPosition(position_id, TrafficType.SMART_YIELD);
              PublisherPositionDTO publisherPosition = new PublisherPositionDTO();
              publisherPosition.setName(position.getName());
              publisherPosition.setPid(position.getPid());
              publisherPosition.setPlacementCategory(PlacementCategory.INSTREAM_VIDEO);
              publisherPosition.setLongform(true);
              publisherPosition.setVideoLinearity(VideoLinearity.LINEAR);
              return publisherPosition;
            });

    when(placementVideoService.getPlacementVideo(any()))
        .thenAnswer(
            invocation -> {
              PlacementVideoDTO placementVideoDTO =
                  TestObjectsFactory.createDefaultPlacementVideoDTO();
              PlacementVideoCompanionDTO placementVideoCompanionDTO =
                  TestObjectsFactory.createDefaultPlacementVideoCompanionDTO();
              PlacementVideoCompanionDTO placementVideoCompanionDTO1 =
                  TestObjectsFactory.createDefaultPlacementVideoCompanionDTO();
              placementVideoCompanionDTO1.setHeight(160);
              placementVideoCompanionDTO1.setWidth(240);
              placementVideoDTO.addCompanion(placementVideoCompanionDTO);
              placementVideoDTO.addCompanion(placementVideoCompanionDTO1);
              return placementVideoDTO;
            });
    PublisherPositionDTO detailedPosition =
        publisherSelfService.detailedPosition(site.getCompanyPid(), site.getPid(), 1L);
    assertNotNull(detailedPosition.getPlacementVideo());
    assertNotNull(detailedPosition.getPlacementVideo().getCompanions());
    assertEquals(
        detailedPosition.getPlacementVideo().getCompanions().get(0).getHeight(), actualHeight);
    assertEquals(
        detailedPosition.getPlacementVideo().getCompanions().get(1).getWidth(), actualWidth);
  }

  @Test
  void shouldGetDetailedPositionWithVideoTagOnlyInPlacementVideoWithoutCompanionData() {
    Site site = getSite();
    long position_id = 1L;

    when(sellerSiteService.getSite(anyLong())).thenReturn(site);

    when(publisherPositionAssembler.make(any(PublisherPositionContext.class), any(Position.class)))
        .thenAnswer(
            invocation -> {
              Position position = getPosition(position_id, TrafficType.SMART_YIELD);
              PublisherPositionDTO publisherPosition = new PublisherPositionDTO();
              publisherPosition.setName(position.getName());
              publisherPosition.setPid(position.getPid());
              publisherPosition.setPlacementCategory(PlacementCategory.INSTREAM_VIDEO);
              publisherPosition.setLongform(true);
              return publisherPosition;
            });
    lenient()
        .when(placementVideoService.getPlacementVideo(any()))
        .thenAnswer(invocation -> TestObjectsFactory.createDefaultPlacementVideoDTO());

    PublisherPositionDTO detailedPosition =
        publisherSelfService.detailedPosition(site.getCompanyPid(), site.getPid(), 1L);
    assertNotNull(detailedPosition.getPlacementVideo());
    assertNull(detailedPosition.getPlacementVideo().getCompanions());
  }

  @Test
  void shouldGetDetailedPositionWithVideoTagInPositionAndPlacementVideoWithoutCompanionData() {
    Site site = getSite();
    long position_id = 1L;

    when(sellerSiteService.getSite(anyLong())).thenReturn(site);

    when(publisherPositionAssembler.make(any(PublisherPositionContext.class), any(Position.class)))
        .then(
            invocation -> {
              Position position = getPosition(position_id, TrafficType.SMART_YIELD);
              PublisherPositionDTO publisherPosition = new PublisherPositionDTO();
              publisherPosition.setName(position.getName());
              publisherPosition.setPid(position.getPid());
              publisherPosition.setVideoLinearity(VideoLinearity.LINEAR);
              publisherPosition.setPlacementCategory(PlacementCategory.INSTREAM_VIDEO);
              publisherPosition.setLongform(true);
              return publisherPosition;
            });
    lenient()
        .when(placementVideoService.getPlacementVideo(any()))
        .thenAnswer(invocation -> TestObjectsFactory.createDefaultPlacementVideoDTO());

    PublisherPositionDTO detailedPosition =
        publisherSelfService.detailedPosition(site.getCompanyPid(), site.getPid(), position_id);
    assertNotNull(detailedPosition.getPlacementVideo());
    assertNull(detailedPosition.getPlacementVideo().getCompanions());
  }

  @Test
  void shouldGetDetailedPositionWithVideoTagOnlyInPosition() {
    Site site = getSite();
    long position_id = 1L;

    when(sellerSiteService.getSite(anyLong())).thenReturn(site);
    when(publisherPositionAssembler.make(any(PublisherPositionContext.class), any(Position.class)))
        .thenAnswer(
            invocation -> {
              Position position = getPosition(position_id, TrafficType.SMART_YIELD);
              PublisherPositionDTO publisherPosition = new PublisherPositionDTO();
              publisherPosition.setName(position.getName());
              publisherPosition.setPid(position.getPid());
              publisherPosition.setVideoSupport(VideoSupport.VIDEO_AND_BANNER);
              publisherPosition.setVideoLinearity(VideoLinearity.LINEAR);
              return publisherPosition;
            });
    lenient().when(placementVideoService.getPlacementVideo(any())).thenReturn(null);

    PublisherPositionDTO detailedPosition =
        publisherSelfService.detailedPosition(site.getCompanyPid(), site.getPid(), position_id);
    assertNull(detailedPosition.getPlacementVideo());
  }

  @Test
  void shouldGetDetailedPositionWithNoVideoTagInPositionAndPlacementVideo() {
    Site site = getSite();
    Long position_id = 1L;

    when(sellerSiteService.getSite(anyLong())).thenReturn(site);
    when(publisherPositionAssembler.make(any(PublisherPositionContext.class), any(Position.class)))
        .thenAnswer(
            invocation -> {
              Position position = getPosition(position_id, TrafficType.SMART_YIELD);
              PublisherPositionDTO publisherPosition = new PublisherPositionDTO();
              publisherPosition.setName(position.getName());
              publisherPosition.setPid(position.getPid());
              return publisherPosition;
            });
    lenient().when(placementVideoService.getPlacementVideo(any())).thenReturn(null);

    PublisherPositionDTO detailedPosition =
        publisherSelfService.detailedPosition(site.getCompanyPid(), site.getPid(), position_id);
    assertNull(detailedPosition.getPlacementVideo());
    assertEquals(detailedPosition.getPid(), position_id);
  }

  @Test
  void shouldCopyNonVideoPositionToSameSite() {
    Site site = getSite();
    String inputPositionName = "CopyPosition1";
    Long positionId = 3L;
    when(sellerSiteService.getSite(anyLong())).thenReturn(site);
    when(sellerSiteService.getSite(anyLong())).thenReturn(site);
    when(publisherPositionAssembler.apply(
            any(PublisherPositionContext.class),
            any(Position.class),
            any(PublisherPositionDTO.class)))
        .thenAnswer(
            invocation -> {
              Position position = invocation.getArgument(1);
              PublisherPositionDTO publisherPositionDTO = invocation.getArgument(2);
              position.setName(publisherPositionDTO.getName());
              return position;
            });
    when(sellerPositionService.createPosition(anyLong(), any(Position.class)))
        .thenAnswer(
            invocation -> {
              Position position = invocation.getArgument(1);
              position.setPid(positionId);
              site.addPosition(position);
              return null;
            });
    when(publisherPositionAssembler.make(
            any(PublisherPositionContext.class), any(Position.class), any()))
        .then(
            invocation -> {
              Position position = invocation.getArgument(1);
              PublisherPositionDTO publisherPosition = new PublisherPositionDTO();
              publisherPosition.setName(position.getName());
              publisherPosition.setPid(position.getPid());
              return publisherPosition;
            });

    // Input request params
    PublisherPositionDTO publisherPosition = new PublisherPositionDTO();
    publisherPosition.setName(inputPositionName);
    publisherPosition.setTrafficType(TrafficType.MEDIATION);
    publisherPosition.setTiers(new HashSet<>(List.of(PublisherTierDTO.newBuilder().build())));

    PublisherPositionDTO copyPosition =
        publisherSelfService.copyPosition(
            site.getCompanyPid(), site.getPid(), 1L, site.getPid(), publisherPosition);
    assertEquals(inputPositionName, copyPosition.getName());
    assertEquals(positionId, copyPosition.getPid());
    assertEquals(TrafficType.MEDIATION, copyPosition.getTrafficType());
  }

  @Test
  void shouldCopyVideoPositionToSameSite() {
    Site site = getSite();
    String inputPositionName = "CopyPosition1";
    String inputPositionName1 = "CopyPosition2";
    Long positionId = 3L;
    when(sellerSiteService.getSite(anyLong())).thenReturn(site);
    doNothing().when(positionLimitChecker).checkLimitsPositionsInSite(1L, 1L);
    when(sellerSiteService.getSite(anyLong())).thenReturn(site);
    when(publisherPositionAssembler.apply(
            any(PublisherPositionContext.class),
            any(Position.class),
            any(PublisherPositionDTO.class)))
        .thenAnswer(
            invocation -> {
              Position position = invocation.getArgument(1);
              PublisherPositionDTO publisherPositionDTO = invocation.getArgument(2);
              position.setName(publisherPositionDTO.getName());
              return position;
            });
    when(sellerPositionService.createPosition(anyLong(), any(Position.class)))
        .thenAnswer(
            invocation -> {
              Position position = invocation.getArgument(1);
              position.setPid(positionId);
              site.addPosition(position);
              return null;
            });
    when(publisherPositionAssembler.make(
            any(PublisherPositionContext.class), any(Position.class), any()))
        .then(
            invocation -> {
              Position position = invocation.getArgument(1);
              PublisherPositionDTO publisherPosition = new PublisherPositionDTO();
              publisherPosition.setName(position.getName());
              publisherPosition.setPid(position.getPid());
              return publisherPosition;
            });
    when(placementVideoService.save(any(), anyLong()))
        .thenAnswer(invocation -> invocation.getArgument(0));
    PlacementVideoDTO placementVideoDTO = TestObjectsFactory.createDefaultPlacementVideoDTO();

    // Input request params
    PublisherPositionDTO publisherPosition = new PublisherPositionDTO();
    publisherPosition.setName(inputPositionName);
    publisherPosition.setTrafficType(TrafficType.MEDIATION);
    publisherPosition.setPlacementCategory(PlacementCategory.INTERSTITIAL);
    publisherPosition.setVideoSupport(VideoSupport.VIDEO_AND_BANNER);
    publisherPosition.setVideoLinearity(VideoLinearity.NON_LINEAR);
    PlacementVideoDTO placementVideoDTO1 = TestObjectsFactory.createDefaultPlacementVideoDTO();
    publisherPosition.setPlacementVideo(placementVideoDTO1);
    // site
    PublisherSiteDTO siteDTO = new PublisherSiteDTO();
    publisherPosition.setSite(siteDTO);
    publisherPosition.setPid(site.getPid());
    publisherPosition.getSite().setType(SiteType.DESKTOP);

    PublisherPositionDTO copyPosition =
        publisherSelfService.copyPosition(
            site.getCompanyPid(), site.getPid(), 1L, site.getPid(), publisherPosition);
    assertEquals(inputPositionName, copyPosition.getName());
    assertEquals(positionId, copyPosition.getPid());
    assertEquals(TrafficType.MEDIATION, copyPosition.getTrafficType());
    assertNotNull(copyPosition.getPlacementVideo());

    // Copy position with video in position and not in placementVideoDTO Same Site
    // Input request params
    PublisherPositionDTO publisherPosition1 = new PublisherPositionDTO();
    publisherPosition1.setName(inputPositionName1);
    ;
    publisherPosition1.setTrafficType(TrafficType.MEDIATION);
    publisherPosition1.setPlacementCategory(PlacementCategory.INTERSTITIAL);
    publisherPosition1.setVideoSupport(VideoSupport.VIDEO_AND_BANNER);
    publisherPosition1.setVideoLinearity(VideoLinearity.NON_LINEAR);
    // site
    PublisherSiteDTO siteDTO1 = new PublisherSiteDTO();
    publisherPosition1.setSite(siteDTO1);
    publisherPosition1.setPid(site.getPid());
    publisherPosition1.getSite().setType(SiteType.DESKTOP);

    PublisherPositionDTO copyPosition1 =
        publisherSelfService.copyPosition(
            site.getCompanyPid(), site.getPid(), 1L, site.getPid(), publisherPosition1);
    assertEquals(inputPositionName1, copyPosition1.getName());
    assertEquals(positionId, copyPosition1.getPid());
    assertEquals(TrafficType.MEDIATION, copyPosition1.getTrafficType());
    assertNull(copyPosition1.getPlacementVideo());
  }

  @Test
  void shouldCopyVideoPositionToDifferentSite() {
    Site sourceSite = getSite();
    Site targetSite = getSite(2L);
    String inputPositionName = "CopyPosition1";
    String inputPositionName1 = "CopyPosition2";
    Long positionId = 3L;
    when(sellerSiteService.getSite(1L)).thenReturn(sourceSite);
    when(sellerSiteService.getSite(2L)).thenReturn(targetSite);
    doNothing().when(positionLimitChecker).checkLimitsPositionsInSite(1L, 2L);
    when(publisherPositionAssembler.apply(
            any(PublisherPositionContext.class),
            any(Position.class),
            any(PublisherPositionDTO.class)))
        .thenAnswer(
            invocation -> {
              Position position = invocation.getArgument(1);
              PublisherPositionDTO publisherPositionDTO = invocation.getArgument(2);
              position.setName(publisherPositionDTO.getName());
              return position;
            });
    when(sellerPositionService.createPosition(anyLong(), any(Position.class)))
        .thenAnswer(
            invocation -> {
              Position position = invocation.getArgument(1);
              position.setPid(positionId);
              targetSite.addPosition(position);
              return null;
            });
    when(publisherPositionAssembler.make(
            any(PublisherPositionContext.class), any(Position.class), any()))
        .then(
            invocation -> {
              Position position = invocation.getArgument(1);
              PublisherPositionDTO publisherPosition = new PublisherPositionDTO();
              publisherPosition.setName(position.getName());
              publisherPosition.setPid(position.getPid());
              return publisherPosition;
            });
    when(placementVideoService.save(any(), anyLong()))
        .thenAnswer(invocation -> invocation.getArgument(0));

    PlacementVideoDTO placementVideoDTO = TestObjectsFactory.createDefaultPlacementVideoDTO();
    placementVideoDTO.setLinearity(PlacementVideoLinearity.NON_LINEAR);

    // Input request params
    PublisherPositionDTO publisherPosition = new PublisherPositionDTO();
    publisherPosition.setName(inputPositionName);
    publisherPosition.setTrafficType(TrafficType.MEDIATION);
    publisherPosition.setPlacementCategory(PlacementCategory.INTERSTITIAL);
    publisherPosition.setVideoSupport(VideoSupport.VIDEO_AND_BANNER);
    publisherPosition.setVideoLinearity(VideoLinearity.NON_LINEAR);
    PlacementVideoDTO placementVideoDTO1 = TestObjectsFactory.createDefaultPlacementVideoDTO();
    publisherPosition.setPlacementVideo(placementVideoDTO1);
    PublisherSiteDTO siteDTO = new PublisherSiteDTO();
    publisherPosition.setSite(siteDTO);
    publisherPosition.setPid(sourceSite.getPid());
    publisherPosition.getSite().setType(SiteType.DESKTOP);

    PublisherPositionDTO copyPosition =
        publisherSelfService.copyPosition(
            sourceSite.getCompanyPid(),
            sourceSite.getPid(),
            1L,
            targetSite.getPid(),
            publisherPosition);
    assertEquals(inputPositionName, copyPosition.getName());
    assertEquals(positionId, copyPosition.getPid());
    assertEquals(TrafficType.MEDIATION, copyPosition.getTrafficType());
    assertNotNull(copyPosition.getPlacementVideo());

    //// Copy position with video in position and not in placementVideoDTO Different Sites
    // Input request params
    PublisherPositionDTO publisherPosition1 = new PublisherPositionDTO();
    publisherPosition1.setName(inputPositionName1);
    publisherPosition1.setTrafficType(TrafficType.MEDIATION);
    publisherPosition1.setPlacementCategory(PlacementCategory.INTERSTITIAL);
    publisherPosition1.setVideoLinearity(VideoLinearity.NON_LINEAR);
    PublisherSiteDTO siteDTO1 = new PublisherSiteDTO();
    publisherPosition1.setPid(sourceSite.getPid());
    publisherPosition1.setSite(siteDTO1);
    publisherPosition1.getSite().setType(SiteType.DESKTOP);

    PublisherPositionDTO copyPosition1 =
        publisherSelfService.copyPosition(
            sourceSite.getCompanyPid(),
            sourceSite.getPid(),
            1L,
            targetSite.getPid(),
            publisherPosition1);
    assertEquals(inputPositionName1, copyPosition1.getName());
    assertEquals(positionId, copyPosition1.getPid());
    assertEquals(TrafficType.MEDIATION, copyPosition1.getTrafficType());
    assertNull(copyPosition1.getPlacementVideo());
  }

  @Test
  void shouldCopyVideoPositionToDifferentSiteWithCompanion() {
    Site sourceSite = getSite();
    Site targetSite = getSite(2L);
    String inputPositionName = "CopyPosition1";
    Long positionId = 3L;
    when(sellerSiteService.getSite(1L)).thenReturn(sourceSite);
    when(sellerSiteService.getSite(2L)).thenReturn(targetSite);
    doNothing().when(positionLimitChecker).checkLimitsPositionsInSite(1L, 2L);
    when(publisherPositionAssembler.apply(
            any(PublisherPositionContext.class),
            any(Position.class),
            any(PublisherPositionDTO.class)))
        .thenAnswer(
            invocation -> {
              Position position = invocation.getArgument(1);
              PublisherPositionDTO publisherPositionDTO = invocation.getArgument(2);
              position.setName(publisherPositionDTO.getName());
              return position;
            });
    when(sellerPositionService.createPosition(anyLong(), any(Position.class)))
        .thenAnswer(
            invocation -> {
              Position position = invocation.getArgument(1);
              position.setPid(positionId);
              targetSite.addPosition(position);
              return null;
            });
    when(publisherPositionAssembler.make(
            any(PublisherPositionContext.class), any(Position.class), any()))
        .then(
            invocation -> {
              Position position = invocation.getArgument(1);
              PublisherPositionDTO publisherPosition = new PublisherPositionDTO();
              publisherPosition.setName(position.getName());
              publisherPosition.setPid(position.getPid());
              return publisherPosition;
            });
    when(placementVideoService.save(any(), anyLong()))
        .thenAnswer(invocation -> invocation.getArgument(0));
    PlacementVideoDTO placementVideoDTO = TestObjectsFactory.createDefaultPlacementVideoDTO();
    placementVideoDTO.setLinearity(PlacementVideoLinearity.NON_LINEAR);
    PlacementVideoCompanionDTO placementVideoCompanionDTO =
        TestObjectsFactory.createDefaultPlacementVideoCompanionDTO();
    placementVideoDTO.addCompanion(placementVideoCompanionDTO);

    // Input request params
    PublisherPositionDTO publisherPosition = new PublisherPositionDTO();
    publisherPosition.setName(inputPositionName);
    publisherPosition.setTrafficType(TrafficType.MEDIATION);
    publisherPosition.setPlacementCategory(PlacementCategory.INTERSTITIAL);
    publisherPosition.setVideoSupport(VideoSupport.VIDEO_AND_BANNER);
    publisherPosition.setVideoLinearity(VideoLinearity.NON_LINEAR);
    PlacementVideoDTO placementVideoDTO1 = TestObjectsFactory.createDefaultPlacementVideoDTO();
    publisherPosition.setPlacementVideo(placementVideoDTO1);
    // site
    PublisherSiteDTO siteDTO = new PublisherSiteDTO();
    publisherPosition.setSite(siteDTO);
    publisherPosition.setPid(sourceSite.getPid());
    publisherPosition.getSite().setType(SiteType.DESKTOP);
    // companions
    PlacementVideoCompanionDTO placementVideoCompanionDTO1 =
        TestObjectsFactory.createDefaultPlacementVideoCompanionDTO();
    placementVideoDTO1.addCompanion(placementVideoCompanionDTO1);

    PublisherPositionDTO copyPosition =
        publisherSelfService.copyPosition(
            sourceSite.getCompanyPid(),
            sourceSite.getPid(),
            1L,
            targetSite.getPid(),
            publisherPosition);
    assertEquals(inputPositionName, copyPosition.getName());
    assertEquals(positionId, copyPosition.getPid());
    assertEquals(TrafficType.MEDIATION, copyPosition.getTrafficType());
    PlacementVideoDTO savedPlacementVideoDTO = copyPosition.getPlacementVideo();
    assertNotNull(savedPlacementVideoDTO);
    assertNotNull(savedPlacementVideoDTO.getCompanions());
    assertEquals(1, savedPlacementVideoDTO.getCompanions().size());
  }

  @Test
  void shouldUpdatePositionWithLongformPlacementVideo() {
    Site site = getSite();
    Long PID = 3L;
    when(sellerSiteService.getSite(anyLong())).thenReturn(site);
    when(sellerSiteService.getValidatedSiteForPublisher(anyLong(), anyLong())).thenReturn(site);
    when(userContext.isPublisherSelfServeEnabled(anyLong())).thenReturn(true);

    var input = getPublisherPosition(PID, TrafficType.MEDIATION, ImmutableList.of(), null);

    Position position = getPosition(PID, TrafficType.MEDIATION);
    position.setVideoSupport(VideoSupport.VIDEO_AND_BANNER);

    site.addPosition(position);
    PlacementVideoDTO placementVideoDTO =
        TestObjectsFactory.createDefaultLongformPlacementVideoDTO();
    placementVideoDTO.setPid(PID);
    placementVideoDTO.setVersion(1);
    input.setPlacementVideo(placementVideoDTO);

    when(placementVideoService.getPlacementVideo(anyLong())).thenReturn(placementVideoDTO);

    when(publisherPositionAssembler.make(any(PublisherPositionContext.class), any(Position.class)))
        .thenReturn(input);
    when(publisherPositionAssembler.apply(
            any(PublisherPositionContext.class),
            any(Position.class),
            any(PublisherPositionDTO.class)))
        .thenReturn(position);

    PlacementVideo placementVideo = TestObjectsFactory.createDefaultPlacementVideo();
    placementVideo.setPid(PID);

    PublisherPositionDTO publisherPosition =
        publisherSelfService.updatePosition(PUBLISHER_PID, 1L, input, false);
    assertEquals(3L, publisherPosition.getPid().longValue());

    PlacementVideoDTO updatedPlacementVideoDTO = publisherPosition.getPlacementVideo();

    assertNotNull(updatedPlacementVideoDTO);
    assertEquals(PlacementVideoStreamType.VOD, updatedPlacementVideoDTO.getStreamType());
    assertEquals("test_player", updatedPlacementVideoDTO.getPlayerBrand());
    assertEquals(PlacementVideoSsai.ALL_CLIENT_SIDE, updatedPlacementVideoDTO.getSsai());
  }

  @Test
  void shouldGetSiteUpdateInfo() {
    // given
    long publisherPid = random.nextLong();
    long sitePid = random.nextLong();
    String txId = "Monty Python";
    PublisherSiteDTO publisherSiteDto =
        PublisherSiteDTO.newBuilder().withPid(sitePid).withVersion(1).build();
    Site site = getSite(sitePid);

    site.setVersion(publisherSiteDto.getVersion());

    given(sellerSiteService.getSite(sitePid)).willReturn(site);
    given(sellerSiteService.calcHashForSiteUpdate(any())).willReturn(txId);
    given(publisherSiteAssembler.apply(publisherPid, site, publisherSiteDto)).willReturn(site);

    // when
    SiteUpdateInfoDTO result =
        publisherSelfService.siteUpdateInfo(publisherPid, publisherSiteDto, false);

    // then
    assertEquals(result.getTxId(), txId);
  }

  @Test
  void shouldDeleteTier() {
    // given
    long companyPid = random.nextLong();
    long sitePid = random.nextLong();
    long positionPid = random.nextLong();
    long tierPid = random.nextLong();

    Site site = getSite(sitePid);
    Tier tier = getTier(tierPid, 0, TierType.WATERFALL, List.of());
    Position position = site.getPositions().iterator().next();
    position.setTiers(List.of(tier));
    position.setPid(positionPid);

    given(sellerSiteService.getSite(sitePid)).willReturn(site);

    // when
    publisherSelfService.deleteTier(companyPid, sitePid, positionPid, tierPid);

    // then
    assertEquals(0, position.getTiers().size());
  }

  @Test
  void shouldGetBuyers() {
    // given
    long publisherPid = random.nextLong();
    String search = "";

    AdSourceSummaryDTO adSourceSummaryDto = new AdSourceSummaryDTO();
    SellerAdSource sellerAdSource = new SellerAdSource();
    PublisherBuyerDTO publisherBuyerDTO = PublisherBuyerDTO.newBuilder().build();

    given(userContext.isNexageUser()).willReturn(true);
    given(buyerService.getAllAdSourceSummaries()).willReturn(List.of(adSourceSummaryDto));
    given(sellerService.getAllAdsourceDefaults(publisherPid)).willReturn(List.of(sellerAdSource));
    given(publisherAdSourceDefaultsAssembler.make(any(), eq(sellerAdSource)))
        .willReturn(PublisherAdSourceDefaultsDTO.newBuilder().build());
    given(publisherBuyerAssembler.make(any(), eq(adSourceSummaryDto)))
        .willReturn(publisherBuyerDTO);

    // when
    List<PublisherBuyerDTO> result = publisherSelfService.getBuyers(publisherPid, search);

    // then
    assertEquals(List.of(publisherBuyerDTO), result);
  }

  @Test
  void shouldGetAvailableAdSources() {
    // given
    long publisherPid = random.nextLong();
    long adSource1Pid = random.nextLong();
    long adSource2Pid = adSource1Pid + 1;

    SellerAdSource sellerAdSource = new SellerAdSource();
    AdSource adSource = new AdSource();
    PublisherAdSourceDefaultsDTO adSourceDefaultsDto1 =
        PublisherAdSourceDefaultsDTO.newBuilder()
            .withAdSourcePid(adSource1Pid, Set.of("adSourcePid"))
            .build();
    PublisherAdSourceDefaultsDTO adSourceDefaultsDto2 =
        PublisherAdSourceDefaultsDTO.newBuilder()
            .withAdSourcePid(adSource2Pid, Set.of("adSourcePid"))
            .build();

    given(sellerService.getAllAdsourceDefaults(publisherPid)).willReturn(List.of(sellerAdSource));
    given(sellerService.getPublisherSelfServeDefaultAdsources()).willReturn(List.of(adSource));
    given(publisherAdSourceDefaultsAssembler.make(any(), eq(sellerAdSource)))
        .willReturn(adSourceDefaultsDto1);
    given(publisherAdSourceDefaultsAssembler.make(any(), eq(adSource)))
        .willReturn(adSourceDefaultsDto2);

    // when
    Collection<PublisherAdSourceDefaultsDTO> result =
        publisherSelfService.getAvailableAdsources(publisherPid);

    // then
    assertEquals(Set.of(adSourceDefaultsDto1, adSourceDefaultsDto2), result);
  }

  @Test
  void shouldThrowGenevaValidationExceptionWhenThereIsNoAdSourceWithGivenId() {
    // given
    PublisherAdSourceDefaultsDTO defaults = new PublisherAdSourceDefaultsDTO.Builder().build();
    // when then
    assertThrows(
        GenevaValidationException.class,
        () -> publisherSelfService.createAdsourceDefaultsForSeller(2L, 1L, defaults));
  }

  @Test
  void shouldSavePublisherAdSource() {
    // given
    given(publisherAdSourceDefaultsAssembler.apply(any(), any(), any())).willCallRealMethod();
    given(publisherAdSourceDefaultsAssembler.make(any(), any(SellerAdSource.class)))
        .willCallRealMethod();
    given(publisherAdSourceDefaultsAssembler.make(any(), any(SellerAdSource.class), any()))
        .willCallRealMethod();
    given(sellerService.saveSellerAdSource(any())).willAnswer((args) -> args.getArgument(0));
    AdSource adSource = new AdSource();
    adSource.setPid(1L);
    given(buyerService.getAdSource(1L)).willReturn(adSource);
    PublisherAdSourceDefaultsDTO defaults = getPublisherAdSourceDTO(null);
    // when
    PublisherAdSourceDefaultsDTO adsourceDefaults =
        publisherSelfService.createAdsourceDefaultsForSeller(2L, 1L, defaults);
    // then
    verify(sellerService).saveSellerAdSource(any());
    assertEquals(defaults.getUsername(), adsourceDefaults.getUsername());
  }

  @Test
  void shouldThrowExceptionOnGetWhenNoResource() {
    // given
    given(sellerService.getSellerAdSourceBySellerPidAndAdSourcePid(1L, 2L))
        .willReturn(Optional.empty());
    // when then
    var exception =
        assertThrows(
            GenevaValidationException.class,
            () -> publisherSelfService.getAdsourceDefaultsForSeller(1L, 2L));
    assertEquals(ServerErrorCodes.SERVER_ADSOURCE_DEFAULTS_NOT_FOUND, exception.getErrorCode());
  }

  @Test
  void shouldReturnDtoOnGet() {
    // given
    SellerAdSource sellerAdSource = new SellerAdSource();
    sellerAdSource.setPid(100L);
    sellerAdSource.setVersion(1);
    given(sellerService.getSellerAdSourceBySellerPidAndAdSourcePid(2L, 3L))
        .willReturn(Optional.of(sellerAdSource));
    given(publisherAdSourceDefaultsAssembler.make(any(), any(SellerAdSource.class)))
        .willCallRealMethod();
    given(publisherAdSourceDefaultsAssembler.make(any(), any(SellerAdSource.class), any()))
        .willCallRealMethod();
    // when then
    PublisherAdSourceDefaultsDTO defaults =
        publisherSelfService.getAdsourceDefaultsForSeller(2L, 3L);
    assertEquals(100L, defaults.getPid());
  }

  @Test
  void shouldThrowExceptionOnSaveAdSourceWhenPidIsNotNull() {
    // given
    doThrow(EntityConstraintViolationException.class)
        .when(beanValidationService)
        .validate(any(), eq(CreateGroup.class));
    PublisherAdSourceDefaultsDTO defaults = getPublisherAdSourceDTO(99L);
    // when then
    assertThrows(
        EntityConstraintViolationException.class,
        () -> publisherSelfService.createAdsourceDefaultsForSeller(2L, 1L, defaults));
  }

  @Test
  void shouldThrowGenevaValidationExceptionOnSaveAdSourceWhenAdSourceNotAvailable() {
    // given
    given(buyerService.getAdSource(1L)).willReturn(null);
    PublisherAdSourceDefaultsDTO defaults = getPublisherAdSourceDTO(99L);
    // when then
    assertThrows(
        GenevaValidationException.class,
        () -> publisherSelfService.createAdsourceDefaultsForSeller(2L, 1L, defaults));
  }

  @Test
  void shouldUpdateAdSource() {
    // given
    given(publisherAdSourceDefaultsAssembler.apply(any(), any(), any())).willCallRealMethod();
    given(publisherAdSourceDefaultsAssembler.make(any(), any(SellerAdSource.class)))
        .willCallRealMethod();
    given(publisherAdSourceDefaultsAssembler.make(any(), any(SellerAdSource.class), any()))
        .willCallRealMethod();
    given(sellerService.saveSellerAdSource(any())).willAnswer((args) -> args.getArgument(0));
    AdSource adSource = new AdSource();
    adSource.setPid(1L);
    SellerAdSource sellerAdSource = new SellerAdSource();
    sellerAdSource.setPid(100L);
    sellerAdSource.setVersion(1);
    given(sellerService.getSellerAdSourceBySellerPidAndAdSourcePid(2L, 1L))
        .willReturn(Optional.of(sellerAdSource));

    PublisherAdSourceDefaultsDTO defaults = getPublisherAdSourceDTO(100L);
    // when
    PublisherAdSourceDefaultsDTO dto =
        publisherSelfService.updateAdsourceDefaultsForSeller(2L, 1L, defaults);
    // then
    verify(sellerService).saveSellerAdSource(any());
    assertEquals(100L, dto.getPid());
    assertEquals(2L, dto.getSellerPid());
  }

  @Test
  void shouldThrowExceptionOnDeleteWhenNoSellerAdSource() {
    var exception =
        assertThrows(
            GenevaValidationException.class,
            () -> publisherSelfService.deleteAdsourceDefaultsForSeller(2L, 1L));
    assertEquals(ServerErrorCodes.SERVER_ADSOURCE_DEFAULTS_NOT_FOUND, exception.getErrorCode());
  }

  @Test
  void shouldDeleteSellerAdSource() {
    given(sellerService.existsSellerAdSource(2L, 1L)).willReturn(true);
    publisherSelfService.deleteAdsourceDefaultsForSeller(2L, 1L);
    verify(sellerService).deleteSellerAdSourceBySellerPidAndAdSourcePid(2L, 1L);
  }

  @Test
  void shouldThrowExceptionOnDeleteNonExistingResource() {
    given(sellerService.existsSellerAdSource(2L, 1L)).willReturn(false);
    var exception =
        assertThrows(
            GenevaValidationException.class,
            () -> publisherSelfService.deleteAdsourceDefaultsForSeller(2L, 1L));
    assertEquals(ServerErrorCodes.SERVER_ADSOURCE_DEFAULTS_NOT_FOUND, exception.getErrorCode());
  }

  @Test
  void shouldReturnCorrectSiteFormatWhenDetailIsTrue() {
    // given
    var site = new Site();
    var profile = new RTBProfile();

    profile.setName("testProfile");
    profile.setLibraryPids(Set.of());
    site.setPid(10L);
    site.setDefaultRtbProfile(profile);

    given(companyRepository.countByPid(anyLong())).willReturn(1L);
    given(
            siteRepository.findByCompanyPidWithStatusNotDeletedAndSiteNotRestricted(
                anyLong(), anyLong()))
        .willReturn(List.of(site));
    given(publisherSiteAssembler.make(site, true))
        .willReturn(
            PublisherSiteDTO.newBuilder()
                .withPid(10L)
                .withDefaultRtbProfile(
                    PublisherDefaultRTBProfileDTO.newBuilder()
                        .withName("testProfile")
                        .withLibraryPids(Set.of())
                        .build())
                .build());

    // when
    var publisherSites = publisherSelfService.getSites(1L, true);

    // then
    assertFalse(publisherSites.isEmpty());
    assertEquals(10L, publisherSites.get(0).getPid());
    assertEquals("testProfile", publisherSites.get(0).getDefaultRtbProfile().getName());
    assertNotNull(publisherSites.get(0).getDefaultRtbProfile().getLibraryPids());
  }

  @Test
  void shouldThrowErrorWhenSitesRetrievedAndCompanyNotFound() {
    // given
    given(companyRepository.countByPid(anyLong())).willReturn(0L);

    // when & then
    var ex =
        assertThrows(
            GenevaValidationException.class, () -> publisherSelfService.getSites(1L, true));
    assertEquals(ServerErrorCodes.SERVER_COMPANY_NOT_FOUND, ex.getErrorCode());
  }

  @Test
  void shouldGetBidderPerformance() {
    // given
    var performance = new BiddersPerformanceForPubSelfServe();
    performance.setBidRequests(1L);
    var details = mock(SpringUserDetails.class);
    given(details.getUsername()).willReturn("User1");
    given(userContext.getCurrentUser()).willReturn(details);
    given(companyService.getCompany(anyLong())).willReturn(new Company());
    given(
            siteRepository.findPidsByCompanyPidsWithStatusNotDeletedAndSiteNotRestricted(
                anyLong(), anySet()))
        .willReturn(Set.of(1L));
    given(userContext.isNexageUser()).willReturn(true);
    given(
            biddersPerformanceFacade.getBiddersPerformanceForPubSelfServe(
                anySet(), any(Date.class), any(Date.class), anyString()))
        .willReturn(List.of(performance));

    // when
    var result = publisherSelfService.getBiddersPerformanceForPSS(1L, new Date(1L), new Date(100L));

    // then
    assertEquals(1L, result.get(0).getBidRequests());
  }

  @Test
  void shouldGetBidderPerformanceWhenNoSites() {
    // given
    given(companyService.getCompany(anyLong())).willReturn(new Company());
    given(
            siteRepository.findPidsByCompanyPidsWithStatusNotDeletedAndSiteNotRestricted(
                anyLong(), anySet()))
        .willReturn(Set.of());
    given(userContext.isNexageUser()).willReturn(true);

    // when
    var result = publisherSelfService.getBiddersPerformanceForPSS(1L, new Date(1L), new Date(100L));

    // then
    assertTrue(result.isEmpty());
  }

  @Test
  void shouldThrowWhenGettingPerformanceButUserNonNexageAndCompanyNotRtbRevenueReportEnabled() {
    // given
    var company = new Company();
    company.setRtbRevenueReportEnabled(false);
    given(companyService.getCompany(anyLong())).willReturn(company);

    // when & then
    var exception =
        assertThrows(
            GenevaSecurityException.class,
            () -> publisherSelfService.getBiddersPerformanceForPSS(1L, START, END));
    assertEquals(SecurityErrorCodes.SECURITY_NOT_AUTHORIZED, exception.getErrorCode());
  }

  @Test
  void shouldNotThrowWhenGettingPerformanceAndUserNonNexageAndCompanyRtbRevenueReportEnabled() {
    // given
    var company = new Company();
    company.setRtbRevenueReportEnabled(true);
    given(companyService.getCompany(anyLong())).willReturn(company);

    // when & then
    assertDoesNotThrow(() -> publisherSelfService.getBiddersPerformanceForPSS(1L, START, END));
  }

  @Test
  void shouldNotThrowWhenGettingPerformanceAndUserNexageAndCompanyNotRtbRevenueReportEnabled() {
    // given
    var company = new Company();
    company.setRtbRevenueReportEnabled(false);
    given(companyService.getCompany(anyLong())).willReturn(company);
    given(userContext.isNexageUser()).willReturn(true);

    // when & then
    assertDoesNotThrow(() -> publisherSelfService.getBiddersPerformanceForPSS(1L, START, END));
  }

  @Test
  void shouldUpdatePositionWithMultiBiddingPlacementVideo() {
    Site site = getSite();
    Long PID = 3L;
    when(sellerSiteService.getSite(anyLong())).thenReturn(site);
    when(sellerSiteService.getValidatedSiteForPublisher(anyLong(), anyLong())).thenReturn(site);
    when(userContext.isPublisherSelfServeEnabled(anyLong())).thenReturn(true);

    var input = getPublisherPosition(PID, TrafficType.MEDIATION, ImmutableList.of(), null);

    Position position = getPosition(PID, TrafficType.MEDIATION);
    position.setVideoSupport(VideoSupport.VIDEO_AND_BANNER);
    site.addPosition(position);
    PlacementVideoDTO placementVideoDTO =
        TestObjectsFactory.createDefaultLongformPlacementVideoDTO();
    placementVideoDTO.setPid(PID);
    placementVideoDTO.setVersion(1);
    placementVideoDTO.setMultiImpressionBid(true);
    placementVideoDTO.setCompetitiveSeparation(false);
    input.setPlacementVideo(placementVideoDTO);

    when(placementVideoService.getPlacementVideo(anyLong())).thenReturn(placementVideoDTO);

    when(publisherPositionAssembler.make(any(PublisherPositionContext.class), any(Position.class)))
        .thenReturn(input);
    when(publisherPositionAssembler.apply(
            any(PublisherPositionContext.class),
            any(Position.class),
            any(PublisherPositionDTO.class)))
        .thenReturn(position);

    PublisherPositionDTO publisherPosition =
        publisherSelfService.updatePosition(PUBLISHER_PID, 1L, input, false);
    assertEquals(3L, publisherPosition.getPid().longValue());
    PlacementVideoDTO updatedPlacementVideoDTO = publisherPosition.getPlacementVideo();
    assertNotNull(updatedPlacementVideoDTO);
    assertTrue(updatedPlacementVideoDTO.isMultiImpressionBid());
    assertFalse(updatedPlacementVideoDTO.isCompetitiveSeparation());
  }

  @Test
  void shouldGenerateUUIDForPositionAliasNameIfProvidedValueIsEmptyOnCreate() {
    Site site = getSite();
    when(sellerSiteService.getSite(anyLong())).thenReturn(site);
    when(userContext.isPublisherSelfServeEnabled(anyLong())).thenReturn(true);
    when(publisherPositionAssembler.apply(
            any(PublisherPositionContext.class),
            any(Position.class),
            any(PublisherPositionDTO.class)))
        .thenReturn(new Position());
    when(publisherPositionAssembler.make(any(PublisherPositionContext.class), any(Position.class)))
        .then(
            invocation -> {
              Position position = invocation.getArgument(1);
              PublisherPositionDTO publisherPosition = new PublisherPositionDTO();
              publisherPosition.setName(position.getName());
              publisherPosition.setPositionAliasName(position.getPositionAliasName());
              return publisherPosition;
            });
    when(sellerPositionService.createPosition(anyLong(), any(Position.class)))
        .thenAnswer(
            invocation -> {
              Position position = invocation.getArgument(1);
              site.addPosition(position);
              return null;
            });

    PublisherPositionDTO publisherPosition =
        getPublisherPosition(1L, TrafficType.MEDIATION, ImmutableList.of(), null);
    publisherPosition.setPositionAliasName("");

    PlacementVideoDTO placementVideoDTO = TestObjectsFactory.createDefaultPlacementVideoDTO();
    placementVideoDTO.setLinearity(PlacementVideoLinearity.LINEAR);
    placementVideoDTO.setLongform(true);
    when(placementVideoService.populateVideoData(any(), any())).thenReturn(placementVideoDTO);
    publisherPosition.setPlacementVideo(placementVideoDTO);

    PublisherPositionDTO createdPosition =
        publisherSelfService.createPosition(site.getPid(), publisherPosition, false);
    assertFalse(StringUtils.isBlank(createdPosition.getPositionAliasName()));
  }

  @Test
  void shouldGenerateUUIDForPositionAliasNameIfProvidedValueIsEmptyOnUpdate() {
    Site site = getSite();
    when(sellerSiteService.getSite(anyLong())).thenReturn(site);
    when(sellerSiteService.getValidatedSiteForPublisher(anyLong(), anyLong())).thenReturn(site);
    when(userContext.isPublisherSelfServeEnabled(anyLong())).thenReturn(true);

    var input = getPublisherPosition(1L, TrafficType.MEDIATION, ImmutableList.of(), null);
    input.setPositionAliasName("");

    PlacementVideoDTO placementVideoDTO = TestObjectsFactory.createDefaultPlacementVideoDTO();
    when(placementVideoService.getPlacementVideo(anyLong())).thenReturn(placementVideoDTO);

    when(publisherPositionAssembler.make(any(PublisherPositionContext.class), any(Position.class)))
        .thenReturn(input);

    PublisherPositionDTO publisherPosition =
        publisherSelfService.updatePosition(PUBLISHER_PID, 1L, input, false);
    assertFalse(StringUtils.isBlank(publisherPosition.getPositionAliasName()));
  }

  @Test
  void shouldGenerateUUIDForPositionAliasNameIfProvidedValueIsEmptyOnCopy() {
    Site sourceSite = getSite();
    Site targetSite = getSite(2L);
    when(sellerSiteService.getSite(1L)).thenReturn(sourceSite);
    when(sellerSiteService.getSite(2L)).thenReturn(targetSite);
    doNothing().when(positionLimitChecker).checkLimitsPositionsInSite(1L, 2L);
    when(publisherPositionAssembler.apply(
            any(PublisherPositionContext.class),
            any(Position.class),
            any(PublisherPositionDTO.class)))
        .thenAnswer(
            invocation -> {
              Position position = invocation.getArgument(1);
              PublisherPositionDTO publisherPositionDTO = invocation.getArgument(2);
              position.setName(publisherPositionDTO.getName());
              return position;
            });
    when(sellerPositionService.createPosition(anyLong(), any(Position.class)))
        .thenAnswer(
            invocation -> {
              Position position = invocation.getArgument(1);
              position.setPid(1L);
              targetSite.addPosition(position);
              return null;
            });
    when(publisherPositionAssembler.make(
            any(PublisherPositionContext.class), any(Position.class), any()))
        .then(
            invocation -> {
              Position position = invocation.getArgument(1);
              PublisherPositionDTO publisherPosition = new PublisherPositionDTO();
              publisherPosition.setName(position.getName());
              publisherPosition.setPid(position.getPid());
              return publisherPosition;
            });
    // Input request params
    PublisherPositionDTO publisherPosition = new PublisherPositionDTO();
    publisherPosition.setPositionAliasName("");

    PublisherPositionDTO copyPosition =
        publisherSelfService.copyPosition(
            sourceSite.getCompanyPid(),
            sourceSite.getPid(),
            1L,
            targetSite.getPid(),
            publisherPosition);
    assertFalse(StringUtils.isBlank(publisherPosition.getPositionAliasName()));
  }

  private PublisherAdSourceDefaultsDTO getPublisherAdSourceDTO(Long pid) {
    Set<String> publisherAdSourceFields =
        Set.of(
            "adSourcePid",
            "sellerPid",
            "username",
            "password",
            "apiKey",
            "apiToken",
            "pid",
            "version");
    return new PublisherAdSourceDefaultsDTO.Builder()
        .withPid(pid, publisherAdSourceFields)
        .withAdSourcePid(1L, publisherAdSourceFields)
        .withSellerPid(2L, publisherAdSourceFields)
        .withUserName("testUser", publisherAdSourceFields)
        .withPassword("testPassword", publisherAdSourceFields)
        .withApiKey("apiKey", publisherAdSourceFields)
        .withApiToken("apiToken", publisherAdSourceFields)
        .withVersion(1, publisherAdSourceFields)
        .build();
  }

  private void verifyBadRequestErrorMessage(
      List<PublisherTagDTO> publisherTags, ServerErrorCodes error) {
    try {
      publisherSelfService.generateSmartYieldDemandSourceTags(1, 1, 1, publisherTags);
      fail("Expected exception not thrown");
    } catch (GenevaValidationException bre) {
      assertEquals(error, bre.getErrorCode());
    } catch (Exception ee) {
      fail("Unexpected Exception: " + ee.getMessage());
    }
  }

  private Site getSite() {
    return getSite(1L);
  }

  private Site getSite(long pid) {
    Site site = new Site();
    site.setId("test-id");
    site.setPid(pid);
    site.setName("geneva-test");
    site.setGroupsEnabled(true);
    site.setPositions(
        new HashSet<>(
            List.of(
                getPosition(1, TrafficType.SMART_YIELD), getPosition(2, TrafficType.MEDIATION))));
    site.setTags(new HashSet<>(tagList));
    site.setCompanyPid(1L);
    return site;
  }

  private Position getPosition(long pid, TrafficType trafficType) {
    Position position = new Position();
    ObjectMapper mapper = new ObjectMapper();
    String jsonString =
        "{\n"
            + "\t\"pid\": "
            + pid
            + ",\n"
            + "\t\"version\": 1,\n"
            + "\t\"site\": {\n"
            + "\t\t\"pid\": 1,\n"
            + "\t\t\"hbEnabled\": false\n"
            + "\t},\n"
            + "\t\"name\": \"testp1\",\n"
            + "\t\"memo\": \"testP1\",\n"
            + "\t\"mraidSupport\": \"YES\",\n"
            + "\t\"videoSupport\": \"BANNER\",\n"
            + "\t\"screenLocation\": \"FOOTER_VISIBLE\",\n"
            + "\t\"placementCategory\": \"BANNER\",\n"
            + "\t\"trafficType\": \""
            + trafficType
            + "\",\n"
            + "\t\"status\": \"ACTIVE\"\n"
            + "}";

    try {
      position = mapper.readValue(jsonString, Position.class);
    } catch (IOException e) {
      log.error("exception while converting json to an object" + e);
      e.printStackTrace();
    }

    return position;
  }

  private Tier getTier(long pid, int level, TierType type, List<Tag> tags) {
    Tier tier = new Tier();
    tier.setPid(pid);
    tier.setLevel(level);
    tier.setTierType(type);
    tier.setVersion(1);
    if (tags == null) {
      tags = new ArrayList<>();
    }
    tier.setTags(tags);
    return tier;
  }

  private Tag getTag(long pid, long buyerPid) {
    Tag tag = new Tag();
    tag.setPid(pid);
    tag.setStatus(Status.ACTIVE);
    tag.setBuyerPid(buyerPid);
    tag.setIdentifier(String.valueOf(pid));
    tag.setName("name-" + pid);
    tag.setBuyerName("buyerName-" + buyerPid);

    return tag;
  }

  private Tag getTag(long pid, long buyerPid, long site, long position, Integer version) {
    Tag tag = getTag(pid, buyerPid);
    tag.setSite(getSite(site));
    TagPosition tp = new TagPosition();
    tp.setPid(position);
    tag.setPosition(tp);
    tag.setVersion(version);

    return tag;
  }

  private PublisherTagDTO getPublisherTag(Long pid, Long buyerPid, Long posPid, Integer version) {
    return PublisherTagDTO.newBuilder()
        .withPid(pid)
        .withBuyer(PublisherBuyerDTO.newBuilder().withPid(buyerPid).build())
        .withSite(PublisherSiteDTO.newBuilder().withPid(1L).build())
        .withPosition(PublisherPositionDTO.builder().withPid(posPid).build())
        .withVersion(version)
        .build();
  }

  private PublisherTierDTO getPublisherTier(
      Long pid, int level, TierType tierType, List<PublisherTagDTO> tags) {
    PublisherTierDTO.Builder builder =
        PublisherTierDTO.newBuilder()
            .withPid(pid)
            .withLevel(level)
            .withTierType(tierType)
            .withVersion(1)
            .withOrderStrategy(PublisherTierDTO.OrderStrategy.Dynamic);
    if (tags != null) tags.forEach(builder::withTag);
    return builder.build();
  }

  private PublisherPositionDTO getPublisherPosition(
      Long pid,
      TrafficType trafficType,
      List<PublisherTierDTO> tiers,
      PublisherTagDTO decisionMaker) {
    PublisherPositionDTO.PublisherPositionDTOBuilder builder =
        PublisherPositionDTO.builder()
            .withPid(pid)
            .withTrafficType(trafficType)
            .withVersion(1)
            .withDecisionMaker(decisionMaker)
            .withSite(PublisherSiteDTO.newBuilder().withPid(1L).build());
    if (tiers != null) tiers.forEach(builder::withTier);
    var dto = builder.build();
    ReflectionTestUtils.setField(dto, "tags", new HashSet<>());
    return dto;
  }

  private AdSource getAdSources(Long buyer) {
    AdSource adSource = new AdSource();
    adSource.setPid(buyer);
    if (buyer.equals(REGULAR_BUYER)) {
      adSource.setDecisionMakerEnabled(AdSource.DecisionMakerEnabled.NO);
      adSource.setBidEnabled(AdSource.BidEnabled.NO);
    }
    if (buyer.equals(BID_DM_ENABLED_BUYER)) {
      adSource.setDecisionMakerEnabled(AdSource.DecisionMakerEnabled.NO);
      adSource.setBidEnabled(AdSource.BidEnabled.YES);
    }
    if (buyer.equals(DM_ENABLED_BUYER)) {
      adSource.setDecisionMakerEnabled(AdSource.DecisionMakerEnabled.YES);
      adSource.setBidEnabled(AdSource.BidEnabled.NO);
    }
    if (buyer.equals(BID_DM_ENABLED_BUYER)) {
      adSource.setDecisionMakerEnabled(AdSource.DecisionMakerEnabled.YES);
      adSource.setBidEnabled(AdSource.BidEnabled.YES);
    }

    return adSource;
  }

  private List<AdSource> getAdSources() {
    AdSource adSource1 = new AdSource();
    adSource1.setPid(REGULAR_BUYER);
    adSource1.setBidEnabled(AdSource.BidEnabled.NO);
    adSource1.setDecisionMakerEnabled(AdSource.DecisionMakerEnabled.NO);

    AdSource adSource2 = new AdSource();
    adSource2.setPid(BID_ENABLED_BUYER);
    adSource2.setBidEnabled(AdSource.BidEnabled.YES);
    adSource2.setDecisionMakerEnabled(AdSource.DecisionMakerEnabled.NO);

    AdSource adSource3 = new AdSource();
    adSource3.setPid(DM_ENABLED_BUYER);
    adSource3.setBidEnabled(AdSource.BidEnabled.NO);
    adSource3.setDecisionMakerEnabled(AdSource.DecisionMakerEnabled.YES);

    AdSource adSource4 = new AdSource();
    adSource4.setPid(BID_DM_ENABLED_BUYER);
    adSource4.setBidEnabled(AdSource.BidEnabled.YES);
    adSource4.setDecisionMakerEnabled(AdSource.DecisionMakerEnabled.YES);

    return List.of(adSource1, adSource2, adSource3, adSource4);
  }

  private Position deepCopy(Position input) {
    Position clonedPosition = new Position();
    BeanUtils.copyProperties(input, clonedPosition);

    return clonedPosition;
  }

  private Position getClonedPosition() {
    Position position = getPosition(1L, TrafficType.SMART_YIELD);
    Site oldSite = position.getSite();
    position.setSite(null);
    Position newPosition = deepCopy(position);
    position.setSite(oldSite);
    newPosition.setPid(null);
    newPosition.setVersion(null);
    newPosition.setTiers(new ArrayList<>());
    return newPosition;
  }

  private PublisherPositionDTO createPublisherPosition(VideoSupport videoSupport) {
    PublisherPositionDTO publisherPosition =
        getPublisherPosition(1L, TrafficType.MEDIATION, ImmutableList.of(), null);
    publisherPosition.setName(null);
    publisherPosition.setVideoSupport(videoSupport);
    publisherPosition.setVideoLinearity(VideoLinearity.LINEAR);

    return publisherPosition;
  }
}
