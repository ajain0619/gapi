package com.ssp.geneva.sdk.xandr.config;

import com.ssp.geneva.sdk.xandr.XandrSdkClient;
import com.ssp.geneva.sdk.xandr.config.factory.XandrSdkRestTemplateBeanFactory;
import com.ssp.geneva.sdk.xandr.repository.AuthRepository;
import com.ssp.geneva.sdk.xandr.repository.DealRepository;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Log4j2
@Getter
@Setter
@Configuration
public class XandrSdkConfig {

  @Value("${xandr.service.endpoint}")
  private String xandrEndpoint;

  @Value("${xandr.service.credentials}")
  private String xandrCredentials;

  @Value("${xandr.service.credentials.ms.rebroadcast}")
  private String xandrCredentialsMsRebroadcast;

  @Bean("xandrSdkConfigProperties")
  @ConditionalOnMissingBean
  public XandrSdkConfigProperties xandrSdkConfigProperties() {
    log.debug("xandrEndpoint: {}", xandrEndpoint);
    return XandrSdkConfigProperties.builder()
        .xandrEndpoint(xandrEndpoint)
        .xandrCredentials(xandrCredentials)
        .xandrCredentialsMsRebroadcast(xandrCredentialsMsRebroadcast)
        .build();
  }

  @Bean("xandrRestTemplate")
  public RestTemplate xandrRestTemplate() {
    return XandrSdkRestTemplateBeanFactory.initRestTemplate();
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

  @Bean("authRepository")
  public AuthRepository authRepository(@Autowired RestTemplate xandrRestTemplate) {
    log.debug("AuthRepository: xandrEndpoint: {}", xandrEndpoint);
    return AuthRepository.builder()
        .xandrEndpoint(xandrEndpoint)
        .xandrCredentials(xandrCredentials)
        .xandrCredentialsMsRebroadcast(xandrCredentialsMsRebroadcast)
        .xandrRestTemplate(xandrRestTemplate)
        .build();
  }

  @Bean("dealRepository")
  public DealRepository dealRepository(
      @Autowired RestTemplate xandrRestTemplate, @Autowired AuthRepository authRepository) {
    log.debug("DealRepository: xandrEndpoint: {}", xandrEndpoint);
    return DealRepository.builder()
        .xandrEndpoint(xandrEndpoint)
        .xandrRestTemplate(xandrRestTemplate)
        .authRepository(authRepository)
        .build();
  }
}
