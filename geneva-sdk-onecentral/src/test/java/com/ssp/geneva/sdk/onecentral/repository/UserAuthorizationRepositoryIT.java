package com.ssp.geneva.sdk.onecentral.repository;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.matching;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import com.ssp.geneva.common.test.junit.extension.WireMockExtension;
import com.ssp.geneva.sdk.onecentral.config.OneCentralSdkConfig;
import com.ssp.geneva.sdk.onecentral.error.OneCentralErrorCodes;
import com.ssp.geneva.sdk.onecentral.exception.OneCentralException;
import com.ssp.geneva.sdk.onecentral.model.OneCentralUserAuthResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith({SpringExtension.class})
@ContextConfiguration(classes = {TestRepositoryConfig.class, OneCentralSdkConfig.class})
@TestPropertySource("classpath:application-test.properties")
class UserAuthorizationRepositoryIT {
  @RegisterExtension
  static WireMockExtension wireMockRule = new WireMockExtension(options().port(9999));

  @Autowired private OAuth2RestTemplate s2sTemplate;
  @Autowired private UserAuthorizationRepository userAuthorizationRepository;

  String accessToken = "11223344";

  @Test
  void shouldFailOnMissingAccessToken() {

    OAuth2ClientContext oAuth2ClientContext = s2sTemplate.getOAuth2ClientContext();
    oAuth2ClientContext.setAccessToken(null);

    // when
    OneCentralException exception =
        assertThrows(
            OneCentralException.class, () -> userAuthorizationRepository.getUserAuth(null));

    // then
    assertNotNull(exception);
    assertEquals(OneCentralErrorCodes.ONECENTRAL_UNABLE_TO_GET_TOKEN, exception.getErrorCode());
  }

  @Test
  void shouldFailOnInvalidAuthentication() {

    OAuth2ClientContext oAuth2ClientContext = s2sTemplate.getOAuth2ClientContext();
    oAuth2ClientContext.setAccessToken(new DefaultOAuth2AccessToken("11223344"));

    wireMockRule.stubFor(
        get(urlPathMatching("/one-central/user-authorization/v3/users/authorization/*"))
            .withHeader("Content-Type", matching(APPLICATION_JSON_VALUE))
            .willReturn(
                aResponse()
                    .withStatus(401)
                    .withHeader("Content-Type", APPLICATION_JSON_VALUE)
                    .withBody(
                        "{\n"
                            + "  \"code\": 401,\n"
                            + "  \"message\": \"Missing Auth token\",\n"
                            + "  \"detail\": \"An Auth token is required, either as a cookie or in the Authorization header\"\n"
                            + "}")));
    String accessToken = oAuth2ClientContext.getAccessToken().getValue();

    // when
    OneCentralException exception =
        assertThrows(
            OneCentralException.class, () -> userAuthorizationRepository.getUserAuth(accessToken));

    // then
    assertNotNull(exception);
    assertEquals(OneCentralErrorCodes.ONECENTRAL_USER_NOT_FOUND, exception.getErrorCode());
    assertEquals(HttpStatus.UNAUTHORIZED, exception.getErrorCode().getHttpStatus());
  }

  @Test
  void shouldComplainUserNotFound() {

    OAuth2ClientContext oAuth2ClientContext = s2sTemplate.getOAuth2ClientContext();
    oAuth2ClientContext.setAccessToken(new DefaultOAuth2AccessToken("11223344"));

    wireMockRule.stubFor(
        get(urlPathMatching("/one-central/user-authorization/v3/users/authorization/*"))
            .withHeader("Content-Type", matching(APPLICATION_JSON_VALUE))
            .willReturn(
                aResponse()
                    .withStatus(401)
                    .withHeader("Content-Type", APPLICATION_JSON_VALUE)
                    .withBody(
                        "[\n"
                            + "  {\n"
                            + "    \"code\": 133,\n"
                            + "    \"message\": \"User Not found\",\n"
                            + "    \"detail\": \"User Not found with the requested token.\"\n"
                            + "  },\n"
                            + "]")));

    // when
    OneCentralException exception =
        assertThrows(
            OneCentralException.class, () -> userAuthorizationRepository.getUserAuth(accessToken));

    // then
    assertNotNull(exception);
    assertEquals(OneCentralErrorCodes.ONECENTRAL_USER_NOT_FOUND, exception.getErrorCode());
    assertEquals(HttpStatus.UNAUTHORIZED, exception.getErrorCode().getHttpStatus());
  }

