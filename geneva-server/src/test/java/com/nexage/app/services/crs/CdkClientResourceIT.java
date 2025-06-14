package com.nexage.app.services.crs;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

import com.aol.crs.cdk.CdkClient;
import com.aol.crs.cdk.CdkConfig;
import com.aol.crs.cdk.SyncedConfig;
import com.aol.crs.cdk.cache.GuavaBasedLocalCache;
import com.codahale.metrics.MetricRegistry;
import com.nexage.app.crs.CrsMockHelper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(
    locations = {
      "classpath:application-context-test.xml",
      "classpath:application-context-test-crs.xml"
    })
class CdkClientResourceIT {

  @Mock CdkConfig cdkConfig;
  @Mock SyncedConfig syncedConfig;
  private CdkClientResource cdkClientResource;
  @Autowired private CrsMockHelper crsMockHelper;
  @Autowired private MetricRegistry metricRegistry;

  @BeforeEach
  public void setup() throws Exception {
    openMocks(this);
    this.cdkClientResource = new CdkClientResource(syncedConfig, metricRegistry);
    when(syncedConfig.getSyncedConfig()).thenReturn(cdkConfig);
    when(syncedConfig.getSyncedConfig().getSsoApiEndpoint()).thenReturn("http://127.0.0.1:18096");
    when(syncedConfig.getSyncedConfig().getSsoApiRefreshIntervalMinutes()).thenReturn(10);
    when(syncedConfig.getSyncedConfig().getSsoApiClientId()).thenReturn("ssoApiCliendId");
    when(syncedConfig.getSyncedConfig().getSsoApiClientSecret()).thenReturn("crsSsoSecret");
    when(syncedConfig.getSyncedConfig().getCrsApiEndpointRead())
        .thenReturn("http://127.0.0.1:18097/");
    when(syncedConfig.getSyncedConfig().getLockStripesCount()).thenReturn(1);
    when(syncedConfig.getSyncedConfig().getMetricRegistryPrefix()).thenReturn("geneva");
    when(syncedConfig.getSyncedConfig().getDatadogHost()).thenReturn("localhost");
    when(syncedConfig.getSyncedConfig().getDatadogPort()).thenReturn(8);
    when(syncedConfig.getSyncedConfig().isCRSGetRequestsEnabled()).thenReturn(true);

    Class localCacheImplementation = GuavaBasedLocalCache.class;
    when(syncedConfig.getSyncedConfig().getLocalCacheImplementation())
        .thenReturn(localCacheImplementation);

    cdkClientResource.init();
    crsMockHelper.reset();
  }

  @AfterEach
  public void close() {
    cdkClientResource.close();
  }

  @Test
  void shouldReturnClient() {
    assertNotNull(cdkClientResource.getCdkClient());
  }

  @Test
  void shouldCreateClientAfterClose() {
    CdkClient cdkClient = cdkClientResource.getCdkClient();
    assertNotNull(cdkClient);
    cdkClientResource.destroyAndReloadCdkClient();
    CdkClient cdkClientNew = cdkClientResource.getCdkClient();
    assertNotNull(cdkClientNew);
    assertNotEquals(cdkClient, cdkClientNew);
  }

  @Test
  void shouldReloadClient() {
    CdkClient cdkClient = cdkClientResource.getCdkClient();
    assertNotNull(cdkClient);
    cdkClientResource.reloadCdkClient();
    CdkClient cdkClientNew = cdkClientResource.getCdkClient();
    assertNotNull(cdkClientNew);
    assertNotEquals(cdkClient, cdkClientNew);
  }
}
