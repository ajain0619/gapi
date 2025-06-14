package com.nexage.app.config;

import com.aol.crs.cdk.CdkConfig;
import com.aol.crs.cdk.cache.AbstractLocalCache;
import com.aol.crs.cdk.cache.GuavaBasedLocalCache;
import com.nexage.admin.core.enums.GlobalConfigProperty;
import com.nexage.admin.core.util.EncryptionUtil;
import com.nexage.app.config.sdk.CdkSdkConfigProperties;
import com.ssp.geneva.common.settings.service.GlobalConfigService;
import com.ssp.geneva.sdk.ckms.CkmsSdkClient;
import com.ssp.geneva.sdk.ckms.provider.CkmsProvider;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.BooleanUtils;

@Log4j2
public class CdkConfigImpl implements CdkConfig {

  private String crsSsoEndpoint;
  private String crsSsoClientId;
  private String crsSsoSecret;
  private int crsSsoRefreshFrequency;
  private String crsReadApiEndpoint;
  private String crsWriteApiEndpoint;
  private String genevaMetricsJmxDomain;

  public CdkConfigImpl(
      GlobalConfigService globalConfigService,
      CkmsSdkClient ckmsSdkClient,
      CdkSdkConfigProperties cdkSdkConfigProperties) {
    loadGlobalParameters(globalConfigService);
    loadSpecificParameters(ckmsSdkClient, cdkSdkConfigProperties);
    logNonSensitiveConfig();
  }

  @Override
  public String getSsoApiEndpoint() {
    return crsSsoEndpoint;
  }

  @Override
  public int getSsoApiRefreshIntervalMinutes() {
    return crsSsoRefreshFrequency;
  }

  @Override
  public String getSsoApiClientId() {
    return crsSsoClientId;
  }

  @Override
  public String getSsoApiClientSecret() {
    return crsSsoSecret;
  }

  @Override
  public String getCrsApiEndpointRead() {
    return crsReadApiEndpoint;
  }

  @Override
  public String getCrsApiEndpointWrite() {
    return crsWriteApiEndpoint;
  }

  @Override
  public long getMalwareStatusExpirationTimeoutMillis() {
    return 0;
  }

  @Override
  public long getSslStatusExpirationTimeoutMillis() {
    return 0;
  }

  @Override
  public long getRefreshStatusExpirationTimeoutMillis() {
    return 0;
  }

  @Override
  public long getLocalCacheOnHeapCount() {
    return 0;
  }

  @Override
  public long getLocalCacheOffHeapMbSize() {
    return 0;
  }

  @Override
  public long getLocalCacheDiskMbSize() {
    return 0;
  }

  @Override
  public String getLocalCacheDirPath() {
    return null;
  }

  @Override
  public long getLocalCacheExpireAfterAccessMillis() {
    return 0;
  }

  @Override
  public boolean isLocalCacheEnabled() {
    return false;
  }

  @Override
  public int getLockStripesCount() {
    return 1;
  }

  @Override
  public boolean isColdStartEnabled() {
    return false;
  }

  @Override
  public int getColdStartTotalSize() {
    return 0;
  }

  @Override
  public int getColdStartBatchSize() {
    return 0;
  }

  @Override
  public int getColdStartTimeFrameInDays() {
    return 0;
  }

  @Override
  public Class<? extends AbstractLocalCache> getLocalCacheImplementation() {
    return GuavaBasedLocalCache.class;
  }

  @Override
  public String getMetricRegistryPrefix() {
    return genevaMetricsJmxDomain;
  }

  @Override
  public String getCacheInitDirectory() {
    return null;
  }

  @Override
  public String getAwsRegion() {
    return null;
  }

  @Override
  public String getAwsBucket() {
    return null;
  }

  @Override
  public String getDatadogHost() {
    return "localhost";
  }

  @Override
  public boolean isNativeAdSubmissionEnabled() {
    return false;
  }

  @Override
  public int getDatadogPort() {
    return 8125;
  }

  @Override
  public boolean isDspCountReportingEnabled() {
    return false;
  }

  private void loadSpecificParameters(
      CkmsSdkClient ckmsSdkClient, CdkSdkConfigProperties cdkSdkConfigProperties) {
    genevaMetricsJmxDomain = cdkSdkConfigProperties.getGenevaMetricsJmxDomain();
    if (BooleanUtils.toBoolean(cdkSdkConfigProperties.getCkmsEnabled())) {
      loadCrsSsoCredentials(ckmsSdkClient.getCkmsProvider(), cdkSdkConfigProperties);
    } else {
      loadCrsSsoCredentials(cdkSdkConfigProperties);
    }
  }

  private void loadGlobalParameters(GlobalConfigService globalConfigService) {
    crsSsoEndpoint = globalConfigService.getStringValue(GlobalConfigProperty.CRS_SSO_ENDPOINT);
    crsSsoRefreshFrequency =
        globalConfigService.getIntegerValue(GlobalConfigProperty.CRS_SSO_REFRESH_FREQUENCY);
    crsReadApiEndpoint =
        globalConfigService.getStringValue(GlobalConfigProperty.CRS_READ_API_ENDPOINT);
    crsWriteApiEndpoint =
        globalConfigService.getStringValue(GlobalConfigProperty.CRS_WRITE_API_ENDPOINT);
  }

  private void loadCrsSsoCredentials(CdkSdkConfigProperties cdkSdkConfigProperties) {
    crsSsoClientId = cdkSdkConfigProperties.getCrsSsoClientId();
    if (cdkSdkConfigProperties.getCrsSsoSecretEncrypted()) {
      crsSsoSecret = EncryptionUtil.decrypt(cdkSdkConfigProperties.getCrsSsoSecret());
    } else {
      crsSsoSecret = cdkSdkConfigProperties.getCrsSsoSecret();
    }
  }

  private void loadCrsSsoCredentials(
      CkmsProvider ckmsProvider, CdkSdkConfigProperties cdkSdkConfigProperties) {
    try {
      crsSsoClientId =
          ckmsProvider.getSecret(
              cdkSdkConfigProperties.getCrsSsoCkmsKeyClient(),
              cdkSdkConfigProperties.getCrsSsoCkmsKeyGroup());
      crsSsoSecret =
          ckmsProvider.getSecret(
              cdkSdkConfigProperties.getCrsSsoCkmsKeySecret(),
              cdkSdkConfigProperties.getCrsSsoCkmsKeyGroup());
    } catch (Exception e) {
      log.error("CKMS exception details", e);
    }
  }

  private void logNonSensitiveConfig() {
    log.debug("crsSsoEndpoint={}", crsSsoEndpoint);
    log.debug("crsSsoRefreshFrequency={}", crsSsoRefreshFrequency);
    log.debug("crsReadApiEndpoint={}", crsReadApiEndpoint);
    log.debug("crsWriteApiEndpoint={}", crsWriteApiEndpoint);
    log.debug("genevaMetricsJmxDomain={}", genevaMetricsJmxDomain);
  }
}
