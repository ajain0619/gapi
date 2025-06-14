package com.nexage.app.util.assemblers;

import static java.lang.Boolean.TRUE;
import static java.util.stream.Stream.concat;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import com.google.common.collect.Sets;
import com.nexage.admin.core.enums.AssociationType;
import com.nexage.admin.core.enums.PlacementCategory;
import com.nexage.admin.core.enums.VideoSupport;
import com.nexage.admin.core.enums.site.Platform;
import com.nexage.admin.core.enums.site.Type;
import com.nexage.admin.core.model.Company;
import com.nexage.admin.core.model.Position;
import com.nexage.admin.core.model.RTBProfile;
import com.nexage.admin.core.model.SellerAttributes;
import com.nexage.admin.core.model.Site;
import com.nexage.admin.core.repository.HbPartnerRepository;
import com.nexage.admin.core.sparta.jpa.model.SiteDealTerm;
import com.nexage.app.dto.HbPartnerAssignmentDTO;
import com.nexage.app.dto.Status;
import com.nexage.app.dto.publisher.PublisherDefaultRTBProfileDTO;
import com.nexage.app.dto.publisher.PublisherImpressionGroupDTO;
import com.nexage.app.dto.publisher.PublisherSiteDTO;
import com.nexage.app.dto.publisher.PublisherSiteDTO.Mode;
import com.nexage.app.dto.publisher.PublisherSiteDealTermDTO;
import com.nexage.app.error.ServerErrorCodes;
import com.nexage.app.mapper.HbPartnerAssignmentDTOMapper;
import com.nexage.app.mapper.HbPartnerSiteMapper;
import com.nexage.app.security.UserContext;
import com.nexage.app.services.CompanyService;
import com.nexage.app.services.HbPartnerService;
import com.nexage.app.services.TransparencyService;
import com.nexage.app.util.Utils;
import com.nexage.app.util.assemblers.context.NullableContext;
import com.nexage.app.util.assemblers.context.PublisherDefaultRTBProfileContext;
import com.nexage.app.util.assemblers.context.PublisherPositionContext;
import com.nexage.app.util.assemblers.publisher.ExternalPublisherAssembler;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import com.ssp.geneva.common.security.error.GenevaSecurityException;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.Instant;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Log4j2
@Component
public class PublisherSiteAssembler extends NoContextAssembler {

  public static final Set<String> DEFAULT_FIELDS =
      Set.of(
          "pid",
          "version",
          "dcn",
          "description",
          "domain",
          "name",
          "platform",
          "status",
          "type",
          "url",
          "appBundle",
          "coppaRestricted",
          "rtb1CategoryRollup",
          "iabCategories",
          "publisher",
          "publisher/pid",
          "positions",
          "integration",
          "mode",
          "hbEnabled",
          "siteTransparencySettings",
          "publisherTransparencySettings",
          "externalAdVerificationSamplingRate",
          "creativeSuccessRateThreshold");

  protected static final Set<String> ALL_FIELDS_NEXAGE_USER =
      concat(
              DEFAULT_FIELDS.stream(),
              Stream.of(
                  "trafficThrottle",
                  "adTruthEnabled",
                  "metadataEnablement",
                  "globalAliasName",
                  "impressionGroup",
                  "reportFrequency",
                  "reportBatchSize",
                  "rulesUpdateFrequency",
                  "filterBots",
                  "buyerTimeout",
                  "daysFree",
                  "totalTimeout",
                  "defaultPositions",
                  "passthruParameters",
                  "consumerProfileContributed",
                  "consumerProfileUsed",
                  "overrideIP",
                  "ethnicityMap",
                  "genderMap",
                  "maritalStatusMap",
                  "inputDateFormat",
                  "siteDealTerm",
                  "hbPartnerAttributes"))
          .collect(Collectors.toSet());

  protected static final Set<String> ALL_FIELDS_NEXAGE_ADMIN_MANAGER =
      concat(ALL_FIELDS_NEXAGE_USER.stream(), Stream.of("defaultRtbProfile"))
          .collect(Collectors.toSet());