  @Test
  void shouldSuccessfullyAuthenticate() {

    OAuth2ClientContext oAuth2ClientContext = s2sTemplate.getOAuth2ClientContext();
    oAuth2ClientContext.setAccessToken(new DefaultOAuth2AccessToken("11223344"));

    wireMockRule.stubFor(
        get(urlPathMatching("/one-central/user-authorization/v3/users/authorization/*"))
            .withHeader("Content-Type", matching(APPLICATION_JSON_VALUE))
            .willReturn(
                aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", APPLICATION_JSON_VALUE)
                    .withBody(
                        "{\n"
                            + "   \"username\":\"test\",\n"
                            + "   \"firstName\":\"Test\",\n"
                            + "   \"lastName\":\"Test Test\",\n"
                            + "   \"email\":\"test.test.test@yahooinc.com\",\n"
                            + "   \"internal\":\"true\",\n"
                            + "   \"systemUser\":\"false\",\n"
                            + "   \"impersonationMode\":\"false\",\n"
                            + "   \"impersonationActualUsername\":null,\n"
                            + "   \"sessionToken\":null,\n"
                            + "   \"sessionTokenType\":null,\n"
                            + "   \"countryCd\":\"IE\",\n"
                            + "   \"status\":\"ACTIVE\",\n"
                            + "   \"cdid\":null,\n"
                            + "   \"oktaShortId\":\"test\",\n"
                            + "   \"defaultOrgSapId\":\"0\",\n"
                            + "   \"defaultVideoOrgId\":\"0\",\n"
                            + "   \"entitlements\":[\n"
                            + "      \n"
                            + "   ],\n"
                            + "   \"agencyAdvertiserAssociations\":[\n"
                            + "      \n"
                            + "   ],\n"
                            + "   \"uri\":null,\n"
                            + "   \"userPreferences\":[\n"
                            + "      \n"
                            + "   ]\n"
                            + "}")));

    ResponseEntity<OneCentralUserAuthResponse> response =
        userAuthorizationRepository.getUserAuth(accessToken);
    assertNotNull(response);
  }

  @Test
  void shouldAssignRole() {

    OAuth2ClientContext oAuth2ClientContext = s2sTemplate.getOAuth2ClientContext();
    oAuth2ClientContext.setAccessToken(new DefaultOAuth2AccessToken("11223344"));

    wireMockRule.stubFor(
        get(urlPathMatching("/one-central/user-authorization/v3/users/authorization/*"))
            .withHeader("Content-Type", matching(APPLICATION_JSON_VALUE))
            .willReturn(
                aResponse()
                    .withStatus(401)
                    .withHeader("Content-Type", APPLICATION_JSON_VALUE)
                    .withBody(
                        "[\n"
                            + "  {\n"
                            + "    \"code\": 133,\n"
                            + "    \"message\": \"User Not found\",\n"
                            + "    \"detail\": \"User Not found with the requested token.\"\n"
                            + "  },\n"
                            + "]")));

    // when
    OneCentralException exception =
        assertThrows(
            OneCentralException.class, () -> userAuthorizationRepository.getUserAuth(accessToken));

    // then
    assertNotNull(exception);
    assertEquals(OneCentralErrorCodes.ONECENTRAL_USER_NOT_FOUND, exception.getErrorCode());
    assertEquals(HttpStatus.UNAUTHORIZED, exception.getErrorCode().getHttpStatus());
  }
}
