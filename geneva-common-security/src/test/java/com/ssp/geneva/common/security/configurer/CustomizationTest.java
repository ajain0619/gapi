package com.ssp.geneva.common.security.configurer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ssp.geneva.common.security.config.configurer.Customization;
import com.ssp.geneva.common.security.oauth2.BearerTokenIntrospector;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.configurers.oauth2.client.OAuth2LoginConfigurer;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.resource.OAuth2ResourceServerConfigurer;
import org.springframework.security.oauth2.client.endpoint.DefaultAuthorizationCodeTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequest;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.endpoint.OAuth2AccessTokenResponse;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationExchange;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationResponse;
import org.springframework.web.client.RestOperations;

@ExtendWith(MockitoExtension.class)
class CustomizationTest {

  private final String REALM = "test/test123";

  @Mock RestOperations restOperations;

  @Mock OAuth2ResourceServerConfigurer oAuth2ResourceServerConfigurer;

  @Mock OAuth2AuthorizationCodeGrantRequest oAuth2AuthorizationCodeGrantRequest;

  @Mock OAuth2AuthorizationRequest auth2AuthorizationRequest;

  @Mock OAuth2AuthorizationResponse auth2AuthorizationResponse;

  @Mock OAuth2AuthorizationExchange auth2AuthorizationExchange;

  @Captor ArgumentCaptor<RequestEntity<?>> requestEntity;

  @Captor
  ArgumentCaptor<Customizer<OAuth2ResourceServerConfigurer.OpaqueTokenConfigurer>>
      opaqueTokenCustomizer;

  @Captor ArgumentCaptor<Converter<String, RequestEntity<?>>> converterArgumentCaptor;
  @Mock BearerTokenIntrospector bearerTokenIntrospector;

  @Mock OAuth2ResourceServerConfigurer.OpaqueTokenConfigurer tokenConfigurer;

  @Test
  void shouldCustomizeAuthorizationEndpointConfig() {

    Customizer customizer =
        Customization.authorizationEndpointConfigCustomizer(
            createClientRegistrationRepository(), REALM);
    OAuth2LoginConfigurer auth2LoginConfigurer = new OAuth2LoginConfigurer<>();
    OAuth2LoginConfigurer.AuthorizationEndpointConfig authorizationEndpointConfig =
        auth2LoginConfigurer.authorizationEndpoint();

    customizer.customize(authorizationEndpointConfig);

    assertEquals(auth2LoginConfigurer, authorizationEndpointConfig.and());
  }

  @Test
  void shouldCustomizeOAuth2ResourceServerConfig() {
    when(bearerTokenIntrospector.getIntrospectionUri()).thenReturn("http://localhost:1111");
    Customizer customizer =
        Customization.oAuth2ResourceServerCustomizer(bearerTokenIntrospector, REALM);

    customizer.customize(oAuth2ResourceServerConfigurer);
    verify(oAuth2ResourceServerConfigurer).opaqueToken(opaqueTokenCustomizer.capture());
    opaqueTokenCustomizer.getValue().customize(tokenConfigurer);
    verify(tokenConfigurer).introspector(bearerTokenIntrospector);
    verify(bearerTokenIntrospector).setRequestEntityConverter(converterArgumentCaptor.capture());
    var convertedRequestEntity = converterArgumentCaptor.getValue().convert("test123");
    assertEquals(HttpMethod.POST, convertedRequestEntity.getMethod());
    assertEquals(
        MediaType.APPLICATION_JSON, convertedRequestEntity.getHeaders().getAccept().get(0));
    assertEquals(
        MediaType.APPLICATION_FORM_URLENCODED,
        convertedRequestEntity.getHeaders().getContentType());
  }

  @Test
  void shouldCustomizeTokenEndpointConfig() {

    var response =
        OAuth2AccessTokenResponse.withToken("test123")
            .refreshToken("765test")
            .expiresIn(3600L)
            .tokenType(OAuth2AccessToken.TokenType.BEARER)
            .scopes(Set.of("openId"))
            .build();

    Customizer customizer = Customization.tokenEndpointConfigCustomizer(REALM);
    OAuth2LoginConfigurer auth2LoginConfigurer = new OAuth2LoginConfigurer<>();
    OAuth2LoginConfigurer.TokenEndpointConfig authorizationTokenEndpointConfig =
        auth2LoginConfigurer.tokenEndpoint();

    OAuth2LoginConfigurer.TokenEndpointConfig authorizationSpiedTokenEndpointConfig =
        Mockito.spy(authorizationTokenEndpointConfig);
    when(oAuth2AuthorizationCodeGrantRequest.getClientRegistration())
        .thenReturn(createClientRegistrationRepository().findByRegistrationId("b2b"));
    when(oAuth2AuthorizationCodeGrantRequest.getAuthorizationExchange())
        .thenReturn(auth2AuthorizationExchange);
    when(auth2AuthorizationExchange.getAuthorizationRequest())
        .thenReturn(auth2AuthorizationRequest);
    when(auth2AuthorizationExchange.getAuthorizationResponse())
        .thenReturn(auth2AuthorizationResponse);
    when(oAuth2AuthorizationCodeGrantRequest.getGrantType())
        .thenReturn(AuthorizationGrantType.AUTHORIZATION_CODE);
    when(auth2AuthorizationResponse.getCode()).thenReturn("e345f555");
    when(restOperations.exchange(any(), eq(OAuth2AccessTokenResponse.class)))
        .thenReturn(new ResponseEntity<>(response, HttpStatus.OK));

    customizer.customize(authorizationSpiedTokenEndpointConfig);

    Mockito.verify(authorizationSpiedTokenEndpointConfig)
        .accessTokenResponseClient(
            ArgumentMatchers.argThat(
                tokenResponseClient -> {
                  ((DefaultAuthorizationCodeTokenResponseClient) tokenResponseClient)
                      .setRestOperations(restOperations);
                  var oAuth2AccessTokenResponse =
                      tokenResponseClient.getTokenResponse(oAuth2AuthorizationCodeGrantRequest);

                  return oAuth2AccessTokenResponse.equals(response);
                }));
    verify(restOperations, times(2))
        .exchange(requestEntity.capture(), eq(OAuth2AccessTokenResponse.class));
    assertEquals(
        "https://id-test.yahoo.com/identity/oauth2/access_token",
        String.valueOf(requestEntity.getValue().getUrl()));
    assertEquals(auth2LoginConfigurer, authorizationSpiedTokenEndpointConfig.and());
  }

  private ClientRegistrationRepository createClientRegistrationRepository() {
    return new InMemoryClientRegistrationRepository(
        ClientRegistration.withRegistrationId("b2b")
            .clientId("C1234")
            .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
            .redirectUri("https://test.api.yahoo.com/geneva/sso")
            .authorizationUri("https://id-test.yahoo.com/identity/oauth2/authorize")
            .tokenUri("https://id-test.yahoo.com/identity/oauth2/access_token")
            .scope("openid", "profile", "email", "address", "phone")
            .clientSecret("testsecret")
            .clientName("Yahoo-B2B")
            .userNameAttributeName("userName")
            .userInfoUri("https://id-test.yahoo.com/identity/oauth2/userinfo?realm=test/test123")
            .jwkSetUri(
                "https://id-test.yahoo.com/identity/oauth2/connect/jwk_uri?realm=test/test123")
            .clientAuthenticationMethod(ClientAuthenticationMethod.NONE)
            .build());
  }
}
