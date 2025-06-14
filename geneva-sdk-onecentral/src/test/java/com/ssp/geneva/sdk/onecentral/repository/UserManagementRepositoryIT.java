package com.ssp.geneva.sdk.onecentral.repository;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.matching;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.put;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static com.ssp.geneva.sdk.onecentral.model.OneCentralSdkResourcePath.CREATE_USER_URL_PATH;
import static com.ssp.geneva.sdk.onecentral.model.OneCentralSdkResourcePath.GET_USERS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import com.ssp.geneva.common.test.junit.extension.WireMockExtension;
import com.ssp.geneva.sdk.onecentral.config.OneCentralSdkConfig;
import com.ssp.geneva.sdk.onecentral.dto.OneCentralUserRequestDTO;
import com.ssp.geneva.sdk.onecentral.dto.OneCentralUserResponseDTO.OneCentralUser;
import com.ssp.geneva.sdk.onecentral.dto.OneCentralUsersResponseDTO;
import com.ssp.geneva.sdk.onecentral.error.OneCentralErrorCodes;
import com.ssp.geneva.sdk.onecentral.exception.OneCentralException;
import com.ssp.geneva.sdk.onecentral.model.OneCentralUserStatus;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.BeforeEach;
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

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {TestRepositoryConfig.class, OneCentralSdkConfig.class})
@TestPropertySource("classpath:application-test.properties")
class UserManagementRepositoryIT {

  @RegisterExtension
  static WireMockExtension wireMockRule = new WireMockExtension(options().port(9999));

  private final String CONNECTION_CLOSE = "close";

  @Autowired private OAuth2RestTemplate s2sTemplate;
  @Autowired private UserManagementRepository userManagementRepository;

  @BeforeEach
  void setUp() {
    OAuth2ClientContext oAuth2ClientContext = s2sTemplate.getOAuth2ClientContext();
    oAuth2ClientContext.setAccessToken(new DefaultOAuth2AccessToken("11223344"));
  }

  @Test
  void shouldFailOnInvalidAuthentication() {
    // given
    var accessToken = s2sTemplate.getAccessToken().getValue();
    OneCentralUserRequestDTO request = OneCentralUserRequestDTO.builder().build();
    OAuth2ClientContext oAuth2ClientContext = s2sTemplate.getOAuth2ClientContext();
    oAuth2ClientContext.setAccessToken(new DefaultOAuth2AccessToken("11223344"));

    wireMockRule.stubFor(
        post(urlPathMatching(CREATE_USER_URL_PATH.getResourcePath()))
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
            OneCentralException.class,
            () -> userManagementRepository.createUser(accessToken, request));

    // then
    assertNotNull(exception);
  }

  @Test
  void shouldComplainInvalidRequest() {
    // given
    var accessToken = s2sTemplate.getAccessToken().getValue();
    OneCentralUserRequestDTO request =
        OneCentralUserRequestDTO.builder()
            .firstName("John")
            .lastName("Doe")
            .email("john.doe@yahooinc.com")
            .status(OneCentralUserStatus.ACTIVE)
            .build();
    OAuth2ClientContext oAuth2ClientContext = s2sTemplate.getOAuth2ClientContext();
    oAuth2ClientContext.setAccessToken(new DefaultOAuth2AccessToken("11223344"));

    wireMockRule.stubFor(
        post(urlPathMatching(CREATE_USER_URL_PATH.getResourcePath()))
            .withHeader("Content-Type", matching(APPLICATION_JSON_VALUE))
            .willReturn(
                aResponse()
                    .withStatus(400)
                    .withHeader("Content-Type", APPLICATION_JSON_VALUE)
                    .withBody(
                        "[\n"
                            + "  {\n"
                            + "    \"code\": 256,\n"
                            + "    \"message\": \"Invalid Email Domain.\",\n"
                            + "    \"detail\": \"Cannot create an External User with an internal email domain.\"\n"
                            + "  }\n"
                            + "]")));

    // when
    OneCentralException exception =
        assertThrows(
            OneCentralException.class,
            () -> userManagementRepository.createUser(accessToken, request));

    // then
    assertNotNull(exception);
    assertEquals(OneCentralErrorCodes.ONECENTRAL_CONSTRAINT_VIOLATION, exception.getErrorCode());
    assertNotNull(exception.getOneCentralErrorResponse());
  }

