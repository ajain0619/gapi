package com.ssp.geneva.sdk.xandr.repository;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.matching;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import com.ssp.geneva.common.test.junit.extension.WireMockExtension;
import com.ssp.geneva.sdk.xandr.config.TestRepositoryConfig;
import com.ssp.geneva.sdk.xandr.error.XandrSdkErrorCodes;
import com.ssp.geneva.sdk.xandr.exception.XandrSdkException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.RestTemplate;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
@ContextConfiguration(classes = {TestRepositoryConfig.class})
@TestPropertySource("classpath:application-test.properties")
class AuthRepositoryIT {
  private static final int port = 8083;

  @RegisterExtension
  static WireMockExtension wireMockRule =
      new WireMockExtension(options().bindAddress("127.0.0.1").port(port));

  @InjectMocks private AuthRepository authRepository;

  @Autowired RestTemplate xandrSdkRestTemplate;

  @Value("${xandr.service.endpoint}")
  private String xandrEndpoint;

  @Value("${xandr.service.credentials}")
  private String xandrCredentials;

  @Value("${xandr.service.credentials.ms.rebroadcast}")
  private String xandrCredentialsMsRebroadcast;

  private String successBodyXandr =
      """
    {
      "response": {
        "status": "OK",
        "token": "h20hbtptiv3vlp1rkm3ve1qig0"
      }
    }
    """;

  private String successBodyXandrMsRebroadcast =
      """
    {
      "response": {
        "status": "OK",
        "token": "hasgkdjhfgasjdfhgkasjdhfg"
      }
    }
    """;

  @BeforeEach
  void setUp() {
    authRepository =
        new AuthRepository(
            xandrEndpoint + ":" + port,
            xandrSdkRestTemplate,
            xandrCredentials,
            xandrCredentialsMsRebroadcast);
  }

  @Test
  void shouldReturnAuthToken() {
    wireMockRule.stubFor(
        post(urlPathMatching("/auth"))
            .withHeader("Content-Type", matching(APPLICATION_JSON_VALUE))
            .willReturn(
                aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", APPLICATION_JSON_VALUE)
                    .withBody(successBodyXandr)));

    String authToken = authRepository.getAuthToken(xandrCredentials);
    assertNotNull(authToken);
    assertEquals("h20hbtptiv3vlp1rkm3ve1qig0", authToken);
  }

  @Test
  void shouldReturnAuthHeaderWithToken() {
    wireMockRule.stubFor(
        post(urlPathMatching("/auth"))
            .withHeader("Content-Type", matching(APPLICATION_JSON_VALUE))
            .willReturn(
                aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", APPLICATION_JSON_VALUE)
                    .withBody(successBodyXandr)));

    HttpHeaders httpHeaders = authRepository.getAuthHeader(xandrCredentials);
    assertNotNull(httpHeaders);
    String authToken = httpHeaders.get("Authorization").get(0);
    assertNotNull(authToken);
    assertEquals("h20hbtptiv3vlp1rkm3ve1qig0", authToken);
  }

  @Test
  void shouldReturnAuthHeaderForXandr() {
    wireMockRule.stubFor(
        post(urlPathMatching("/auth"))
            .withHeader("Content-Type", matching(APPLICATION_JSON_VALUE))
            .willReturn(
                aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", APPLICATION_JSON_VALUE)
                    .withBody(successBodyXandr)));

    HttpHeaders httpHeaders = authRepository.getAuthHeaderForXandr();
    assertNotNull(httpHeaders);
    String authToken = httpHeaders.get("Authorization").get(0);
    assertNotNull(authToken);
    assertEquals("h20hbtptiv3vlp1rkm3ve1qig0", authToken);
  }

  @Test
  void shouldReturnAuthHeaderForXandrMsRebroadcast() {
    wireMockRule.stubFor(
        post(urlPathMatching("/auth"))
            .withHeader("Content-Type", matching(APPLICATION_JSON_VALUE))
            .willReturn(
                aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", APPLICATION_JSON_VALUE)
                    .withBody(successBodyXandrMsRebroadcast)));

    HttpHeaders httpHeaders = authRepository.getAuthHeaderForXandrMsRebroadcast();
    assertNotNull(httpHeaders);
    String authToken = httpHeaders.get("Authorization").get(0);
    assertNotNull(authToken);
    assertEquals("hasgkdjhfgasjdfhgkasjdhfg", authToken);
  }

  @Test
  void shouldThrowExceptionWithHttpClientError() {

    Integer[] codes = new Integer[] {401, 403, 404, 500};
    for (Integer code : codes) {
      wireMockRule.stubFor(
          post(urlPathMatching("/auth"))
              .withHeader("Content-Type", matching(APPLICATION_JSON_VALUE))
              .willReturn(
                  aResponse().withStatus(code).withHeader("Content-Type", APPLICATION_JSON_VALUE)));

      XandrSdkException exception =
          assertThrows(XandrSdkException.class, () -> authRepository.getAuthHeaderForXandr());
      assertNotNull(exception);
      assertNotNull(exception.getErrorCode());
      assertEquals(XandrSdkErrorCodes.XANDR_SDK_HTTP_CLIENT_ERROR, exception.getErrorCode());
    }
  }
}
