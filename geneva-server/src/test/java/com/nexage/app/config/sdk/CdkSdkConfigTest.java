package com.nexage.app.config.sdk;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.aol.crs.cdk.SyncedConfig;
import com.codahale.metrics.MetricRegistry;
import com.nexage.app.config.SyncedConfigImpl;
import com.nexage.app.services.crs.CdkClientResource;
import com.ssp.geneva.common.settings.service.GlobalConfigService;
import com.ssp.geneva.sdk.ckms.CkmsSdkClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class CdkSdkConfigTest {

  Boolean ckmsEnabled = true;
  String crsSsoClientId = "crsSsoClientId";
  Boolean crsSsoSecretEncrypted = true;
  String crsSsoSecret = "crsSsoSecret";
  String genevaMetricsJmxDomain = "genevaMetricsJmxDomain";
  String crsSsoCkmsKeyClient = "crsSsoCkmsKeyClient";
  String crsSsoCkmsKeySecret = "crsSsoCkmsKeySecret";
  String crsSsoCkmsKeyGroup = "crsSsoCkmsKeyGroup";

  @Mock GlobalConfigService globalConfigService;
  @Mock CkmsSdkClient ckmsSdkClient;
  @Mock CdkSdkConfigProperties cdkSdkConfigProperties;
  @Mock SyncedConfig syncedConfig;
  @Mock MetricRegistry metricRegistry;

  @InjectMocks CdkSdkConfig cdkSdkConfig;

  @BeforeEach
  public void before() {
    ReflectionTestUtils.setField(cdkSdkConfig, "ckmsEnabled", ckmsEnabled);
    ReflectionTestUtils.setField(cdkSdkConfig, "crsSsoClientId", crsSsoClientId);
    ReflectionTestUtils.setField(cdkSdkConfig, "crsSsoSecretEncrypted", crsSsoSecretEncrypted);
    ReflectionTestUtils.setField(cdkSdkConfig, "crsSsoSecret", crsSsoSecret);
    ReflectionTestUtils.setField(cdkSdkConfig, "genevaMetricsJmxDomain", genevaMetricsJmxDomain);
    ReflectionTestUtils.setField(cdkSdkConfig, "crsSsoCkmsKeyClient", crsSsoCkmsKeyClient);
    ReflectionTestUtils.setField(cdkSdkConfig, "crsSsoCkmsKeySecret", crsSsoCkmsKeySecret);
    ReflectionTestUtils.setField(cdkSdkConfig, "crsSsoCkmsKeyGroup", crsSsoCkmsKeyGroup);

    cdkSdkConfigProperties =
        new CdkSdkConfigProperties(
            ckmsEnabled,
            crsSsoClientId,
            crsSsoSecretEncrypted,
            crsSsoSecret,
            genevaMetricsJmxDomain,
            crsSsoCkmsKeyClient,
            crsSsoCkmsKeySecret,
            crsSsoCkmsKeyGroup);

    syncedConfig = new SyncedConfigImpl(globalConfigService, ckmsSdkClient, cdkSdkConfigProperties);
  }

  @Test
  void cdkSdkConfigProperties() {
    CdkSdkConfigProperties props = cdkSdkConfig.cdkSdkConfigProperties();
    assertNotNull(cdkSdkConfigProperties);
    assertEquals(ckmsEnabled, cdkSdkConfigProperties.getCkmsEnabled());
    assertEquals(crsSsoClientId, cdkSdkConfigProperties.getCrsSsoClientId());
    assertEquals(crsSsoSecretEncrypted, cdkSdkConfigProperties.getCrsSsoSecretEncrypted());
    assertEquals(crsSsoSecret, cdkSdkConfigProperties.getCrsSsoSecret());
    assertEquals(genevaMetricsJmxDomain, cdkSdkConfigProperties.getGenevaMetricsJmxDomain());
    assertEquals(crsSsoCkmsKeyClient, cdkSdkConfigProperties.getCrsSsoCkmsKeyClient());
    assertEquals(crsSsoCkmsKeySecret, cdkSdkConfigProperties.getCrsSsoCkmsKeySecret());
    assertEquals(crsSsoCkmsKeyGroup, cdkSdkConfigProperties.getCrsSsoCkmsKeyGroup());
  }

  @Test
  void syncedConfig() {
    SyncedConfig config =
        cdkSdkConfig.syncedConfig(globalConfigService, ckmsSdkClient, cdkSdkConfigProperties);
    assertNotNull(config);
    assertEquals(
        globalConfigService,
        (GlobalConfigService) ReflectionTestUtils.getField(config, "globalConfigService"));
    assertEquals(
        ckmsSdkClient, (CkmsSdkClient) ReflectionTestUtils.getField(config, "ckmsSdkClient"));
    assertEquals(
        cdkSdkConfigProperties,
        (CdkSdkConfigProperties) ReflectionTestUtils.getField(config, "cdkSdkConfigProperties"));
  }

  @Test
  void cdkClientResource() {
    CdkClientResource resource = cdkSdkConfig.cdkClientResource(syncedConfig, metricRegistry);
    assertNotNull(resource);
    assertEquals(
        syncedConfig, (SyncedConfig) ReflectionTestUtils.getField(resource, "syncedConfig"));
    assertEquals(
        metricRegistry, (MetricRegistry) ReflectionTestUtils.getField(resource, "metricRegistry"));
  }
}