  @Test
  void shouldSuccessfullyResponse() {
    // given
    var accessToken = s2sTemplate.getAccessToken().getValue();
    OneCentralUserRequestDTO request =
        OneCentralUserRequestDTO.builder()
            .firstName("John")
            .lastName("Doe")
            .email("john.doe1@ssp.geneva.com")
            .status(OneCentralUserStatus.ACTIVE)
            .username("john.doe1")
            .systemUser(true)
            .build();
    OAuth2ClientContext oAuth2ClientContext = s2sTemplate.getOAuth2ClientContext();
    oAuth2ClientContext.setAccessToken(new DefaultOAuth2AccessToken("11223344"));

    wireMockRule.stubFor(
        post(urlPathMatching(CREATE_USER_URL_PATH.getResourcePath()))
            .withHeader("Content-Type", matching(APPLICATION_JSON_VALUE))
            .willReturn(
                aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", APPLICATION_JSON_VALUE)
                    .withBody(
                        "{\n"
                            + "  \"id\": 224264,\n"
                            + "  \"firstName\": \"John\",\n"
                            + "  \"lastName\": \"Doe\",\n"
                            + "  \"username\": \"john.doe1\",\n"
                            + "  \"email\": \"john.doe1@ssp.geneva.com\",\n"
                            + "  \"status\": \"ACTIVE\",\n"
                            + "  \"internal\": false,\n"
                            + "  \"systemUser\": true,\n"
                            + "  \"defaultOrgSapId\": 0,\n"
                            + "  \"defaultVideoOrgId\": 0,\n"
                            + "  \"primaryUserContact\": false,\n"
                            + "  \"last_login_date\": \"\"\n"
                            + "}")));

    // when
    ResponseEntity<OneCentralUser> response =
        userManagementRepository.createUser(accessToken, request);

    // then
    assertNotNull(response);
  }

  @Test
  void shouldSuccessfullyUpdateUser() {
    // given
    var accessToken = s2sTemplate.getAccessToken().getValue();
    OneCentralUserRequestDTO request =
        OneCentralUserRequestDTO.builder()
            .firstName("John")
            .lastName("Doe")
            .email("john.doe1@ssp.geneva.com")
            .status(OneCentralUserStatus.ACTIVE)
            .username("john.doe1")
            .systemUser(true)
            .contactName("John")
            .phone("555-555-5555")
            .build();

    SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
    requestFactory.setOutputStreaming(false);
    s2sTemplate.setRequestFactory(requestFactory);

    wireMockRule.stubFor(
        put(urlPathMatching(CREATE_USER_URL_PATH.getResourcePath() + request.getUsername()))
            .withHeader("Content-Type", matching(APPLICATION_JSON_VALUE))
            .willReturn(
                aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", APPLICATION_JSON_VALUE)
                    .withHeader("Connection", CONNECTION_CLOSE)
                    .withBody(
                        "{\n"
                            + "  \"id\": 224264,\n"
                            + "  \"firstName\": \"John\",\n"
                            + "  \"lastName\": \"Doe\",\n"
                            + "  \"username\": \"john.doe1\",\n"
                            + "  \"email\": \"john.doe1@ssp.geneva.com\",\n"
                            + "  \"status\": \"ACTIVE\",\n"
                            + "  \"internal\": false,\n"
                            + "  \"systemUser\": true,\n"
                            + "  \"defaultOrgSapId\": 0,\n"
                            + "  \"defaultVideoOrgId\": 0,\n"
                            + "  \"phone\": \"555-555-5555\",\n"
                            + "  \"primaryUserContact\": false,\n"
                            + "  \"contactName\": \"John\",\n"
                            + "  \"contactEmail\": \"john.doe1@ssp.geneva.com\",\n"
                            + "  \"countryCd\": \"\",\n"
                            + "  \"last_login_date\": \"\"\n"
                            + "}")));
    // when
    ResponseEntity<OneCentralUser> response =
        userManagementRepository.updateUser(accessToken, request);

    // then
    assertNotNull(response);
  }

  @Test
  void shouldSuccessfullyGetUserByEmail() {
    // given
    final String userEmail = "john.doe+1@ssp.geneva.com";
    final String searchSource = "One-Central";
    var accessToken = s2sTemplate.getAccessToken().getValue();

    wireMockRule.stubFor(
        get(urlMatching(
                GET_USERS.getResourcePath()
                    + "\\?email="
                    + URLEncoder.encode(userEmail, StandardCharsets.UTF_8)
                    + "&searchSource="
                    + searchSource))
            .willReturn(
                aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", APPLICATION_JSON_VALUE)
                    .withBody(
                        "{\n"
                            + "  \"list\": ["
                            + "{\n"
                            + "  \"id\": 224264,\n"
                            + "  \"firstName\": \"John\",\n"
                            + "  \"lastName\": \"Doe\",\n"
                            + "  \"username\": \"john.doe1\",\n"
                            + "  \"email\": \"john.doe+1@ssp.geneva.com\",\n"
                            + "  \"status\": \"ACTIVE\",\n"
                            + "  \"internal\": false,\n"
                            + "  \"systemUser\": true,\n"
                            + "  \"defaultOrgSapId\": 0,\n"
                            + "  \"defaultVideoOrgId\": 0,\n"
                            + "  \"primaryUserContact\": false,\n"
                            + "  \"last_login_date\": \"\"\n"
                            + "}"
                            + "], \"totalCount\": 1}")));

    // when
    ResponseEntity<OneCentralUsersResponseDTO> response =
        userManagementRepository.getUsersByEmail(accessToken, userEmail);

    // then
    assertNotNull(response);
    assertNotNull(response.getBody());

    assertEquals(1, response.getBody().getUsers().size());

    OneCentralUser user = response.getBody().getUsers().get(0);
    assertEquals("John", user.getFirstName());
    assertEquals("Doe", user.getLastName());
    assertEquals("john.doe1", user.getUsername());
    assertEquals("john.doe+1@ssp.geneva.com", user.getEmail());
  }
}
