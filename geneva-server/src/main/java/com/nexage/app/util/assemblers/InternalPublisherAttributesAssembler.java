package com.nexage.app.util.assemblers;

import static com.nexage.app.mapper.SmartExchangeAttributesDTOMapper.SMART_EXCHANGE_ATTR_MAPPER;

import com.nexage.admin.core.model.SellerAttributes;
import com.nexage.admin.core.sparta.jpa.model.SmartExchangeAttributes;
import com.nexage.app.dto.publisher.PublisherAttributes;
import com.nexage.app.dto.smartexchangeattributes.SmartExchangeAttributesDTO;
import com.nexage.app.security.UserContext;
import com.nexage.app.services.validation.RevenueShareUpdateValidator;
import com.nexage.app.util.Utils;
import com.nexage.app.util.assemblers.context.NullableContext;
import com.ssp.geneva.common.security.error.GenevaSecurityException;
import com.ssp.geneva.common.security.error.SecurityErrorCodes;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Log4j2
@Component
public class InternalPublisherAttributesAssembler extends PublisherAttributesAssembler {

  public static final Set<String> DEFAULT_SELLER_ATTRIBUTES_FIELDS =
      Set.of(
          "effectiveDate",
          "revenueShare",
          "rtbFee",
          "hbThrottleEnabled",
          "hbThrottlePercentage",
          "siteLimit",
          "positionsPerSiteLimit",
          "pfoEnabled",
          "tagsPerPositionLimit",
          "campaignsLimit",
          "creativesPerCampaignLimit",
          "bidderLibrariesLimit",
          "blockLibrariesLimit",
          "userLimit",
          "limitEnabled",
          "hbPricePreference",
          "superAuctionEnabled",
          "adFeedbackOptOut",
          "buyerTransparencyOptOut",
          "humanOptOut",
          "revenueGroupPid",
          "sellerType",
          "defaultRtbProfile",
          "sellerDomainVerificationAuthLevel",
          "smartQPSEnabled",
          "smartExchangeAttributes",
          "sspDealRevShare",
          "jointDealRevShare",
          "sellerDealRevShare",
          "enableCtvSelling",
          "creativeSuccessRateThreshold",
          "creativeSuccessRateThresholdOptOut");

  private Set<String> ALL_FIELDS = null;

  public InternalPublisherAttributesAssembler(
      UserContext userContext,
      PublisherTagAssembler PublisherTagAssembler,
      PublisherRTBProfileAssembler publisherRTBProfileAssembler,
      RevenueShareUpdateValidator revenueShareUpdateValidator) {
    super(
        userContext,
        PublisherTagAssembler,
        publisherRTBProfileAssembler,
        revenueShareUpdateValidator);
  }

  @Override
  protected Set<String> getFields() {
    if (ALL_FIELDS == null) {
      ALL_FIELDS = new HashSet<>(super.getFields());
      ALL_FIELDS.addAll(DEFAULT_SELLER_ATTRIBUTES_FIELDS);
    }
    return ALL_FIELDS;
  }

