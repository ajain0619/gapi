package com.nexage.app.util.assemblers.publisher;

import com.nexage.admin.core.enums.CrsReviewStatusBlock;
import com.nexage.admin.core.enums.CrsSecureStatusBlock;
import com.nexage.admin.core.enums.SellerDomainVerificationAuthLevel;
import com.nexage.admin.core.model.Company;
import com.nexage.admin.core.model.SellerAttributes;
import com.nexage.admin.core.repository.HbPartnerRepository;
import com.nexage.app.dto.HbPartnerAssignmentDTO;
import com.nexage.app.dto.publisher.PublisherDTO;
import com.nexage.app.dto.publisher.PublisherEligibleBiddersDTO;
import com.nexage.app.dto.transparency.TransparencyMgmtEnablement;
import com.nexage.app.dto.transparency.TransparencySettingsDTO;
import com.nexage.app.error.ServerErrorCodes;
import com.nexage.app.mapper.HbPartnerAssignmentDTOMapper;
import com.nexage.app.mapper.HbPartnerCompanyMapper;
import com.nexage.app.security.UserContext;
import com.nexage.app.util.Utils;
import com.nexage.app.util.assemblers.PublisherAttributesAssembler;
import com.nexage.app.util.assemblers.PublisherEligibleBiddersAssembler;
import com.nexage.app.util.assemblers.context.CompanyContext;
import com.nexage.app.util.assemblers.context.NullableContext;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.collections.CollectionUtils;

@RequiredArgsConstructor
@Log4j2
public abstract class BasePublisherAssembler implements PublisherAssembler {

  private static final Set<String> FIELDS =
      Set.of(
          "pid",
          "version",
          "id",
          "name",
          "sellerAttributes",
          "eligibleBidders",
          "currency",
          "hbPartnerAttributes");

  private final PublisherEligibleBiddersAssembler eligibleBiddersAssembler;
  private final HbPartnerRepository hbPartnerRepository;
  protected final UserContext userContext;

  protected abstract PublisherAttributesAssembler getPublisherAttributesAssembler();

  protected Set<String> getFields() {
    return FIELDS;
  }

