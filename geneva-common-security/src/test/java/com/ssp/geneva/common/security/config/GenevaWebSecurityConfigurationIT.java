package com.ssp.geneva.common.security.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;

import com.nexage.admin.core.repository.CompanyRepository;
import com.nexage.admin.core.repository.UserRepository;
import com.ssp.geneva.common.security.config.GenevaWebSecurityConfigurationIT.TestApplicationProperties;
import com.ssp.geneva.sdk.onecentral.OneCentralSdkClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.vote.RoleHierarchyVoter;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(
    classes = {TestApplicationProperties.class, GenevaWebSecurityConfiguration.class})
@TestPropertySource("classpath:application-test.properties")
@TestPropertySource(properties = {"geneva.server.login=form-login"})
class GenevaWebSecurityConfigurationIT {

  @Autowired ApplicationContext context;

  @Test
  void shouldRegisterExpectedBeans() {
    assertNotNull(context.getBean("genevaSecurityProperties"));
    assertNotNull(context.getBean("objectMapper"));
    assertNotNull(context.getBean("simpleRestTemplate"));
  }

  @Test
  void shouldSetGenevaSecurityPropertiesAccordingly() {
    // given
    GenevaSecurityProperties genevaSecurityProperties =
        (GenevaSecurityProperties) context.getBean("genevaSecurityProperties");
    assertNotNull(genevaSecurityProperties);

    // when
    var ssoOneIdBaseUrl = ReflectionTestUtils.getField(genevaSecurityProperties, "ssoOneIdBaseUrl");
    var ssoOneIdRealm = ReflectionTestUtils.getField(genevaSecurityProperties, "ssoOneIdRealm");
    var ssoOneIdBaseApplicationUri =
        ReflectionTestUtils.getField(genevaSecurityProperties, "ssoOneIdBaseApplicationUri");
    var ssoUiBaseEndpoint =
        ReflectionTestUtils.getField(genevaSecurityProperties, "ssoUiBaseEndpoint");
    var genevaServerTestingUserEnabled =
        ReflectionTestUtils.getField(genevaSecurityProperties, "genevaServerTestingUserEnabled");
    var springSessionMaxInactiveTimeout =
        ReflectionTestUtils.getField(genevaSecurityProperties, "springSessionMaxInactiveTimeout");
    var genevaSsoClientId =
        ReflectionTestUtils.getField(genevaSecurityProperties, "genevaSsoClientId");
    var genevaSsoClientSecret =
        ReflectionTestUtils.getField(genevaSecurityProperties, "genevaSsoClientSecret");

    // then
    assertEquals("https://id-uat.b2b.ouryahoo.com", ssoOneIdBaseUrl);
    assertEquals("aolcorporate/aolexternals", ssoOneIdRealm);
    assertEquals("http://127.0.0.1:8080", ssoOneIdBaseApplicationUri);
    assertEquals("", ssoUiBaseEndpoint);
    assertEquals(false, genevaServerTestingUserEnabled);
    assertEquals(1440, springSessionMaxInactiveTimeout);
    assertEquals("1234567", genevaSsoClientId);
    assertEquals("111112333", genevaSsoClientSecret);
  }

  @Configuration
  static class TestApplicationProperties {

    @Bean("roleVoter")
    public RoleHierarchyVoter roleVoter() {
      return mock(RoleHierarchyVoter.class);
    }

    @Bean
    public UserRepository userRepository() {
      return mock(UserRepository.class);
    }

    @Bean
    public CompanyRepository companyRepository() {
      return mock(CompanyRepository.class);
    }

    @Bean
    public OneCentralSdkClient oneCentralSdkClient() {
      return mock(OneCentralSdkClient.class);
    }
  }
}
