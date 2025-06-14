package com.ssp.geneva.sdk.identityb2b.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssp.geneva.sdk.identityb2b.IdentityB2bSdkClient;
import com.ssp.geneva.sdk.identityb2b.repository.AccessTokenRepository;
import com.ssp.geneva.sdk.identityb2b.repository.UserAuthenticationRepository;
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
import org.springframework.web.client.RestTemplate;

@Log4j2
@Getter
@Setter
@Configuration
public class IdentityB2bSdkConfig {

  @Value("${sso.oneId.realm:aolcorporate/aolexternals}")
  private String realm;

  @Value("${geneva.sso.oidc.client.id:clientid}")
  private String clientId;

  @Value("${geneva.sso.oidc.client.secret:clientsecret}")
  private String clientSecret;

  @Value("${sso.oneId.baseURL:https://id-uat.b2b.yahooinc.com}")
  private String b2bHost;

  @Bean("identityB2bSdkConfigProperties")
  @ConditionalOnMissingBean
  public IdentityB2bSdkConfigProperties identityB2bSdkConfigProperties() {
    return IdentityB2bSdkConfigProperties.builder()
        .realm(realm)
        .b2bHost(b2bHost)
        .clientSecret(clientSecret)
        .clientId(clientId)
        .build();
  }

  @Bean("identityB2bRestTemplate")
  public RestTemplate restTemplate() {
    return IdentityB2bSdkRestTemplateFactory.initRestTemplate();
  }

  @Bean("identityB2bObjectMapper")
  public ObjectMapper identityB2bObjectMapper() {
    return IdentityB2bSdkJacksonBeanFactory.initObjectMapper();
  }

  @Bean("accessTokenRepository")
  @ConditionalOnClass({
    ObjectMapper.class,
    RestTemplate.class,
    IdentityB2bSdkConfigProperties.class
  })
  @ConditionalOnBean(
      name = {
        "identityB2bObjectMapper",
        "identityB2bRestTemplate",
        "identityB2bSdkConfigProperties"
      })
  public AccessTokenRepository accessTokenRepository(
      @Autowired ObjectMapper identityB2bObjectMapper,
      @Autowired RestTemplate identityB2bRestTemplate,
      @Autowired IdentityB2bSdkConfigProperties identityB2bSdkConfigProperties) {
    return new AccessTokenRepository(
        identityB2bObjectMapper, identityB2bRestTemplate, identityB2bSdkConfigProperties);
  }

  @Bean("userAuthenticationRepository")
  @ConditionalOnClass({
    ObjectMapper.class,
    RestTemplate.class,
    IdentityB2bSdkConfigProperties.class
  })
  @ConditionalOnBean(
      name = {
        "identityB2bObjectMapper",
        "identityB2bRestTemplate",
        "identityB2bSdkConfigProperties"
      })
  public UserAuthenticationRepository userAuthenticationRepository(
      @Autowired ObjectMapper identityB2bObjectMapper,
      @Autowired RestTemplate identityB2bRestTemplate,
      @Autowired IdentityB2bSdkConfigProperties identityB2bSdkConfigProperties) {
    return new UserAuthenticationRepository(
        identityB2bObjectMapper, identityB2bRestTemplate, identityB2bSdkConfigProperties);
  }

  @Bean("identityB2bSdkClient")
  @ConditionalOnClass(IdentityB2bSdkConfigProperties.class)
  @ConditionalOnBean(
      name = {
        "identityB2bSdkConfigProperties",
        "accessTokenRepository",
        "userAuthenticationRepository"
      })
  public IdentityB2bSdkClient identityB2bSdkClient(
      @Autowired IdentityB2bSdkConfigProperties identityB2bSdkConfigProperties,
      @Autowired AccessTokenRepository accessTokenRepository,
      @Autowired UserAuthenticationRepository userAuthenticationRepository) {
    return IdentityB2bSdkClient.builder()
        .identityB2bSdkConfigProperties(identityB2bSdkConfigProperties)
        .accessTokenRepository(accessTokenRepository)
        .userAuthenticationRepository(userAuthenticationRepository)
        .build();
  }
}
