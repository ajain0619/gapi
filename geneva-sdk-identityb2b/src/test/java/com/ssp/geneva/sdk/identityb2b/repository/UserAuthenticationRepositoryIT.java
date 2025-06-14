package com.ssp.geneva.sdk.identityb2b.repository;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.matching;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.MockitoAnnotations.openMocks;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import com.ssp.geneva.common.test.junit.extension.WireMockExtension;
import com.ssp.geneva.sdk.identityb2b.config.IdentityB2bSdkConfig;
import com.ssp.geneva.sdk.identityb2b.exception.IdentityB2bSdkException;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {TestRepositoryConfig.class, IdentityB2bSdkConfig.class})
@TestPropertySource("classpath:application-test.properties")
class UserAuthenticationRepositoryIT {
  @RegisterExtension
  static WireMockExtension wireMockRule = new WireMockExtension(options().port(9999));

  @Autowired private UserAuthenticationRepository userAuthenticationRepository;

  @BeforeEach
  void setUp() {
    openMocks(this);
  }

  String accessToken = "11223344";

  @Test
  void shouldSuccessfullyAuthenticate() {

    wireMockRule.stubFor(
        get(urlPathMatching("/identity/oauth2/userinfo/*"))
            .withHeader("Content-Type", matching(APPLICATION_JSON_VALUE))
            .willReturn(
                aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", APPLICATION_JSON_VALUE)
                    .withBody(
                        "{\n"
                            + "  \"sub\": \"57dab7bc3933cd0289da09c8\",\n"
                            + "  \"updated_at\": \"1481665184\",\n"
                            + "  \"name\": \"BJ73-DY3U-AJQV-BM5F5\",\n"
                            + "  \"given_name\": \"Aaren\",\n"
                            + "  \"family_name\": \"Atp\",\n"
                            + "  \"email\": \"BJ73-DY3U-AJQV-BM5F5@externalstest.aol.com\"\n"
                            + "}")));

    ResponseEntity<Map<String, Object>> response =
        userAuthenticationRepository.getUserInfo(accessToken);
    assertNotNull(response);
  }

  @Test
  void shouldFailOnInvalidAuthentication() {

    wireMockRule.stubFor(
        get(urlPathMatching("/identity/oauth2/userinfo/*"))
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
    var exception =
        assertThrows(
            IdentityB2bSdkException.class,
            () -> userAuthenticationRepository.getUserInfo(accessToken));

    // then
    assertEquals(HttpStatus.UNAUTHORIZED, exception.getErrorCode().getHttpStatus());
  }
}
