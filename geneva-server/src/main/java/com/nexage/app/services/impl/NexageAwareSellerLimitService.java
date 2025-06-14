package com.nexage.app.services.impl;

import com.nexage.app.security.UserContext;
import com.nexage.app.services.RtbProfileLibrarySellerLimitService;
import com.nexage.app.services.SellerLimitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

/**
 * Limits are available for Non-Nexage sellers only, Small composition to avoid the same checks
 * around codebase
 */
@Primary
@Service("sellerLimitService")
public class NexageAwareSellerLimitService
    implements SellerLimitService, RtbProfileLibrarySellerLimitService {
  private final UserContext userContext;
  private final SellerLimitService sellerLimitService;
  private final RtbProfileLibrarySellerLimitService rtbSellerLimitService;

  @Autowired
  public NexageAwareSellerLimitService(
      UserContext loginUserContext,
      @Qualifier("sellerLimitServiceImpl") SellerLimitService sellerLimitServiceImpl,
      RtbProfileLibrarySellerLimitService rtbProfileLibrarySellerLimitService) {
    this.userContext = loginUserContext;
    this.sellerLimitService = sellerLimitServiceImpl;
    this.rtbSellerLimitService = rtbProfileLibrarySellerLimitService;
  }

  @Override
  public boolean canCreateSites(long publisher) {
    return userContext.isNexageUser() || sellerLimitService.canCreateSites(publisher);
  }

  @Override
  public boolean canCreatePositionsInSite(long publisher, long site) {
    return userContext.isNexageUser()
        || sellerLimitService.canCreatePositionsInSite(publisher, site);
  }

  @Override
  public boolean canCreateTagsInPosition(long publisher, long site, long position) {
    return userContext.isNexageUser()
        || sellerLimitService.canCreateTagsInPosition(publisher, site, position);
  }

  @Override
  public boolean canCreateCampaigns(long publisher) {
    return userContext.isNexageUser() || sellerLimitService.canCreateCampaigns(publisher);
  }

  @Override
  public boolean canCreateCreativesInCampaign(long publisher, long campaignPid) {
    return userContext.isNexageUser()
        || sellerLimitService.canCreateCreativesInCampaign(publisher, campaignPid);
  }

  @Override
  public boolean canCreateBidderGroups(long publisher) {
    return userContext.isNexageUser() || rtbSellerLimitService.canCreateBidderGroups(publisher);
  }

  @Override
  public boolean canCreateBlockGroups(long publisher) {
    return userContext.isNexageUser() || rtbSellerLimitService.canCreateBlockGroups(publisher);
  }

  @Override
  public boolean canCreateUsers(long publisher) {
    return userContext.isNexageUser() || sellerLimitService.canCreateUsers(publisher);
  }

  @Override
  public int checkSitesLimit(long publisher) {
    return sellerLimitService.checkSitesLimit(publisher);
  }

  @Override
  public int checkPositionsInSiteLimit(long publisher, long site) {
    return sellerLimitService.checkPositionsInSiteLimit(publisher, site);
  }

  @Override
  public int checkTagsInPositionLimit(long publisher, long site, long position) {
    return sellerLimitService.checkTagsInPositionLimit(publisher, site, position);
  }

  @Override
  public int checkCampaignsLimit(long publisher) {
    return sellerLimitService.checkCampaignsLimit(publisher);
  }

  @Override
  public int checkCreativesInCampaignLimit(long publisher, long campaignPid) {
    return sellerLimitService.checkCreativesInCampaignLimit(publisher, campaignPid);
  }

  @Override
  public int checkBidderLibrariesLimit(long publisher) {
    return rtbSellerLimitService.checkBidderLibrariesLimit(publisher);
  }

  @Override
  public int checkBlockLibrariesLimit(long publisher) {
    return rtbSellerLimitService.checkBlockLibrariesLimit(publisher);
  }

  @Override
  public int checkUsersLimit(long publisher) {
    return sellerLimitService.checkUsersLimit(publisher);
  }

  @Override
  public boolean isLimitEnabled(long publisher) {
    return !userContext.isNexageUser() && sellerLimitService.isLimitEnabled(publisher);
  }
}
