package com.ssp.geneva.sdk.identityb2b.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssp.geneva.sdk.identityb2b.IdentityB2bSdkClient;
import com.ssp.geneva.sdk.identityb2b.repository.AccessTokenRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {IdentityB2bSdkConfig.class})
@TestPropertySource(
    properties = {
      "sso.oneId.realm:aolcorporate/aolexternals",
      "geneva.sso.oidc.client.id=123456",
      "geneva.sso.oidc.client.secret=1111111111"
    })
class IdentityB2bSdkConfigIT {

  @Autowired ApplicationContext context;

  @Test
  void shouldSetPropertyAccordingly() {
    IdentityB2bSdkConfigProperties identityB2bSdkConfigProperties =
        (IdentityB2bSdkConfigProperties) context.getBean("identityB2bSdkConfigProperties");
    assertNotNull(identityB2bSdkConfigProperties);
    var identityB2bRealm = ReflectionTestUtils.getField(identityB2bSdkConfigProperties, "realm");
    assertNotNull(identityB2bRealm);
    assertEquals("aolcorporate/aolexternals", identityB2bRealm);
    var identityB2bClientId =
        ReflectionTestUtils.getField(identityB2bSdkConfigProperties, "clientId");
    assertNotNull(identityB2bClientId);
    assertEquals("123456", identityB2bClientId);
    var identityB2bSdkClientSecret =
        ReflectionTestUtils.getField(identityB2bSdkConfigProperties, "clientSecret");
    assertNotNull(identityB2bSdkClientSecret);
    assertEquals("1111111111", identityB2bSdkClientSecret);
    var identityB2bSdkHost =
        ReflectionTestUtils.getField(identityB2bSdkConfigProperties, "b2bHost");
    assertNotNull(identityB2bSdkHost);
    assertEquals("https://id-uat.b2b.yahooinc.com", identityB2bSdkHost);
  }

  @Test
  void shouldRegisterExpectedBeans() {

    IdentityB2bSdkClient identityB2bSdkClient =
        (IdentityB2bSdkClient) context.getBean("identityB2bSdkClient");
    assertNotNull(identityB2bSdkClient);

    IdentityB2bSdkConfigProperties identityB2bSdkConfigProperties =
        (IdentityB2bSdkConfigProperties) context.getBean("identityB2bSdkConfigProperties");
    assertNotNull(identityB2bSdkConfigProperties);

    RestTemplate identityB2bRestTemplate =
        (RestTemplate) context.getBean("identityB2bRestTemplate");
    assertNotNull(identityB2bRestTemplate);

    ObjectMapper identityB2bObjectMapper =
        (ObjectMapper) context.getBean("identityB2bObjectMapper");
    assertNotNull(identityB2bObjectMapper);

    AccessTokenRepository accessTokenRepository =
        (AccessTokenRepository) context.getBean("accessTokenRepository");
    assertNotNull(accessTokenRepository);
  }
}
