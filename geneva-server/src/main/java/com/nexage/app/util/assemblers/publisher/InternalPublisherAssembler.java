package com.nexage.app.util.assemblers.publisher;

import static java.util.Objects.nonNull;
import static java.util.stream.Stream.concat;

import com.nexage.admin.core.model.Company;
import com.nexage.admin.core.repository.HbPartnerRepository;
import com.nexage.app.dto.publisher.PublisherDTO;
import com.nexage.app.security.UserContext;
import com.nexage.app.util.assemblers.InternalPublisherAttributesAssembler;
import com.nexage.app.util.assemblers.PublisherAttributesAssembler;
import com.nexage.app.util.assemblers.PublisherEligibleBiddersAssembler;
import com.nexage.app.util.assemblers.context.NullableContext;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class InternalPublisherAssembler extends BasePublisherAssembler {

  private static final Set<String> INTERNAL_FIELDS =
      Set.of(
          "contactUserPid",
          "reportingApiEnabled",
          "adServingEnabled",
          "restrictDrillDown",
          "reportingType",
          "houseAdServingFee",
          "nonRemnantHouseAdCap",
          "houseAdOverageFee",
          "cpiTrackingEnabled",
          "cpiConversionNoticeUrl",
          "rtbEnabled",
          "mediationEnabled",
          "rtbRevenueReportEnabled",
          "salesforceId",
          "bidderAdServingFee",
          "status",
          "globalAliasName",
          "website",
          "description",
          "type",
          "test",
          "selfServeAllowed",
          "regionId",
          "payoutEnabled",
          "numberOfRtbTags",
          "numberOfMediationSites",
          "adsourceNames",
          "externalDataProviderNames",
          "activeIOs",
          "credit",
          "etr",
          "numberOfUsers",
          "hasHeaderBiddingSites",
          "directAdServingFee",
          "defaultRtbProfilesEnabled",
          "sellerSeatPid",
          "externalAdVerificationEnabled",
          "fraudDetectionJavascriptEnabled",
          "thirdPartyFraudDetectionEnabled");

  private final InternalPublisherAttributesAssembler internalPublisherAttributesAssembler;

  public InternalPublisherAssembler(
      PublisherEligibleBiddersAssembler publisherEligibleBiddersAssembler,
      HbPartnerRepository hbPartnerRepository,
      InternalPublisherAttributesAssembler internalPublisherAttributesAssembler,
      UserContext userContext) {
    super(publisherEligibleBiddersAssembler, hbPartnerRepository, userContext);
    this.internalPublisherAttributesAssembler = internalPublisherAttributesAssembler;
  }

  @Override
  protected PublisherAttributesAssembler getPublisherAttributesAssembler() {
    return internalPublisherAttributesAssembler;
  }

  @Override
  protected Set<String> getFields() {
    return concat(super.getFields().stream(), INTERNAL_FIELDS.stream()).collect(Collectors.toSet());
  }

  public Company apply(
      final NullableContext context, final Company companyDB, final PublisherDTO dto) {
    super.apply(context, companyDB, dto);
    if (userContext.isNexageAdminOrManager()) {
      companyDB.setName(dto.getName());
      companyDB.setContactUserPid(dto.getContactUserPid());
      companyDB.setAdServingEnabled(dto.getAdServingEnabled());
      companyDB.setRestrictDrillDown(dto.getRestrictDrillDown());
      companyDB.setDirectAdServingFee(dto.getDirectAdServingFee());
      companyDB.setHouseAdServingFee(dto.getHouseAdServingFee());
      companyDB.setNonRemnantHouseAdCap(dto.getNonRemnantHouseAdCap());
      companyDB.setHouseAdOverageFee(dto.getHouseAdOverageFee());
      companyDB.setCpiTrackingEnabled(dto.getCpiTrackingEnabled());
      companyDB.setCpiConversionNoticeUrl(dto.getCpiConversionNoticeUrl());
      companyDB.setRtbEnabled(dto.getRtbEnabled());
      companyDB.setMediationEnabled(dto.getMediationEnabled());
      companyDB.setRtbRevenueReportEnabled(dto.getRtbRevenueReportEnabled());
      companyDB.setSalesforceId(dto.getSalesforceId());
      companyDB.setBidderAdServingFee(dto.getBidderAdServingFee());
      companyDB.setStatus(dto.getStatus());
      companyDB.setGlobalAliasName(dto.getGlobalAliasName());
      companyDB.setWebsite(dto.getWebsite());
      companyDB.setDescription(dto.getDescription());
      companyDB.setType(dto.getType());
      companyDB.setTest(dto.getTest());
      companyDB.setSelfServeAllowed(dto.getSelfServeAllowed());
      companyDB.setRegionId(dto.getRegionId());
      companyDB.setPayoutEnabled(dto.getPayoutEnabled());

      companyDB.setThirdPartyFraudDetectionEnabled(dto.getThirdPartyFraudDetectionEnabled());
      if (!nonNull(dto.getThirdPartyFraudDetectionEnabled()) && !nonNull(dto.getPid())) {
        companyDB.setThirdPartyFraudDetectionEnabled(true);
      }

      companyDB.setEstimatedTimeRemaining(dto.getEstimatedTimeRemain());
      companyDB.setDefaultRtbProfilesEnabled(dto.getDefaultRtbProfilesEnabled());
      if (nonNull(dto.getCurrency())) {
        companyDB.setCurrency(dto.getCurrency());
      }

      companyDB.setSellerSeatPid(dto.getSellerSeatPid());

      if (dto.getExternalAdVerificationEnabled() != null) {
        companyDB.setExternalAdVerificationEnabled(dto.getExternalAdVerificationEnabled());
      }

      if (dto.getFraudDetectionJavascriptEnabled() != null) {
        companyDB.setFraudDetectionJavascriptEnabled(dto.getFraudDetectionJavascriptEnabled());
      }
    }
    return companyDB;
  }
}
