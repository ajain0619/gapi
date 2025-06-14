package com.ssp.geneva.sdk.onecentral.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.ssp.geneva.sdk.onecentral.OneCentralSdkClient;
import com.ssp.geneva.sdk.onecentral.config.OneCentralSdkConfigIT.TestApplicationProperties;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.resource.BaseOAuth2ProtectedResourceDetails;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {TestApplicationProperties.class, OneCentralSdkConfig.class})
@TestPropertySource("classpath:application-test.properties")
class OneCentralSdkConfigIT {

  @Autowired ApplicationContext context;

  @Test
  void shouldSetPropertyAccordingly() {
    OneCentralSdkConfigProperties oneCentralSdkConfigProperties =
        (OneCentralSdkConfigProperties) context.getBean("oneCentralSdkConfigProperties");
    assertNotNull(oneCentralSdkConfigProperties);
    Object ssoCreateOneCentralUser =
        ReflectionTestUtils.getField(oneCentralSdkConfigProperties, "ssoCreateOneCentralUser");
    assertNotNull(ssoCreateOneCentralUser);
    assertEquals(true, ssoCreateOneCentralUser);
    Object ssoOneApiBaseUrl =
        ReflectionTestUtils.getField(oneCentralSdkConfigProperties, "ssoOneApiBaseUrl");
    assertNotNull(ssoOneApiBaseUrl);
    assertEquals("http://127.0.0.1:9999", ssoOneApiBaseUrl);
    Object ssoUiBaseEndpoint =
        ReflectionTestUtils.getField(oneCentralSdkConfigProperties, "ssoUiBaseEndpoint");
    assertNotNull(ssoUiBaseEndpoint);
    assertEquals("http://127.0.0.1:8888", ssoUiBaseEndpoint);
    Object ssoSystemName =
        ReflectionTestUtils.getField(oneCentralSdkConfigProperties, "ssoSystemName");
    assertNotNull(ssoSystemName);
    assertEquals("One", ssoSystemName);
    Object ssoRoleId = ReflectionTestUtils.getField(oneCentralSdkConfigProperties, "ssoRoleId");
    assertNotNull(ssoRoleId);
    assertEquals("11111", ssoRoleId);
    Object ssoApiUserRoleId =
        ReflectionTestUtils.getField(oneCentralSdkConfigProperties, "ssoApiUserRoleId");
    assertNotNull(ssoApiUserRoleId);
    assertEquals("22222", ssoApiUserRoleId);
  }

  @Test
  void shouldRegisterExpectedBeans() {

    OneCentralSdkClient oneCentralSdkClient =
        (OneCentralSdkClient) context.getBean("oneCentralSdkClient");
    assertNotNull(oneCentralSdkClient);
  }

  @Configuration
  static class TestApplicationProperties {

    @Bean
    OAuth2RestTemplate s2sTemplate() {
      return new OAuth2RestTemplate(new BaseOAuth2ProtectedResourceDetails());
    }
  }
}
