package com.nexage.app.util.assemblers;

import com.nexage.app.security.UserContext;
import com.nexage.app.services.validation.RevenueShareUpdateValidator;
import java.util.Set;
import org.springframework.stereotype.Component;

@Component
public class ExternalPublisherAttributesAssembler extends PublisherAttributesAssembler {

  public static final Set<String> EXTERNAL_SELLER_ATTRIBUTES_FIELDS =
      Set.of(
          "version",
          "sellerPid",
          "defaultBlock",
          "defaultBidderGroups",
          "defaultBiddersAllowList",
          "transparencyMgmtEnablement",
          "transparencySettings",
          "crsReviewStatusBlock",
          "crsSecureStatusBlock",
          "revenueShare",
          "rtbFee",
          "sspDealRevShare",
          "jointDealRevShare",
          "sellerDealRevShare",
          "creativeSuccessRateThresholdOptOut",
          "rawResponse");

  public ExternalPublisherAttributesAssembler(
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
    return EXTERNAL_SELLER_ATTRIBUTES_FIELDS;
  }
}
