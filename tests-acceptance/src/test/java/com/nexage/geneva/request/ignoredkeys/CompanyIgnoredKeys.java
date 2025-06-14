package com.nexage.geneva.request.ignoredkeys;

public class CompanyIgnoredKeys {

  public static final String[] actualObjectCreateBuyer = {
    "globalAliasName",
    "directAdServingFee",
    "reportingType",
    "pid",
    "numberOfRtbTags",
    "externalDataProviderNames",
    "contactUserPid",
    "numberOfUsers",
    "adServingEnabled",
    "id",
    "credit",
    "houseAdOverageFee",
    "test",
    "activeIOs",
    "selfServeAllowed",
    "sellerAttributes",
    "estimatedTimeRemain",
    "adsourceNames",
    "version",
    "numberOfMediationSites",
    "restrictDrillDown",
    "bidderAdServingFee",
    "houseAdServingFee",
    "nonRemnantHouseAdCap",
    "accessKey"
  };

  public static final String[] expectedObjectCreateBuyer = {"pid", "id", "accessKey"};

  public static final String[] actualAndExpectedObjectUpdate = {
    // "version",
    "sellerAttributes.version",
    "sellerAttributes.effectiveDate",
    "sellerAttributes.defaultBlock",
    "sellerAttributes.defaultBidderGroups",
    "sellerAttributes.version",
    "numberOfUsers",
    "numberOfRtbTags",
    "numberOfMediationSites"
  };

  public static final String[] actualAndExpectedNewObjectUpdate = {
    //      "version",
    "pid",
    "id",
    "sellerAttributes.version",
    "sellerAttributes.effectiveDate",
    "sellerAttributes.defaultBlock",
    "sellerAttributes.defaultBidderGroups",
    "sellerAttributes.version",
    "numberOfUsers",
    "numberOfRtbTags",
    "numberOfMediationSites"
  };

  public static final String[] actualAndExpectedObjectUpdatePartner = {"version", "lastUpdate"};

  public static final String[] actualObjectCreatePartner = {
    "pid", "creationDate", "version", "lastUpdate"
  };

  public static final String[] actualAndExpectedCreateSeller = {
    "pid",
    "id",
    "sellerAttributes.effectiveDate",
    "sellerAttributes.defaultBlock",
    "sellerAttributes.defaultBidderGroups",
    "sellerAttributes.version",
    "version",
    "accessKey"
  };

  public static final String[] actualObjectEligibleBidders = {
    "version", "eligibleBidderGroups->pid", "eligibleBidderGroups->version"
  };

  public static final String[] expectedObjectEligibleBidders = {
    "version", "eligibleBidderGroups->pid", "eligibleBidderGroups->version",
  };
}
