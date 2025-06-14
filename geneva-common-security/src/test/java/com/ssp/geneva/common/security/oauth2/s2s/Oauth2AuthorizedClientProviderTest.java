package com.ssp.geneva.common.security.oauth2.s2s;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;

import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.client.AuthorizedClientServiceOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.DelegatingOAuth2AuthorizedClientProvider;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProvider;

@ExtendWith(MockitoExtension.class)
class Oauth2AuthorizedClientProviderTest {

  @Captor ArgumentCaptor<OAuth2AuthorizedClientProvider> oAuth2AuthorizedClientProvider;

  @Mock
  private AuthorizedClientServiceOAuth2AuthorizedClientManager
      authorizedClientServiceOAuth2AuthorizedClientManager;

  private Oauth2AuthorizedClientProvider oauth2AuthorizedClientProvider;

  @BeforeEach
  void setUp() {
    oauth2AuthorizedClientProvider =
        new Oauth2AuthorizedClientProvider(
            Set.of(authorizedClientServiceOAuth2AuthorizedClientManager));
  }

  @Test
  void shouldReturnValidClientWhenGetAuthorizedClient() {
    oauth2AuthorizedClientProvider.getAuthorizedClient();
    verify(authorizedClientServiceOAuth2AuthorizedClientManager)
        .setAuthorizedClientProvider(oAuth2AuthorizedClientProvider.capture());
    var provider = oAuth2AuthorizedClientProvider.getValue();
    assertTrue(provider instanceof DelegatingOAuth2AuthorizedClientProvider);
  }
}
