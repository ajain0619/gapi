package com.nexage.geneva.request.ignoredkeys;

public class PositionIgnoredKeys {

  public static final String[] expectedObjectUpdatePositionNonPss = {"version", "nativeVersion"};

  public static final String[] actualObjectUpdatePositionNonPss = {"version", "nativeVersion"};

  public static final String[] expectedObjectCreatePositionNonPss = {
    "pid", "version", "nativeVersion"
  };

  public static final String[] actualObjectCreatePositionNonPss = {
    "pid", "version", "nativeVersion"
  };

  public static final String[] expectedObjectUpdatePlacementPss = {"version", "videoLinearity"};

  public static final String[] actualObjectUpdatePlacementPss = {"version", "videoLinearity"};

  public static final String[] expectedObjectCreatePlacementPss = {"pid", "version"};

  public static final String[] actualObjectCreatePlacementPss = {
    "pid", "version", "videoLinearity"
  };

  public static final String[] expectedObjectCreatePlacementProvision = {};

  public static final String[] actualObjectCreatePlacementProvision = {};

  public static final String[] expectedObjectUpdatePlacementProvision = {};

  public static final String[] actualObjectUpdatePlacementProvision = {};

  public static final String[] actualAndExpectedObjectAudit = {
    "revisionDate", "delta.Position.tiers[*].tags[*].name", "delta.Position.tiers[*].tags[*].id"
  };

  public static final String[] actualAndExpectedObjectArchive = {"version"};

  public static String[] actualAndExpectedPositionClone = {
    "pid",
    "site.pid",
    "version",
    "tiers[*].tags[*].pid",
    "tags[*].pid",
    "tags[*].primaryId",
    "tags[*].site.pid",
    "tags[*].rtbProfile.pid",
    "tags[*].rules[*].pid",
    "tags[*].version",
    "tags[*].rtbProfile.id",
    "tags[*].rtbProfile.pid",
    "tags[*].rtbProfile.siteNameAlias",
    "tags[*].rtbProfile.version",
    "tags[*].position.pid",
    "position.pid",
    "rules[*].pid",
    "position.primaryId",
    "position.primaryName",
    "tiers[*].pid",
    "tiers[*].position.pid",
    "tiers[*].tags[*].pid",
    "tiers[*].version"
  };
}
