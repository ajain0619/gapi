package com.nexage.app.services;

public interface SellerLimitService {

  /**
   * Check whether limits are enabled for user and number of sites for a publisher is less than the
   * set limit.
   *
   * @see #isLimitEnabled(long)
   * @param publisher
   * @return {@code true} if number of active sites for publisher is less than the limit else {@code
   *     false}
   */
  boolean canCreateSites(long publisher);

  /**
   * Check whether limits are enabled for user and number positions per site for a publisher is less
   * than the set limit.
   *
   * @see #isLimitEnabled(long)
   * @param publisher
   * @param site
   * @return
   */
  boolean canCreatePositionsInSite(long publisher, long site);

  /**
   * Check whether limits are enabled for user and number of tags per position for a publisher is
   * less than the set limit.
   *
   * @see #isLimitEnabled(long)
   * @param publisher
   * @param site
   * @param position
   * @return
   */
  boolean canCreateTagsInPosition(long publisher, long site, long position);

  /**
   * Check whether limits are enabled for user and number of campaigns for a publisher is less than
   * the set limit.
   *
   * @param publisher
   * @return
   * @see #isLimitEnabled(long)
   */
  boolean canCreateCampaigns(long publisher);

  /**
   * Check whether limits are enabled for user and number of creatives per campaign for a publisher
   * is less than the set limit.
   *
   * @see #isLimitEnabled(long)
   * @param publisher
   * @param campaignPid
   * @return
   */
  boolean canCreateCreativesInCampaign(long publisher, long campaignPid);

  /**
   * Check whether limits are enabled for user and number of users for a publisher is less than the
   * set limit.
   *
   * @see #isLimitEnabled(long)
   * @param publisher
   * @return
   */
  boolean canCreateUsers(long publisher);

  /**
   * Check whether number of sites for a publisher is less than the set limit.
   *
   * @param publisher
   * @return {@code true} if number of active sites for publisher is less than the limit else {@code
   *     false}
   */
  int checkSitesLimit(long publisher);

  /**
   * Check whether number positions per site for a publisher is less than the set limit.
   *
   * @param publisher
   * @param site
   * @return
   */
  int checkPositionsInSiteLimit(long publisher, long site);

  /**
   * Check whether number of tags per position for a publisher is less than the set limit.
   *
   * @param publisher
   * @param site
   * @param position
   * @return
   */
  int checkTagsInPositionLimit(long publisher, long site, long position);

  /**
   * Check whether number of campaigns for a publisher is less than the set limit.
   *
   * @param publisher
   * @return
   */
  int checkCampaignsLimit(long publisher);

  /**
   * Check whether number of creatives per campaign for a publisher is less than the set limit.
   *
   * @param publisher
   * @param campaignPid
   * @return
   */
  int checkCreativesInCampaignLimit(long publisher, long campaignPid);

  /**
   * Check whether number of users for a publisher is less than the set limit.
   *
   * @param publisher
   * @return
   */
  int checkUsersLimit(long publisher);

  /**
   * @param publisher
   * @return {@code true} if limit feature is enabled for the publisher and {@code false} otherwise
   */
  boolean isLimitEnabled(long publisher);
}
