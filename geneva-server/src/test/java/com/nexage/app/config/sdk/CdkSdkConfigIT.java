package com.nexage.app.config.sdk;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.aol.crs.cdk.SyncedConfig;
import com.codahale.metrics.MetricRegistry;
import com.nexage.app.config.sdk.CdkSdkConfigIT.TestApplicationProperties;
import com.nexage.app.services.crs.CdkClientResource;
import com.ssp.geneva.common.settings.service.GlobalConfigService;
import com.ssp.geneva.sdk.ckms.CkmsSdkClient;
import com.ssp.geneva.sdk.ckms.provider.CkmsProvider;
import java.util.UUID;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {TestApplicationProperties.class, CdkSdkConfig.class})
@TestPropertySource(
    properties = {
      "ckms.enabled=true",
      "crs.sso.clientId=my-sso-client",
      "crs.sso.secret.encrypted=false",
      "crs.sso.secret=whatever",
      "geneva.metrics.jmx.domain=ssp",
      "crs.sso.ckms.key.client=my-client",
      "crs.sso.ckms.key.secret=my-secret",
      "crs.sso.ckms.key.group=my-group"
    })
class CdkSdkConfigIT {

  @Autowired ApplicationContext context;

  @Test
  void shouldSetPropertyAccordingly() {
    CdkSdkConfigProperties properties =
        (CdkSdkConfigProperties) context.getBean("cdkSdkConfigProperties");
    assertNotNull(properties);
    Object ckmsEnabled = ReflectionTestUtils.getField(properties, "ckmsEnabled");
    assertNotNull(ckmsEnabled);
    assertEquals(true, ckmsEnabled);
    Object crsSsoClientId = ReflectionTestUtils.getField(properties, "crsSsoClientId");
    assertNotNull(crsSsoClientId);
    assertEquals("my-sso-client", crsSsoClientId);
    Object crsSsoSecretEncrypted =
        ReflectionTestUtils.getField(properties, "crsSsoSecretEncrypted");
    assertNotNull(crsSsoSecretEncrypted);
    assertEquals(false, crsSsoSecretEncrypted);
    Object crsSsoSecret = ReflectionTestUtils.getField(properties, "crsSsoSecret");
    assertNotNull(crsSsoSecret);
    assertEquals("whatever", crsSsoSecret);
    Object genevaMetricsJmxDomain =
        ReflectionTestUtils.getField(properties, "genevaMetricsJmxDomain");
    assertNotNull(genevaMetricsJmxDomain);
    assertEquals("ssp", genevaMetricsJmxDomain);
    Object crsSsoCkmsKeyClient = ReflectionTestUtils.getField(properties, "crsSsoCkmsKeyClient");
    assertNotNull(crsSsoCkmsKeyClient);
    assertEquals("my-client", crsSsoCkmsKeyClient);
    Object crsSsoCkmsKeySecret = ReflectionTestUtils.getField(properties, "crsSsoCkmsKeySecret");
    assertNotNull(crsSsoCkmsKeySecret);
    assertEquals("my-secret", crsSsoCkmsKeySecret);
    Object crsSsoCkmsKeyGroup = ReflectionTestUtils.getField(properties, "crsSsoCkmsKeyGroup");
    assertNotNull(crsSsoCkmsKeyGroup);
    assertEquals("my-group", crsSsoCkmsKeyGroup);
  }

  @Test
  void shouldRegisterExpectedBeans() {
    SyncedConfig syncedConfig = (SyncedConfig) context.getBean("syncedConfig");
    assertNotNull(syncedConfig);

    CdkClientResource cdkClientResource = (CdkClientResource) context.getBean("cdkClientResource");
    assertNotNull(cdkClientResource);
  }

  @Configuration
  public static class TestApplicationProperties {

    @Bean(value = "globalConfigService")
    public GlobalConfigService globalConfigService() {
      GlobalConfigService globalConfigService = mock(GlobalConfigService.class);
      final String globalStringValue = UUID.randomUUID().toString();
      final int globalIntValue = UUID.randomUUID().hashCode();
      when(globalConfigService.getStringValue(any())).thenReturn(globalStringValue);
      when(globalConfigService.getIntegerValue(any())).thenReturn(globalIntValue);
      return globalConfigService;
    }

    @SneakyThrows
    @Bean(value = "ckmsSdkClient")
    public CkmsSdkClient ckmsSdkClient() {
      final String secret = UUID.randomUUID().toString();
      CkmsSdkClient ckmsSdkClient = mock(CkmsSdkClient.class);
      CkmsProvider ckmsProvider = mock(CkmsProvider.class);
      when(ckmsSdkClient.getCkmsProvider()).thenReturn(ckmsProvider);
      when(ckmsProvider.getSecret(any(), any())).thenReturn(secret);
      return ckmsSdkClient;
    }

    @Bean(value = "metricRegistry")
    public MetricRegistry metricRegistry() {
      return new MetricRegistry();
    }
  }
}