  private final ExternalPublisherAssembler externalPublisherAssembler;
  private final PublisherPositionAssembler publisherPositionAssembler;
  private final PublisherTagAssembler publisherTagAssembler;
  private final PublisherRTBProfileAssembler publisherRTBProfileAssembler;
  private final TransparencyService transparencyService;
  private final CompanyService companyService;
  private final HbPartnerService hbPartnerService;
  private final UserContext userContext;
  private final HbPartnerRepository hbPartnerRepository;

  public PublisherSiteDTO make(final Site site, final Set<String> fields, final boolean detailDRP) {
    PublisherSiteDTO.Builder siteDtoBuilder = PublisherSiteDTO.newBuilder();

    Set<String> fieldsToMap = (fields != null) ? fields : DEFAULT_FIELDS;

    for (String field : fieldsToMap) {
      switch (field) {
        case "pid":
          siteDtoBuilder.withPid(site.getPid());
          break;
        case "version":
          siteDtoBuilder.withVersion(site.getVersion());
          break;
        case "dcn":
          siteDtoBuilder.withDcn(site.getDcn());
          break;
        case "domain":
          siteDtoBuilder.withDomain(site.getDomain());
          break;
        case "description":
          siteDtoBuilder.withDescription(site.getDescription());
          break;
        case "name":
          siteDtoBuilder.withName(site.getName());
          break;
        case "platform":
          siteDtoBuilder.withPlatform(
              PublisherSiteDTO.Platform.valueOf(site.getPlatform().toString()));
          break;
        case "publisher":
          if (site.getCompany() != null) {
            siteDtoBuilder.withPublisher(
                externalPublisherAssembler.make(
                    NullableContext.nullableContext, site.getCompany(), Sets.newHashSet("pid")));
          }
          break;
        case "status":
          siteDtoBuilder.withStatus(Status.valueOf(site.getStatus().toString()));
          break;
        case "type":
          siteDtoBuilder.withType(PublisherSiteDTO.SiteType.valueOf(site.getType().toString()));
          break;
        case "url":
          siteDtoBuilder.withUrl(site.getUrl());
          break;
        case "appBundle":
          siteDtoBuilder.withAppBundle(site.getAppBundle());
          break;
        case "coppaRestricted":
          siteDtoBuilder.withCoppaRestricted(site.isCoppaRestricted());
          break;
        case "rtb1CategoryRollup":
          siteDtoBuilder.withRtb1CategoryRollup(site.getRtb1CategoryRollup());
          break;
        case "iabCategories":
          siteDtoBuilder.withIabCategories(site.getIabCategories());
          break;
        case "positions":
          if (site.getPositions() != null) {
            for (Position position : site.getPositions()) {
              PublisherPositionContext positionContext =
                  PublisherPositionContext.newBuilder().withSite(site).build();
              siteDtoBuilder.withPosition(
                  publisherPositionAssembler.make(
                      positionContext, position, Sets.newHashSet("pid", "name", "memo")));
            }
          }
          break;
        case "mode":
          siteDtoBuilder.withMode(Mode.getMode(site.isLive()));
          break;
        case "hbEnabled":
          siteDtoBuilder.withHbEnabled(site.getHbEnabled());
          break;
        case "trafficThrottle":
          siteDtoBuilder.withTrafficThrottle(site.getTrafficThrottle());
          break;
        case "adTruthEnabled":
          siteDtoBuilder.withAdTruthEnabled(site.isAdTruthEnabled());
          break;
        case "metadataEnablement":
          siteDtoBuilder.withMetadataEnablement(site.getMetadataEnablement());
          break;
        case "globalAliasName":
          siteDtoBuilder.withGlobalAliasName(site.getGlobalAliasName());
          break;
        case "impressionGroup":
          siteDtoBuilder.withImpressionGroup(
              new PublisherImpressionGroupDTO(site.isGroupsEnabled(), site.getImpressionGroups()));
          break;
        case "reportFrequency":
          siteDtoBuilder.withReportFrequency(site.getReportFrequency());
          break;
        case "reportBatchSize":
          siteDtoBuilder.withReportBatchSize(site.getReportBatchSize());
          break;
        case "rulesUpdateFrequency":
          siteDtoBuilder.withRulesUpdateFrequency(site.getRulesUpdateFrequency());
          break;
        case "filterBots":
          siteDtoBuilder.withFilterBots(site.isFilterBots());
          break;
        case "buyerTimeout":
          siteDtoBuilder.withBuyerTimeout(site.getBuyerTimeout());
          break;
        case "daysFree":
          siteDtoBuilder.withDaysFree(site.getDaysFree());
          break;
        case "totalTimeout":
          siteDtoBuilder.withTotalTimeout(site.getTotalTimeout());
          break;
        case "defaultPositions":
          siteDtoBuilder.withDefaultPositions(site.getDefaultPositions());
          break;
        case "passthruParameters":
          siteDtoBuilder.withPassthruParameters(site.getPassthruParameters());
          break;
        case "consumerProfileContributed":
          siteDtoBuilder.withConsumerProfileContributed(site.isConsumerProfileContributed());
          break;
        case "consumerProfileUsed":
          siteDtoBuilder.withConsumerProfileUsed(site.isConsumerProfileUsed());
          break;
        case "overrideIP":
          siteDtoBuilder.withOverrideIP(site.isOverrideIP());
          break;
        case "ethnicityMap":
          siteDtoBuilder.withEthnicityMap(site.getEthnicityMap());
          break;
        case "genderMap":
          siteDtoBuilder.withGenderMap(site.getGenderMap());
          break;
        case "maritalStatusMap":
          siteDtoBuilder.withMaritalStatusMap(site.getMaritalStatusMap());
          break;
        case "inputDateFormat":
          siteDtoBuilder.withInputDateFormat(site.getInputDateFormat());
          break;
        case "defaultRtbProfile":
          if (site.getDefaultRtbProfile() != null && userContext.isNexageAdminOrManager()) {
            PublisherDefaultRTBProfileContext rtbContext =
                PublisherDefaultRTBProfileContext.newBuilder().withSite(site).build();
            Set<String> defaultRtbProfileFields =
                detailDRP
                    ? PublisherRTBProfileAssembler.ALL_DEFAULT_RTB_PROFILE_FIELDS
                    : PublisherRTBProfileAssembler.LIMITED_DEFAULT_RTB_PROFILE_FIELDS;
            PublisherDefaultRTBProfileDTO publisherDefaultRTBProfile =
                makePublisherDefaultRtbProfileDto(
                    rtbContext, site.getDefaultRtbProfile(), defaultRtbProfileFields);
            siteDtoBuilder.withDefaultRtbProfile(publisherDefaultRTBProfile);
          }
          break;
        case "siteDealTerm":
          SiteDealTerm currentDealTerm = site.getCurrentDealTerm();
          if (currentDealTerm != null) {
            PublisherSiteDealTermDTO publisherSiteDealTerm =
                PublisherSiteDealTermDTO.newBuilder()
                    .withNexageRevenueShare(currentDealTerm.getNexageRevenueShare())
                    .withRtbFee(currentDealTerm.getRtbFee())
                    .build();
            siteDtoBuilder.withCurrentDealTerm(publisherSiteDealTerm);
          }
          break;
        case "hbPartnerAttributes":
          siteDtoBuilder.withHbPartnerAttributes(
              HbPartnerAssignmentDTOMapper.MAPPER.mapHbPartnerSite(site.getHbPartnerSite()));
          break;
        case "creativeSuccessRateThreshold":
          siteDtoBuilder.withCreativeSuccessRateThreshold(site.getCreativeSuccessRateThreshold());
          break;
        default:
      }
    }

    // TODO get the icon ...

    return siteDtoBuilder.build();
  }

