package com.nexage.app.config;

import com.aol.crs.cdk.CdkConfig;
import com.aol.crs.cdk.SyncedConfig;
import com.nexage.app.config.sdk.CdkSdkConfigProperties;
import com.ssp.geneva.common.settings.service.GlobalConfigService;
import com.ssp.geneva.sdk.ckms.CkmsSdkClient;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class SyncedConfigImpl implements SyncedConfig {

  private final GlobalConfigService globalConfigService;
  private final CkmsSdkClient ckmsSdkClient;
  private final CdkSdkConfigProperties cdkSdkConfigProperties;

  public SyncedConfigImpl(
      GlobalConfigService globalConfigService,
      CkmsSdkClient ckmsSdkClient,
      CdkSdkConfigProperties cdkSdkConfigProperties) {
    this.globalConfigService = globalConfigService;
    this.ckmsSdkClient = ckmsSdkClient;
    this.cdkSdkConfigProperties = cdkSdkConfigProperties;
  }

  @Override
  public CdkConfig getSyncedConfig() {
    return new CdkConfigImpl(globalConfigService, ckmsSdkClient, cdkSdkConfigProperties);
  }
}
