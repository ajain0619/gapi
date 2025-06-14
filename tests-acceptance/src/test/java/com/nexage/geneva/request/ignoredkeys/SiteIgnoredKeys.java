package com.nexage.geneva.request.ignoredkeys;

/**
 * Class with keys which should be ignored during comparing responses of different operations with
 * sites
 */
public class SiteIgnoredKeys {
  public static final String[] expectedObjectCreate = {
    "currentDealTerm", "dcn", "revenueLaunchDate", "maskIP"
  };
  public static final String[] actualObjectCreate = {
    "rtbProfiles",
    "pid",
    "id",
    "defaultPositions",
    "maskIP",
    "positions",
    "currentDealTerm",
    "version",
    "tags",
    "dcn"
  };
  public static final String[] expectedObjectCreatePss = {"pid", "name"};
  public static final String[] actualObjectCreatePss = {"pid", "version", "publisher", "name"};

  public static final String[] expectedObjectCreateProvision = {};
  public static final String[] actualObjectCreateProvision = {};

  public static final String[] expectedObjectUpdate = {
    "id", "dcn", "version", "currentDealTerm", "positions", "name"
  };
  public static final String[] actualObjectUpdate = {
    "version", "currentDealTerm", "positions", "name", "dcn", "id"
  };
  public static final String[] actualAndExpectedObjectUpdatePss = {"version"};

  public static final String[] actualAndExpectedObjectGet = {"positions"};

  public static final String[] actualAndExpectedObjectPssGet = {"version"};

  public static final String[] actualAndExpectedObjectAudit = {
    "revisionDate",
    "delta->SiteDTO->tags->name",
    "delta->SiteDTO->tags->id",
    "delta->SiteDTO->tags->primaryId",
    "delta->SiteDTO->rtbProfiles->id",
    "delta->SiteDTO->lastUpdate",
    "delta->SiteDTO->rtbProfiles->lastUpdate",
    "delta->SiteDTO->rtbProfiles->creationDate",
    "delta->SiteDTO->dealTerms->effectiveDate",
    "delta->SiteDTO->tags->effectiveDate",
    "delta->SiteDTO->tags->nexageRevenueShare",
    "delta->SiteDTO->tags->nexageRevenueShareOverride",
    "delta->SiteDTO->tags->pid",
    "delta->SiteDTO->tags->rtbFee",
    "delta->SiteDTO->tags->rtbFeeOverride",
    "delta->SiteDTO->positions->tiers->tags->id",
    "delta->SiteDTO->positions->tiers->tags->effectiveDate",
    "delta->SiteDTO->pid",
    "delta->SiteDTO->tags",
    "delta->SiteDTO->tags->rules",
    "delta->SiteDTO->rtbFee",
    "delta->SiteDTO->effectiveDate",
    "delta->SiteDTO->nexageRevenueShare"
  };
}