  public PublisherSiteDTO make(final Site site, final boolean detailDRP) {
    if (userContext.isNexageUser()) {
      if (userContext.isNexageAdminOrManager()) {
        return make(site, ALL_FIELDS_NEXAGE_ADMIN_MANAGER, detailDRP);
      } else {
        return make(site, ALL_FIELDS_NEXAGE_USER, detailDRP);
      }
    } else {
      return make(site, DEFAULT_FIELDS, detailDRP);
    }
  }

  com.nexage.admin.core.enums.Status applyStatus(com.nexage.app.dto.Status inStatus) {
    com.nexage.admin.core.enums.Status returnStatus;
    switch (inStatus) {
      case ACTIVE:
        returnStatus = com.nexage.admin.core.enums.Status.ACTIVE;
        break;
      case DELETED:
        returnStatus = com.nexage.admin.core.enums.Status.DELETED;
        break;
      default:
        returnStatus = com.nexage.admin.core.enums.Status.INACTIVE;
        break;
    }
    return returnStatus;
  }

  public Site apply(long publisher, Site site, final PublisherSiteDTO publisherSiteDto) {

    validate(publisher, site, publisherSiteDto);

    site.setDescription(publisherSiteDto.getDescription());
    site.setName(publisherSiteDto.getName());
    site.setAppBundle(publisherSiteDto.getAppBundle());
    site.setCoppaRestricted(publisherSiteDto.isCoppaRestricted());
    site.setDomain(publisherSiteDto.getDomain());
    site.setIabCategories(publisherSiteDto.getIabCategories());
    site.setRtb1CategoryRollup(publisherSiteDto.getRtb1CategoryRollup());
    site.setType(Type.valueOf(publisherSiteDto.getType().name()));
    site.setPlatform(Platform.valueOf(publisherSiteDto.getPlatform().name()));
    site.setUrl(publisherSiteDto.getUrl());
    site.setLive(publisherSiteDto.getMode() != null && publisherSiteDto.getMode().getModeCode());
    site.setStatus(applyStatus(publisherSiteDto.getStatus()));
    site.setHbEnabled(publisherSiteDto.isHbEnabled());

    BigDecimal creativeSuccessRate = publisherSiteDto.getCreativeSuccessRateThreshold();
    boolean creativeSuccessRateOptOut =
        Optional.ofNullable(site.getCompany())
            .or(
                () ->
                    Optional.ofNullable(companyService)
                        .map(service -> service.getCompany(publisher)))
            .map(Company::getSellerAttributes)
            .map(SellerAttributes::isCreativeSuccessRateThresholdOptOut)
            .orElse(false);

    if (creativeSuccessRate != null) {
      Utils.validateCreativeSuccessRate(creativeSuccessRate, creativeSuccessRateOptOut);
      site.setCreativeSuccessRateThreshold(creativeSuccessRate);
    }

    if (userContext.isNexageUser()) {
      site.setAdTruthEnabled(publisherSiteDto.isAdTruthEnabled());
      site.setMetadataEnablement(TRUE.equals(publisherSiteDto.getMetadataEnablement()));
      site.setGlobalAliasName(publisherSiteDto.getGlobalAliasName());
      if (publisherSiteDto.getImpressionGroup() != null) {
        site.setImpressionGroup(
            new Site.ImpressionGroup(
                publisherSiteDto.getImpressionGroup().isEnabled(),
                publisherSiteDto.getImpressionGroup().getGroups()));
      }
      site.setReportFrequency(publisherSiteDto.getReportFrequency());
      site.setReportBatchSize(publisherSiteDto.getReportBatchSize());
      site.setRulesUpdateFrequency(publisherSiteDto.getRulesUpdateFrequency());
      site.setFilterBots(publisherSiteDto.isFilterBots());
      site.setBuyerTimeout(publisherSiteDto.getBuyerTimeout());
      site.setDaysFree(publisherSiteDto.getDaysFree());
      site.setTotalTimeout(publisherSiteDto.getTotalTimeout());
      site.setDefaultPositions(publisherSiteDto.getDefaultPositions());
      site.setPassthruParameters(publisherSiteDto.getPassthruParameters());
      site.setTrafficThrottle(publisherSiteDto.getTrafficThrottle());

      site.setConsumerProfileContributed(publisherSiteDto.isConsumerProfileContributed());
      site.setConsumerProfileUsed(publisherSiteDto.isConsumerProfileUsed());
      site.setOverrideIP(publisherSiteDto.isOverrideIP());
      site.setEthnicityMap(publisherSiteDto.getEthnicityMap());
      site.setGenderMap(publisherSiteDto.getGenderMap());
      site.setMaritalStatusMap(publisherSiteDto.getMaritalStatusMap());
      site.setInputDateFormat(publisherSiteDto.getInputDateFormat());
    }

    fillDealTerms(publisher, site, publisherSiteDto.getCurrentDealTerm());
    fillHbPartnerAttributes(publisher, site, publisherSiteDto);
    return site;
  }

