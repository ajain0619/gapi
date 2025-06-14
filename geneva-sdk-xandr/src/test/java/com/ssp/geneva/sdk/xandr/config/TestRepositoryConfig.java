package com.ssp.geneva.sdk.xandr.config;

import com.ssp.geneva.sdk.xandr.config.factory.XandrSdkRestTemplateBeanFactory;
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

  @Bean("xandrSdkConfigProperties")
  @ConditionalOnMissingBean
  public XandrSdkConfigProperties xandrSdkConfigProperties() {
    return XandrSdkConfigProperties.builder()
        .xandrEndpoint("xandrEndpoint")
        .xandrCredentials("xandrCredentials")
        .xandrCredentialsMsRebroadcast("xandrCredentialsMsRebroadcast")
        .build();
  }

  @Bean("xandrRestTemplate")
  public RestTemplate xandrRestTemplate() {
    return XandrSdkRestTemplateBeanFactory.initRestTemplate();
  }
}
