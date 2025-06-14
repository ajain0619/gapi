package com.nexage.app.services;

/** Defines an interface to manage campaigns in the system. */
public interface CampaignService {

  /**
   * Remove position from all campaign targets
   *
   * @param sitePid site Pid
   * @param positionName position name
   */
  void removePositionFromCampaignTargets(long sitePid, String positionName);
}
