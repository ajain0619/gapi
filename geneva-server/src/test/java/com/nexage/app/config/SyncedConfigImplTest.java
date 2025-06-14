package com.nexage.app.config;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.nexage.app.config.sdk.CdkSdkConfigProperties;
import com.ssp.geneva.common.settings.service.GlobalConfigService;
import com.ssp.geneva.sdk.ckms.CkmsSdkClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SyncedConfigImplTest {

  @Mock GlobalConfigService globalConfigService;
  @Mock CkmsSdkClient ckmsSdkClient;
  @Mock CdkSdkConfigProperties cdkSdkConfigProperties;
  @InjectMocks SyncedConfigImpl syncedConfigImpl;

  @Test
  void testGetSyncedConfig() {
    assertNotNull(syncedConfigImpl.getSyncedConfig());
  }
}