  private void validate(long publisher, Site site, final PublisherSiteDTO publisherSiteDto) {
    if (isNotBlank(site.getDcn())
        && isNotBlank(publisherSiteDto.getDcn())
        && !site.getDcn().equals(publisherSiteDto.getDcn())) {
      throw new GenevaSecurityException(ServerErrorCodes.SERVER_SITE_DCN_READONLY);
    }

    if (PublisherSiteDTO.Platform.CTV_OTT == publisherSiteDto.getPlatform()) {
      var company = companyService.getCompany(publisher);
      var sellerAttributes = company.getSellerAttributes();
      if (!sellerAttributes.isEnableCtvSelling()) {
        throw new GenevaValidationException(
            ServerErrorCodes.SERVER_SITE_PLATFORM_CTV_OTT_NOT_ALLOWED);
      }
    }

    if (site.getPid() != null && userContext.isNexageUser()) {
      validatePositionsForNewIntegration(site);
    }

    validateDealTerms(site, publisherSiteDto.getCurrentDealTerm());
  }

  private void fillDealTerms(
      long publisher, Site site, final PublisherSiteDealTermDTO publisherSiteDealTermDto) {
    if (userContext.isNexageUser()
        && publisherSiteDealTermDto != null
        && publisherSiteDealTermDto.isNotEmpty()) {
      SiteDealTerm currentSiteDealTerm = site.getCurrentDealTerm();
      site.setCurrentDealTerm(currentSiteDealTerm);
      if (currentSiteDealTerm == null) {
        currentSiteDealTerm = new SiteDealTerm();
        currentSiteDealTerm.setDealTermChanged(false);
        currentSiteDealTerm.setRevenueMode(SiteDealTerm.RevenueMode.REV_SHARE);
        site.setCurrentDealTerm(currentSiteDealTerm);
      }
      currentSiteDealTerm.setEffectiveDate(Instant.now().toDate());
      if (!currentSiteDealTerm.equalsPss(
          publisherSiteDealTermDto.getNexageRevenueShare(), publisherSiteDealTermDto.getRtbFee())) {
        currentSiteDealTerm.setPid(null);
        currentSiteDealTerm.setSite(site);
        currentSiteDealTerm.setNexageRevenueShare(publisherSiteDealTermDto.getNexageRevenueShare());
        currentSiteDealTerm.setRtbFee(publisherSiteDealTermDto.getRtbFee());
        if (site.getPid() != null) {
          site.addToDealTerms(currentSiteDealTerm);
        }
      }
    } else if (site.getPid() == null) {
      // create a new default deal term
      Company company = companyService.getCompany(publisher);
      SellerAttributes sellerAttributes = company.getSellerAttributes();

      SiteDealTerm term = new SiteDealTerm();
      term.setDealTermChanged(false);
      term.setEffectiveDate(Instant.now().toDate());
      term.setRevenueMode(SiteDealTerm.RevenueMode.REV_SHARE);
      term.setNexageRevenueShare(getDefaultRevenueShare(sellerAttributes));
      term.setRtbFee(getDefaultRTBFee(sellerAttributes));
      site.setCurrentDealTerm(term);
    }
  }

