package com.ssp.geneva.common.security.config;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;

import com.nexage.admin.core.repository.CompanyRepository;
import com.nexage.admin.core.repository.UserRepository;
import com.ssp.geneva.common.security.config.GenevaOauth2SecurityConfigurationIT.TestApplicationProperties;
import com.ssp.geneva.common.security.config.oauth2.GenevaOauth2SecurityConfiguration;
import com.ssp.geneva.common.security.oauth2.s2s.Oauth2AuthorizedClientProvider;
import com.ssp.geneva.sdk.onecentral.OneCentralSdkClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.vote.RoleHierarchyVoter;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@TestPropertySource(properties = {"geneva.server.login=form-login"})
@ContextConfiguration(
    classes = {
      TestApplicationProperties.class,
      GenevaOauth2SecurityConfiguration.class,
      GenevaWebSecurityConfiguration.class
    })
@TestPropertySource("classpath:application-test.properties")
class GenevaOauth2SecurityConfigurationIT {

  @Autowired ApplicationContext context;

  @Test
  void shouldRegisterExpectedBeans() {
    assertNotNull(context.getBean("userAuthorizationService"));
    assertNotNull(context.getBean("testUserUtil"));
    assertNotNull(context.getBean("userDetailsService"));
    assertNotNull(context.getBean("apiRequestMatcher"));
    assertNotNull(context.getBean("roleExprHandler"));
    assertNotNull(context.getBean("apiAccessDecisionManager"));
  }

  @Configuration
  static class TestApplicationProperties {

    @Bean("roleVoter")
    public RoleHierarchyVoter roleVoter() {
      return mock(RoleHierarchyVoter.class);
    }

    @Bean("oneCentralSdkClient")
    public OneCentralSdkClient oneCentralSdkClient() {
      return mock(OneCentralSdkClient.class);
    }

    @Bean("userRepository")
    public UserRepository userRepository() {
      return mock(UserRepository.class);
    }

    @Bean("companyRepository")
    public CompanyRepository companyRepository() {
      return mock(CompanyRepository.class);
    }

    @Bean("roleHierarchy")
    public RoleHierarchy roleHierarchy() {
      return mock(RoleHierarchy.class);
    }

    @Bean("s2sTemplate")
    public OAuth2RestTemplate s2sTemplate() {
      return mock(OAuth2RestTemplate.class);
    }

    @Bean
    public Oauth2AuthorizedClientProvider oauth2AuthorizedClientProvider() {
      return mock(Oauth2AuthorizedClientProvider.class);
    }
  }
}
