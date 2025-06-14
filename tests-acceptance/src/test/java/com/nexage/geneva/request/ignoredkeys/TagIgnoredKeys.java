package com.nexage.geneva.request.ignoredkeys;

public class TagIgnoredKeys {

  public static final String[] expectedObjectCreateNonExchangeTagNonPss = {
    "pid",
    "id",
    "buyerName",
    "rules[*].pid",
    "rules[*].tagPid",
    "currentDealTerm.tagPid",
    "currentDealTerm.pid",
    "currentDealTerm.effectiveDate",
    "currentDealTerm.hasDealTermChanged"
  };

  public static final String[] actualObjectCreateNonExchangeTagNonPss = {
    "pid",
    "id",
    "currentDealTerm.tagPid",
    "currentDealTerm.pid",
    "currentDealTerm.version",
    "currentDealTerm.effectiveDate",
    "currentDealTerm.hasDealTermChanged",
    "version",
    "rtbDescription",
    "videoLinearity",
    "isInterstitial",
    "adSize",
    "buyerName",
    "screenLocation",
    "isVideoAllowed",
    "width",
    "height",
    "videoSupport",
    "rules[*].pid",
    "rules[*].tagPid",
    "rules[*].version"
  };

  public static final String[] expectedObjectCreateExchangeTagNonPss = {
    "rtbProfile.pid", "rtbProfile.defaultReserve", "rtbProfile.lowReserve", "tag->pid",
    "tag.primaryId", "tag.id", "tag.currentDealTerm.tagPid", "tag.currentDealTerm.effectiveDate",
    "tag.rtbFloor", "tag.buyerName", "tag.rules[*].pid", "tag.rules[*].tagPid"
  };

  public static final String[] actualObjectCreateExchangeTagNonPss = {
    "rtbProfile.libraryPids",
    "rtbProfile.pid",
    "rtbProfile.id",
    "rtbProfile.defaultReserve",
    "rtbProfile.creationDate",
    "rtbProfile.version",
    "rtbProfile.lastUpdate",
    "rtbProfile.lowReserve",
    "tag.pid",
    "tag.primaryId",
    "tag.id",
    "tag.height",
    "tag.width",
    "tag.currentDealTerm.tagPid",
    "tag.currentDealTerm.version",
    "tag.currentDealTerm.effectiveDate",
    "tag.version",
    "tag.position",
    "tag.rtbDescription",
    "tag.videoLinearity",
    "tag.isInterstitial",
    "tag.adSize",
    "tag.rtbFloor",
    "tag.buyerName",
    "tag.screenLocation",
    "tag.isVideoAllowed",
    "tag.videoSupport",
    "tag.rules[*].pid",
    "tag.rules[*].tagPid",
    "tag.rules[*].version"
  };

  public static final String[] expectedObjectUpdateNonExchangeTagNonPss = {
    "rules[*].pid",
    "rules[*].version",
    "version",
    "width",
    "height",
    "videoSupport",
    "currentDealTerm.effectiveDate",
    "currentDealTerm.hasDealTermChanged",
    "currentDealTerm.pid"
  };

  public static final String[] actualObjectUpdateNonExchangeTagNonPss = {
    "rules[*].pid",
    "rules[*].version",
    "version",
    "rules[*].version",
    "videoLinearity",
    "videoSupport",
    "width",
    "height",
    "currentDealTerm.effectiveDate",
    "currentDealTerm.hasDealTermChanged",
    "currentDealTerm.pid"
  };

  public static final String[] expectedObjectUpdateExchangeTagNonPss = {
    "rtbProfile.lastUpdate",
    "tag.rules[*].pid",
    "tag.version",
    "tag.rules[*].version",
    "tag.currentDealTerm.pid",
    "tag.currentDealTerm.effectiveDate",
    "tag.currentDealTerm.hasDealTermChanged",
    "rtbProfile.defaultReserve",
    "rtbProfile.lowReserve",
    "tag->height",
    "tag->width",
    "tag->videoSupport",
  };

  public static final String[] actualObjectUpdateExchangeTagNonPss = {
    "rtbProfile.lastUpdate",
    "tag.rules.pid",
    "tag.rules.version",
    "tag.version",
    "tag.currentDealTerm.pid",
    "tag.currentDealTerm.effectiveDate",
    "tag.currentDealTerm.hasDealTermChanged",
    "tag.videoLinearity",
    "rtbProfile.defaultReserve",
    "rtbProfile.lowReserve",
    "tag->height",
    "tag->width",
    "tag->videoSupport",
  };

