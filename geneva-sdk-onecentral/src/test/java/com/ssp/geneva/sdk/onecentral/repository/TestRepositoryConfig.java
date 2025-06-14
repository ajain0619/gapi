package com.ssp.geneva.sdk.onecentral.repository;

import static java.util.Collections.emptySet;

import com.ssp.geneva.sdk.onecentral.model.Role;
import com.ssp.geneva.sdk.onecentral.model.Roles;
import java.io.IOException;
import java.util.Set;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.resource.BaseOAuth2ProtectedResourceDetails;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

@Configuration
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

  @Bean
  public OAuth2RestTemplate s2sTemplate() {
    return new OAuth2RestTemplate(new BaseOAuth2ProtectedResourceDetails());
  }

  @Bean
  public Set<Role> roles() {
    try {
      Yaml yaml = new Yaml(new Constructor(Roles.class, new LoaderOptions()));
      Roles roles = yaml.load(new ClassPathResource("onecentral-roles.yaml").getInputStream());
      return roles.getQa();
    } catch (IOException e) {
      return emptySet();
    }
  }
}
