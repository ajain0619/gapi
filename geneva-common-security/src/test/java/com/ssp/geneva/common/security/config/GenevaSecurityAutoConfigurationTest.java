package com.ssp.geneva.common.security.config;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;

import com.ssp.geneva.common.settings.util.SysConfigUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.web.HttpSessionOAuth2AuthorizedClientRepository;
import org.springframework.web.client.RestTemplate;

@ExtendWith(MockitoExtension.class)
class GenevaSecurityAutoConfigurationTest {

  private GenevaSecurityAutoConfiguration configuration;

  @BeforeEach
  void setUp() {
    configuration = new GenevaSecurityAutoConfiguration();
  }

  @Test
  void shouldConfig() {
    OAuth2RestTemplate restTemplate = mock(OAuth2RestTemplate.class);
    RestTemplate simpleRestTemplate = mock(RestTemplate.class);
    SysConfigUtil sysConfigUtil = mock(SysConfigUtil.class);
    GenevaSecurityProperties properties = mock(GenevaSecurityProperties.class);
    HttpSessionOAuth2AuthorizedClientRepository repository =
        mock(HttpSessionOAuth2AuthorizedClientRepository.class);
    assertAll(
        () ->
            assertNotNull(
                configuration.singleSignOnSessionFilter(
                    restTemplate, simpleRestTemplate, sysConfigUtil, properties, repository)));
  }
}