  public static final String[] expectedObjectCreateRtbTagPss = {
    "pid", "selfServeEnablement", "version", "publisherPid",
    "rtbProfile.pid", "rtbProfile.id", "rtbProfile.version", "rtbProfile.bidderFilters"
  };

  public static final String[] actualObjectCreateRtbTagPss = {
    "pid",
    "primaryId",
    "version",
    "buyer.secondaryNameRequired",
    "buyer.primaryIdRequired",
    "buyer.primaryNameRequired",
    "buyer.secondaryIdRequired",
    "tagType",
    "rtbProfile.pid",
    "rtbProfile.id",
    "rtbProfile.version",
    "rules[*].pid",
    "rules[*].version"
  };

  public static final String[] expectedObjectCreateMedTagPss = {
    "pid", "selfServeEnablement", "version", "publisherPid"
  };

  public static final String[] actualObjectCreateMedTagPss = {
    "pid",
    "version",
    "buyer.secondaryNameRequired",
    "buyer.primaryIdRequired",
    "buyer.primaryNameRequired",
    "buyer.secondaryIdRequired",
    "tagType",
    "rules[*].pid",
    "rules[*].version"
  };

  public static final String[] expectedObjectUpdateMedTagPss = {
    "publisherPid", "version", "currentDealTerm"
  };

  public static final String[] actualObjectUpdateMedTagPss = {
    "buyer.secondaryNameRequired",
    "buyer.primaryIdRequired",
    "buyer.primaryNameRequired",
    "buyer.secondaryIdRequired",
    "version",
    "rules[*].pid",
    "rules[*].version"
  };

  public static final String[] expectedObjectUpdateRtbTagPss = {
    "publisherPid",
    "version",
    "currentDealTerm",
    "rules[*].pid",
    "rules[*].version",
    "buyer.primaryNameRequired",
    "buyer.secondaryIdRequired",
    "version",
    "rules[*].pid",
    "buyer.secondaryNameRequired",
    "buyer.primaryIdRequired",
    "rtbProfile.rtbProfileBidders[*].pid",
    "rtbProfile.rtbProfileBidders[*].version"
  };

  public static final String[] actualObjectUpdateRtbTagPss = {
    "buyer.secondaryNameRequired",
    "buyer.primaryIdRequired",
    "buyer.primaryNameRequired",
    "buyer.secondaryIdRequired",
    "version",
    "rules[*].pid",
    "rules[*].version",
    "rtbProfile.rtbProfileBidders[*].pid",
    "rtbProfile.rtbProfileBidders[*].version"
  };

  public static final String[] actualAndExpectedObjectAudit = {
    "revisionDate",
    "delta->Tag->name",
    "delta->Tag->id",
    "delta->Tag->primaryId",
    "delta->Tag->rules"
  };

  public static final String[] actualAndExpectedObjectTagArchive = {"version"};

  public static final String[] expectedObjectCloneRtbTagPss = {
    "pid",
    "selfServeEnablement",
    "version",
    "publisherPid",
    "site.pid",
    "position.pid",
    "rtbProfile.pid",
    "rtbProfile.id",
    "rtbProfile.version",
    "rtbProfile.bidderFilters"
  };

  public static final String[] actualObjectCloneRtbTagPss = {
    "pid",
    "primaryId",
    "version",
    "site.pid",
    "position.pid",
    "buyer.secondaryNameRequired",
    "buyer.primaryIdRequired",
    "buyer.primaryNameRequired",
    "buyer.secondaryIdRequired",
    "tagType",
    "rtbProfile.pid",
    "rtbProfile.id",
    "rtbProfile.version",
    "rules[*].pid",
    "rules[*].version"
  };

  public static final String[] expectedObjectCloneMedTagPss = {
    "pid", "selfServeEnablement", "version", "publisherPid", "site.pid", "position.pid"
  };

  public static final String[] actualObjectCloneMedTagPss = {
    "pid",
    "version",
    "site.pid",
    "position.pid",
    "buyer.secondaryNameRequired",
    "buyer.primaryIdRequired",
    "buyer.primaryNameRequired",
    "buyer.secondaryIdRequired",
    "tagType",
    "rules[*].pid",
    "rules[*].version"
  };
}
