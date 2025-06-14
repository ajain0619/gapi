package com.nexage.app.services;

/**
 * The methods listed here were originally at {@link SellerLimitService}. Code was decouple to avoid
 * certain circular dependencies.
 */
public interface RtbProfileLibrarySellerLimitService {

  /**
   * Check whether limits are enabled for user and number of bidder groups for a publisher is less
   * than the set limit.
   *
   * @see #isLimitEnabled(long)
   * @param publisher
   * @return
   */
  boolean canCreateBidderGroups(long publisher);

  /**
   * Check whether limits are enabled for user and number of block groups for a publisher is less
   * than the set limit.
   *
   * @see #isLimitEnabled(long)
   * @param publisher
   * @return
   */
  boolean canCreateBlockGroups(long publisher);

  /**
   * Check whether number of bidder groups for a publisher is less than the set limit.
   *
   * @param publisher
   * @return
   */
  int checkBidderLibrariesLimit(long publisher);

  /**
   * Check whether number of block groups for a publisher is less than the set limit.
   *
   * @param publisher
   * @return
   */
  int checkBlockLibrariesLimit(long publisher);

  /**
   * @param publisher
   * @return {@code true} if limit feature is enabled for the publisher and {@code false} otherwise
   */
  boolean isLimitEnabled(long publisher);
}