  @Override
  public PublisherDTO make(
      final NullableContext context, final Company model, final Set<String> fields) {

    PublisherDTO.Builder publisherBuilder = PublisherDTO.newBuilder();
    Set<String> fieldsToMap = (fields != null) ? fields : getFields();

    if (model != null) {
      for (String field : fieldsToMap) {
        switch (field) {
          case "pid":
            publisherBuilder.withPid(model.getPid());
            break;
          case "version":
            publisherBuilder.withVersion(model.getVersion());
            break;
          case "id":
            publisherBuilder.withId(model.getId());
            break;
          case "name":
            publisherBuilder.withName(model.getName());
            break;
          case "sellerAttributes":
            if (model.getSellerAttributes() != null) {
              publisherBuilder.withAttributes(
                  getPublisherAttributesAssembler()
                      .make(NullableContext.nullableContext, model.getSellerAttributes()));
            }

            break;
          case "eligibleBidders":
            Set<PublisherEligibleBiddersDTO> publisherEligibleBidders =
                eligibleBiddersAssembler.make(
                    CompanyContext.newBuilder().build(), model.getEligibleBidders());
            publisherBuilder.withEligibleBidderGroups(publisherEligibleBidders);
            break;
          case "contactUserPid":
            publisherBuilder.withContactUserPid(model.getContactUserPid());
            break;
          case "adServingEnabled":
            publisherBuilder.withAdServingEnabled(model.isAdServingEnabled());
            break;
          case "restrictDrillDown":
            publisherBuilder.withRestrictDrillDown(model.isRestrictDrillDown());
            break;
          case "directAdServingFee":
            publisherBuilder.withDirectAdServingFee(model.getDirectAdServingFee());
            break;
          case "houseAdServingFee":
            publisherBuilder.withHouseAdServingFee(model.getHouseAdServingFee());
            break;
          case "nonRemnantHouseAdCap":
            publisherBuilder.withNonRemnantHouseAdCap(model.getNonRemnantHouseAdCap());
            break;
          case "houseAdOverageFee":
            publisherBuilder.withHouseAdOverageFee(model.getHouseAdOverageFee());
            break;
          case "cpiTrackingEnabled":
            publisherBuilder.withCpiTrackingEnabled(model.isCpiTrackingEnabled());
            break;
          case "cpiConversionNoticeUrl":
            publisherBuilder.withCpiConversionNoticeUrl(model.getCpiConversionNoticeUrl());
            break;
          case "rtbEnabled":
            publisherBuilder.withRtbEnabled(model.isRtbEnabled());
            break;
          case "mediationEnabled":
            publisherBuilder.withMediationEnabled(model.isMediationEnabled());
            break;
          case "rtbRevenueReportEnabled":
            publisherBuilder.withRtbRevenueReportEnabled(model.isRtbRevenueReportEnabled());
            break;
          case "salesforceId":
            publisherBuilder.withSalesforceId(model.getSalesforceId());
            break;
          case "bidderAdServingFee":
            publisherBuilder.withBidderAdServingFee(model.getBidderAdServingFee());
            break;
          case "status":
            publisherBuilder.withStatus(model.getStatus());
            break;
          case "globalAliasName":
            publisherBuilder.withGlobalAliasName(model.getGlobalAliasName());
            break;
          case "website":
            publisherBuilder.withWebsite(model.getWebsite());
            break;
          case "description":
            publisherBuilder.withDescription(model.getDescription());
            break;
          case "type":
            publisherBuilder.withType(model.getType());
            break;
          case "test":
            publisherBuilder.withTest(model.getTest());
            break;
          case "selfServeAllowed":
            publisherBuilder.withSelfServeAllowed(model.isSelfServeAllowed());
            break;
          case "regionId":
            publisherBuilder.withRegionId(model.getRegionId());
            break;
          case "payoutEnabled":
            publisherBuilder.withPayoutEnabled(model.getPayoutEnabled());
            break;
          case "numberOfRtbTags":
            publisherBuilder.withNumberOfRtbTags(model.getNumberOfRtbTags());
            break;
          case "numberOfMediationSites":
            publisherBuilder.withNumberOfMediationSites(model.getNumberOfMediationSites());
            break;
          case "adsourceNames":
            publisherBuilder.withAdsourceNames(model.getAdsourceNames());
            break;
          case "externalDataProviderNames":
            publisherBuilder.withExternalDataProviderNames(model.getExternalDataProviderNames());
            break;
          case "activeIOs":
            publisherBuilder.withActiveIOs(model.getActiveIOs());
            break;
          case "credit":
            publisherBuilder.withCredit(model.getCredit());
            break;
          case "estimatedTimeRemain":
            publisherBuilder.withEstimatedTimeRemain(model.getEstimatedTimeRemaining());
            break;
          case "numberOfUsers":
            publisherBuilder.withNumberOfUsers(model.getNumberOfUsers());
            break;
          case "hasHeaderBiddingSites":
            publisherBuilder.withHasHeaderBiddingSites(model.getHasHeaderBiddingSites());
            break;
          case "defaultRtbProfilesEnabled":
            publisherBuilder.withDefaultRtbProfilesEnabled(model.isDefaultRtbProfilesEnabled());
            break;
          case "thirdPartyFraudDetectionEnabled":
            publisherBuilder.withThirdPartyFraudDetectionEnabled(
                (model.getThirdPartyFraudDetectionEnabled()));
            break;
          case "currency":
            publisherBuilder.withCurrency(model.getCurrency());
            break;
          case "hbPartnerAttributes":
            publisherBuilder.withHbPartnerAttributes(
                HbPartnerAssignmentDTOMapper.MAPPER.mapHbPartnerCompany(
                    model.getHbPartnerCompany()));
            break;
          case "sellerSeatPid":
            publisherBuilder.withSellerSeatPid(model.getSellerSeatPid());
            break;
          case "externalAdVerificationEnabled":
            publisherBuilder.withExternalAdVerificationEnabled(
                model.getExternalAdVerificationEnabled());
            break;
          case "fraudDetectionJavascriptEnabled":
            publisherBuilder.withFraudDetectionJavascriptEnabled(
                model.getFraudDetectionJavascriptEnabled());
            break;
          default:
        }
      }
    }

    return publisherBuilder.build();
  }

