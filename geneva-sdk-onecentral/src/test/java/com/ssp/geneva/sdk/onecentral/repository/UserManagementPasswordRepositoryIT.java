package com.ssp.geneva.sdk.onecentral.repository;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.matching;
import static com.github.tomakehurst.wiremock.client.WireMock.put;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static com.ssp.geneva.sdk.onecentral.model.OneCentralSdkResourcePath.GET_USERS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import com.ssp.geneva.common.test.junit.extension.WireMockExtension;
import com.ssp.geneva.sdk.onecentral.config.OneCentralSdkConfig;
import com.ssp.geneva.sdk.onecentral.error.OneCentralErrorCodes;
import com.ssp.geneva.sdk.onecentral.exception.OneCentralException;
import com.ssp.geneva.sdk.onecentral.model.PasswordUpdateRequest;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@Disabled
@ExtendWith({SpringExtension.class})
@ContextConfiguration(classes = {TestRepositoryConfig.class, OneCentralSdkConfig.class})
@TestPropertySource("classpath:application-test.properties")
class UserManagementPasswordRepositoryIT {

  @RegisterExtension
  static WireMockExtension wireMockRule = new WireMockExtension(options().port(9999));

  @Autowired private OAuth2RestTemplate s2sTemplate;
  @Autowired private UserManagementPasswordRepository userManagementPasswordRepository;

  private final String CONNECTION_CLOSE = "close";

  private static final String ONE_CENTRAL_USER_MANAGEMENT_V_6_USERS_PASSWORD =
      GET_USERS.getResourcePath() + "/.*/password";

  @Test
  void shouldFailOnInvalidAuthentication() {
    // given
    final String userName = "john.macclain";
    final String oldPass = "12345";
    final String newPass = "AABBCC";
    var accessToken = s2sTemplate.getAccessToken().getValue();

    PasswordUpdateRequest request =
        PasswordUpdateRequest.builder()
            .username(userName)
            .oldPassword(oldPass)
            .newPassword(newPass)
            .build();
    setupAuthClientContext(true);

    wireMockRule.stubFor(
        put(urlPathMatching(ONE_CENTRAL_USER_MANAGEMENT_V_6_USERS_PASSWORD))
            .withHeader("Content-Type", matching(APPLICATION_JSON_VALUE))
            .willReturn(
                aResponse()
                    .withStatus(401)
                    .withHeader("Content-Type", APPLICATION_JSON_VALUE)
                    .withHeader("Connection", CONNECTION_CLOSE)
                    .withBody(
                        "{\n"
                            + "  \"code\": 401,\n"
                            + "  \"message\": \"Missing Auth token\",\n"
                            + "  \"detail\": \"An Auth token is required, either as a cookie or in the Authorization header\"\n"
                            + "}")));

    // when
    OneCentralException exception =
        assertThrows(
            OneCentralException.class,
            () -> userManagementPasswordRepository.reset(accessToken, request));

    // then
    assertNotNull(exception);
    assertEquals(OneCentralErrorCodes.ONECENTRAL_CONSTRAINT_VIOLATION, exception.getErrorCode());
  }

  @Test
  void shouldFailOnMissingAccessToken() {
    // given
    final String userName = "john.macclain";
    final String oldPass = "12345";
    final String newPass = "AABBCC";
    var accessToken = "";

    PasswordUpdateRequest request =
        PasswordUpdateRequest.builder()
            .username(userName)
            .oldPassword(oldPass)
            .newPassword(newPass)
            .build();
    setupAuthClientContext(true);

    // when
    OneCentralException exception =
        assertThrows(
            OneCentralException.class,
            () -> userManagementPasswordRepository.reset(accessToken, request));

    // then
    assertNotNull(exception);
    assertEquals(OneCentralErrorCodes.ONECENTRAL_UNABLE_TO_GET_TOKEN, exception.getErrorCode());
  }

  @Test
  void shouldComplainInvalidRequest() {
    // given
    final String userName = "john.macclain";
    final String oldPass = "";
    final String newPass = "WhatEver456!";
    var accessToken = s2sTemplate.getAccessToken().getValue();

    PasswordUpdateRequest request =
        PasswordUpdateRequest.builder()
            .username(userName)
            .oldPassword(oldPass)
            .newPassword(newPass)
            .build();
    setupAuthClientContext(false);

    wireMockRule.stubFor(
        put(urlPathMatching(ONE_CENTRAL_USER_MANAGEMENT_V_6_USERS_PASSWORD))
            .withHeader("Content-Type", matching(APPLICATION_JSON_VALUE))
            .willReturn(
                aResponse()
                    .withStatus(400)
                    .withHeader("Content-Type", APPLICATION_JSON_VALUE)
                    .withHeader("Connection", CONNECTION_CLOSE)
                    .withBody(
                        "[\n"
                            + "  {\n"
                            + "    \"code\": 112,\n"
                            + "    \"message\": \"Invalid Password\",\n"
                            + "    \"detail\": \"Password must have at least 8 characters, containing at least 1 number and 1 upper case and 1 lower case letter.\"\n"
                            + "  },\n"
                            + "  {\n"
                            + "    \"code\": 113,\n"
                            + "    \"message\": \"Invalid Password\",\n"
                            + "    \"detail\": \"Password cannot be empty.\"\n"
                            + "  }\n"
                            + "]")));

    // when
    OneCentralException exception =
        assertThrows(
            OneCentralException.class,
            () -> userManagementPasswordRepository.reset(accessToken, request));

    // then
    assertNotNull(exception);
    assertEquals(OneCentralErrorCodes.ONECENTRAL_CONSTRAINT_VIOLATION, exception.getErrorCode());
    assertNotNull(exception.getOneCentralErrorResponse());
  }

  @Test
  void shouldSuccessfullyResponse() {
    // given
    final String userName = "john.doe";
    final String oldPass = "WhatEver123!";
    final String newPass = "WhatEver456!";
    var accessToken = s2sTemplate.getAccessToken().getValue();

    PasswordUpdateRequest request =
        PasswordUpdateRequest.builder()
            .username(userName)
            .oldPassword(oldPass)
            .newPassword(newPass)
            .build();
    setupAuthClientContext(false);

    wireMockRule.stubFor(
        put(urlPathMatching(ONE_CENTRAL_USER_MANAGEMENT_V_6_USERS_PASSWORD))
            .withHeader("Content-Type", matching(APPLICATION_JSON_VALUE))
            .willReturn(
                aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", APPLICATION_JSON_VALUE)
                    .withHeader("Connection", CONNECTION_CLOSE)
                    .withBody(
                        "{\n"
                            + "  \"success\": \"Password updated successfully for john.doe\"\n"
                            + "}")));

    // when
    ResponseEntity<String> response = userManagementPasswordRepository.reset(accessToken, request);

    // then
    assertNotNull(response);
  }

  private void setupAuthClientContext(boolean outputStreaming) {
    SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
    requestFactory.setOutputStreaming(outputStreaming);
    s2sTemplate.setRequestFactory(requestFactory);
    OAuth2ClientContext oAuth2ClientContext = s2sTemplate.getOAuth2ClientContext();
    oAuth2ClientContext.setAccessToken(new DefaultOAuth2AccessToken("accessToken"));
  }
}