  private void fillHbPartnerAttributes(
      Long publisher, Site site, PublisherSiteDTO publisherSiteDto) {

    Set<HbPartnerAssignmentDTO> hbPartnerAssignmentDTOS = publisherSiteDto.getHbPartnerAttributes();
    if (hbPartnerAssignmentDTOS != null) {
      hbPartnerAssignmentDTOS.forEach(
          hdto -> {
            if (hdto.getHbPartnerPid() != null && StringUtils.isEmpty(hdto.getExternalId())) {
              if (hdto.getType() == AssociationType.DEFAULT) {
                hdto.setExternalId(site.getName());
              } else {
                throw new GenevaValidationException(
                    ServerErrorCodes.SERVER_HB_PARTNER_FIELDS_MISSING);
              }
            }
          });
    } else {
      hbPartnerAssignmentDTOS = Collections.emptySet();
    }
    if (CollectionUtils.isEmpty(hbPartnerAssignmentDTOS)) {
      site.setHbPartnerSite(Collections.emptySet());
    } else {
      List<Long> hbPartnerAssociationsPids = hbPartnerService.findPidsByCompanyPid(publisher);
      HashSet<Long> hbPartnerSitePid = Sets.newHashSet();
      hbPartnerAssignmentDTOS.forEach(h -> hbPartnerSitePid.add(h.getHbPartnerPid()));
      if (!hbPartnerAssociationsPids.containsAll(hbPartnerSitePid)) {
        throw new GenevaValidationException(ServerErrorCodes.SERVER_HB_PARTNER_ASSIGNMENT_INVALID);
      }
      site.setHbPartnerSite(
          HbPartnerSiteMapper.MAPPER.map(hbPartnerAssignmentDTOS, site, hbPartnerRepository));
    }
  }

