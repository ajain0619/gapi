package com.ssp.geneva.sdk.onecentral.repository;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.delete;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import com.ssp.geneva.common.test.junit.extension.WireMockExtension;
import com.ssp.geneva.sdk.onecentral.config.OneCentralSdkConfig;
import com.ssp.geneva.sdk.onecentral.dto.OneCentralUserRequestDTO;
import com.ssp.geneva.sdk.onecentral.model.OneCentralSdkResourcePath;
import org.junit.jupiter.api.BeforeEach;
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

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {TestRepositoryConfig.class, OneCentralSdkConfig.class})
@TestPropertySource("classpath:application-test.properties")
class AuthorizationManagementRepositoryIT {

  @RegisterExtension
  static WireMockExtension wireMockRule = new WireMockExtension(options().port(9999));

  @Autowired private OAuth2RestTemplate s2sTemplate;
  @Autowired private AuthorizationManagementRepository authorizationManagementRepository;

  @BeforeEach
  void setUp() {
    OAuth2ClientContext oAuth2ClientContext = s2sTemplate.getOAuth2ClientContext();
    oAuth2ClientContext.setAccessToken(new DefaultOAuth2AccessToken("11223344"));
  }

  @Test
  void shouldGetUserRoles() {
    // given
    var accessToken = s2sTemplate.getAccessToken().getValue();
    var request = OneCentralUserRequestDTO.builder().username("user1").build();
    final String testUser = "user1";
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
                            + "  \"id\": 1,"
                            + "  \"firstName\": \"John\","
                            + "  \"lastName\": \"Smith\","
                            + "  \"username\": \"user1\","
                            + "  \"email\": \"john@smith.com\""
                            + "}")));

    var rolesRequestPath =
        OneCentralSdkResourcePath.ROLE_FETCH_URL_PATH
            .getResourcePath()
            .replace("{type}", "user")
            .replace("{id}", "user1");
    wireMockRule.stubFor(
        get(rolesRequestPath)
            .willReturn(
                aResponse()
                    .withStatus(HttpStatus.OK.value())
                    .withHeader("Content-Type", APPLICATION_JSON_VALUE)
                    .withBody(
                        "{\"list\": [{\"id\": \"100\", \"roleId\": 100, \"type\": \"role1\"}]}")));

    var response = authorizationManagementRepository.getUserRoles(accessToken, request);
    assertEquals(1, response.getBody().getList().size());
    var role = response.getBody().getList().get(0);
    assertEquals(100, role.getRoleId());
    assertEquals("role1", role.getType());
  }

  @Test
  void shouldDeleteUserRole() {
    // given
    var accessToken = s2sTemplate.getAccessToken().getValue();
    var requestUrl =
        OneCentralSdkResourcePath.ROLE_ASSIGN_URL_PATH
            .getResourcePath()
            .replace("{type}", "user")
            .replace("{id}", "1")
            .replace("{roleId}", "100");
    wireMockRule.stubFor(
        delete(requestUrl)
            .willReturn(
                aResponse()
                    .withStatus(HttpStatus.OK.value())
                    .withHeader("Content-Type", APPLICATION_JSON_VALUE)
                    .withBody("{\"roleId\": 100,\"id\": \"1\"}")));

    // then
    var response = authorizationManagementRepository.deleteUserRole(accessToken, "1", "100");
    assertEquals(100, response.getBody().getRoleId());
  }

  @Test
  void shouldSuccessfullyAssignUserRole() {

    // given
    var accessToken = s2sTemplate.getAccessToken().getValue();
    var requestUrl =
        OneCentralSdkResourcePath.ROLE_ASSIGN_URL_PATH
            .getResourcePath()
            .replace("{type}", "user")
            .replace("{id}", "1")
            .replace("{roleId}", "100");
    wireMockRule.stubFor(
        post(requestUrl)
            .willReturn(
                aResponse()
                    .withStatus(HttpStatus.OK.value())
                    .withHeader("Content-Type", APPLICATION_JSON_VALUE)
                    .withBody("{\"id\": \"100\", \"roleId\": 100, \"name\": \"role1\"}")));

    // then
    var response = authorizationManagementRepository.assignUserRole(accessToken, "1", 100L);
    assertEquals(100, response.getBody().getRoleId());
  }

  @Test
  void shouldAssignUserRoleWithStringId() {
    // given
    var accessToken = s2sTemplate.getAccessToken().getValue();
    var requestUrl =
        OneCentralSdkResourcePath.ROLE_ASSIGN_URL_PATH
            .getResourcePath()
            .replace("{type}", "user")
            .replace("{id}", "1")
            .replace("{roleId}", "510335");
    wireMockRule.stubFor(
        post(requestUrl)
            .willReturn(
                aResponse()
                    .withStatus(HttpStatus.OK.value())
                    .withHeader("Content-Type", APPLICATION_JSON_VALUE)
                    .withBody("{\"id\": \"100\", \"roleId\": 100, \"name\": \"role1\"}")));

    // when
    var response =
        authorizationManagementRepository.assignRole(accessToken, "1", "ROLE_ADMIN_NEXAGE");

    // then
    assertEquals(100, response.getBody().getRoleId());
  }

  @Test
  void shouldGetOneCentralRoles() {
    var result = authorizationManagementRepository.getOneCentralRole("ROLE_ADMIN_NEXAGE");
    assertEquals(510335, result);
    result = authorizationManagementRepository.getOneCentralRole("ROLE_MANAGER_YIELD_NEXAGE");
    assertEquals(510338, result);
    result = authorizationManagementRepository.getOneCentralRole("ROLE_MANAGER_SMARTEX_NEXAGE");
    assertEquals(510374, result);
    result = authorizationManagementRepository.getOneCentralRole("ROLE_MANAGER_NEXAGE");
    assertEquals(510380, result);
    result = authorizationManagementRepository.getOneCentralRole("ROLE_USER_NEXAGE");
    assertEquals(510383, result);
    result = authorizationManagementRepository.getOneCentralRole("ROLE_ADMIN_SELLER");
    assertEquals(510386, result);
    result = authorizationManagementRepository.getOneCentralRole("ROLE_MANAGER_SELLER");
    assertEquals(510365, result);
    result = authorizationManagementRepository.getOneCentralRole("ROLE_USER_SELLER");
    assertEquals(510368, result);
    result = authorizationManagementRepository.getOneCentralRole("ROLE_ADMIN_BUYER");
    assertEquals(510389, result);
    result = authorizationManagementRepository.getOneCentralRole("ROLE_MANAGER_BUYER");
    assertEquals(510371, result);
    result = authorizationManagementRepository.getOneCentralRole("ROLE_USER_BUYER");
    assertEquals(510377, result);
    result = authorizationManagementRepository.getOneCentralRole("ROLE_ADMIN_SEATHOLDER");
    assertEquals(510350, result);
    result = authorizationManagementRepository.getOneCentralRole("ROLE_MANAGER_SEATHOLDER");
    assertEquals(510401, result);
    result = authorizationManagementRepository.getOneCentralRole("ROLE_USER_SEAT_HOLDER");
    assertEquals(510395, result);
    result = authorizationManagementRepository.getOneCentralRole("ROLE_ADMIN_SELLER_SEAT");
    assertEquals(510392, result);
    result = authorizationManagementRepository.getOneCentralRole("ROLE_MANAGER_SELLER_SEAT");
    assertEquals(510356, result);
    result = authorizationManagementRepository.getOneCentralRole("ROLE_USER_SELLER_SEAT");
    assertEquals(510398, result);
    result = authorizationManagementRepository.getOneCentralRole("ROLE_API_BUYER");
    assertEquals(510870, result);
    result = authorizationManagementRepository.getOneCentralRole("ROLE_API_SELLER");
    assertEquals(510869, result);
    result = authorizationManagementRepository.getOneCentralRole("ROLE_API_IIQ_NEXAGE");
    assertEquals(510362, result);
    result = authorizationManagementRepository.getOneCentralRole("ROLE_DUMMY_NEXAGE");
    assertEquals(510896, result);
    result = authorizationManagementRepository.getOneCentralRole("ROLE_DUMMY_SELLER");
    assertEquals(510896, result);
    result = authorizationManagementRepository.getOneCentralRole("ROLE_DUMMY_BUYER");
    assertEquals(510896, result);
    result = authorizationManagementRepository.getOneCentralRole("OTHER");
    assertNull(result);
  }

  @Test
  void shouldBeGenevaRole() {
    assertTrue(authorizationManagementRepository.isGenevaRole(510356L));
  }

  @Test
  void shouldNotBeGenevaRole() {
    assertFalse(authorizationManagementRepository.isGenevaRole(1L));
  }
}
