package com.ssp.geneva.sdk.onecentral.repository;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.matching;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static com.ssp.geneva.sdk.onecentral.model.OneCentralSdkResourcePath.CREATE_SSP_USER_URL_PATH;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import com.ssp.geneva.common.test.junit.extension.WireMockExtension;
import com.ssp.geneva.sdk.onecentral.config.OneCentralSdkConfig;
import com.ssp.geneva.sdk.onecentral.dto.OneCentralUserResponseDTO.OneCentralUser;
import com.ssp.geneva.sdk.onecentral.error.OneCentralErrorCodes;
import com.ssp.geneva.sdk.onecentral.exception.OneCentralException;
import com.ssp.geneva.sdk.onecentral.model.OneCentralUserStatus;
import com.ssp.geneva.sdk.onecentral.model.UserMigrationRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {TestRepositoryConfig.class, OneCentralSdkConfig.class})
@TestPropertySource("classpath:application-test.properties")
class UserMigrationRepositoryIT {

  @RegisterExtension
  static WireMockExtension wireMockRule = new WireMockExtension(options().port(9999));

  @Autowired private OAuth2RestTemplate s2sTemplate;
  @Autowired private UserMigrationRepository userMigrationRepository;

  @BeforeEach
  void setUp() {
    OAuth2ClientContext oAuth2ClientContext = s2sTemplate.getOAuth2ClientContext();
    oAuth2ClientContext.setAccessToken(new DefaultOAuth2AccessToken("11223344"));
  }

  @Test
  void shouldFailOnInvalidAuthentication() {
    // given
    var accessToken = s2sTemplate.getAccessToken().getValue();
    UserMigrationRequest request =
        UserMigrationRequest.builder()
            .firstName("Jane")
            .lastName("Doe")
            .email("jane.doe@yahooinc.com")
            .status(OneCentralUserStatus.ACTIVE)
            .build();
    OAuth2ClientContext oAuth2ClientContext = s2sTemplate.getOAuth2ClientContext();
    oAuth2ClientContext.setAccessToken(new DefaultOAuth2AccessToken("11223344"));

    wireMockRule.stubFor(
        post(urlPathMatching(CREATE_SSP_USER_URL_PATH.getResourcePath()))
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

    // when
    Exception exception =
        assertThrows(
            OneCentralException.class, () -> userMigrationRepository.create(accessToken, request));

    // then
    assertNotNull(exception);
  }

  @Test
  void shouldComplainInvalidRequest() {
    // given
    var accessToken = s2sTemplate.getAccessToken().getValue();
    UserMigrationRequest request =
        UserMigrationRequest.builder()
            .firstName("John")
            .lastName("Doe")
            .email("john.doe@yahooinc.com")
            .status(OneCentralUserStatus.ACTIVE)
            .build();
    OAuth2ClientContext oAuth2ClientContext = s2sTemplate.getOAuth2ClientContext();
    oAuth2ClientContext.setAccessToken(new DefaultOAuth2AccessToken("11223344"));

    wireMockRule.stubFor(
        post(urlPathMatching(CREATE_SSP_USER_URL_PATH.getResourcePath()))
            .withHeader("Content-Type", matching(APPLICATION_JSON_VALUE))
            .willReturn(
                aResponse()
                    .withStatus(400)
                    .withHeader("Content-Type", APPLICATION_JSON_VALUE)
                    .withBody(
                        "[\n"
                            + "  {\n"
                            + "    \"code\": 400,\n"
                            + "    \"message\": \"First name cannot be empty\",\n"
                            + "    \"detail\": \"First name cannot be empty\"\n"
                            + "  },\n"
                            + "  {\n"
                            + "    \"code\": 400,\n"
                            + "    \"message\": \"Status cannot be empty.\",\n"
                            + "    \"detail\": \"Status cannot be empty.\"\n"
                            + "  },\n"
                            + "  {\n"
                            + "    \"code\": 400,\n"
                            + "    \"message\": \"Last name cannot be empty\",\n"
                            + "    \"detail\": \"Last name cannot be empty\"\n"
                            + "  },\n"
                            + "  {\n"
                            + "    \"code\": 400,\n"
                            + "    \"message\": \"Email address cannot be empty\",\n"
                            + "    \"detail\": \"Email address cannot be empty\"\n"
                            + "  }\n"
                            + "]")));

    // when
    OneCentralException exception =
        assertThrows(
            OneCentralException.class, () -> userMigrationRepository.create(accessToken, request));

    // then
    assertNotNull(exception);
    assertEquals(OneCentralErrorCodes.ONECENTRAL_CONSTRAINT_VIOLATION, exception.getErrorCode());
    assertNotNull(exception.getOneCentralErrorResponse());
  }

  @Test
  void shouldSuccessfullyResponse() {
    // given
    var accessToken = s2sTemplate.getAccessToken().getValue();
    UserMigrationRequest request =
        UserMigrationRequest.builder()
            .firstName("John")
            .lastName("MacClain")
            .email("john.macclain@yahooinc.com")
            .status(OneCentralUserStatus.ACTIVE)
            .build();
    OAuth2ClientContext oAuth2ClientContext = s2sTemplate.getOAuth2ClientContext();
    oAuth2ClientContext.setAccessToken(new DefaultOAuth2AccessToken("11223344"));

    wireMockRule.stubFor(
        post(urlPathMatching(CREATE_SSP_USER_URL_PATH.getResourcePath()))
            .withHeader("Content-Type", matching(APPLICATION_JSON_VALUE))
            .willReturn(
                aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", APPLICATION_JSON_VALUE)
                    .withBody(
                        "{\n"
                            + "  \"id\": 224258,\n"
                            + "  \"firstName\": \"John\",\n"
                            + "  \"lastName\": \"MacClain\",\n"
                            + "  \"username\": \"johnmacclain2003\",\n"
                            + "  \"email\": \"john.macclain2@yahoo.com\",\n"
                            + "  \"status\": \"ACTIVE\",\n"
                            + "  \"internal\": false,\n"
                            + "  \"systemUser\": false,\n"
                            + "  \"defaultOrgSapId\": 0,\n"
                            + "  \"defaultVideoOrgId\": 0,\n"
                            + "  \"primaryUserContact\": false,\n"
                            + "  \"last_login_date\": \"\"\n"
                            + "}")));

    // when
    ResponseEntity<OneCentralUser> response = userMigrationRepository.create(accessToken, request);

    // then
    assertNotNull(response);
  }
}
