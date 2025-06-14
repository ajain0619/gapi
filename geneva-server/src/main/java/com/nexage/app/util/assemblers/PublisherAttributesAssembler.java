package com.nexage.app.util.assemblers;

import static com.nexage.app.mapper.SmartExchangeAttributesDTOMapper.SMART_EXCHANGE_ATTR_MAPPER;

import com.nexage.admin.core.enums.CrsReviewStatusBlock;
import com.nexage.admin.core.enums.CrsSecureStatusBlock;
import com.nexage.admin.core.model.RTBProfile;
import com.nexage.admin.core.model.SellerAttributes;
import com.nexage.admin.core.sparta.jpa.model.SmartExchangeAttributes;
import com.nexage.app.dto.publisher.PublisherAttributes;
import com.nexage.app.dto.publisher.PublisherDefaultRTBProfileDTO;
import com.nexage.app.dto.smartexchangeattributes.SmartExchangeAttributesDTO;
import com.nexage.app.dto.transparency.TransparencyMgmtEnablement;
import com.nexage.app.dto.transparency.TransparencyMode;
import com.nexage.app.dto.transparency.TransparencySettingsDTO;
import com.nexage.app.security.UserContext;
import com.nexage.app.services.validation.RevenueShareUpdateValidator;
import com.nexage.app.util.assemblers.context.NullableContext;
import com.nexage.app.util.assemblers.context.PublisherDefaultRTBProfileContext;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@RequiredArgsConstructor
@Log4j2
public class PublisherAttributesAssembler
    extends Assembler<PublisherAttributes, SellerAttributes, NullableContext> {

  private static final int DEFAULT_PUBLISHER_DATA_PROTECTION_ROLE = 0;
  private static final int DEFAULT_GDPR_JURISDICTION = 0;
  private static final boolean DEFAULT_VIDEO_USE_INBOUND_SITE_OR_APP = false;

  public static final Set<String> DEFAULT_SELLER_ATTRIBUTES_FIELDS =
      Set.of(
          "version",
          "sellerPid",
          "defaultBlock",
          "defaultBidderGroups",
          "defaultBiddersAllowList",
          "transparencyMgmtEnablement",
          "transparencySettings",
          "crsReviewStatusBlock",
          "defaultRtbProfile",
          "crsSecureStatusBlock",
          "revenueShare",
          "rtbFee",
          "publisherDataProtectionRole",
          "videoUseInboundSiteOrApp",
          "sellerDomainVerificationAuthLevel",
          "smartQPSEnabled",
          "externalAdVerificationSamplingRate",
          "dynamicFloorEnabled",
          "adStrictApproval",
          "smartExchangeAttributes",
          "externalAdVerificationPolicyKey",
          "sspDealRevShare",
          "jointDealRevShare",
          "sellerDealRevShare",
          "enableCtvSelling",
          "creativeSuccessRateThresholdOptOut",
          "rawResponse",
          "humanPrebidSampleRate",
          "humanPostbidSampleRate",
          "customDealFloorEnabled");

  protected final UserContext userContext;
  private final PublisherTagAssembler publisherTagAssembler;
  private final PublisherRTBProfileAssembler publisherRTBProfileAssembler;
  private final RevenueShareUpdateValidator revenueShareUpdateValidator;

  protected Set<String> getFields() {
    return DEFAULT_SELLER_ATTRIBUTES_FIELDS;
  }

  @Override
  public PublisherAttributes make(
      final NullableContext context, final SellerAttributes model, final Set<String> fields) {

    PublisherAttributes.Builder builder = PublisherAttributes.newBuilder();
    Set<String> fieldsToMap = (fields != null) ? fields : getFields();

    if (model != null) {
      for (String field : fieldsToMap) {
        switch (field) {
          case "version":
            builder.withVersion(model.getVersion());
            break;
          case "defaultBlock":
            builder.withDefaultBlock(model.getDefaultBlock());
            break;
          case "defaultBidderGroups":
            builder.withDefaultBidderGroups(model.getDefaultBidderGroups());
            break;
          case "defaultBiddersAllowList":
            builder.withDefaultBiddersAllowList(model.isDefaultBiddersAllowList());
            break;
          case "transparencyMgmtEnablement":
            builder.withDefaultTransparencyMgmtEnablement(
                TransparencyMgmtEnablement.getById(model.getTransparencyMgmtEnablement()));
            break;
          case "transparencySettings":
            builder.withDefaultTransparencySettings(
                new TransparencySettingsDTO(
                    TransparencyMode.fromInt(model.getIncludePubName()),
                    model.getPubAliasId(),
                    model.getPubNameAlias()));
            break;
          case "crsReviewStatusBlock":
            builder.withReviewStatusBlock(
                model.getReviewStatusBlock() == null
                    ? CrsReviewStatusBlock.ALLOW_ALL
                    : model.getReviewStatusBlock());
            break;
          case "defaultRtbProfile":
            if (model.getDefaultRtbProfile() != null && userContext.isNexageAdminOrManager()) {
              PublisherDefaultRTBProfileContext defaultRtbProfileContext =
                  PublisherDefaultRTBProfileContext.newBuilder()
                      .withCompany(model.getSeller())
                      .build();
              PublisherDefaultRTBProfileDTO publisherDefaultRtbProfile =
                  makePublisherDefaultRtbProfileDto(
                      defaultRtbProfileContext,
                      model.getDefaultRtbProfile(),
                      PublisherRTBProfileAssembler.ALL_DEFAULT_RTB_PROFILE_FIELDS);
              builder.withDefaultRtbProfile(publisherDefaultRtbProfile);
            }
            break;
          case "crsSecureStatusBlock":
            builder.withSecureStatusBlock(
                model.getSecureStatusBlock() == null
                    ? CrsSecureStatusBlock.ALLOW_ALL
                    : model.getSecureStatusBlock());
            break;
          case "effectiveDate":
            builder.withEffectiveDate(model.getEffectiveDate());
            break;
          case "revenueShare":
            builder.withRevenueShare(model.getRevenueShare());
            break;
          case "rtbFee":
            builder.withRtbFee(model.getRtbFee());
            break;
          case "hbThrottleEnabled":
            builder.withHbThrottleEnabled(model.isHbThrottleEnabled());
            break;
          case "hbThrottlePercentage":
            builder.withHbThrottlePercentage(model.getHbThrottlePercentage());
            break;
          case "pfoEnabled":
            builder.withPfoEnabled(model.isPfoEnabled());
            break;
          case "siteLimit":
            builder.withSiteLimit(model.getSiteLimit());
            break;
          case "positionsPerSiteLimit":
            builder.withPositionsPerSiteLimit(model.getPositionsPerSiteLimit());
            break;
          case "tagsPerPositionLimit":
            builder.withTagsPerPositionLimit(model.getTagsPerPositionLimit());
            break;
          case "campaignsLimit":
            builder.withCampaignsLimit(model.getCampaignsLimit());
            break;
          case "creativesPerCampaignLimit":
            builder.withCreativesPerCampaignLimit(model.getCreativesPerCampaignLimit());
            break;
          case "bidderLibrariesLimit":
            builder.withBidderLibrariesLimit(model.getBidderLibrariesLimit());
            break;
          case "blockLibrariesLimit":
            builder.withBlockLibrariesLimit(model.getBlockLibrariesLimit());
            break;
          case "userLimit":
            builder.withUserLimit(model.getUserLimit());
            break;
          case "limitEnabled":
            builder.withLimitEnabled(model.isLimitEnabled());
            break;
          case "hbPricePreference":
            builder.withHbPricePreference(model.getHbPricePreference());
            break;
          case "superAuctionEnabled":
            builder.withSuperAuctionEnabled(model.isSuperAuctionEnabled());
            break;
          case "adFeedbackOptOut":
            builder.withAdFeedbackOptOut(model.getAdFeedbackOptOut());
            break;
          case "buyerTransparencyOptOut":
            builder.withBuyerTransparencyOptOut(model.getBuyerTransparencyOptOut());
            break;
          case "humanOptOut":
            builder.withHumanOptOut(model.getHumanOptOut());
            break;
          case "revenueGroupPid":
            builder.withRevenueGroupPid(model.getRevenueGroupPid());
            break;
          case "sellerType":
            builder.withSellerType(model.getSellerType());
            break;
          case "publisherDataProtectionRole":
            builder.withPublisherDataProtectionRole(
                model.getPublisherDataProtectionRole() != null
                    ? model.getPublisherDataProtectionRole()
                    : DEFAULT_PUBLISHER_DATA_PROTECTION_ROLE);
            break;
          case "videoUseInboundSiteOrApp":
            builder.withVideoUseInboundSiteOrApp(
                model.getVideoUseInboundSiteOrApp() != null
                    ? model.getVideoUseInboundSiteOrApp()
                    : DEFAULT_VIDEO_USE_INBOUND_SITE_OR_APP);
            break;
          case "sellerDomainVerificationAuthLevel":
            builder.withSellerDomainVerificationAuthLevel(
                model.getSellerDomainVerificationAuthLevel());
            break;
          case "smartQPSEnabled":
            builder.withSmartQPSEnabled(model.getSmartQPSEnabled());
            break;
          case "externalAdVerificationSamplingRate":
            builder.withExternalAdVerificationSamplingRate(
                model.getExternalAdVerificationSamplingRate());
            break;
          case "dynamicFloorEnabled":
            builder.withDynamicFloorEnabled(model.getDynamicFloorEnabled());
            break;
          case "adStrictApproval":
            builder.withAdStrictApproval(model.getAdStrictApproval());
            break;
          case "smartExchangeAttributes":
            SmartExchangeAttributes smartExchangeAttributes = model.getSmartExchangeAttributes();
            SmartExchangeAttributesDTO smartExchangeAttributesDTO =
                SMART_EXCHANGE_ATTR_MAPPER.map(smartExchangeAttributes);
            builder.withSmartExchangeAttributes(smartExchangeAttributesDTO);
            break;
          case "externalAdVerificationPolicyKey":
            builder.withExternalAdVerificationPolicyKey(model.getExternalAdVerificationPolicyKey());
            break;
          case "sspDealRevShare":
            builder.withSspDealRevShare(model.getSspDealRevShare());
            break;
          case "jointDealRevShare":
            builder.withJointDealRevShare(model.getJointDealRevShare());
            break;
          case "sellerDealRevShare":
            builder.withSellerDealRevShare(model.getSellerDealRevShare());
            break;
          case "enableCtvSelling":
            builder.withEnableCtvSelling(model.isEnableCtvSelling());
            break;
          case "creativeSuccessRateThreshold":
            builder.withCreativeSuccessRateThreshold(model.getCreativeSuccessRateThreshold());
            break;
          case "creativeSuccessRateThresholdOptOut":
            builder.withCreativeSuccessRateThresholdOptOut(
                model.isCreativeSuccessRateThresholdOptOut());
            break;
          case "rawResponse":
            builder.withRawResponse(model.isRawResponse());
            break;
          case "humanPrebidSampleRate":
            builder.withHumanPrebidSampleRate(model.getHumanPrebidSampleRate());
            break;
          case "humanPostbidSampleRate":
            builder.withHumanPostbidSampleRate(model.getHumanPostbidSampleRate());
            break;
          case "customDealFloorEnabled":
            builder.withCustomDealFloorEnabled(model.isCustomDealFloorEnabled());
            break;
          default:
        }
      }
    }

    return builder.build();
  }

  @Override
  public PublisherAttributes make(final NullableContext context, final SellerAttributes model) {
    return make(NullableContext.nullableContext, model, getFields());
  }

  @Override
  public SellerAttributes apply(
      final NullableContext context, final SellerAttributes model, final PublisherAttributes dto) {
    if (revenueShareUpdateValidator.isRevenueShareUpdate(model, dto)) {
      model.setRevenueShare(dto.getRevenueShare());
      model.setRtbFee(dto.getRtbFee());
    }
    return model;
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
