package com.nexage.app.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

import com.aol.crs.cdk.cache.GuavaBasedLocalCache;
import com.nexage.admin.core.enums.GlobalConfigProperty;
import com.nexage.app.config.sdk.CdkSdkConfigProperties;
import com.ssp.geneva.common.settings.service.GlobalConfigService;
import com.ssp.geneva.sdk.ckms.CkmsSdkClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CdkConfigImplTest {

  private static final String crsSsoEndpoint = "crsSsoEndpoint";
  private static final String crsSsoClientId = "crsSsoClientId";
  private static final String crsSsoSecret = "crsSsoSecret";
  private static final int crsSsoRefreshFrequency = 123;
  private static final String crsReadApiEndpoint = "crsReadApiEndpoint";
  private static final String crsWriteApiEndpoint = "crsReadApiEndpoint";
  private static final String genevaMetricsJmxDomain = "genevaMetricsJmxDomain";

  @Mock GlobalConfigService globalConfigService;
  @Mock CkmsSdkClient ckmsSdkClient;
  @Mock CdkSdkConfigProperties cdkSdkConfigProperties;
  @InjectMocks CdkConfigImpl cdkConfigImpl;

  @BeforeEach
  void begin() {
    when(globalConfigService.getStringValue(GlobalConfigProperty.CRS_SSO_ENDPOINT))
        .thenReturn(crsSsoEndpoint);
    when(globalConfigService.getIntegerValue(GlobalConfigProperty.CRS_SSO_REFRESH_FREQUENCY))
        .thenReturn(crsSsoRefreshFrequency);
    when(cdkSdkConfigProperties.getCkmsEnabled()).thenReturn(false);
    when(cdkSdkConfigProperties.getCrsSsoClientId()).thenReturn(crsSsoClientId);
    when(cdkSdkConfigProperties.getCrsSsoSecretEncrypted()).thenReturn(false);
    when(cdkSdkConfigProperties.getCrsSsoSecret()).thenReturn(crsSsoSecret);
    when(globalConfigService.getStringValue(GlobalConfigProperty.CRS_READ_API_ENDPOINT))
        .thenReturn(crsReadApiEndpoint);
    when(globalConfigService.getStringValue(GlobalConfigProperty.CRS_WRITE_API_ENDPOINT))
        .thenReturn(crsWriteApiEndpoint);
    when(cdkSdkConfigProperties.getGenevaMetricsJmxDomain()).thenReturn(genevaMetricsJmxDomain);

    cdkConfigImpl = new CdkConfigImpl(globalConfigService, ckmsSdkClient, cdkSdkConfigProperties);
  }

  @Test
  void testGetSsoApiEndpoint() {
    assertEquals(crsSsoEndpoint, cdkConfigImpl.getSsoApiEndpoint());
  }

  @Test
  void testGetSsoApiRefreshIntervalMinutes() {
    assertEquals(crsSsoRefreshFrequency, cdkConfigImpl.getSsoApiRefreshIntervalMinutes());
  }

  @Test
  void testGetSsoApiClientId() {
    assertEquals(crsSsoClientId, cdkConfigImpl.getSsoApiClientId());
  }

  @Test
  void testGetSsoApiClientSecret() {
    assertEquals(crsSsoSecret, cdkConfigImpl.getSsoApiClientSecret());
  }

  @Test
  void testGetCrsApiEndpointRead() {
    assertEquals(crsReadApiEndpoint, cdkConfigImpl.getCrsApiEndpointRead());
  }

  @Test
  void testGetCrsApiEndpointWrite() {
    assertEquals(crsWriteApiEndpoint, cdkConfigImpl.getCrsApiEndpointWrite());
  }

  @Test
  void testGetMalwareStatusExpirationTimeoutMillis() {
    assertEquals(0, cdkConfigImpl.getMalwareStatusExpirationTimeoutMillis());
  }

  @Test
  void testGetSslStatusExpirationTimeoutMillis() {
    assertEquals(0, cdkConfigImpl.getSslStatusExpirationTimeoutMillis());
  }

  @Test
  void testGetRefreshStatusExpirationTimeoutMillis() {
    assertEquals(0, cdkConfigImpl.getRefreshStatusExpirationTimeoutMillis());
  }

  @Test
  void testGetLocalCacheOnHeapCount() {
    assertEquals(0, cdkConfigImpl.getLocalCacheOnHeapCount());
  }

  @Test
  void testGetLocalCacheOffHeapMbSize() {
    assertEquals(0, cdkConfigImpl.getLocalCacheOffHeapMbSize());
  }

  @Test
  void testGetLocalCacheDiskMbSize() {
    assertEquals(0, cdkConfigImpl.getLocalCacheDiskMbSize());
  }

  @Test
  void testGetLocalCacheDirPath() {
    assertNull(cdkConfigImpl.getLocalCacheDirPath());
  }

  @Test
  void testGetLocalCacheExpireAfterAccessMillis() {
    assertEquals(0, cdkConfigImpl.getLocalCacheExpireAfterAccessMillis());
  }

  @Test
  void testIsLocalCacheEnabled() {
    assertFalse(cdkConfigImpl.isLocalCacheEnabled());
  }

  @Test
  void testGetLockStripesCount() {
    assertEquals(1, cdkConfigImpl.getLockStripesCount());
  }

  @Test
  void testIsColdStartEnabled() {
    assertFalse(cdkConfigImpl.isColdStartEnabled());
  }

  @Test
  void testGetColdStartTotalSize() {
    assertEquals(0, cdkConfigImpl.getColdStartTotalSize());
  }

  @Test
  void testGetColdStartBatchSize() {
    assertEquals(0, cdkConfigImpl.getColdStartBatchSize());
  }

  @Test
  void testGetColdStartTimeFrameInDays() {
    assertEquals(0, cdkConfigImpl.getColdStartTimeFrameInDays());
  }

  @Test
  void testGetLocalCacheImplementation() {
    assertEquals(GuavaBasedLocalCache.class, cdkConfigImpl.getLocalCacheImplementation());
  }

  @Test
  void testGetMetricRegistryPrefix() {
    assertEquals(genevaMetricsJmxDomain, cdkConfigImpl.getMetricRegistryPrefix());
  }

  @Test
  void testGetCacheInitDirectory() {
    assertNull(cdkConfigImpl.getCacheInitDirectory());
  }

  @Test
  void testGetAwsRegion() {
    assertNull(cdkConfigImpl.getAwsRegion());
  }

  @Test
  void testGetAwsBucket() {
    assertNull(cdkConfigImpl.getAwsBucket());
  }

  @Test
  void testGetDatadogHost() {
    assertEquals("localhost", cdkConfigImpl.getDatadogHost());
  }

  @Test
  void testGetDatadogPort() {
    assertEquals(8125, cdkConfigImpl.getDatadogPort());
  }

  @Test
  void testIsDspCountReportingEnabled() {
    assertFalse(cdkConfigImpl.isDspCountReportingEnabled());
  }

  @Test
  void testIsNativeAdSubmissionEnabled() {
    assertFalse(cdkConfigImpl.isNativeAdSubmissionEnabled());
  }
}
