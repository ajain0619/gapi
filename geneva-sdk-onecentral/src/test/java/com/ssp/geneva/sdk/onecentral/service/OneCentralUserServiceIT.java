package com.ssp.geneva.sdk.onecentral.service;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.delete;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.matching;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.put;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static com.ssp.geneva.sdk.onecentral.model.OneCentralSdkResourcePath.CREATE_USER_URL_PATH;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import com.ssp.geneva.common.test.junit.extension.WireMockExtension;
import com.ssp.geneva.sdk.onecentral.config.OneCentralSdkConfig;
import com.ssp.geneva.sdk.onecentral.dto.OneCentralExtendedUserRequestDTO;
import com.ssp.geneva.sdk.onecentral.dto.OneCentralUserRequestDTO;
import com.ssp.geneva.sdk.onecentral.model.OneCentralSdkResourcePath;
import com.ssp.geneva.sdk.onecentral.model.OneCentralUserStatus;
import com.ssp.geneva.sdk.onecentral.repository.TestRepositoryConfig;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@Disabled
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {TestRepositoryConfig.class, OneCentralSdkConfig.class})
@TestPropertySource("classpath:application-test.properties")
class OneCentralUserServiceIT {

  @RegisterExtension
  static WireMockExtension wireMockRule = new WireMockExtension(options().port(9999));

  @Autowired private OneCentralUserService userService;
  @Autowired private OAuth2RestTemplate s2sTemplate;

  private final String CONNECTION_CLOSE = "close";

  @Test
  void shouldSuccessfullyUpdateUserWithRoles() {
    // given
    var accessToken = "accessToken";
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
            .roleIds(new long[] {101L, 102L})
            .build();
    var extendedRequest =
        OneCentralExtendedUserRequestDTO.builder()
            .userRequestDTO(request)
            .roleName("ROLE_ADMIN_SELLER")
            .sellerSeatEnabled(true)
            .build();

    OAuth2ClientContext oAuth2ClientContext = s2sTemplate.getOAuth2ClientContext();
    oAuth2ClientContext.setAccessToken(new DefaultOAuth2AccessToken("11223344"));

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

    final String testUser = "john.doe1";
    var requestPath =
        OneCentralSdkResourcePath.GET_SINGLE_USER_URL_PATH
            .getResourcePath()
            .replace("{username}", testUser);
    wireMockRule.stubFor(
        get(requestPath)
            .willReturn(
                aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", APPLICATION_JSON_VALUE)
                    .withBody(
                        "{"
                            + "  \"id\": 224264,"
                            + "  \"firstName\": \"John\","
                            + "  \"lastName\": \"Doe\","
                            + "  \"username\": \"john.doe1\","
                            + "  \"email\": \"john@smith.com\""
                            + "}")));

    var rolesRequestPath =
        OneCentralSdkResourcePath.ROLE_FETCH_URL_PATH
            .getResourcePath()
            .replace("{type}", "user")
            .replace("{id}", "john.doe1");
    wireMockRule.stubFor(
        get(rolesRequestPath)
            .willReturn(
                aResponse()
                    .withStatus(HttpStatus.OK.value())
                    .withHeader("Content-Type", APPLICATION_JSON_VALUE)
                    .withBody(
                        "{\"list\": [{\"id\": 100, \"roleId\": 100, \"type\": \"role1\"}, {\"id\": 510362, \"roleId\": 510362, \"type\": \"apirole\"}]}")));

    var requestUrl =
        OneCentralSdkResourcePath.ROLE_ASSIGN_URL_PATH
            .getResourcePath()
            .replace("{type}", "user")
            .replace("{id}", "john.doe1")
            .replace("{roleId}", "510362");
    wireMockRule.stubFor(
        delete(requestUrl)
            .willReturn(
                aResponse()
                    .withStatus(HttpStatus.OK.value())
                    .withHeader("Content-Type", APPLICATION_JSON_VALUE)
                    .withBody("{\"roleId\": 100,\"id\": \"100\"}")));

    requestUrl =
        OneCentralSdkResourcePath.ROLE_ASSIGN_URL_PATH
            .getResourcePath()
            .replace("{type}", "user")
            .replace("{id}", "john.doe1")
            .replace("{roleId}", "[0-9]+");
    wireMockRule.stubFor(
        post(urlMatching(requestUrl))
            .willReturn(
                aResponse()
                    .withStatus(HttpStatus.OK.value())
                    .withHeader("Content-Type", APPLICATION_JSON_VALUE)
                    .withBody("{\"id\": 100, \"roleId\": 100,  \"type\": \"role1\"}")));

    var out = userService.updateUser(accessToken, extendedRequest);
    // then
    assertNotNull(out);
  }
}
