package com.ssp.geneva.sdk.ckms.config;

import com.ssp.geneva.sdk.ckms.CkmsSdkClient;
import com.ssp.geneva.sdk.ckms.provider.CkmsProvider;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Log4j2
@Getter
@Setter
@Configuration
public class CkmsSdkConfig {

  @Value("${ckms.ykeykey.mock:false}")
  private Boolean mockYKeyKey;

  @Value("${ckms.athens.cert.path}")
  private String certPath;

  @Value("#{systemProperties['java.home'] + '/lib/security/cacerts'}")
  private String trustStorePath;

  @Value("${ckms.athens.key.path}")
  private String keyPath;

  @Value("${ckms.ykeykey.environment}")
  private String yKeyKeyEnvironment;

  @Value("${ckms.default.keygroups.list}")
  private String defaultKeyGroups;

  @Value("${ckms.trust.store.password.encoded}")
  private String trustStorePassword;

  @Value("${ckms.connection.retry.count:3}")
  private String connectionRetryCount;

  @Value("${ckms.connection.timeout.ms:5000}")
  private String connectionTimeout;

  @Bean
  @ConditionalOnMissingBean
  public CkmsSdkConfigProperties ckmsSdkConfigProperties() {
    return CkmsSdkConfigProperties.builder()
        .mockYKeyKey(mockYKeyKey)
        .certPath(certPath)
        .trustStorePath(trustStorePath)
        .keyPath(keyPath)
        .yKeyKeyEnvironment(yKeyKeyEnvironment)
        .defaultKeyGroups(defaultKeyGroups)
        .trustStorePassword(trustStorePassword)
        .connectionRetryCount(connectionRetryCount)
        .connectionTimeout(connectionTimeout)
        .build();
  }

  @Bean("ckmsProvider")
  @ConditionalOnClass({CkmsProvider.class})
  @ConditionalOnBean(name = {"ckmsSdkConfigProperties"})
  public CkmsProvider ckmsProvider(@Autowired CkmsSdkConfigProperties ckmsSdkConfigProperties) {
    return new CkmsProvider(ckmsSdkConfigProperties);
  }

  @Bean("ckmsSdkClient")
  @ConditionalOnClass({CkmsSdkConfigProperties.class, CkmsProvider.class})
  @ConditionalOnBean(name = {"ckmsSdkConfigProperties", "ckmsProvider"})
  public CkmsSdkClient ckmsSdkClient(
      @Autowired CkmsSdkConfigProperties ckmsSdkConfigProperties,
      @Autowired CkmsProvider ckmsProvider) {
    return CkmsSdkClient.builder()
        .ckmsSdkConfigProperties(ckmsSdkConfigProperties)
        .ckmsProvider(ckmsProvider)
        .build();
  }
}
