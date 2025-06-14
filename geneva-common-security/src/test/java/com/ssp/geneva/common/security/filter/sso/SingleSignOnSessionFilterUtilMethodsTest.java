package com.ssp.geneva.common.security.filter.sso;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.ssp.geneva.common.security.config.GenevaSecurityProperties;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.security.oauth2.client.web.HttpSessionOAuth2AuthorizedClientRepository;
import org.springframework.web.client.RestTemplate;

class SingleSignOnSessionFilterUtilMethodsTest {

  @Test
  void shouldReturnNullWhenB2BUriIsNull() {
    GenevaSecurityProperties properties = mock(GenevaSecurityProperties.class);
    SingleSignOnSessionFilter singleSignOnSessionFilter =
        new SingleSignOnSessionFilter(
            null,
            new RestTemplate(),
            null,
            properties,
            new HttpSessionOAuth2AuthorizedClientRepository());
    assertNull(singleSignOnSessionFilter.getB2bUrl(null));
  }

  @ParameterizedTest
  @ValueSource(strings = {"https://localhost:8443/test", "https://localhost:8443/test/"})
  void shouldReturnURLWithAPIWhenB2BUriIsValid(String b2bUri) {
    GenevaSecurityProperties properties = mock(GenevaSecurityProperties.class);
    when(properties.getSsoOneIdBaseUrl()).thenReturn(b2bUri);
    SingleSignOnSessionFilter singleSignOnSessionFilter =
        new SingleSignOnSessionFilter(
            null,
            new RestTemplate(),
            null,
            properties,
            new HttpSessionOAuth2AuthorizedClientRepository());
    String b2bUrl = singleSignOnSessionFilter.getB2bUrl("param");
    assertEquals("https://localhost:8443/test/param", b2bUrl);
  }
}
