package com.nexage.app.services.impl;

import com.nexage.admin.core.enums.GlobalConfigProperty;
import com.nexage.app.services.LimitService;
import com.ssp.geneva.common.settings.service.GlobalConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("limitService")
public class LimitServiceImpl implements LimitService {

  private final GlobalConfigService globalConfigService;

  @Autowired
  public LimitServiceImpl(GlobalConfigService globalConfigService) {
    this.globalConfigService = globalConfigService;
  }

  @Override
  public Integer getGlobalSiteLimit() {
    return globalConfigService.getIntegerValue(GlobalConfigProperty.SELLER_SITES_LIMIT);
  }

  @Override
  public Integer getGlobalPositionsPerSiteLimit() {
    return globalConfigService.getIntegerValue(GlobalConfigProperty.SELLER_POSITIONS_SITE_LIMIT);
  }

  @Override
  public Integer getGlobalTagsPerPositionLimit() {
    return globalConfigService.getIntegerValue(GlobalConfigProperty.SELLER_TAGS_POSITION_LIMIT);
  }

  @Override
  public Integer getGlobalCampaignsLimit() {
    return globalConfigService.getIntegerValue(GlobalConfigProperty.SELLER_CAMPAIGNS_LIMIT);
  }

  @Override
  public Integer getGlobalCreativesPerCampaignsLimit() {
    return globalConfigService.getIntegerValue(
        GlobalConfigProperty.SELLER_CREATIVES_CAMPAIGN_LIMIT);
  }

  @Override
  public Integer getGlobalBidderLibrariesLimit() {
    return globalConfigService.getIntegerValue(GlobalConfigProperty.SELLER_BIDDER_LIBRARIES_LIMIT);
  }

  @Override
  public Integer getGlobalBlockLibrariesLimit() {
    return globalConfigService.getIntegerValue(GlobalConfigProperty.SELLER_BLOCK_LIBRARIES_LIMIT);
  }

  @Override
  public Integer getGlobalUsersLimit() {
    return globalConfigService.getIntegerValue(GlobalConfigProperty.SELLER_USERS_LIMIT);
  }
}