  @Override
  public PublisherDTO make(final NullableContext context, final Company model) {
    return make(NullableContext.nullableContext, model, getFields());
  }

  @Override
  public Company apply(
      final NullableContext context, final Company companyDB, final PublisherDTO dto) {
    if (dto.getAttributes() != null) {
      applySellerAttributes(companyDB, dto);

      getPublisherAttributesAssembler()
          .apply(context, companyDB.getSellerAttributes(), dto.getAttributes());

      applyTransparencySettings(companyDB, dto);
    }

    if (userContext.isNexageAdminOrManager() && dto.getEligibleBidderGroups() != null) {
      eligibleBiddersAssembler.apply(
          CompanyContext.newBuilder().withCompany(companyDB).build(),
          companyDB.getEligibleBidders(),
          dto.getEligibleBidderGroups());
    }

    return companyDB;
  }

  public void applySellerAttributes(Company companyDB, PublisherDTO dto) {
    if (companyDB.getSellerAttributes() == null) {
      SellerAttributes attributes = new SellerAttributes();
      companyDB.setSellerAttributes(attributes);
    }

    companyDB
        .getSellerAttributes()
        .setDefaultBidderGroups(dto.getAttributes().getDefaultBidderGroups());
    companyDB
        .getSellerAttributes()
        .setDefaultBiddersAllowList(dto.getAttributes().isDefaultBiddersAllowList());
    companyDB.getSellerAttributes().setDefaultBlock(dto.getAttributes().getDefaultBlock());
    companyDB
        .getSellerAttributes()
        .setReviewStatusBlock(
            dto.getAttributes().getReviewStatusBlock() == null
                ? CrsReviewStatusBlock.ALLOW_ALL
                : dto.getAttributes().getReviewStatusBlock());

    companyDB
        .getSellerAttributes()
        .setSecureStatusBlock(
            dto.getAttributes().getSecureStatusBlock() == null
                ? CrsSecureStatusBlock.ALLOW_ALL
                : dto.getAttributes().getSecureStatusBlock());

    companyDB
        .getSellerAttributes()
        .setVideoUseInboundSiteOrApp(dto.getAttributes().getVideoUseInboundSiteOrApp());

    companyDB
        .getSellerAttributes()
        .setPublisherDataProtectionRole(dto.getAttributes().getPublisherDataProtectionRole());

    companyDB
        .getSellerAttributes()
        .setSellerDomainVerificationAuthLevel(
            dto.getAttributes().getSellerDomainVerificationAuthLevel() == null
                ? SellerDomainVerificationAuthLevel.ALLOW_BASED_ON_BIDDER
                : dto.getAttributes().getSellerDomainVerificationAuthLevel());

    Float samplingRate = dto.getAttributes().getExternalAdVerificationSamplingRate();
    if (samplingRate != null) {
      if (samplingRate < 0 || samplingRate > 100) {
        log.error(
            "Geo Edge Sampling Rate for the publisher id {} is not within the value 0-100",
            dto.getPid());
        throw new GenevaValidationException(ServerErrorCodes.SERVER_INVALID_GEO_EDGE_SAMPLING_RATE);
      }
      companyDB.getSellerAttributes().setExternalAdVerificationSamplingRate(samplingRate);
    }

    BigDecimal successCreativeRate = dto.getAttributes().getCreativeSuccessRateThreshold();
    boolean successCreativeRateOptOut = dto.getAttributes().isCreativeSuccessRateThresholdOptOut();
    if (successCreativeRate != null) {
      Utils.validateCreativeSuccessRate(successCreativeRate, successCreativeRateOptOut);
      companyDB.getSellerAttributes().setCreativeSuccessRateThreshold(successCreativeRate);
    } else if (successCreativeRateOptOut) {
      companyDB.getSellerAttributes().setCreativeSuccessRateThreshold(null);
    }

    companyDB
        .getSellerAttributes()
        .setDynamicFloorEnabled(dto.getAttributes().getDynamicFloorEnabled());

    String externalAdVerificationPolicyKey =
        dto.getAttributes().getExternalAdVerificationPolicyKey();
    boolean isExternalAdVerificationEnabled =
        dto.getExternalAdVerificationEnabled() != null && dto.getExternalAdVerificationEnabled();
    if (isExternalAdVerificationEnabled && externalAdVerificationPolicyKey == null) {
      log.error(
          "ExternalAdVerification is enabled for the publisher id {} is not having policy key",
          dto.getPid());
      throw new GenevaValidationException(
          ServerErrorCodes.SERVER_EXTERNAL_AD_VERIFICATION_POLICY_KEY_NOT_FOUND);
    } else if (isExternalAdVerificationEnabled) {
      companyDB
          .getSellerAttributes()
          .setExternalAdVerificationPolicyKey(externalAdVerificationPolicyKey);
    } else {
      companyDB.getSellerAttributes().setExternalAdVerificationPolicyKey(null);
    }

    if (dto.getAttributes().getHumanPrebidSampleRate() != null) {
      companyDB
          .getSellerAttributes()
          .setHumanPrebidSampleRate(dto.getAttributes().getHumanPrebidSampleRate());
    }

    if (dto.getAttributes().getHumanPostbidSampleRate() != null) {
      companyDB
          .getSellerAttributes()
          .setHumanPostbidSampleRate(dto.getAttributes().getHumanPostbidSampleRate());
    }

    companyDB
        .getSellerAttributes()
        .setCustomDealFloorEnabled(dto.getAttributes().isCustomDealFloorEnabled());
  }

