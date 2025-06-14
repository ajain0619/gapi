package com.ssp.geneva.sdk.onecentral.config;

import static java.util.Collections.emptySet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssp.geneva.sdk.onecentral.OneCentralSdkClient;
import com.ssp.geneva.sdk.onecentral.model.Role;
import com.ssp.geneva.sdk.onecentral.model.Roles;
import com.ssp.geneva.sdk.onecentral.repository.AuthorizationManagementRepository;
import com.ssp.geneva.sdk.onecentral.repository.UserAuthorizationRepository;
import com.ssp.geneva.sdk.onecentral.repository.UserManagementPasswordRepository;
import com.ssp.geneva.sdk.onecentral.repository.UserManagementRepository;
import com.ssp.geneva.sdk.onecentral.repository.UserMigrationRepository;
import com.ssp.geneva.sdk.onecentral.service.OneCentralUserService;
import java.io.IOException;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.client.RestTemplate;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

@Log4j2
@Getter
@Setter
@Configuration
public class OneCentralSdkConfig {

  @Value("${sso.create.onecentral.user:false}")
  private Boolean ssoCreateOneCentralUser;

  @Value("${sso.oneApi.baseURL}")
  private String ssoOneApiBaseUrl;

  @Value("${sso.ui.base.endpoint}")
  private String ssoUiBaseEndpoint;

  @Value("${sso.systemName:One}")
  private String ssoSystemName;

  @Value("${sso.roleId}")
  private String ssoRoleId;

  @Value("${sso.apiUserRoleId}")
  private String ssoApiUserRoleId;

  @Value("${sso.environment:qa}")
  private String environment;

  @Bean
  public Set<Role> roles() {
    try {
      Yaml yaml = new Yaml(new Constructor(Roles.class, new LoaderOptions()));
      Roles roles = yaml.load(new ClassPathResource("onecentral-roles.yaml").getInputStream());
      if (environment.equals("qa")) return roles.getQa();
      if (environment.equals("prod")) return roles.getProd();
      throw new IOException("Invalid environment loaded.");
    } catch (IOException e) {
      return emptySet();
    }
  }

  @Bean("oneCentralSdkConfigProperties")
  @ConditionalOnMissingBean
  public OneCentralSdkConfigProperties oneCentralSdkConfigProperties() {
    return OneCentralSdkConfigProperties.builder()
        .ssoCreateOneCentralUser(ssoCreateOneCentralUser)
        .ssoOneApiBaseUrl(ssoOneApiBaseUrl)
        .ssoUiBaseEndpoint(ssoUiBaseEndpoint)
        .ssoSystemName(ssoSystemName)
        .environment(environment)
        .ssoRoleId(ssoRoleId)
        .ssoApiUserRoleId(ssoApiUserRoleId)
        .roles(roles())
        .build();
  }

  @Bean("oneCentralObjectMapper")
  public ObjectMapper oneCentralObjectMapper() {
    return OneCentralSdkJacksonBeanFactory.initObjectMapper();
  }

  @Bean("oneCentralRestTemplate")
  public RestTemplate restTemplate() {
    return OneCentralSdkRestTemplateFactory.initRestTemplate();
  }

  @Bean("userMigrationRepository")
  @ConditionalOnClass({ObjectMapper.class, RestTemplate.class, OneCentralSdkConfigProperties.class})
  public UserMigrationRepository userMigrationRepository(
      @Autowired ObjectMapper oneCentralObjectMapper,
      @Autowired RestTemplate oneCentralRestTemplate,
      @Autowired OneCentralSdkConfigProperties oneCentralSdkConfigProperties) {
    return new UserMigrationRepository(
        oneCentralObjectMapper, oneCentralRestTemplate, oneCentralSdkConfigProperties);
  }

  @Bean("userManagementRepository")
  @ConditionalOnClass({ObjectMapper.class, RestTemplate.class, OneCentralSdkConfigProperties.class})
  public UserManagementRepository userManagementRepository(
      @Autowired ObjectMapper oneCentralObjectMapper,
      @Autowired RestTemplate oneCentralRestTemplate,
      @Autowired OneCentralSdkConfigProperties oneCentralSdkConfigProperties) {
    return new UserManagementRepository(
        oneCentralObjectMapper, oneCentralRestTemplate, oneCentralSdkConfigProperties);
  }

  @Bean("userManagementPasswordRepository")
  @ConditionalOnClass({ObjectMapper.class, RestTemplate.class, OneCentralSdkConfigProperties.class})
  public UserManagementPasswordRepository userManagementPasswordRepository(
      @Autowired ObjectMapper oneCentralObjectMapper,
      @Autowired RestTemplate oneCentralRestTemplate,
      @Autowired OneCentralSdkConfigProperties oneCentralSdkConfigProperties) {
    return new UserManagementPasswordRepository(
        oneCentralObjectMapper, oneCentralRestTemplate, oneCentralSdkConfigProperties);
  }

  @Bean("userAuthorizationRepository")
  @ConditionalOnClass({ObjectMapper.class, RestTemplate.class, OneCentralSdkConfigProperties.class})
  public UserAuthorizationRepository userAuthorizationRepository(
      @Autowired ObjectMapper oneCentralObjectMapper,
      @Autowired RestTemplate oneCentralRestTemplate,
      @Autowired OneCentralSdkConfigProperties oneCentralSdkConfigProperties) {
    return new UserAuthorizationRepository(
        oneCentralObjectMapper, oneCentralRestTemplate, oneCentralSdkConfigProperties);
  }

  @Bean("authorizationManagementRepository")
  public AuthorizationManagementRepository authorizationManagementRepository(
      @Autowired ObjectMapper oneCentralObjectMapper,
      @Autowired RestTemplate oneCentralRestTemplate,
      @Autowired OneCentralSdkConfigProperties oneCentralSdkConfigProperties) {
    return new AuthorizationManagementRepository(
        oneCentralObjectMapper, oneCentralRestTemplate, oneCentralSdkConfigProperties);
  }

  @Bean("oneCentralUserService")
  public OneCentralUserService userService(
      @Autowired UserManagementRepository userManagementRepository,
      @Autowired AuthorizationManagementRepository authorizationManagementRepository) {
    return new OneCentralUserService(userManagementRepository, authorizationManagementRepository);
  }

  @Bean("oneCentralSdkClient")
  @ConditionalOnClass({OneCentralSdkConfigProperties.class})
  public OneCentralSdkClient oneCentralSdkClient(
      @Autowired OneCentralSdkConfigProperties oneCentralSdkConfigProperties,
      @Autowired UserMigrationRepository userMigrationRepository,
      @Autowired UserManagementRepository userManagementRepository,
      @Autowired UserManagementPasswordRepository userManagementPasswordRepository,
      @Autowired UserAuthorizationRepository userAuthorizationRepository,
      @Autowired OneCentralUserService userService,
      @Autowired AuthorizationManagementRepository authorizationManagementRepository) {
    return OneCentralSdkClient.builder()
        .oneCentralSdkConfigProperties(oneCentralSdkConfigProperties)
        .userMigrationRepository(userMigrationRepository)
        .userManagementRepository(userManagementRepository)
        .userManagementPasswordRepository(userManagementPasswordRepository)
        .userAuthorizationRepository(userAuthorizationRepository)
        .authorizationManagementRepository(authorizationManagementRepository)
        .userService(userService)
        .build();
  }
}
