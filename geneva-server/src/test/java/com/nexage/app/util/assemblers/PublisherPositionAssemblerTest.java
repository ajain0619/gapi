package com.nexage.app.util.assemblers;

import static com.nexage.admin.core.enums.AssociationType.NON_DEFAULT;
import static com.nexage.admin.core.enums.PlacementCategory.BANNER;
import static com.nexage.admin.core.enums.PlacementCategory.IN_ARTICLE;
import static com.nexage.admin.core.enums.VideoSupport.VIDEO;
import static com.nexage.admin.core.enums.VideoSupport.VIDEO_AND_BANNER;
import static com.nexage.app.util.PlacementAssociationTypeTestUtil.AMAZON_TAM;
import static com.nexage.app.util.PlacementAssociationTypeTestUtil.GOOGLE_EB;
import static com.nexage.app.util.PlacementAssociationTypeTestUtil.formattedAssociationTypeSupportedHBPartners;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

import com.google.common.collect.Sets;
import com.nexage.admin.core.enums.AdSizeType;
import com.nexage.admin.core.enums.AssociationType;
import com.nexage.admin.core.enums.ImpressionTypeHandling;
import com.nexage.admin.core.enums.Status;
import com.nexage.admin.core.enums.TierType;
import com.nexage.admin.core.enums.TrafficType;
import com.nexage.admin.core.enums.site.Platform;
import com.nexage.admin.core.enums.site.Type;
import com.nexage.admin.core.model.HbPartner;
import com.nexage.admin.core.model.HbPartnerPosition;
import com.nexage.admin.core.model.HbPartnerSite;
import com.nexage.admin.core.model.HbPartnersAssociationView;
import com.nexage.admin.core.model.PlacementDooh;
import com.nexage.admin.core.model.Position;
import com.nexage.admin.core.model.RTBProfile;
import com.nexage.admin.core.model.Site;
import com.nexage.admin.core.model.Tag;
import com.nexage.admin.core.model.Tier;
import com.nexage.admin.core.repository.HbPartnerRepository;
import com.nexage.admin.core.repository.PositionRepository;
import com.nexage.admin.core.sparta.jpa.model.TagPosition;
import com.nexage.app.dto.HbPartnerAssignmentDTO;
import com.nexage.app.dto.publisher.PublisherDefaultRTBProfileDTO;
import com.nexage.app.dto.publisher.PublisherPositionDTO;
import com.nexage.app.dto.publisher.PublisherSiteDTO;
import com.nexage.app.dto.publisher.PublisherTagDTO;
import com.nexage.app.dto.publisher.PublisherTierDTO;
import com.nexage.app.dto.seller.PlacementDoohDTO;
import com.nexage.app.dto.seller.PlacementVideoDTO;
import com.nexage.app.error.ServerErrorCodes;
import com.nexage.app.security.UserContext;
import com.nexage.app.services.SellerSiteService;
import com.nexage.app.util.assemblers.context.PublisherPositionContext;
import com.nexage.app.web.support.TestObjectsFactory;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PublisherPositionAssemblerTest {

  private static final Long HB_PARTNER_PID = 1L;

  private Position position;
  private PublisherPositionContext publisherPositionContext;

  @Mock private UserContext userContext;
  @Mock private HbPartnerRepository hbPartnerRepository;
  @Mock private PositionRepository positionRepository;
  @Mock private PositionTierAssembler positionTierAssembler;
  @Mock private SellerSiteService sellerSiteService;

  @Mock private PublisherRTBProfileAssembler publisherRTBProfileAssembler;
  @Mock private PublisherTagAssembler publisherTagAssembler;

  @Mock private PublisherTierAssembler publisherTierAssembler;
  @InjectMocks private PublisherPositionAssembler assembler;

  @Test
  void shouldMapAdSizeTypeWhenRequested() {
    // given
    Position position = aBannerWithAdSizeType(AdSizeType.STANDARD);
    // when
    PublisherPositionDTO adSizeType =
        assembler.make(null, position, Collections.singleton("adSizeType"));
    // then
    assertEquals(AdSizeType.STANDARD, adSizeType.getAdSizeType());
  }

  @Test
  void shouldIgnoreAdSizeTypeTypeWhenNotRequested() {
    // given
    Position position = aBannerWithAdSizeType(AdSizeType.STANDARD);
    // when
    PublisherPositionDTO adSizeType = assembler.make(null, position, Collections.singleton("pid"));
    // then
    assertNull(adSizeType.getAdSizeType());
  }

  @Test
  void shouldHaveSiteTypeAndPlatformForSite() {
    // given
    Position position = createPositionWithSiteTypeAndPlatform(Type.DESKTOP, Platform.OTHER);
    publisherPositionContext =
        PublisherPositionContext.newBuilder().withSite(position.getSite()).build();
    // when
    PublisherPositionDTO publisherPositionDTO =
        assembler.make(publisherPositionContext, position, Collections.singleton("site"));
    // then
    assertEquals(PublisherSiteDTO.SiteType.DESKTOP, publisherPositionDTO.getSite().getType());
    assertEquals(PublisherSiteDTO.Platform.OTHER, publisherPositionDTO.getSite().getPlatform());
  }

  @Test
  void shouldHaveSiteTypeAndPlatformForWebsite() {
    // given
    Position position = createPositionWithSiteTypeAndPlatform(Type.WEBSITE, Platform.OTHER);
    publisherPositionContext =
        PublisherPositionContext.newBuilder().withSite(position.getSite()).build();
    // when
    PublisherPositionDTO publisherPositionDTO =
        assembler.make(publisherPositionContext, position, Collections.singleton("site"));
    // then
    assertEquals(PublisherSiteDTO.SiteType.WEBSITE, publisherPositionDTO.getSite().getType());
    assertEquals(PublisherSiteDTO.Platform.OTHER, publisherPositionDTO.getSite().getPlatform());
  }

  @Test
  void shouldSetAdSizeTypeDuringPlacementCreation() {
    // given
    Site site = new Site();
    PublisherPositionDTO publisherPosition =
        PublisherPositionDTO.builder()
            .withPlacementCategory(BANNER)
            .withAdSizeType(AdSizeType.CUSTOM)
            .build();
    // when
    Position position =
        assembler.apply(
            PublisherPositionContext.newBuilder().withSite(site).build(),
            new Position(),
            publisherPosition);
    // then
    assertEquals(publisherPosition.getAdSizeType(), position.getAdSizeType());
  }

  @Test
  void shouldSetImpressionTypeHandlingDuringPlacementCreation() {
    // given
    Site site = new Site();
    PublisherPositionDTO publisherPosition =
        PublisherPositionDTO.builder()
            .withImpressionTypeHandling(ImpressionTypeHandling.BASED_ON_INBOUND_REQUEST)
            .build();
    // when
    Position position =
        assembler.apply(
            PublisherPositionContext.newBuilder().withSite(site).build(),
            new Position(),
            publisherPosition);
    // then
    assertEquals(
        publisherPosition.getImpressionTypeHandling(), position.getImpressionTypeHandling());
  }

  @Test
  void shouldSanitizeUserInput() {
    // given
    Site site = new Site();
    PublisherPositionDTO publisherPosition =
        PublisherPositionDTO.builder()
            .withPlacementCategory(BANNER)
            .withAdSizeType(AdSizeType.CUSTOM)
            .withName("<script>alert('name');</script>")
            .withMemo("<script>alert('memo');</script>")
            .build();

    // when
    Position position =
        assembler.apply(
            PublisherPositionContext.newBuilder().withSite(site).build(),
            new Position(),
            publisherPosition);

    // then
    assertEquals("&lt;script&gt;alert('name');&lt;/script&gt;", position.getName());
    assertEquals("&lt;script&gt;alert('memo');&lt;/script&gt;", position.getMemo());
  }

  @Test
  void shouldNotSanitizeIfThereIsNoHtmlContent() {
    // given
    Site site = new Site();
    PublisherPositionDTO publisherPosition =
        PublisherPositionDTO.builder()
            .withPlacementCategory(BANNER)
            .withAdSizeType(AdSizeType.CUSTOM)
            .withName("position_")
            .withMemo("position&position&")
            .build();

    // when
    Position position =
        assembler.apply(
            PublisherPositionContext.newBuilder().withSite(site).build(),
            new Position(),
            publisherPosition);

    // then
    assertEquals("position_", position.getName());
    assertEquals("position&position&", position.getMemo());
  }

  @Test
  void shouldThrowWhenUpdateAdSizeTypeFromDynamicAdSizeType() {
    // given
    Position existingPosition = aBannerWithAdSizeType(AdSizeType.DYNAMIC);
    PublisherPositionDTO publisherPosition =
        PublisherPositionDTO.builder()
            .withPid(999L)
            .withPlacementCategory(BANNER)
            .withAdSizeType(AdSizeType.CUSTOM)
            .build();
    // when
    PublisherPositionContext publisherPositionContext =
        PublisherPositionContext.newBuilder().build();
    GenevaValidationException exception =
        assertThrows(
            GenevaValidationException.class,
            () -> assembler.apply(publisherPositionContext, existingPosition, publisherPosition));

    // then
    assertEquals(
        ServerErrorCodes.SERVER_PLACEMENT_UPDATE_DYNAMIC_AD_SIZE_TYPE_IS_NOT_ALLOWED,
        exception.getErrorCode());
  }

  @Test
  void test_fillHbPartnerAttributes_nullDTO() {
    position = new Position();
    Site site = new Site();
    publisherPositionContext = PublisherPositionContext.newBuilder().withSite(site).build();

    PublisherPositionDTO publisherPosition = PublisherPositionDTO.builder().build();
    publisherPosition.setHbPartnerAttributes(null);
    Set<HbPartnerPosition> hbPartnerPositionSet = Sets.newHashSet();
    hbPartnerPositionSet.add(new HbPartnerPosition());

    assembler.apply(publisherPositionContext, position, publisherPosition);
    assertEquals(Collections.emptySet(), site.getHbPartnerSite(), "Hb Partner shoulb be empty");
  }

  @Test
  void fillHbPartnerWithAssignmentsPresent() {
    position = new Position();
    Site site = new Site();
    HbPartner partner = createHbPartnerAndSiteAssociation(site, HB_PARTNER_PID);
    publisherPositionContext = PublisherPositionContext.newBuilder().withSite(site).build();

    when(hbPartnerRepository.findById(anyLong())).thenReturn(Optional.of(partner));

    PublisherPositionDTO publisherPosition = PublisherPositionDTO.builder().build();
    publisherPosition.setHbPartnerAttributes(null);
    Set<HbPartnerPosition> hbPartnerPositionSet = Sets.newHashSet();
    hbPartnerPositionSet.add(new HbPartnerPosition());
    HbPartnerAssignmentDTO assignmentDTO = new HbPartnerAssignmentDTO();
    assignmentDTO.setExternalId("pid");
    assignmentDTO.setHbPartnerPid(HB_PARTNER_PID);
    assignmentDTO.setType(AssociationType.DEFAULT);
    publisherPosition.setHbPartnerAttributes(Collections.singleton(assignmentDTO));

    assembler.apply(publisherPositionContext, position, publisherPosition);
    assertNotNull(position.getHbPartnerPosition());
    assertFalse(position.getHbPartnerPosition().isEmpty());
  }

  @Test
  void fillHbPartnerWithAssignmentsPresentWithNoExistingDefaultPosition() {
    position = new Position();
    Site site = new Site();
    site.setPid(1234L);
    HbPartner partner = createHbPartnerAndSiteAssociation(site, HB_PARTNER_PID);
    publisherPositionContext = PublisherPositionContext.newBuilder().withSite(site).build();

    when(hbPartnerRepository.findById(anyLong())).thenReturn(Optional.of(partner));

    List<HbPartnersAssociationView> defaultPositionsPerPartner =
        TestObjectsFactory.createDummyDefaultInventoriesPerHbPartnersWithDefaultPosition();
    defaultPositionsPerPartner.clear();
    when(positionRepository.findDefaultPositionsPerPartners(anyLong()))
        .thenReturn(defaultPositionsPerPartner);

    PublisherPositionDTO publisherPosition = PublisherPositionDTO.builder().build();
    publisherPosition.setName("Banner");
    publisherPosition.setHbPartnerAttributes(null);
    Set<HbPartnerPosition> hbPartnerPositionSet = Sets.newHashSet();
    hbPartnerPositionSet.add(new HbPartnerPosition());
    HbPartnerAssignmentDTO assignmentDTO = new HbPartnerAssignmentDTO();
    assignmentDTO.setExternalId(null);
    assignmentDTO.setHbPartnerPid(HB_PARTNER_PID);
    assignmentDTO.setType(AssociationType.DEFAULT);
    publisherPosition.setHbPartnerAttributes(Collections.singleton(assignmentDTO));

    assembler.apply(publisherPositionContext, position, publisherPosition);
    assertNotNull(position.getHbPartnerPosition());
    assertFalse(position.getHbPartnerPosition().isEmpty());
    assertEquals(assignmentDTO.getExternalId(), publisherPosition.getName(), "Invalid external id");
  }

  @Test
  void fillHbPartnerWithExistingDefaultPositionAndDefaultPositionHbPartnerAssignment() {
    position = new Position();
    Site site = new Site();
    site.setPid(123L);

    publisherPositionContext = PublisherPositionContext.newBuilder().withSite(site).build();

    List<HbPartnersAssociationView> defaultPositionsPerPartner =
        TestObjectsFactory.createDummyDefaultInventoriesPerHbPartnersWithDefaultPosition();
    when(positionRepository.findDefaultPositionsPerPartners(anyLong()))
        .thenReturn(defaultPositionsPerPartner);

    PublisherPositionDTO publisherPosition = PublisherPositionDTO.builder().build();
    publisherPosition.setHbPartnerAttributes(null);
    Set<HbPartnerPosition> hbPartnerPositionSet = Sets.newHashSet();
    hbPartnerPositionSet.add(new HbPartnerPosition());
    HbPartnerAssignmentDTO assignmentDTO = new HbPartnerAssignmentDTO();
    assignmentDTO.setExternalId("pid");
    assignmentDTO.setHbPartnerPid(HB_PARTNER_PID);
    assignmentDTO.setType(AssociationType.DEFAULT);
    publisherPosition.setHbPartnerAttributes(Collections.singleton(assignmentDTO));

    // when
    GenevaValidationException exception =
        assertThrows(
            GenevaValidationException.class,
            () -> assembler.apply(publisherPositionContext, position, publisherPosition));

    // then
    assertEquals(
        ServerErrorCodes.SERVER_HB_PARTNER_CANNOT_SET_DEFAULT_POSITION_WHEN_ALREADY_ONE_EXISTS,
        exception.getErrorCode());
  }

  @Test
  void
      shouldThrowExceptionWhenHbPartnerHasExistingBannerDefaultPositionAndAssignmentIsBannerDefault() {
    position = new Position();
    Site site = new Site();
    site.setPid(123L);
    HbPartner partner = createHbPartnerAndSiteAssociation(site, GOOGLE_EB);
    publisherPositionContext = PublisherPositionContext.newBuilder().withSite(site).build();

    List<HbPartnersAssociationView> defaultPositionsPerPartner =
        TestObjectsFactory.createDummyDefaultInventoriesPerHbPartnersWithDefaultPosition();
    when(positionRepository.findDefaultPositionsPerPartners(anyLong()))
        .thenReturn(defaultPositionsPerPartner);
    when(hbPartnerRepository.findPidsWhichSupportFormattedDefaults())
        .thenReturn(formattedAssociationTypeSupportedHBPartners);

    PublisherPositionDTO publisherPosition = PublisherPositionDTO.builder().build();
    publisherPosition.setHbPartnerAttributes(null);
    publisherPosition.setPlacementCategory(BANNER);
    publisherPosition.setVideoSupport(VIDEO_AND_BANNER);
    Set<HbPartnerPosition> hbPartnerPositionSet = Sets.newHashSet();
    hbPartnerPositionSet.add(new HbPartnerPosition());
    HbPartnerAssignmentDTO assignmentDTO = new HbPartnerAssignmentDTO();
    assignmentDTO.setExternalId("pid");
    assignmentDTO.setHbPartnerPid(GOOGLE_EB);
    assignmentDTO.setType(AssociationType.DEFAULT_BANNER);
    publisherPosition.setHbPartnerAttributes(Collections.singleton(assignmentDTO));

    // when
    GenevaValidationException exception =
        assertThrows(
            GenevaValidationException.class,
            () -> assembler.apply(publisherPositionContext, position, publisherPosition));

    // then
    assertEquals(
        ServerErrorCodes.SERVER_HB_PARTNER_CANNOT_SET_DEFAULT_POSITION_WHEN_ALREADY_ONE_EXISTS,
        exception.getErrorCode());
  }

  @Test
  void
      shouldNotThrowExceptionWhenHbPartnerHasExistingBannerDefaultPositionAndAssignmentIsVideoDefault() {
    position = new Position();
    Site site = new Site();
    site.setPid(123L);
    HbPartner partner = createHbPartnerAndSiteAssociation(site, GOOGLE_EB);

    publisherPositionContext = PublisherPositionContext.newBuilder().withSite(site).build();

    List<HbPartnersAssociationView> defaultPositionsPerPartner =
        TestObjectsFactory.createDummyDefaultInventoriesPerHbPartnersWithDefaultPosition();
    when(hbPartnerRepository.findById(anyLong())).thenReturn(Optional.of(partner));
    when(hbPartnerRepository.findPidsWhichSupportFormattedDefaults())
        .thenReturn(formattedAssociationTypeSupportedHBPartners);
    when(positionRepository.findDefaultPositionsPerPartners(anyLong()))
        .thenReturn(defaultPositionsPerPartner);

    PublisherPositionDTO publisherPosition = PublisherPositionDTO.builder().build();
    publisherPosition.setHbPartnerAttributes(null);
    publisherPosition.setPlacementCategory(IN_ARTICLE);
    Set<HbPartnerPosition> hbPartnerPositionSet = Sets.newHashSet();
    hbPartnerPositionSet.add(new HbPartnerPosition());
    HbPartnerAssignmentDTO assignmentDTO = new HbPartnerAssignmentDTO();
    assignmentDTO.setExternalId("pid");
    assignmentDTO.setHbPartnerPid(GOOGLE_EB);
    assignmentDTO.setType(AssociationType.DEFAULT_VIDEO);
    publisherPosition.setHbPartnerAttributes(Collections.singleton(assignmentDTO));

    assembler.apply(publisherPositionContext, position, publisherPosition);
    assertNotNull(position.getHbPartnerPosition());
    assertFalse(position.getHbPartnerPosition().isEmpty());
  }

  @Test
  void shouldThrowExceptionWithInvalidFormattedDefaultHbPartnerAssignment() {
    position = new Position();
    Site site = new Site();
    site.setPid(123L);
    HbPartner partner = createHbPartnerAndSiteAssociation(site, GOOGLE_EB);
    publisherPositionContext = PublisherPositionContext.newBuilder().withSite(site).build();

    PublisherPositionDTO publisherPosition = PublisherPositionDTO.builder().build();
    publisherPosition.setHbPartnerAttributes(null);
    publisherPosition.setPlacementCategory(BANNER);
    publisherPosition.setVideoSupport(VIDEO);
    Set<HbPartnerPosition> hbPartnerPositionSet = Sets.newHashSet();
    hbPartnerPositionSet.add(new HbPartnerPosition());
    HbPartnerAssignmentDTO assignmentDTO = new HbPartnerAssignmentDTO();
    assignmentDTO.setExternalId("pid");
    assignmentDTO.setHbPartnerPid(GOOGLE_EB);
    assignmentDTO.setType(AssociationType.DEFAULT_BANNER);
    publisherPosition.setHbPartnerAttributes(Collections.singleton(assignmentDTO));

    // when
    GenevaValidationException exception =
        assertThrows(
            GenevaValidationException.class,
            () -> assembler.apply(publisherPositionContext, position, publisherPosition));

    // then
    assertEquals(
        ServerErrorCodes.SERVER_INVALID_FORMATTED_DEFAULTS_HB_PARTNER_ASSIGNMENTS,
        exception.getErrorCode());
  }

  @Test
  void fillHbPartnerWithExistingDefaultPositionAndNonDefaultPositionHbPartnerAssignment() {
    position = new Position();
    Site site = new Site();
    site.setPid(123L);
    publisherPositionContext = PublisherPositionContext.newBuilder().withSite(site).build();

    List<HbPartnersAssociationView> defaultPositionsPerPartner =
        TestObjectsFactory.createDummyDefaultInventoriesPerHbPartnersWithDefaultPosition();
    when(positionRepository.findDefaultPositionsPerPartners(anyLong()))
        .thenReturn(defaultPositionsPerPartner);

    PublisherPositionDTO publisherPosition = PublisherPositionDTO.builder().build();
    publisherPosition.setName("interstitial");
    publisherPosition.setHbPartnerAttributes(null);
    Set<HbPartnerPosition> hbPartnerPositionSet = Sets.newHashSet();
    hbPartnerPositionSet.add(new HbPartnerPosition());
    HbPartnerAssignmentDTO assignmentDTO = new HbPartnerAssignmentDTO();
    assignmentDTO.setExternalId(null);
    assignmentDTO.setHbPartnerPid(HB_PARTNER_PID);
    assignmentDTO.setType(NON_DEFAULT);
    publisherPosition.setHbPartnerAttributes(Collections.singleton(assignmentDTO));

    // when
    GenevaValidationException exception =
        assertThrows(
            GenevaValidationException.class,
            () -> assembler.apply(publisherPositionContext, position, publisherPosition));

    // then
    assertEquals(ServerErrorCodes.SERVER_HB_PARTNER_FIELDS_MISSING, exception.getErrorCode());
  }

  @Test
  void
      fillHbPartnerWithExistingDefaultPositionAndNonDefaultPositionHbPartnerAssignmentExistingPositionMatching() {
    position = new Position();
    position.setPid(34L);
    Site site = new Site();
    site.setPid(123L);
    publisherPositionContext = PublisherPositionContext.newBuilder().withSite(site).build();

    List<HbPartnersAssociationView> defaultPositionsPerPartner =
        TestObjectsFactory.createDummyDefaultInventoriesPerHbPartnersWithDefaultPosition();
    when(positionRepository.findDefaultPositionsPerPartners(anyLong()))
        .thenReturn(defaultPositionsPerPartner);

    PublisherPositionDTO publisherPosition = PublisherPositionDTO.builder().build();
    publisherPosition.setName("interstitial");
    publisherPosition.setHbPartnerAttributes(null);
    Set<HbPartnerPosition> hbPartnerPositionSet = Sets.newHashSet();
    hbPartnerPositionSet.add(new HbPartnerPosition());
    HbPartnerAssignmentDTO assignmentDTO = new HbPartnerAssignmentDTO();
    assignmentDTO.setExternalId(null);
    assignmentDTO.setHbPartnerPid(HB_PARTNER_PID);
    assignmentDTO.setType(AssociationType.DEFAULT);
    publisherPosition.setHbPartnerAttributes(Collections.singleton(assignmentDTO));

    // when
    GenevaValidationException exception =
        assertThrows(
            GenevaValidationException.class,
            () -> assembler.apply(publisherPositionContext, position, publisherPosition));

    // then
    assertEquals(ServerErrorCodes.SERVER_HB_PARTNER_ASSIGNMENT_INVALID, exception.getErrorCode());

    assertEquals(assignmentDTO.getExternalId(), publisherPosition.getName(), "Invalid external id");
  }

  @Test
  void
      fillHbPartnerWithExistingDefaultPositionAndNonDefaultPositionHbPartnerAssignmentExistingPositionNotMatching() {
    position = new Position();
    position.setPid(23L);
    Site site = new Site();
    site.setPid(123L);
    publisherPositionContext = PublisherPositionContext.newBuilder().withSite(site).build();

    List<HbPartnersAssociationView> defaultPositionsPerPartner =
        TestObjectsFactory.createDummyDefaultInventoriesPerHbPartnersWithDefaultPosition();
    when(positionRepository.findDefaultPositionsPerPartners(anyLong()))
        .thenReturn(defaultPositionsPerPartner);

    PublisherPositionDTO publisherPosition = PublisherPositionDTO.builder().build();
    publisherPosition.setHbPartnerAttributes(null);
    Set<HbPartnerPosition> hbPartnerPositionSet = Sets.newHashSet();
    hbPartnerPositionSet.add(new HbPartnerPosition());
    HbPartnerAssignmentDTO assignmentDTO = new HbPartnerAssignmentDTO();
    assignmentDTO.setExternalId(null);
    assignmentDTO.setHbPartnerPid(HB_PARTNER_PID);
    assignmentDTO.setType(AssociationType.DEFAULT);
    publisherPosition.setHbPartnerAttributes(Collections.singleton(assignmentDTO));

    // when
    GenevaValidationException exception =
        assertThrows(
            GenevaValidationException.class,
            () -> assembler.apply(publisherPositionContext, position, publisherPosition));

    // then
    assertEquals(
        ServerErrorCodes.SERVER_HB_PARTNER_CANNOT_SET_DEFAULT_POSITION_WHEN_ALREADY_ONE_EXISTS,
        exception.getErrorCode());
  }

  @Test
  void fillHbPartnerWithNoExistingDefaultPositionAndDefaultVideoPositionHbPartnerAssignment() {
    position = new Position();
    Site site = new Site();
    site.setPid(123L);
    HbPartner partner = createHbPartnerAndSiteAssociation(site, GOOGLE_EB);
    publisherPositionContext = PublisherPositionContext.newBuilder().withSite(site).build();

    when(hbPartnerRepository.findById(anyLong())).thenReturn(Optional.of(partner));
    when(hbPartnerRepository.findPidsWhichSupportFormattedDefaults())
        .thenReturn(formattedAssociationTypeSupportedHBPartners);
    when(positionRepository.findDefaultPositionsPerPartners(anyLong()))
        .thenReturn(Collections.emptyList());

    PublisherPositionDTO publisherPosition = PublisherPositionDTO.builder().build();
    publisherPosition.setName("Banner");
    publisherPosition.setPlacementCategory(BANNER);
    publisherPosition.setVideoSupport(VIDEO_AND_BANNER);
    Set<HbPartnerAssignmentDTO> hbPartnerAssignmentDTOSet = Sets.newHashSet();
    HbPartnerAssignmentDTO hbPartnerAssignmentDTO = new HbPartnerAssignmentDTO();
    hbPartnerAssignmentDTO.setHbPartnerPid(GOOGLE_EB);
    hbPartnerAssignmentDTO.setExternalId(null);
    hbPartnerAssignmentDTO.setType(AssociationType.DEFAULT_BANNER);
    hbPartnerAssignmentDTOSet.add(hbPartnerAssignmentDTO);
    publisherPosition.setHbPartnerAttributes(hbPartnerAssignmentDTOSet);
    assembler.apply(publisherPositionContext, position, publisherPosition);
    assertEquals(
        hbPartnerAssignmentDTO.getExternalId(), publisherPosition.getName(), "Invalid external id");
  }

  @Test
  void fillHbPartnerNoAssignmentPresent() {
    position = new Position();
    Site site = new Site();
    publisherPositionContext = PublisherPositionContext.newBuilder().withSite(site).build();

    PublisherPositionDTO publisherPosition = PublisherPositionDTO.builder().build();

    assembler.apply(publisherPositionContext, position, publisherPosition);
    assertNotNull(position.getHbPartnerPosition());
    assertTrue(position.getHbPartnerPosition().isEmpty());
  }

  @Test
  void test_fillHbPartnerAttributes_pidPresentExternalIdAbsent_AssociationTypeNotDefault() {
    position = new Position();
    publisherPositionContext = PublisherPositionContext.newBuilder().withSite(new Site()).build();

    PublisherPositionDTO publisherPosition = PublisherPositionDTO.builder().build();
    Set<HbPartnerAssignmentDTO> hbPartnerAssignmentDTOSet = Sets.newHashSet();
    HbPartnerAssignmentDTO hbPartnerAssignmentDTO = new HbPartnerAssignmentDTO();
    hbPartnerAssignmentDTO.setHbPartnerPid(HB_PARTNER_PID);
    hbPartnerAssignmentDTO.setExternalId(null);
    hbPartnerAssignmentDTO.setType(NON_DEFAULT);
    hbPartnerAssignmentDTOSet.add(hbPartnerAssignmentDTO);
    publisherPosition.setHbPartnerAttributes(hbPartnerAssignmentDTOSet);

    // when
    GenevaValidationException exception =
        assertThrows(
            GenevaValidationException.class,
            () -> assembler.apply(publisherPositionContext, position, publisherPosition));

    // then
    assertEquals(ServerErrorCodes.SERVER_HB_PARTNER_FIELDS_MISSING, exception.getErrorCode());
  }

  @Test
  void test_fillHbPartnerAttributes_pidPresentExternalIdAbsent_AssociationTypeDefault() {
    position = new Position();
    Site site = new Site();
    HbPartner partner = createHbPartnerAndSiteAssociation(site, HB_PARTNER_PID);
    publisherPositionContext = PublisherPositionContext.newBuilder().withSite(site).build();

    when(hbPartnerRepository.findById(anyLong())).thenReturn(Optional.of(partner));

    PublisherPositionDTO publisherPosition = PublisherPositionDTO.builder().build();
    publisherPosition.setName("Banner");
    Set<HbPartnerAssignmentDTO> hbPartnerAssignmentDTOSet = Sets.newHashSet();
    HbPartnerAssignmentDTO hbPartnerAssignmentDTO = new HbPartnerAssignmentDTO();
    hbPartnerAssignmentDTO.setHbPartnerPid(HB_PARTNER_PID);
    hbPartnerAssignmentDTO.setExternalId(null);
    hbPartnerAssignmentDTO.setType(AssociationType.DEFAULT);
    hbPartnerAssignmentDTOSet.add(hbPartnerAssignmentDTO);
    publisherPosition.setHbPartnerAttributes(hbPartnerAssignmentDTOSet);
    assembler.apply(publisherPositionContext, position, publisherPosition);
    assertEquals(
        hbPartnerAssignmentDTO.getExternalId(), publisherPosition.getName(), "Invalid external id");
  }

  @Test
  void test_fillHbPartnerAttributes_failsWhenHbPartnerNotPresentInSite() throws Exception {
    position = new Position();
    Site site = new Site();
    publisherPositionContext = PublisherPositionContext.newBuilder().withSite(site).build();

    PublisherPositionDTO publisherPosition = PublisherPositionDTO.builder().build();
    Set<HbPartnerAssignmentDTO> hbPartnerAssignmentDTOSet = Sets.newHashSet();
    HbPartnerAssignmentDTO hbPartnerAssignmentDTO = new HbPartnerAssignmentDTO();
    hbPartnerAssignmentDTO.setHbPartnerPid(HB_PARTNER_PID);
    hbPartnerAssignmentDTO.setExternalId("external_id");
    hbPartnerAssignmentDTO.setType(NON_DEFAULT);
    hbPartnerAssignmentDTOSet.add(hbPartnerAssignmentDTO);
    publisherPosition.setHbPartnerAttributes(hbPartnerAssignmentDTOSet);

    HbPartnerSite hbPartnerSite = new HbPartnerSite();
    site.setPid(1234L);
    HbPartner hbPartner = new HbPartner();
    hbPartner.setPid(111L);
    hbPartnerSite.setHbPartner(hbPartner);
    hbPartnerSite.setSite(site);
    Set<HbPartnerSite> hbPartnerSiteSet = Sets.newHashSet();
    hbPartnerSiteSet.add(hbPartnerSite);
    site.setHbPartnerSite(hbPartnerSiteSet);

    // when
    GenevaValidationException exception =
        assertThrows(
            GenevaValidationException.class,
            () -> assembler.apply(publisherPositionContext, position, publisherPosition));

    // then
    assertEquals(ServerErrorCodes.SERVER_HB_PARTNER_ASSIGNMENT_INVALID, exception.getErrorCode());
  }

  @Test
  void testApiUserCanUpdateMraidAdvancedTracking() {
    // given
    Site site = new Site();

    PublisherPositionDTO publisherPosition =
        PublisherPositionDTO.builder()
            .withMraidAdvancedTracking(true)
            .withAdSizeType(AdSizeType.CUSTOM)
            .build();
    // when
    Position position =
        assembler.apply(
            PublisherPositionContext.newBuilder().withSite(site).build(),
            new Position(),
            publisherPosition);
    // then
    assertTrue(position.isMraidAdvancedTracking());
  }

  @Test
  void testApiUserCanUpdatePositionAliasName() {
    // given
    Site site = new Site();

    PublisherPositionDTO publisherPosition =
        PublisherPositionDTO.builder()
            .withPositionAliasName("aliasName")
            .withAdSizeType(AdSizeType.CUSTOM)
            .build();
    // when
    Position position =
        assembler.apply(
            PublisherPositionContext.newBuilder().withSite(site).build(),
            new Position(),
            publisherPosition);
    // then
    assertEquals("aliasName", position.getPositionAliasName());
  }

  @Test
  void testExternalUiUserCanUpdatePositionAliasName() {
    // Given
    Site site = new Site();

    PublisherPositionDTO publisherPosition =
        PublisherPositionDTO.builder()
            .withPositionAliasName("aliasName")
            .withAdSizeType(AdSizeType.CUSTOM)
            .build();
    // When
    Position position =
        assembler.apply(
            PublisherPositionContext.newBuilder().withSite(site).build(),
            new Position(),
            publisherPosition);
    // Then
    assertEquals("aliasName", position.getPositionAliasName());
  }

  @Test
  void testPositionAliasNameIsVisibleForApiUser() {
    // given
    Position position = createPosition();
    position.setPositionAliasName("aliasName");
    // when
    PublisherPositionDTO publisherPosition =
        assembler.make(PublisherPositionContext.newBuilder().build(), position);
    // then
    assertEquals("aliasName", publisherPosition.getPositionAliasName());
  }

  @Test
  void testPositionAliasNameIsVisibleForExternalUiUser() {
    // Given
    Position position = createPosition();
    position.setPositionAliasName("aliasName");
    // When
    PublisherPositionDTO publisherPosition =
        assembler.make(PublisherPositionContext.newBuilder().build(), position);
    // Then
    assertEquals("aliasName", publisherPosition.getPositionAliasName());
  }

  @Test
  void shouldMapDoohWhenApplyPositionDTOwithNonNullDooh() {
    Site site = new Site();
    BigDecimal impMultiply = BigDecimal.TEN;
    PlacementDoohDTO dooh = new PlacementDoohDTO();
    dooh.setDefaultImpressionMultiplier(impMultiply);

    PublisherPositionDTO publisherPosition =
        PublisherPositionDTO.builder()
            .withPositionAliasName("aliasName")
            .withAdSizeType(AdSizeType.CUSTOM)
            .withDooh(dooh)
            .build();
    Position position =
        assembler.apply(
            PublisherPositionContext.newBuilder().withSite(site).build(),
            new Position(),
            publisherPosition);

    assertNotNull(position.getPlacementDooh());
    assertEquals(impMultiply, position.getPlacementDooh().getDefaultImpressionMultiplier());
  }

  @Test
  void shouldMapDefaultDoohWhenSiteTypeIsDoohAndDoohIsNull() {
    Site site = new Site();
    site.setType(Type.DOOH);

    PublisherPositionDTO publisherPosition =
        PublisherPositionDTO.builder()
            .withPositionAliasName("aliasName")
            .withAdSizeType(AdSizeType.CUSTOM)
            .build();

    Position position =
        assembler.apply(
            PublisherPositionContext.newBuilder().withSite(site).build(),
            new Position(),
            publisherPosition);
    assertNotNull(position.getPlacementDooh());
    assertEquals(BigDecimal.ONE, position.getPlacementDooh().getDefaultImpressionMultiplier());
  }

  @Test
  void shouldNotMapDefaultDoohWhenSiteTypeNotDooh() {
    Site site = new Site();
    site.setType(Type.DESKTOP);

    PublisherPositionDTO publisherPosition =
        PublisherPositionDTO.builder()
            .withPositionAliasName("aliasName")
            .withAdSizeType(AdSizeType.CUSTOM)
            .build();

    Position position =
        assembler.apply(
            PublisherPositionContext.newBuilder().withSite(site).build(),
            new Position(),
            publisherPosition);
    assertNull(position.getPlacementDooh());
  }

  @Test
  void shouldPopulateTiers() {
    PublisherTierDTO publisherTierDTO = PublisherTierDTO.newBuilder().withName("tier1").build();
    when(publisherTierAssembler.make(any(), any())).thenReturn(publisherTierDTO);

    Position position = new Position();
    List<Tier> tierList = List.of(new Tier(1));
    position.setTiers(tierList);
    Site site = new Site();
    site.setPid(1L);
    position.setSite(site);
    position.setPid(1L);
    site.setPositions(Set.of(position));
    when(sellerSiteService.getSite(any())).thenReturn(site);

    PublisherPositionDTO publisherPositionDTO =
        assembler.make(PublisherPositionContext.newBuilder().build(), position);
    assertTrue(publisherPositionDTO.getTiers().contains(publisherTierDTO));
    assertEquals(1, publisherPositionDTO.getTiers().size());
  }

  @Test
  void shouldPopulateTagsOnCopyOperation() {
    Position position = new Position();
    position.setPid(2L);
    Tag tag1 = new Tag();
    TagPosition tagPosition = new TagPosition();
    tagPosition.setPid(2L);
    tag1.setPosition(tagPosition);
    tag1.setPid(2L);
    Set<Tag> tagSet = Set.of(tag1);

    Site site = new Site();
    site.setPid(1L);
    site.setPositions(Set.of(position));
    site.setTags(tagSet);
    position.setSite(site);
    when(sellerSiteService.getSite(any())).thenReturn(site);
    position.setLongform(true);

    PublisherTagDTO publisherTagDTO = PublisherTagDTO.newBuilder().withPid(2L).build();
    when(publisherTagAssembler.make(any(), any())).thenReturn(publisherTagDTO);

    PublisherPositionDTO publisherPositionDTO =
        assembler.make(
            PublisherPositionContext.newBuilder().withCopyOperation(true).build(), position);
    assertEquals(1, publisherPositionDTO.getTags().size());
    assertEquals(2L, publisherPositionDTO.getTags().stream().findFirst().get().getPid());
  }

  @Test
  void shouldPopulateTagsWhenOperationIsNotCopy() {
    Position position = new Position();
    position.setPid(3L);
    Tag tag1 = new Tag();
    TagPosition tagPosition = new TagPosition();
    tagPosition.setPid(3L);
    tag1.setPosition(tagPosition);
    tag1.setStatus(Status.ACTIVE);
    tag1.setPid(3L);
    Set<Tag> tagSet = Set.of(tag1);

    Site site = new Site();
    site.setPid(1L);
    site.setPositions(Set.of(position));
    site.setTags(tagSet);
    when(sellerSiteService.getSite(any())).thenReturn(site);

    position.setSite(site);
    position.setLongform(true);
    PublisherPositionDTO publisherPositionDTO =
        assembler.make(
            PublisherPositionContext.newBuilder().withCopyOperation(false).build(), position);
    assertEquals(1, publisherPositionDTO.getTags().size());
    assertEquals(tag1.getPid(), publisherPositionDTO.getTags().stream().findFirst().get().getPid());
    assertEquals(
        tag1.getStatus().name(),
        publisherPositionDTO.getTags().stream().findFirst().get().getStatus().name());
  }

  @Test
  void shouldPopulateDecisionMaker() {
    Tier tier1 = new Tier();
    tier1.setTierType(TierType.SY_DECISION_MAKER);
    Tag tierTag1 = new Tag();
    tierTag1.setStatus(Status.ACTIVE);
    tierTag1.setPid(4L);
    tier1.setTags(List.of(tierTag1));
    Position position = new Position();
    position.setPid(3L);
    position.setTrafficType(TrafficType.SMART_YIELD);
    List<Tier> tierList = List.of(tier1);
    position.setTiers(tierList);

    Tag tag1 = new Tag();
    TagPosition tagPosition = new TagPosition();
    tagPosition.setPid(4L);
    tag1.setPosition(tagPosition);
    tag1.setStatus(Status.ACTIVE);
    tag1.setPid(4L);
    Set<Tag> tagSet = Set.of(tag1);

    Site site = new Site();
    site.setPid(1L);
    site.setPositions(Set.of(position));
    site.setTags(tagSet);
    when(sellerSiteService.getSite(any())).thenReturn(site);

    PublisherTagDTO publisherTagDTO =
        PublisherTagDTO.newBuilder().withPid(4L).withName("tag1").build();
    when(publisherTagAssembler.make(any(), any())).thenReturn(publisherTagDTO);

    position.setSite(site);
    PublisherPositionDTO publisherPositionDTO =
        assembler.make(PublisherPositionContext.newBuilder().build(), position);
    assertEquals(4L, publisherPositionDTO.getDecisionMaker().getPid());
  }

  @Test
  void shouldPopulateDefaultRtbProfile() {
    when(userContext.isNexageAdminOrManager()).thenReturn(true);

    RTBProfile rtbProfile = new RTBProfile();
    rtbProfile.setPid(5L);
    rtbProfile.setDefaultRtbProfileOwnerCompanyPid(5L);
    Position position = new Position();
    position.setDefaultRtbProfile(rtbProfile);

    PublisherDefaultRTBProfileDTO publisherRTBProfileDTO = new PublisherDefaultRTBProfileDTO();
    publisherRTBProfileDTO.setPid(5L);
    publisherRTBProfileDTO.setDefaultRtbProfileOwnerCompanyPid(5L);
    when(publisherRTBProfileAssembler.make(any(), any(), any())).thenReturn(publisherRTBProfileDTO);

    Site site = new Site();
    site.setPid(1L);
    site.setPositions(Set.of(position));
    when(sellerSiteService.getSite(any())).thenReturn(site);

    position.setSite(site);
    position.setPid(5L);
    PublisherPositionDTO publisherPositionDTO =
        assembler.make(PublisherPositionContext.newBuilder().withDetail(true).build(), position);
    assertEquals(
        5L, publisherPositionDTO.getDefaultRtbProfile().getDefaultRtbProfileOwnerCompanyPid());
  }

  @Test
  void shouldKeepSameInstanceOfDoohObjectWhenMappingExisting() {
    Site site = new Site();
    site.setType(Type.DOOH);
    BigDecimal impMultiply = BigDecimal.ONE;
    PlacementDoohDTO dooh = new PlacementDoohDTO();
    dooh.setDefaultImpressionMultiplier(impMultiply);

    PublisherPositionDTO publisherPosition =
        PublisherPositionDTO.builder()
            .withPositionAliasName("aliasName")
            .withAdSizeType(AdSizeType.CUSTOM)
            .withDooh(dooh)
            .build();
    Position existingPosition = new Position();
    PlacementDooh existingPlacementDooh = new PlacementDooh();
    existingPlacementDooh.setVersion(1);
    existingPlacementDooh.setDefaultImpressionMultiplier(BigDecimal.TEN);
    existingPosition.setPlacementDooh(existingPlacementDooh);

    Position position =
        assembler.apply(
            PublisherPositionContext.newBuilder().withSite(site).build(),
            existingPosition,
            publisherPosition);

    assertNotNull(position.getPlacementDooh());
    assertEquals(BigDecimal.ONE, position.getPlacementDooh().getDefaultImpressionMultiplier());
    assertSame(existingPlacementDooh, position.getPlacementDooh());
  }

  @Test
  void shouldSetDefaultPlacementDoohWhenSiteTypeDoohAndDoohNull() {
    Position position = new Position();
    position.setSite(new Site());
    position.getSite().setType(Type.DOOH);
    PublisherPositionDTO publisherPositionDTO =
        assembler.make(PublisherPositionContext.newBuilder().build(), position);
    assertNotNull(publisherPositionDTO.getDooh());
    assertEquals(1, publisherPositionDTO.getDooh().getDefaultImpressionMultiplier().intValue());
    assertEquals(0, publisherPositionDTO.getDooh().getVersion().intValue());
  }

  @Test
  void shouldNotSetDefaultDoohWhenSiteNotDooh() {
    Position position = new Position();
    position.setSite(new Site());
    PublisherPositionDTO publisherPositionDTO =
        assembler.make(PublisherPositionContext.newBuilder().build(), position);
    assertNull(publisherPositionDTO.getDooh());
  }

  @Test
  void testCreatePositionWhenExternalAdVerificationSamplingRateIsVisibleIsDefaultValue() {
    // given
    Position position = createPosition();
    // when
    PublisherPositionDTO publisherPosition =
        assembler.make(PublisherPositionContext.newBuilder().build(), position);
    // then
    assertNull(publisherPosition.getExternalAdVerificationSamplingRate());
  }

  @Test
  void testCreatePositionWhenExternalAdVerificationSamplingRateIsVisibleIsValidValue() {
    // given
    Position position = createPosition();
    position.setExternalAdVerificationSamplingRate(40.3f);
    // when
    PublisherPositionDTO publisherPosition =
        assembler.make(PublisherPositionContext.newBuilder().build(), position);
    // then
    assertEquals(40.3f, publisherPosition.getExternalAdVerificationSamplingRate());
  }

  @Test
  void testUserCanUpdateExternalAdVerificationSamplingRateWithValidValue() {
    // Given
    Site site = new Site();

    PublisherPositionDTO publisherPosition =
        PublisherPositionDTO.builder()
            .withPositionAliasName("aliasName")
            .withAdSizeType(AdSizeType.CUSTOM)
            .build();
    // When
    Position position =
        assembler.apply(
            PublisherPositionContext.newBuilder().withSite(site).build(),
            new Position(),
            publisherPosition);
    // Then
    assertNull(position.getExternalAdVerificationSamplingRate());

    PublisherPositionDTO publisherPosition2 =
        PublisherPositionDTO.builder()
            .withPositionAliasName("aliasName")
            .withAdSizeType(AdSizeType.CUSTOM)
            .withExternalAdVerificationSamplingRate(60f)
            .build();
    // When
    Position position2 =
        assembler.apply(
            PublisherPositionContext.newBuilder().withSite(site).build(),
            new Position(),
            publisherPosition2);
    // Then
    assertEquals(60f, position2.getExternalAdVerificationSamplingRate());
  }

  @Test
  void testCreatePositionWhenExternalAdVerificationSamplingRateIsVisibleIsInvalidValue() {
    // given
    Position position = createPosition();
    position.setExternalAdVerificationSamplingRate(90f);
    // when
    PublisherPositionDTO dto =
        assembler.make(PublisherPositionContext.newBuilder().build(), position);
    // then
    assertEquals(90f, dto.getExternalAdVerificationSamplingRate());
  }

  @Test
  void testUserCanUpdateExternalAdVerificationSamplingRateWithInvalidValue() {
    // Given
    Site site = new Site();
    PublisherPositionContext positionContext =
        PublisherPositionContext.newBuilder().withSite(site).build();
    Position pos = new Position();

    PublisherPositionDTO publisherPosition =
        PublisherPositionDTO.builder()
            .withPositionAliasName("aliasName")
            .withAdSizeType(AdSizeType.CUSTOM)
            .withExternalAdVerificationSamplingRate(-20.5f)
            .build();
    // When
    GenevaValidationException exception =
        assertThrows(
            GenevaValidationException.class,
            () -> assembler.apply(positionContext, pos, publisherPosition));
    // then
    assertEquals(ServerErrorCodes.SERVER_INVALID_GEO_EDGE_SAMPLING_RATE, exception.getErrorCode());

    // given
    PublisherPositionDTO publisherPosition2 =
        PublisherPositionDTO.builder()
            .withPositionAliasName("aliasName")
            .withAdSizeType(AdSizeType.CUSTOM)
            .withExternalAdVerificationSamplingRate(120f)
            .build();

    // When
    exception =
        assertThrows(
            GenevaValidationException.class,
            () -> assembler.apply(positionContext, pos, publisherPosition2));
    // then
    assertEquals(ServerErrorCodes.SERVER_INVALID_GEO_EDGE_SAMPLING_RATE, exception.getErrorCode());
  }

  @Test
  void shouldThrowWhenInputCreativeSuccessRateThresholdWithInvalidLowerLimitValue() {
    // Given
    Site site = new Site();
    PublisherPositionContext positionContext =
        PublisherPositionContext.newBuilder().withSite(site).build();
    Position pos = new Position();

    PublisherPositionDTO publisherPosition =
        PublisherPositionDTO.builder()
            .withPositionAliasName("aliasName")
            .withAdSizeType(AdSizeType.CUSTOM)
            .withCreativeSuccessRateThreshold(new BigDecimal(-1))
            .build();
    // When
    GenevaValidationException exception =
        assertThrows(
            GenevaValidationException.class,
            () -> assembler.apply(positionContext, pos, publisherPosition));
    // then
    assertEquals(
        ServerErrorCodes.SERVER_CREATIVE_SUCCESS_RATE_PERCENTAGE_INVALID, exception.getErrorCode());
  }

  @Test
  void shouldThrowWhenUserInputCreativeSuccessRateThresholdWithInvalidUpperLimitValue() {
    // Given
    Site site = new Site();
    PublisherPositionContext positionContext =
        PublisherPositionContext.newBuilder().withSite(site).build();
    Position pos = new Position();
    PublisherPositionDTO publisherPosition =
        PublisherPositionDTO.builder()
            .withPositionAliasName("aliasName")
            .withAdSizeType(AdSizeType.CUSTOM)
            .withCreativeSuccessRateThreshold(new BigDecimal(101))
            .build();

    // When
    GenevaValidationException exception =
        assertThrows(
            GenevaValidationException.class,
            () -> assembler.apply(positionContext, pos, publisherPosition));
    // Then
    assertEquals(
        ServerErrorCodes.SERVER_CREATIVE_SUCCESS_RATE_PERCENTAGE_INVALID, exception.getErrorCode());
  }

  @Test
  void shouldUserInputCreativeSuccessRateThresholdWithValidValue() {
    // Given
    Position position = createPosition();
    position.setCreativeSuccessRateThreshold(new BigDecimal(90));
    // when
    PublisherPositionDTO dto =
        assembler.make(PublisherPositionContext.newBuilder().build(), position);
    // then
    assertEquals(90f, dto.getCreativeSuccessRateThreshold().floatValue());
  }

  @Test
  void shouldReturnNullWhenUserCanUpdateCreativeSuccessRateThresholdWithNullValue() {
    // Given
    Site site = new Site();

    PublisherPositionDTO publisherPosition =
        PublisherPositionDTO.builder()
            .withPositionAliasName("aliasName")
            .withAdSizeType(AdSizeType.CUSTOM)
            .build();
    // When
    Position position =
        assembler.apply(
            PublisherPositionContext.newBuilder().withSite(site).build(),
            new Position(),
            publisherPosition);
    // Then
    assertNull(position.getCreativeSuccessRateThreshold());
  }

  @Test
  void shouldReturnValueWhenUserCanUpdateCreativeSuccessRateThresholdWithValidValue() {
    // Given
    Site site = new Site();

    PublisherPositionDTO publisherPosition =
        PublisherPositionDTO.builder()
            .withPositionAliasName("aliasName")
            .withAdSizeType(AdSizeType.CUSTOM)
            .withCreativeSuccessRateThreshold(new BigDecimal(60))
            .build();
    // When
    Position position =
        assembler.apply(
            PublisherPositionContext.newBuilder().withSite(site).build(),
            new Position(),
            publisherPosition);
    // Then
    assertEquals(60f, position.getCreativeSuccessRateThreshold().floatValue());
  }

  @Test
  void shouldMapImpressionTypeHandlingWhenRequested() {
    // given
    Position position =
        createPositionWithImpressionTypeHandling(ImpressionTypeHandling.BASED_ON_INBOUND_REQUEST);
    // when
    PublisherPositionDTO publisherPosition =
        assembler.make(null, position, Collections.singleton("impressionTypeHandling"));
    // then
    assertEquals(
        ImpressionTypeHandling.BASED_ON_INBOUND_REQUEST,
        publisherPosition.getImpressionTypeHandling());
  }

  @Test
  void shouldNotThrowExceptionWhenMultiImpressionBidIsDisabledAndValidHbPartnerAssignment() {
    position = new Position();
    Site site = new Site();
    site.setPid(123L);
    HbPartner partner = createHbPartnerAndSiteAssociation(site, GOOGLE_EB);
    publisherPositionContext = PublisherPositionContext.newBuilder().withSite(site).build();

    PublisherPositionDTO publisherPosition = PublisherPositionDTO.builder().build();
    PlacementVideoDTO placementVideoDTO = new PlacementVideoDTO();
    placementVideoDTO.setMultiImpressionBid(false);
    publisherPosition.setPlacementVideo(placementVideoDTO);

    // given
    HbPartnerAssignmentDTO assignmentDTO = new HbPartnerAssignmentDTO();
    assignmentDTO.setExternalId("pid");
    assignmentDTO.setHbPartnerPid(GOOGLE_EB);
    assignmentDTO.setType(AssociationType.DEFAULT);
    publisherPosition.setHbPartnerAttributes(Collections.singleton(assignmentDTO));
    when(hbPartnerRepository.findById(GOOGLE_EB)).thenReturn(Optional.of(partner));

    // when

    assembler.apply(publisherPositionContext, position, publisherPosition);

    // then
    assertNotNull(position.getHbPartnerPosition());
    assertFalse(position.getHbPartnerPosition().isEmpty());
  }

  @Test
  void shouldThrowExceptionWhenMultiImpressionBidIsEnabledWithMultipleHbPartnerAssignments() {
    position = new Position();
    Site site = new Site();
    site.setPid(123L);
    HbPartner partner = createHbPartnerAndSiteAssociation(site, GOOGLE_EB);
    publisherPositionContext = PublisherPositionContext.newBuilder().withSite(site).build();

    PublisherPositionDTO publisherPosition = PublisherPositionDTO.builder().build();
    PlacementVideoDTO placementVideoDTO = new PlacementVideoDTO();
    placementVideoDTO.setMultiImpressionBid(true);
    publisherPosition.setPlacementVideo(placementVideoDTO);

    // given
    HbPartnerAssignmentDTO firstAssignmentDTO = new HbPartnerAssignmentDTO();
    firstAssignmentDTO.setExternalId("pid");
    firstAssignmentDTO.setHbPartnerPid(GOOGLE_EB);
    firstAssignmentDTO.setType(AssociationType.DEFAULT);
    HbPartnerAssignmentDTO secondAssignmentDTO = new HbPartnerAssignmentDTO();
    secondAssignmentDTO.setExternalId("secondpid");
    secondAssignmentDTO.setHbPartnerPid(AMAZON_TAM);
    secondAssignmentDTO.setType(AssociationType.DEFAULT);
    publisherPosition.setHbPartnerAttributes(Set.of(firstAssignmentDTO, secondAssignmentDTO));

    // when
    GenevaValidationException exception =
        assertThrows(
            GenevaValidationException.class,
            () -> assembler.apply(publisherPositionContext, position, publisherPosition));

    // then
    assertEquals(
        ServerErrorCodes.SERVER_INVALID_HB_PARTNER_ASSIGNMENT_FOR_MULTI_BIDDING_INVALID_SIZE,
        exception.getErrorCode());
  }

  @Test
  void shouldThrowExceptionWhenMultiImpressionBidIsEnabledButHbPartnerDidNotEnableMultiBidding() {
    position = new Position();
    Site site = new Site();
    site.setPid(123L);
    HbPartner partner = createHbPartnerAndSiteAssociation(site, GOOGLE_EB);
    publisherPositionContext = PublisherPositionContext.newBuilder().withSite(site).build();

    PublisherPositionDTO publisherPosition = PublisherPositionDTO.builder().build();
    PlacementVideoDTO placementVideoDTO = new PlacementVideoDTO();
    placementVideoDTO.setMultiImpressionBid(true);
    publisherPosition.setPlacementVideo(placementVideoDTO);

    // given
    HbPartnerAssignmentDTO assignmentDTO = new HbPartnerAssignmentDTO();
    assignmentDTO.setExternalId("pid");
    assignmentDTO.setHbPartnerPid(GOOGLE_EB);
    assignmentDTO.setType(AssociationType.DEFAULT);
    publisherPosition.setHbPartnerAttributes(Collections.singleton(assignmentDTO));
    when(hbPartnerRepository.isHbPartnerEnabledForMultiBidding(GOOGLE_EB)).thenReturn(false);

    // when
    GenevaValidationException exception =
        assertThrows(
            GenevaValidationException.class,
            () -> assembler.apply(publisherPositionContext, position, publisherPosition));

    // then
    assertEquals(
        ServerErrorCodes.SERVER_INVALID_HB_PARTNER_ASSIGNMENT_FOR_MULTI_BIDDING_NOT_SUPPORTED,
        exception.getErrorCode());
  }

  @Test
  void shouldReturnPositionNameIfPositionAliasNameIsEmpty() {
    position = new Position();
    Site site = new Site();
    site.setPid(123L);
    publisherPositionContext = PublisherPositionContext.newBuilder().withSite(site).build();

    Position position = new Position();
    position.setName("name");
    position.setPositionAliasName("");

    PublisherPositionDTO publisherPositionDTO =
        assembler.make(publisherPositionContext, position, Set.of("positionAliasName"));
    assertEquals("name", publisherPositionDTO.getPositionAliasName());
  }

  private Position aBannerWithAdSizeType(AdSizeType adSizeType) {
    Position position = new Position();
    position.setPid(999L);
    position.setPlacementCategory(BANNER);
    position.setAdSizeType(adSizeType);
    return position;
  }

  private Position createPosition() {
    Position position = new Position();
    Site site = new Site();
    site.setPid(111L);
    position.setSite(site);
    position.setLongform(false);
    return position;
  }

  private HbPartner createHbPartnerAndSiteAssociation(Site site, Long hbPartnerPid) {
    HbPartnerSite partnerSite = new HbPartnerSite();
    partnerSite.setPid(123L);
    partnerSite.setExternalSiteId("pid");
    HbPartner partner = new HbPartner();
    partner.setPid(hbPartnerPid);
    partnerSite.setHbPartner(partner);
    site.setHbPartnerSite(Collections.singleton(partnerSite));
    return partner;
  }

  private Position createPositionWithSiteTypeAndPlatform(Type type, Platform platform) {
    Position position = new Position();
    position.setPid(999L);
    Site site = new Site();
    site.setPid(1111L);
    site.setType(type);
    site.setPlatform(platform);
    position.setSite(site);
    return position;
  }

  private Position createPositionWithImpressionTypeHandling(
      ImpressionTypeHandling impressionTypeHandling) {
    Position position = new Position();
    position.setPid(999L);
    Site site = new Site();
    site.setPid(1111L);
    position.setSite(site);
    position.setImpressionTypeHandling(impressionTypeHandling);
    return position;
  }
}