  public Company applyTransparencySettings(final Company companyDB, final PublisherDTO dto) {
    if (dto.getAttributes() != null) {
      if (companyDB.getSellerAttributes() == null) {
        SellerAttributes attributes = new SellerAttributes();
        companyDB.setSellerAttributes(attributes);
      }
      TransparencyMgmtEnablement transparencyMgmtEnablement =
          dto.getAttributes().getDefaultTransparencyMgmtEnablement();
      if (transparencyMgmtEnablement != null) {
        companyDB
            .getSellerAttributes()
            .setTransparencyMgmtEnablement(transparencyMgmtEnablement.getId());
      }

      TransparencySettingsDTO transparencySettings =
          dto.getAttributes().getDefaultTransparencySettings();
      if (transparencySettings != null) {
        if (transparencySettings.getTransparencyMode() != null) {
          companyDB
              .getSellerAttributes()
              .setIncludePubName(transparencySettings.getTransparencyMode().asInt());
        } else {
          companyDB.getSellerAttributes().setIncludePubName(null);
        }
        companyDB.getSellerAttributes().setPubNameAlias(transparencySettings.getNameAlias());
      } else {
        companyDB.getSellerAttributes().setPubAliasId(null);
        companyDB.getSellerAttributes().setIncludePubName(null);
        companyDB.getSellerAttributes().setPubNameAlias(null);
      }
    }
    return companyDB;
  }

  public Company applyHbPartnerAttributes(final Company company, final PublisherDTO dto) {

    Set<HbPartnerAssignmentDTO> hbPartnerAssignmentDTOS = dto.getHbPartnerAttributes();

    if (CollectionUtils.isEmpty(hbPartnerAssignmentDTOS)) {
      company.setHbPartnerCompany(Collections.emptySet());
      return company;
    }

    company.setHbPartnerCompany(
        HbPartnerCompanyMapper.MAPPER.map(hbPartnerAssignmentDTOS, company, hbPartnerRepository));

    return company;
  }
}
