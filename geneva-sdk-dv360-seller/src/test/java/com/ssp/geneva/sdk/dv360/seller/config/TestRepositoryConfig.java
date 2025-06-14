package com.ssp.geneva.sdk.dv360.seller.config;

import com.google.auth.oauth2.AccessToken;
import com.google.auth.oauth2.GoogleCredentials;
import com.ssp.geneva.sdk.dv360.seller.config.factory.Dv360SellerSdkRestTemplateBeanFactory;
import java.util.Date;
import java.util.Properties;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.client.RestTemplate;

@Configuration
@ExtendWith(MockitoExtension.class)
public class TestRepositoryConfig {

  private String accessTokenString = "b89fba14-24ac-481b-a1e2-664c81af8888";

  @Bean(value = "applicationProperties")
  public PropertiesFactoryBean getApplicationProperties() {
    PropertiesFactoryBean propertiesFactoryBean = new PropertiesFactoryBean();
    propertiesFactoryBean.setSingleton(true);
    propertiesFactoryBean.setIgnoreResourceNotFound(true);
    ClassPathResource classPathResource = new ClassPathResource("application-test.properties");
    propertiesFactoryBean.setLocation(classPathResource);
    return propertiesFactoryBean;
  }

  @Bean(value = "properties")
  public Properties properties() {
    try {
      return getApplicationProperties().getObject();
    } catch (Exception ex) {
      return null;
    }
  }

  @Bean
  public AccessToken accessToken() {
    return new AccessToken(accessTokenString, new Date());
  }

  @Bean("dv360SdkConfigProperties")
  @ConditionalOnMissingBean
  public Dv360SellerSdkConfigProperties dv360SellerSdkConfigProperties() {
    return Dv360SellerSdkConfigProperties.builder()
        .dv360Endpoint("dv360Endpoint")
        .dv360ExchangeId("dv360ExchangeId")
        .dv360Credentials("dv360Credentials")
        .build();
  }

  @Bean("dv360SellerRestTemplate")
  public RestTemplate dv360SellerRestTemplate() {
    return Dv360SellerSdkRestTemplateBeanFactory.initRestTemplate();
  }

  @Bean("googleCredentials")
  public GoogleCredentials googleCredentials() {
    return GoogleCredentials.newBuilder().build();
  }
}
