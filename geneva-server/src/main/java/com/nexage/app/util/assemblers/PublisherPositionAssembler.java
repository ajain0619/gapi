package com.nexage.app.util.assemblers;

import static com.nexage.admin.core.enums.site.Type.DOOH;
import static com.nexage.app.util.HtmlSanitizerUtil.sanitizeHtmlElement;
import static java.util.stream.Stream.concat;

import com.nexage.admin.core.enums.AdSizeType;
import com.nexage.admin.core.enums.AssociationType;
import com.nexage.admin.core.enums.PlacementCategory;
import com.nexage.admin.core.enums.TierType;
import com.nexage.admin.core.enums.TrafficType;
import com.nexage.admin.core.model.Company;
import com.nexage.admin.core.model.HbPartnersAssociationView;
import com.nexage.admin.core.model.PlacementDooh;
import com.nexage.admin.core.model.Position;
import com.nexage.admin.core.model.RTBProfile;
import com.nexage.admin.core.model.SellerAttributes;
import com.nexage.admin.core.model.Site;
import com.nexage.admin.core.model.Tag;
import com.nexage.admin.core.model.Tier;
import com.nexage.admin.core.repository.HbPartnerRepository;
import com.nexage.admin.core.repository.PositionRepository;
import com.nexage.app.dto.HbPartnerAssignmentDTO;
import com.nexage.app.dto.Status;
import com.nexage.app.dto.publisher.PublisherDefaultRTBProfileDTO;
import com.nexage.app.dto.publisher.PublisherPositionDTO;
import com.nexage.app.dto.publisher.PublisherSiteDTO;
import com.nexage.app.dto.publisher.PublisherTagDTO;
import com.nexage.app.dto.seller.PlacementDoohDTO;
import com.nexage.app.dto.seller.PlacementVideoDTO;
import com.nexage.app.error.ServerErrorCodes;
import com.nexage.app.mapper.HbPartnerAssignmentDTOMapper;
import com.nexage.app.mapper.HbPartnerPositionMapper;
import com.nexage.app.mapper.PlacementDoohDTOMapper;
import com.nexage.app.mapper.site.PlatformTypeMapper;
import com.nexage.app.mapper.site.SiteTypeMapper;
import com.nexage.app.security.UserContext;
import com.nexage.app.services.SellerSiteService;
import com.nexage.app.util.Utils;
import com.nexage.app.util.assemblers.context.PublisherDefaultRTBProfileContext;
import com.nexage.app.util.assemblers.context.PublisherPositionContext;
import com.nexage.app.util.assemblers.context.PublisherTagContext;
import com.nexage.app.util.assemblers.context.PublisherTierContext;
import com.nexage.app.util.validator.placement.PlacementAssociationTypeValidator;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Log4j2
@Component
public class PublisherPositionAssembler
    extends Assembler<PublisherPositionDTO, Position, PublisherPositionContext> {

  private static final Set<String> DEFAULT_FIELDS =
      Set.of(
          "pid",
          "version",
          "site",
          "name",
          "memo",
          "tiers",
          "mraidSupport",
          "videoSupport",
          "screenLocation",
          "interstitial",
          "tags",
          "videoLinearity",
          "height",
          "width",
          "placementCategory",
          "trafficType",
          "status",
          "decisionMaker",
          "defaultRtbProfile",
          "hbPartnerAttributes",
          "adSizeType",
          "positionAliasName",
          "placementDooh",
          "longform",
          "externalAdVerificationSamplingRate",
          "creativeSuccessRateThreshold",
          "impressionTypeHandling");

  private static final Set<String> ALL_FIELDS =
      concat(DEFAULT_FIELDS.stream(), Stream.of("mraidAdvancedTracking", "defaultPositions"))
          .collect(Collectors.toSet());

  private static final Set<AssociationType> DEFAULT_ASSOCIATION_TYPES =
      EnumSet.of(
          AssociationType.DEFAULT, AssociationType.DEFAULT_BANNER, AssociationType.DEFAULT_VIDEO);

  private final PositionTierAssembler positionTierAssembler;
  private final PublisherTierAssembler publisherTierAssembler;
  private final PublisherTagAssembler publisherTagAssembler;
  private final PublisherRTBProfileAssembler publisherRTBProfileAssembler;
  private final SellerSiteService sellerSiteService;
  private final UserContext userContext;
  private final HbPartnerRepository hbPartnerRepository;
  private final PositionRepository positionRepository;

  public PublisherPositionDTO make(
      final PublisherPositionContext context, final Position position, final Set<String> fields) {
    PublisherPositionDTO.PublisherPositionDTOBuilder positionBuilder =
        PublisherPositionDTO.builder();

    Set<String> fieldsToMap = (fields != null) ? fields : ALL_FIELDS;

    for (String field : fieldsToMap) {
      switch (field) {
        case "pid":
          positionBuilder.withPid(position.getPid());
          break;
        case "version":
          positionBuilder.withVersion(position.getVersion());
          break;
        case "site":
          populateSite(position, positionBuilder);
          break;
        case "name":
          positionBuilder.withName(position.getName());
          break;
        case "memo":
          positionBuilder.withMemo(position.getMemo());
          break;
        case "mraidSupport":
          positionBuilder.withMraidSupport(position.getMraidSupport());
          break;
        case "videoSupport":
          positionBuilder.withVideoSupport(position.getVideoSupport());
          break;
        case "screenLocation":
          positionBuilder.withScreenLocation(position.getScreenLocation());
          break;
        case "placementCategory":
          positionBuilder.withPlacementCategory(position.getPlacementCategory());
          break;
        case "trafficType":
          positionBuilder.withTrafficType(position.getTrafficType());
          break;
        case "tiers":
          populateTiers(position, positionBuilder);
          break;
        case "interstitial":
          positionBuilder.withInterstitial(position.getIsInterstitial());
          break;
        case "tags":
          populateTags(context, position, positionBuilder);
          break;
        case "videoLinearity":
          positionBuilder.withVideoLinearity(position.getVideoLinearity());
          break;
        case "height":
          positionBuilder.withHeight(position.getHeight());
          break;
        case "width":
          positionBuilder.withWidth(position.getWidth());
          break;
        case "status":
          positionBuilder.withStatus(position.getStatus());
          break;
        case "decisionMaker":
          populateDecisionMaker(position, positionBuilder);
          break;
        case "positionAliasName":
          positionBuilder.withPositionAliasName(
              StringUtils.isBlank(position.getPositionAliasName())
                  ? position.getName()
                  : position.getPositionAliasName());
          break;
        case "mraidAdvancedTracking":
          positionBuilder.withMraidAdvancedTracking(position.isMraidAdvancedTracking());
          break;
        case "defaultRtbProfile":
          populateDefaultRtbProfile(context, position, positionBuilder);
          break;
        case "hbPartnerAttributes":
          positionBuilder.withHbPartnerAttributes(
              HbPartnerAssignmentDTOMapper.MAPPER.mapHbPartnerPosition(
                  position.getHbPartnerPosition()));
          break;
        case "adSizeType":
          positionBuilder.withAdSizeType(position.getAdSizeType());
          break;
        case "placementDooh":
          positionBuilder.withDooh(resolveNullPlacementDooh(position));
          break;
        case "externalAdVerificationSamplingRate":
          positionBuilder.withExternalAdVerificationSamplingRate(
              position.getExternalAdVerificationSamplingRate());
          break;
        case "creativeSuccessRateThreshold":
          positionBuilder.withCreativeSuccessRateThreshold(
              position.getCreativeSuccessRateThreshold());
          break;
        case "impressionTypeHandling":
          positionBuilder.withImpressionTypeHandling(position.getImpressionTypeHandling());
          break;
        case "longform":
          positionBuilder.withLongform(Boolean.TRUE.equals(position.getLongform()));
          break;
        default:
      }
    }

    return positionBuilder.build();
  }

  private void populateDefaultRtbProfile(
      PublisherPositionContext context,
      Position position,
      PublisherPositionDTO.PublisherPositionDTOBuilder positionBuilder) {
    if (position.getDefaultRtbProfile() != null && userContext.isNexageAdminOrManager()) {
      PublisherDefaultRTBProfileContext rtbContext =
          PublisherDefaultRTBProfileContext.newBuilder().withSite(position.getSite()).build();
      Set<String> defaultRtbProfileFields =
          context.isDetail()
              ? PublisherRTBProfileAssembler.ALL_DEFAULT_RTB_PROFILE_FIELDS
              : PublisherRTBProfileAssembler.LIMITED_DEFAULT_RTB_PROFILE_FIELDS;
      PublisherDefaultRTBProfileDTO publisherRtbProfile =
          makePublisherDefaultRtbProfileDto(
              rtbContext, position.getDefaultRtbProfile(), defaultRtbProfileFields);
      positionBuilder.withDefaultRtbProfile(publisherRtbProfile);
    }
  }

  private void populateDecisionMaker(
      Position position, PublisherPositionDTO.PublisherPositionDTOBuilder positionBuilder) {
    List<Tier> tiers = position.getTiers();
    if (position.getTrafficType() == TrafficType.SMART_YIELD && tiers != null && !tiers.isEmpty()) {
      // assumes DM, one decision maker with one tag
      Tier dmTier =
          tiers.stream()
              .filter(tier -> tier.getTierType() == TierType.SY_DECISION_MAKER)
              .findFirst()
              .orElse(null);
      if (thereIsAnActiveDecisionMakerTag(dmTier)) {
        Site site = sellerSiteService.getSite(position.getSite().getPid());
        PublisherTagContext tagContext = PublisherTagContext.newBuilder().withSite(site).build();
        Tag decisionMakerTag = dmTier.getTags().get(0);
        final Long dmPid = decisionMakerTag.getPid();
        decisionMakerTag =
            site.getTags().stream()
                .filter(tag -> tag.getPid().equals(dmPid))
                .findFirst()
                .orElse(null);
        positionBuilder.withDecisionMaker(publisherTagAssembler.make(tagContext, decisionMakerTag));
      }
    }
  }

  private void populateTags(
      PublisherPositionContext context,
      Position position,
      PublisherPositionDTO.PublisherPositionDTOBuilder positionBuilder) {
    if (position.getPid() != null && position.getSite().getPid() != null) {
      Site site = sellerSiteService.getSite(position.getSite().getPid());
      if (context.isCopyOperation()) {
        PublisherTagContext tagContext = PublisherTagContext.newBuilder().withSite(site).build();
        for (Tag tag : site.getTags()) {
          if (tag.getPosition() != null && position.getPid().equals(tag.getPosition().getPid())) {
            positionBuilder.withTag(publisherTagAssembler.make(tagContext, tag));
          }
        }
      } else {
        buildTags(position, positionBuilder, site);
      }
    }
  }

  private void buildTags(
      Position position,
      PublisherPositionDTO.PublisherPositionDTOBuilder positionBuilder,
      Site site) {
    for (Tag tag : site.getTags()) {
      if (tag.getPosition() != null
          && position.getPid().equals(tag.getPosition().getPid())
          && isStatusActiveOrInactive(tag.getStatus())) {
        positionBuilder.withTag(
            PublisherTagDTO.newBuilder()
                .withPid(tag.getPid())
                .withStatus(Status.valueOf(tag.getStatus().name()))
                .build());
      }
    }
  }

  private boolean isStatusActiveOrInactive(com.nexage.admin.core.enums.Status status) {
    return Status.ACTIVE.name().equals(status.name())
        || Status.INACTIVE.name().equals(status.name());
  }

  private void populateTiers(
      Position position, PublisherPositionDTO.PublisherPositionDTOBuilder positionBuilder) {
    for (Tier tier : position.getTiers()) {
      if (tier != null) {
        Site site = sellerSiteService.getSite(position.getSite().getPid());
        PublisherTierContext tierContext =
            PublisherTierContext.newBuilder()
                .withSite(site)
                .withPosition(getPositionByPid(site, position.getPid()))
                .build();
        positionBuilder.withTier(publisherTierAssembler.make(tierContext, tier));
      }
    }
  }

  private void populateSite(
      Position position, PublisherPositionDTO.PublisherPositionDTOBuilder positionBuilder) {
    if (position.getSite().getPid() != null) {
      Site site = position.getSite();
      positionBuilder.withSite(
          PublisherSiteDTO.newBuilder()
              .withPid(site.getPid())
              .withType(SiteTypeMapper.MAPPER.map(site.getType()))
              .withPlatform(PlatformTypeMapper.MAPPER.map(site.getPlatform()))
              .build());
    }
  }

  public PublisherPositionDTO make(
      final PublisherPositionContext context, final Position position) {
    return make(context, position, getFields());
  }

  public Position apply(
      final PublisherPositionContext context,
      Position position,
      final PublisherPositionDTO publisherPosition) {

    if (context.getSite() != null) {
      position.setSite(context.getSite());
    }

    position.setIsInterstitial(publisherPosition.getInterstitial());
    position.setName(sanitizeHtmlElement(publisherPosition.getName()));
    position.setMemo(sanitizeHtmlElement(publisherPosition.getMemo()));
    position.setMraidSupport(publisherPosition.getMraidSupport());
    position.setPlacementCategory(publisherPosition.getPlacementCategory());
    position.setTrafficType(publisherPosition.getTrafficType());

    position.setVideoSupport(publisherPosition.getVideoSupport());
    position.setScreenLocation(publisherPosition.getScreenLocation());
    position.setVideoLinearity(publisherPosition.getVideoLinearity());
    position.setHeight(publisherPosition.getHeight());
    position.setWidth(publisherPosition.getWidth());
    position.setLongform(publisherPosition.isLongform());
    position.setImpressionTypeHandling(publisherPosition.getImpressionTypeHandling());
    if (isDoohSite(context.getSite()) || publisherPosition.getDooh() != null) {
      PlacementDooh placementDooh =
          PlacementDoohDTOMapper.MAPPER.map(
              Optional.ofNullable(publisherPosition.getDooh()).orElseGet(PlacementDoohDTO::new));
      if (position.getPlacementDooh() == null) {
        position.setPlacementDooh(placementDooh);
      } else {
        var dooh = position.getPlacementDooh();
        dooh.setDefaultImpressionMultiplier(placementDooh.getDefaultImpressionMultiplier());
        dooh.setVersion(placementDooh.getVersion());
      }
    }

    if (isUserTryingToUpdateDynamicAdSizedBannerWithOtherAdSize(position, publisherPosition)) {
      throw new GenevaValidationException(
          ServerErrorCodes.SERVER_PLACEMENT_UPDATE_DYNAMIC_AD_SIZE_TYPE_IS_NOT_ALLOWED);
    }
    position.setAdSizeType(publisherPosition.getAdSizeType());

    position.setPositionAliasName(publisherPosition.getPositionAliasName());

    Float samplingRate = publisherPosition.getExternalAdVerificationSamplingRate();
    if (samplingRate != null && (samplingRate < 0 || samplingRate > 100)) {
      log.error(
          "Geo Edge Sampling Rate for the position id {} is not within the value 0-100",
          position.getPid());
      throw new GenevaValidationException(ServerErrorCodes.SERVER_INVALID_GEO_EDGE_SAMPLING_RATE);
    }
    position.setExternalAdVerificationSamplingRate(samplingRate);

    BigDecimal creativeSuccessRate = publisherPosition.getCreativeSuccessRateThreshold();

    boolean creativeSuccessRateOptOut =
        Optional.ofNullable(position.getSite())
            .map(Site::getCompany)
            .map(Company::getSellerAttributes)
            .map(SellerAttributes::isCreativeSuccessRateThresholdOptOut)
            .orElse(false);

    if (creativeSuccessRate != null) {
      Utils.validateCreativeSuccessRate(creativeSuccessRate, creativeSuccessRateOptOut);
      position.setCreativeSuccessRateThreshold(creativeSuccessRate);
    }

    if (userContext.isNexageUser()) {
      position.setMraidAdvancedTracking(
          publisherPosition.getMraidAdvancedTracking() != null
              ? publisherPosition.getMraidAdvancedTracking()
              : true);
    }

    fillHbPartnerAttributes(
        context,
        position,
        publisherPosition.getHbPartnerAttributes(),
        publisherPosition.getPlacementVideo());
    positionTierAssembler.handleTiers(position, publisherPosition.getTiers());
    return position;
  }

  public void fillHbPartnerAttributes(
      PublisherPositionContext context,
      Position position,
      Set<HbPartnerAssignmentDTO> hbPartnerAssignmentDTOS,
      PlacementVideoDTO placementVideoDTO) {

    if (!PlacementAssociationTypeValidator.isAssociationTypeValid(
        position.getPlacementCategory(),
        position.getVideoSupport(),
        hbPartnerAssignmentDTOS,
        hbPartnerRepository.findPidsWhichSupportFormattedDefaults())) {
      throw new GenevaValidationException(
          ServerErrorCodes.SERVER_INVALID_FORMATTED_DEFAULTS_HB_PARTNER_ASSIGNMENTS);
    }
    if (null != placementVideoDTO && placementVideoDTO.isMultiImpressionBid()) {
      if ((hbPartnerAssignmentDTOS.size() != 1)) {
        throw new GenevaValidationException(
            ServerErrorCodes.SERVER_INVALID_HB_PARTNER_ASSIGNMENT_FOR_MULTI_BIDDING_INVALID_SIZE);
      } else if (!hbPartnerRepository.isHbPartnerEnabledForMultiBidding(
          hbPartnerAssignmentDTOS.iterator().next().getHbPartnerPid())) {
        throw new GenevaValidationException(
            ServerErrorCodes.SERVER_INVALID_HB_PARTNER_ASSIGNMENT_FOR_MULTI_BIDDING_NOT_SUPPORTED);
      }
    }
    if (context.getSite() != null) {
      if (CollectionUtils.isNotEmpty(hbPartnerAssignmentDTOS)) {
        var positionsPerPartners =
            positionRepository.findDefaultPositionsPerPartners(context.getSite().getPid());
        DEFAULT_ASSOCIATION_TYPES.forEach(
            defaultType -> {
              var partnersMap =
                  getDefaultPositionPartnerMappingForAssociationType(
                      positionsPerPartners, defaultType);
              populateExternalIdForHbPartnerAssignmentDTO(
                  position, hbPartnerAssignmentDTOS, partnersMap, defaultType);
            });
        performPositionHbPartnerAssignmentDTOs(
            position, context.getSite(), hbPartnerAssignmentDTOS);
      } else {
        position.setHbPartnerPosition(Collections.emptySet());
      }
    }
  }

  private boolean thereIsAnActiveDecisionMakerTag(Tier dmTier) {
    return dmTier != null
        && dmTier.getTags() != null
        && !dmTier.getTags().isEmpty()
        && dmTier.getTags().get(0).getStatus() == com.nexage.admin.core.enums.Status.ACTIVE;
  }

  private boolean isUserTryingToUpdateDynamicAdSizedBannerWithOtherAdSize(
      Position position, PublisherPositionDTO publisherPosition) {
    return position.getPid() != null
        && PlacementCategory.BANNER.equals(position.getPlacementCategory())
        && AdSizeType.DYNAMIC.equals(position.getAdSizeType())
        && !AdSizeType.DYNAMIC.equals(publisherPosition.getAdSizeType());
  }

  private Position getPositionByPid(Site site, long pid) {

    for (Position position : site.getPositions()) {
      if (position.getPid().equals(pid)) {
        return position;
      }
    }

    throw new GenevaValidationException(ServerErrorCodes.SERVER_POSITION_NOT_FOUND_IN_SITE);
  }

  private Set<String> getFields() {
    if (userContext.isNexageUser()) {
      return ALL_FIELDS;
    }
    return DEFAULT_FIELDS;
  }

  private Map<Long, HbPartnersAssociationView> getDefaultPositionPartnerMappingForAssociationType(
      List<HbPartnersAssociationView> positionsPerPartners, AssociationType defaultType) {

    return positionsPerPartners.stream()
        .filter(p -> AssociationType.getFromValue(p.getType()).equals(defaultType))
        .collect(Collectors.toMap(HbPartnersAssociationView::getHbPartnerPid, Function.identity()));
  }

  private void populateExternalIdForHbPartnerAssignmentDTO(
      Position position,
      Set<HbPartnerAssignmentDTO> hbPartnerAssignmentDTOS,
      Map<Long, HbPartnersAssociationView> partnersMap,
      AssociationType defaultType) {
    hbPartnerAssignmentDTOS.forEach(
        hbPartnerAssignmentDTO -> {
          if (hbPartnerAssignmentDTO.getHbPartnerPid() != null) {
            HbPartnersAssociationView hbPartnersAssociationView =
                partnersMap.get(hbPartnerAssignmentDTO.getHbPartnerPid());
            if (hbPartnersAssociationView != null
                && (hbPartnerAssignmentDTO.getType() == defaultType)
                && (position.getPid() == null
                    || !position.getPid().equals(hbPartnersAssociationView.getPid()))) {
              throw new GenevaValidationException(
                  ServerErrorCodes
                      .SERVER_HB_PARTNER_CANNOT_SET_DEFAULT_POSITION_WHEN_ALREADY_ONE_EXISTS);
            }
            if (StringUtils.isEmpty(hbPartnerAssignmentDTO.getExternalId())) {
              if (DEFAULT_ASSOCIATION_TYPES.contains(hbPartnerAssignmentDTO.getType())) {
                hbPartnerAssignmentDTO.setExternalId(position.getName());
              } else
                throw new GenevaValidationException(
                    ServerErrorCodes.SERVER_HB_PARTNER_FIELDS_MISSING);
            }
          }
        });
  }

  private void performPositionHbPartnerAssignmentDTOs(
      Position position, Site site, Set<HbPartnerAssignmentDTO> hbPartnerAssignmentDTOS) {

    var hbPartnerSites = site.getHbPartnerSite();
    var hbPartnerSitePid = new HashSet<Long>();
    var hbPartnerPositionPid = new HashSet<Long>();
    hbPartnerSites.forEach(h -> hbPartnerSitePid.add(h.getHbPartner().getPid()));
    hbPartnerAssignmentDTOS.forEach(h -> hbPartnerPositionPid.add(h.getHbPartnerPid()));
    if (!hbPartnerSitePid.containsAll(hbPartnerPositionPid)) {
      throw new GenevaValidationException(ServerErrorCodes.SERVER_HB_PARTNER_ASSIGNMENT_INVALID);
    }
    position.setHbPartnerPosition(
        HbPartnerPositionMapper.MAPPER.map(hbPartnerAssignmentDTOS, position, hbPartnerRepository));
  }

  private boolean isDoohSite(Site site) {
    return Optional.ofNullable(site).map(Site::getType).filter(type -> type == DOOH).isPresent();
  }

  private PlacementDoohDTO resolveNullPlacementDooh(Position position) {
    var placementDooh = position.getPlacementDooh();
    if (placementDooh == null && isDoohSite(position.getSite())) {
      placementDooh = new PlacementDooh();
    }
    return PlacementDoohDTOMapper.MAPPER.map(placementDooh);
  }

  private PublisherDefaultRTBProfileDTO makePublisherDefaultRtbProfileDto(
      PublisherDefaultRTBProfileContext context, RTBProfile rtbProfile, Set<String> fields) {
    var builder = PublisherDefaultRTBProfileDTO.newBuilder();
    context.setProfileBuilder(builder);
    publisherRTBProfileAssembler.make(context, rtbProfile, fields);
    publisherTagAssembler.addTagToPublisherDefaultRTBProfileDtoBuilder(builder, context, fields);
    return builder
        .withDefaultRtbProfileOwnerCompanyPid(rtbProfile.getDefaultRtbProfileOwnerCompanyPid())
        .build();
  }
}
