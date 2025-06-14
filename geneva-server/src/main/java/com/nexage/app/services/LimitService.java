package com.nexage.app.services;

public interface LimitService {

  Integer getGlobalSiteLimit();

  Integer getGlobalPositionsPerSiteLimit();

  Integer getGlobalTagsPerPositionLimit();

  Integer getGlobalCampaignsLimit();

  Integer getGlobalCreativesPerCampaignsLimit();

  Integer getGlobalBidderLibrariesLimit();

  Integer getGlobalBlockLibrariesLimit();

  Integer getGlobalUsersLimit();
}
