package com.ssp.geneva.sdk.xandr.config;

import com.ssp.geneva.sdk.xandr.XandrSdkClient;
import com.ssp.geneva.sdk.xandr.config.factory.XandrSdkRestTemplateBeanFactory;
import com.ssp.geneva.sdk.xandr.repository.AuthRepository;
import com.ssp.geneva.sdk.xandr.repository.DealRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class TestConfig {

  @Bean("xandrSdkConfigProperties")
  @ConditionalOnMissingBean
  public XandrSdkConfigProperties xandrSdkConfigProperties() {
    return XandrSdkConfigProperties.builder()
        .xandrEndpoint("xandrCredentials")
        .xandrCredentials("xandrCredentials")
        .xandrCredentialsMsRebroadcast("xandrCredentialsMsRebroadcast")
        .build();
  }

  @Bean("xandrRestTemplate")
  public RestTemplate xandrRestTemplate() {
    return XandrSdkRestTemplateBeanFactory.initRestTemplate();
  }

  @Bean("authRepository")
  public AuthRepository authRepository(@Autowired RestTemplate xandrRestTemplate) {
    return AuthRepository.builder()
        .xandrEndpoint("xandrEndpoint")
        .xandrCredentials("xandrCredentials")
        .xandrCredentialsMsRebroadcast("xandrCredentialsMsRebroadcast")
        .xandrRestTemplate(xandrRestTemplate)
        .build();
  }

  @Bean("dealRepository")
  public DealRepository dealRepository(
      @Autowired RestTemplate xandrRestTemplate, @Autowired AuthRepository authRepository) {
    return DealRepository.builder()
        .xandrEndpoint("xandrEndpoint")
        .xandrRestTemplate(xandrRestTemplate)
        .authRepository(authRepository)
        .build();
  }

  @Bean("xandrSdkClient")
  public XandrSdkClient xandrSdkClient(
      @Autowired XandrSdkConfigProperties xandrSdkConfigProperties,
      @Autowired RestTemplate xandrRestTemplate,
      @Autowired DealRepository dealRepository,
      @Autowired AuthRepository authRepository) {
    return XandrSdkClient.builder()
        .xandrSdkConfigProperties(xandrSdkConfigProperties)
        .xandrRestTemplate(xandrRestTemplate)
        .dealRepository(dealRepository)
        .authRepository(authRepository)
        .build();
  }
}