  @Override
  public SellerAttributes apply(
      NullableContext context, SellerAttributes model, PublisherAttributes dto) {
    SellerAttributes result = super.apply(context, model, dto);
    if (userContext.isNexageAdminOrManager()) {
      model.setEffectiveDate(dto.getEffectiveDate());
      model.setRevenueShare(dto.getRevenueShare());
      model.setRtbFee(dto.getRtbFee());
      model.setHbThrottlePercentage(dto.getHbThrottlePercentage());
      model.setSiteLimit(dto.getSiteLimit());
      model.setPositionsPerSiteLimit(dto.getPositionsPerSiteLimit());
      model.setTagsPerPositionLimit(dto.getTagsPerPositionLimit());
      model.setCampaignsLimit(dto.getCampaignsLimit());
      model.setCreativesPerCampaignLimit(dto.getCreativesPerCampaignLimit());
      model.setBidderLibrariesLimit(dto.getBidderLibrariesLimit());
      model.setBlockLibrariesLimit(dto.getBlockLibrariesLimit());
      model.setUserLimit(dto.getUserLimit());
      model.setAdFeedbackOptOut(dto.getAdFeedbackOptOut());
      model.setSellerType(dto.getSellerType());
      model.setBuyerTransparencyOptOut(dto.getBuyerTransparencyOptOut());
      model.setHumanOptOut(dto.getHumanOptOut());
      model.setRevenueGroupPid(dto.getRevenueGroupPid());
      model.setSspDealRevShare(dto.getSspDealRevShare());
      model.setJointDealRevShare(dto.getJointDealRevShare());
      model.setSellerDealRevShare(dto.getSellerDealRevShare());
      model.setCreativeSuccessRateThresholdOptOut(dto.isCreativeSuccessRateThresholdOptOut());
      /*
      TODO: uncomment and check when RTBProfile will ready for publisher
      if(dto.getDefaultRTBProfile() != null){
          RTBProfile profile = model.getDefaultRTBProfile();
          if(profile == null){
              profile = new RTBProfile();
              model.setDefaultRTBProfile(profile);
          }

          PublisherRTBProfileContext rtbContext =  PublisherRTBProfileContext.newBuilder().build();
          model.setDefaultRTBProfile(publisherRTBProfileAssembler.apply(rtbContext, profile, dto.getDefaultRTBProfile()));
      }
      model.setDefaultRTBProfileId(dto.getDefaultRTBProfileId());
      */

      if (dto.getSuperAuctionEnabled() != null) {
        model.setSuperAuctionEnabled(dto.getSuperAuctionEnabled());
      }

      if (dto.getHbThrottleEnabled() != null) {
        model.setHbThrottleEnabled(dto.getHbThrottleEnabled());
      }

      if (dto.getHbPricePreference() != null) {
        model.setHbPricePreference(dto.getHbPricePreference());
      }

      if (dto.getPfoEnabled() != null) {
        model.setPfoEnabled(dto.getPfoEnabled());
      }

      if (dto.getLimitEnabled() != null) {
        model.setLimitEnabled(dto.getLimitEnabled());
      }

      if (dto.getSellerDomainVerificationAuthLevel() != null) {
        model.setSellerDomainVerificationAuthLevel(dto.getSellerDomainVerificationAuthLevel());
      }

      if (dto.getSmartQPSEnabled() != null) {
        model.setSmartQPSEnabled(dto.getSmartQPSEnabled());
      }

      if (dto.getAdStrictApproval() != null) {
        model.setAdStrictApproval(dto.getAdStrictApproval());
      }

      if (dto.getRawResponse() != null) {
        model.setRawResponse(dto.getRawResponse());
      }

      boolean creativeSuccessRateOptOut = dto.isCreativeSuccessRateThresholdOptOut();
      BigDecimal creativeSuccessRateThreshold = dto.getCreativeSuccessRateThreshold();

      if (creativeSuccessRateThreshold != null) {
        Utils.validateCreativeSuccessRate(creativeSuccessRateThreshold, creativeSuccessRateOptOut);
        model.setCreativeSuccessRateThreshold(creativeSuccessRateThreshold);
      } else {
        model.setCreativeSuccessRateThreshold(null);
      }

      setSmartExchangeAttributes(dto, model);
      model.setEnableCtvSelling(dto.isEnableCtvSelling());
    }
    return result;
  }

  private void setSmartExchangeAttributes(
      PublisherAttributes dto, SellerAttributes sellerAttributes) {
    SmartExchangeAttributesDTO smartExchangeAttributesDTO = dto.getSmartExchangeAttributes();
    SmartExchangeAttributes smartExchangeAttributes = sellerAttributes.getSmartExchangeAttributes();

    if (smartExchangeAttributesDTO != null) {
      SmartExchangeAttributes updated =
          SMART_EXCHANGE_ATTR_MAPPER.map(smartExchangeAttributesDTO, sellerAttributes);
      checkSmartExchangeAttributesEditPermissions(smartExchangeAttributes, updated);

      if (smartExchangeAttributes == null) {
        sellerAttributes.setSmartExchangeAttributes(updated);
      } else {
        SMART_EXCHANGE_ATTR_MAPPER.updateOriginal(updated, smartExchangeAttributes);
      }
    }
  }

  private void checkSmartExchangeAttributesEditPermissions(
      SmartExchangeAttributes original, SmartExchangeAttributes updated) {

    if (original == null
        && updated.getSmartMarginOverride()
        && !userContext.canEditSmartExchange()) {
      throw new GenevaSecurityException(SecurityErrorCodes.SECURITY_NOT_AUTHORIZED);
    }

    if (original != null && !original.equals(updated) && !userContext.canEditSmartExchange()) {
      throw new GenevaSecurityException(SecurityErrorCodes.SECURITY_NOT_AUTHORIZED);
    }
  }
}
