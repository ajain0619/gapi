package com.nexage.app.config.sdk;

import com.aol.crs.cdk.SyncedConfig;
import com.codahale.metrics.MetricRegistry;
import com.nexage.app.config.CdkConfigImpl;
import com.nexage.app.config.SyncedConfigImpl;
import com.nexage.app.services.crs.CdkClientResource;
import com.ssp.geneva.common.settings.service.GlobalConfigService;
import com.ssp.geneva.sdk.ckms.CkmsSdkClient;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Log4j2
@Configuration
public class CdkSdkConfig {

  @Value("${ckms.enabled:false}")
  private Boolean ckmsEnabled;

  @Value("${crs.sso.clientId}")
  private String crsSsoClientId;

  @Value("${crs.sso.secret.encrypted:false}")
  private Boolean crsSsoSecretEncrypted;

  @Value("${crs.sso.secret}")
  private String crsSsoSecret;

  @Value("${geneva.metrics.jmx.domain}")
  private String genevaMetricsJmxDomain;

  @Value("${crs.sso.ckms.key.client}")
  private String crsSsoCkmsKeyClient;

  @Value("${crs.sso.ckms.key.secret}")
  private String crsSsoCkmsKeySecret;

  @Value("${crs.sso.ckms.key.group}")
  private String crsSsoCkmsKeyGroup;

  @Bean("cdkSdkConfigProperties")
  @ConditionalOnClass({CdkSdkConfigProperties.class, CdkConfigImpl.class})
  public CdkSdkConfigProperties cdkSdkConfigProperties() {
    log.info("Creating bean for class {}", this.getClass());
    return CdkSdkConfigProperties.builder()
        .ckmsEnabled(ckmsEnabled)
        .crsSsoClientId(crsSsoClientId)
        .crsSsoSecretEncrypted(crsSsoSecretEncrypted)
        .crsSsoSecret(crsSsoSecret)
        .crsSsoCkmsKeyClient(crsSsoCkmsKeyClient)
        .crsSsoCkmsKeySecret(crsSsoCkmsKeySecret)
        .crsSsoCkmsKeyGroup(crsSsoCkmsKeyGroup)
        .genevaMetricsJmxDomain(genevaMetricsJmxDomain)
        .build();
  }

  @Bean("syncedConfig")
  @ConditionalOnClass({
    GlobalConfigService.class,
    CkmsSdkClient.class,
    CdkSdkConfigProperties.class
  })
  public SyncedConfig syncedConfig(
      @Autowired GlobalConfigService globalConfigService,
      @Autowired CkmsSdkClient ckmsSdkClient,
      @Autowired CdkSdkConfigProperties cdkSdkConfigProperties) {
    return new SyncedConfigImpl(globalConfigService, ckmsSdkClient, cdkSdkConfigProperties);
  }

  @Bean
  public CdkClientResource cdkClientResource(
      @Autowired SyncedConfig syncedConfig, @Autowired MetricRegistry metricRegistry) {
    return new CdkClientResource(syncedConfig, metricRegistry);
  }
}
