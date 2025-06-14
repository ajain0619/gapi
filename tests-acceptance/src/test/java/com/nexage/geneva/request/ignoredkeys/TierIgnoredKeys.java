package com.nexage.geneva.request.ignoredkeys;

public class TierIgnoredKeys {
  public static final String[] ignoredKeysModifyTierNonPss = {
    "version",
    "tiers[*].version",
    "tiers[*].tags[*].version",
    "tiers[*].tags[*].height",
    "tiers[*].tags[*].videoLinearity",
    "tiers[*].tags[*].screenLocation",
    "tiers[*].tags[*].width",
    "tiers[*].tags[*].videoSupport",
    "height",
    "width",
    "tiers[*].tags[*].importRevenueFlag",
    "tiers[*].tags[*].deployments",
    "tiers[*].pid",
    "tiers[*].tags[*].currentDealTerm.effectiveDate",
    "tiers[*].tags[*].name",
    "tiers[*].tags[*].rules[*].version"
  };
  public static final String[] ignoredKeysCreateTierPss = {"pid", "version", "position"};
  public static final String[] ignoredKeysUpdateTierPss = {"version"};
  public static final String[] ignoredKeyGetTierPss = {"version"};
  public static final String[] ignoredKeyGetTiersPss = {"version"};
}