  private void validatePositionsForNewIntegration(Site site) {
    for (Position position : site.getPositions()) {
      checkPositionVideoSupport(site, position);
    }
  }

  private void checkPositionVideoSupport(Site site, Position position) {
    boolean isValid = true;
    Type siteType = site.getType();
    VideoSupport videoSupport = position.getVideoSupport();
    PlacementCategory placementCategory = position.getPlacementCategory();
    boolean positionContainsVideoSupport =
        VideoSupport.WITH_VIDEO.contains(videoSupport)
            || placementCategory == PlacementCategory.INSTREAM_VIDEO;

    if (positionContainsVideoSupport && siteType == Type.DOOH) {
      isValid = false;
    }

    if (!isValid) {
      throw new GenevaValidationException(
          ServerErrorCodes.SERVER_SITE_INTEGRATION_IS_NOT_CHANGEABLE_CONTAINS_VIDEO_PLACEMENT);
    }
  }

  private BigDecimal getDefaultRevenueShare(SellerAttributes sellerAttributes) {
    BigDecimal share = BigDecimal.ZERO;
    if (null != sellerAttributes && null != sellerAttributes.getRevenueShare()) {
      share = sellerAttributes.getRevenueShare();
    }
    return share;
  }

  private BigDecimal getDefaultRTBFee(SellerAttributes sellerAttributes) {
    BigDecimal fee = BigDecimal.ZERO;
    if (null != sellerAttributes && null != sellerAttributes.getRtbFee()) {
      fee = sellerAttributes.getRtbFee();
    }
    return fee;
  }

  private void validateDealTerms(Site siteDTO, PublisherSiteDealTermDTO publisherSiteDealTerm) {
    if (siteDTO.getPid() != null
        && userContext.isNexageUser()
        && (publisherSiteDealTerm == null || publisherSiteDealTerm.isEmpty())) {
      throw new GenevaValidationException(ServerErrorCodes.SERVER_SITE_CURRENT_DEAL_TERM_MISSING);
    }
    if (!userContext.isNexageUser()
        && publisherSiteDealTerm != null
        && publisherSiteDealTerm.isNotEmpty()) {
      throw new GenevaValidationException(
          ServerErrorCodes.SERVER_SITE_CURRENT_DEAL_TERM_MODIFYING_NOT_ALLOWED);
    }
    if (userContext.isNexageUser() && publisherSiteDealTerm != null) {
      BigDecimal nexageRevenueShare = publisherSiteDealTerm.getNexageRevenueShare();
      if (nexageRevenueShare == null) {
        throw new GenevaValidationException(
            ServerErrorCodes.SERVER_SITE_CURRENT_DEAL_TERM_REVENUE_SHARE_MISSING);
      } else if (nexageRevenueShare.doubleValue() < 0 || nexageRevenueShare.doubleValue() >= 1) {
        throw new GenevaValidationException(
            ServerErrorCodes.SERVER_SITE_CURRENT_DEAL_TERM_REVENUE_SHARE_WRONG);
      }
      if (nexageRevenueShare.doubleValue() >= 1) {
        throw new GenevaValidationException(
            ServerErrorCodes.SERVER_SITE_CURRENT_DEAL_TERM_SUM_WRONG);
      }
    }
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
