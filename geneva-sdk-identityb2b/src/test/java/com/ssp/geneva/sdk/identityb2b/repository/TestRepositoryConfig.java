package com.ssp.geneva.sdk.identityb2b.repository;

import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

@Configuration
class TestRepositoryConfig {

  @Bean(value = "applicationProperties")
  public PropertiesFactoryBean getApplicationProperties() {
    PropertiesFactoryBean propertiesFactoryBean = new PropertiesFactoryBean();
    propertiesFactoryBean.setSingleton(true);
    propertiesFactoryBean.setIgnoreResourceNotFound(true);
    ClassPathResource classPathResource = new ClassPathResource("application-test.properties");
    propertiesFactoryBean.setLocation(classPathResource);
    return propertiesFactoryBean;
  }
}
