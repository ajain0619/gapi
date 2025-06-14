package com.ssp.geneva.common.security.config.configurer;

import com.ssp.geneva.common.security.oauth2.BearerTokenIntrospector;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jose4j.jws.AlgorithmIdentifiers;
import org.jose4j.jws.JsonWebSignature;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.jwt.NumericDate;
import org.jose4j.keys.HmacKey;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.oauth2.client.OAuth2LoginConfigurer;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.resource.OAuth2ResourceServerConfigurer;
import org.springframework.security.oauth2.client.endpoint.AbstractOAuth2AuthorizationGrantRequest;
import org.springframework.security.oauth2.client.endpoint.DefaultAuthorizationCodeTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.DefaultClientCredentialsTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequestEntityConverter;
import org.springframework.security.oauth2.client.endpoint.OAuth2ClientCredentialsGrantRequestEntityConverter;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.HttpSessionOAuth2AuthorizationRequestRepository;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestRedirectFilter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Customization {

  private static final String REALM = "realm";

  private static final String CLIENT_ASSERTION_TYPE = "client_assertion_type";
  private static final String CLIENT_ASSERTION_TYPE_VALUE =
      "urn:ietf:params:oauth:client-assertion-type:jwt-bearer";
  private static final String CLIENT_ASSERTION = "client_assertion";

  public static Customizer<OAuth2LoginConfigurer<HttpSecurity>.AuthorizationEndpointConfig>
      authorizationEndpointConfigCustomizer(
          ClientRegistrationRepository clientRegistrationRepository, String realm) {
    return endpointConfig -> {
      DefaultOAuth2AuthorizationRequestResolver oAuth2AuthorizationRequestResolver =
          new DefaultOAuth2AuthorizationRequestResolver(
              clientRegistrationRepository,
              OAuth2AuthorizationRequestRedirectFilter.DEFAULT_AUTHORIZATION_REQUEST_BASE_URI);
      oAuth2AuthorizationRequestResolver.setAuthorizationRequestCustomizer(
          builder -> builder.additionalParameters(parameters -> parameters.put(REALM, realm)));
      endpointConfig.authorizationRequestResolver(oAuth2AuthorizationRequestResolver);
      endpointConfig.authorizationRequestRepository(
          new HttpSessionOAuth2AuthorizationRequestRepository());
    };
  }

  public static Customizer<OAuth2ResourceServerConfigurer<HttpSecurity>>
      oAuth2ResourceServerCustomizer(
          BearerTokenIntrospector bearerTokenIntrospector, String realm) {
    return oAuth2ResourceServerConfigurer ->
        oAuth2ResourceServerConfigurer.opaqueToken(
            opaqueTokenConfigurer -> {
              opaqueTokenConfigurer.introspector(bearerTokenIntrospector);
              bearerTokenIntrospector.setRequestEntityConverter(
                  token -> {
                    MultiValueMap<String, String> body = bearerTokenRequestBody(token, realm);
                    return RequestEntity.post(bearerTokenIntrospector.getIntrospectionUri())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .body(body);
                  });
            });
  }

  private static MultiValueMap<String, String> bearerTokenRequestBody(String token, String realm) {
    MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
    body.add("token", token);
    body.add(REALM, realm);
    return body;
  }

  public static Customizer<OAuth2LoginConfigurer<HttpSecurity>.TokenEndpointConfig>
      tokenEndpointConfigCustomizer(String realm) {
    return tokenEndpointConfig -> {
      var oAuth2AuthorizationCodeGrantRequestEntityConverter =
          new OAuth2AuthorizationCodeGrantRequestEntityConverter();
      oAuth2AuthorizationCodeGrantRequestEntityConverter.addParametersConverter(
          getTokenEndPointRequestConvertor(realm));
      var accessTokenResponseClient = new DefaultAuthorizationCodeTokenResponseClient();
      accessTokenResponseClient.setRequestEntityConverter(
          oAuth2AuthorizationCodeGrantRequestEntityConverter);
      tokenEndpointConfig.accessTokenResponseClient(accessTokenResponseClient);
    };
  }

  public static Customizer<DefaultClientCredentialsTokenResponseClient>
      clientCredentialsTokenResponseClientCustomizerCustomizer(String realm) {
    return clientCredentialsTokenResponseClient -> {
      var oAuth2ClientCredentialsGrantRequestEntityConverter =
          new OAuth2ClientCredentialsGrantRequestEntityConverter();
      oAuth2ClientCredentialsGrantRequestEntityConverter.addParametersConverter(
          getTokenEndPointRequestConvertor(realm));
      clientCredentialsTokenResponseClient.setRequestEntityConverter(
          oAuth2ClientCredentialsGrantRequestEntityConverter);
    };
  }

  private static <
          T extends AbstractOAuth2AuthorizationGrantRequest,
          R extends MultiValueMap<String, String>>
      Converter<T, R> getTokenEndPointRequestConvertor(String realm) {
    return authorizationClientCredentialsRequest -> {
      var parameters = new LinkedMultiValueMap<String, String>();
      parameters.add(REALM, realm);
      parameters.add(CLIENT_ASSERTION_TYPE, CLIENT_ASSERTION_TYPE_VALUE);
      parameters.add(CLIENT_ASSERTION, generate(authorizationClientCredentialsRequest, realm));

      return (R) parameters;
    };
  }

  private static String generate(AbstractOAuth2AuthorizationGrantRequest request, String realm) {
    var registeredClient = request.getClientRegistration();
    String audience =
        registeredClient.getProviderDetails().getTokenUri().concat("?realm=").concat(realm);
    JwtClaims claims = new JwtClaims();
    claims.setIssuedAt(NumericDate.now());
    claims.setExpirationTimeMinutesInTheFuture(10);
    claims.setSubject(registeredClient.getClientId());
    claims.setIssuer(registeredClient.getClientId());
    claims.setAudience(audience);
    claims.setGeneratedJwtId();

    try {
      Key key = new HmacKey(registeredClient.getClientSecret().getBytes(StandardCharsets.UTF_8));

      JsonWebSignature jws = new JsonWebSignature();
      jws.setPayload(claims.toJson());
      jws.setAlgorithmHeaderValue(AlgorithmIdentifiers.HMAC_SHA256);
      jws.setKey(key);
      jws.setDoKeyValidation(false);

      return jws.getCompactSerialization();

    } catch (Exception e) {
      return "";
    }
  }
}
